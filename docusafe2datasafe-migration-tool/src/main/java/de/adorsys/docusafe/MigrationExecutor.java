package de.adorsys.docusafe;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.adorsys.datasafe.simple.adapter.api.types.AmazonS3DFSCredentials;
import de.adorsys.datasafe.simple.adapter.api.types.DFSCredentials;
import de.adorsys.dfs.connection.api.service.api.DFSConnection;
import de.adorsys.dfs.connection.api.types.connection.AmazonS3RootBucketName;
import de.adorsys.dfs.connection.impl.amazons3.AmazonS3ConnectionProperitesImpl;
import de.adorsys.dfs.connection.impl.amazons3.AmazonS3DFSConnection;
import de.adorsys.docusafe.service.api.types.UserID;
import de.adorsys.docusafe.spring.config.SpringAmazonS3ConnectionProperties;
import de.adorsys.docusafe.spring.config.SpringDFSConnectionProperties;
import de.adorsys.docusafe.spring.factory.SpringDFSConnectionFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class MigrationExecutor {

    public static void main(String[] args) {
        System.out.println(
                "Usage: java -jar docusafe2datasafe-migration-tool-pkg.jar " +
                        "<PATH TO DOCUSAFE PROPERTIES FILE> " +
                        "<PATH TO DATASAFE ROOT> " +
                        "<USERS' GENERIC PASSWORD> " +
                        "-skip <OPTIONAL, FILE WITH LIST OF USERS TO SKIP> " +
                        "-only <OPTIONAL, FILE WITH LIST OF USERS TO MIGRATE (if they are found)>"
        );

        if (args.length < 3 || args.length > 5) {
            System.err.println("Wrong number of arguments supplied, aborting");
            System.exit(1);
        }

        SpringAmazonS3ConnectionProperties properties = properties(args[0]);
        String datasafeBucketRoot = args[1];
        String userGenericPassword = args[2];

        Set<String> skipUsers = Collections.emptySet();
        Set<String> migrateOnlyUsers = Collections.emptySet();

        if (args.length > 3) {
            String[] extras = Arrays.copyOfRange(args, 3, args.length);
            skipUsers = tryReadStringSetFromArgs(extras, "-skip", "skip user list");
            migrateOnlyUsers= tryReadStringSetFromArgs(extras, "-only", "migrate only users");
        }

        SpringDFSConnectionProperties wired = new SpringDFSConnectionProperties();
        wired.setAmazons3(properties);
        DFSConnection connection = new SpringDFSConnectionFactory(wired).getDFSConnectionWithSubDir(null);

        String migrationId = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("YYYYMMddhhmm"));
        Thread.currentThread().setName("ID-" + migrationId);
        log.info("Start of Docusafe-2-Datasafe migration with id '{}' with root path bucket/path for Datasafe: '{}'",
                migrationId, datasafeBucketRoot);

        Set<UserID> users = new DocusafeUsersFinder().getUsers(connection);

        String datasafeRoot = datasafeBucketRoot + "/" + migrationId;

        // point to correct Datasafe directory
        AmazonS3ConnectionProperitesImpl datasafe = cloneProperties(connection);
        datasafe.setAmazonS3RootBucketName(new AmazonS3RootBucketName(datasafeRoot));

        int cntMigrated = new UserMigratingService(
                (AmazonS3DFSConnection) connection,
                getDatasafeDFSCredentials(datasafe, "")
        ).migrate(
                users,
                migrateOnlyUsers,
                skipUsers,
                userGenericPassword
        );

        log.info("Migrated {} of {} total found users to Datasafe with path: '{}/'", cntMigrated, users.size(), datasafeRoot);
    }

    private static SpringAmazonS3ConnectionProperties properties(String path) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try (InputStream is = Files.newInputStream(Paths.get(path), StandardOpenOption.READ)) {
            JsonNode node = mapper.readTree(is).get("docusafe").get("storeconnection").get("amazons3");
            return mapper
                    .readerFor(SpringAmazonS3ConnectionProperties.class)
                    .readValue(node);
        } catch (IOException ex) {
            System.err.printf("Failed to open: %s due to %s%n", path, ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    private static DFSCredentials getDatasafeDFSCredentials(AmazonS3ConnectionProperitesImpl props, String root) {
        return AmazonS3DFSCredentials.builder()
                .url(props.getUrl().toString())
                .accessKey(props.getAmazonS3AccessKey().getValue())
                .secretKey(props.getAmazonS3SecretKey().getValue())
                .region(props.getAmazonS3Region().getValue())
                .rootBucket(props.getAmazonS3RootBucketName().getValue() + "/" + root)
                .build();
    }

    @SneakyThrows
    private static AmazonS3ConnectionProperitesImpl cloneProperties(DFSConnection connection) {
        AmazonS3ConnectionProperitesImpl props = (AmazonS3ConnectionProperitesImpl) connection.getConnectionProperties();
        return new AmazonS3ConnectionProperitesImpl(props);

    }

    @SneakyThrows
    private static Set<String> tryReadStringSetFromArgs(String[] arguments, String key, String hint) {
        for (String argument : arguments) {
            if (!argument.startsWith(key)) {
                continue;
            }

            String path = argument.split(key, 2)[1];
            log.info("Reading {} from '{}'", hint, path);
            try (Stream<String> is = Files.lines(Paths.get(path))) {
                return is.collect(Collectors.toSet());
            }
        }

        return Collections.emptySet();
    }
}
