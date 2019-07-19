package de.adorsys.docusafe;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.adorsys.dfs.connection.api.service.api.DFSConnection;
import de.adorsys.docusafe.service.api.types.UserID;
import de.adorsys.docusafe.spring.config.SpringAmazonS3ConnectionProperties;
import de.adorsys.docusafe.spring.config.SpringDFSConnectionProperties;
import de.adorsys.docusafe.spring.factory.SpringDFSConnectionFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Slf4j
public class MigrationExecutor {

    public static void main(String[] args) {
        System.out.println(
                "Usage: java -jar docusafe2datasafe-migration-tool.jar " +
                        "<PATH TO DOCUSAFE PROPERTIES FILE> <PATH TO DATASAFE ROOT> <GENERIC PASSWORD>"
        );

        if (args.length != 3) {
            System.err.println("Wrong number of arguments supplied, aborting");
            System.exit(1);
        }

        SpringAmazonS3ConnectionProperties properties = properties(args[0]);
        String datasafeBucketRoot = args[1];
        String genericPassword = args[2];

        SpringDFSConnectionProperties wired = new SpringDFSConnectionProperties();
        wired.setAmazons3(properties);
        DFSConnection connection = new SpringDFSConnectionFactory(wired).getDFSConnectionWithSubDir(null);

        String migrationId = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("YYYYMMddhhmm"));
        Thread.currentThread().setName("ID-" + migrationId);
        log.info("Start of Docusafe-2-Datasafe migration with id '{}'", migrationId);

        Set<UserID> users = new UserIdExtractor().getUsers(connection);

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
}
