package de.adorsys.docusafe.batch.client;

import de.adorsys.common.exceptions.BaseException;
import de.adorsys.dfs.connection.api.complextypes.BucketPath;
import de.adorsys.dfs.connection.api.filesystem.FilesystemConnectionPropertiesImpl;
import de.adorsys.dfs.connection.api.service.api.DFSConnection;
import de.adorsys.dfs.connection.api.types.ListRecursiveFlag;
import de.adorsys.dfs.connection.api.types.connection.*;
import de.adorsys.dfs.connection.impl.amazons3.AmazonS3ConnectionProperitesImpl;
import de.adorsys.dfs.connection.impl.factory.DFSConnectionFactory;
import de.adorsys.docusafe.business.impl.DocumentSafeServiceImpl;
import de.adorsys.docusafe.business.types.DocumentDirectoryFQN;
import de.adorsys.docusafe.business.types.DocumentFQN;
import de.adorsys.docusafe.service.api.keystore.types.ReadKeyPassword;
import de.adorsys.docusafe.service.api.types.UserID;
import de.adorsys.docusafe.service.api.types.UserIDAuth;
import lombok.SneakyThrows;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.security.Security;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.BaseStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Main {
    private static final String FILESYSTEM = "-filesystem";
    private static final String ROOT_BUCKET = "-s3rootbucket";
    private static final String S3URL = "-s3url";
    private static final String ACCESS_KEY = "-s3accesskey";
    private static final String SECRET_KEY = "-s3secretkey";
    private static final String REGION = "-s3region";
    private static final String USER = "-user";
    private static final String PASSWORD = "-password";
    private static final String OUTPUT_DIR = "-outputdir";
    private static final String[] knownArgs = {FILESYSTEM, ROOT_BUCKET, S3URL, ACCESS_KEY, SECRET_KEY, REGION, USER, PASSWORD, OUTPUT_DIR};


    @SneakyThrows
    public static void main(String[] args) {

        List<String> unknownArgs = Stream.of(args).filter(arg -> !isKnwon(arg)).collect(Collectors.toList());
        if (!unknownArgs.isEmpty()) {
            throw new BaseException("arguments not known: " + unknownArgs + " known arguments:" + Arrays.asList(knownArgs));
        }

        Map<String, String> givenArgs = Stream.of(args).collect(Collectors.toMap(arg -> arg.split("=", 2)[0], arg -> arg.split("=", 2)[1]));
        DFSConnection dfsConnection = null;
        if (givenArgs.containsKey(FILESYSTEM)) {
            FilesystemConnectionPropertiesImpl properties = new FilesystemConnectionPropertiesImpl();
            properties.setFilesystemRootBucketName(new FilesystemRootBucketName(givenArgs.get(FILESYSTEM)));
            dfsConnection = DFSConnectionFactory.get(properties);
        } else {
            if (!givenArgs.containsKey(S3URL) ||
                    !givenArgs.containsKey(ROOT_BUCKET) ||
                    !givenArgs.containsKey(ACCESS_KEY) ||
                    !givenArgs.containsKey(SECRET_KEY)
            ) {
                throw new BaseException("need argument for filesystem or s3 " + Arrays.asList(knownArgs));
            }
            AmazonS3ConnectionProperitesImpl properties = new AmazonS3ConnectionProperitesImpl();
            properties.setAmazonS3AccessKey(new AmazonS3AccessKey(givenArgs.get(ACCESS_KEY)));
            properties.setAmazonS3SecretKey(new AmazonS3SecretKey(givenArgs.get(SECRET_KEY)));
            properties.setUrl(new URL(givenArgs.get(S3URL)));
            properties.setAmazonS3Region(new AmazonS3Region(givenArgs.get(REGION)));
            properties.setAmazonS3RootBucketName(new AmazonS3RootBucketName(givenArgs.get(ROOT_BUCKET)));
            dfsConnection = DFSConnectionFactory.get(properties);
        }
        DocumentSafeServiceImpl documentSafeService = new DocumentSafeServiceImpl(dfsConnection);

        if (!givenArgs.containsKey(USER) || !givenArgs.containsKey(PASSWORD)) {
            throw new BaseException("need user and password " + Arrays.asList(knownArgs));
        }
        Security.addProvider(new BouncyCastleProvider());

        UserID userID = new UserID(givenArgs.get(USER));
        ReadKeyPassword readKeyPassword = new ReadKeyPassword(givenArgs.get(PASSWORD));
        UserIDAuth userIDAuth = new UserIDAuth(userID, readKeyPassword);
        List<DocumentFQN> list = documentSafeService.list(userIDAuth, new DocumentDirectoryFQN("/"), ListRecursiveFlag.TRUE);
        if (list.isEmpty()) {
            throw new BaseException(userIDAuth.getUserID() + " does not have any files. Did you think about -DSC-NO-BUCKETPATH-ENCRYPTION?");
        }

        String outputDir = ".";
        if (givenArgs.get(OUTPUT_DIR) != null) {
            outputDir = givenArgs.get(OUTPUT_DIR);
        }
        String filename = outputDir + "/" + givenArgs.get(USER) + ".zip";

        FileOutputStream fos = new FileOutputStream(filename);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        ZipOutputStream zos = new ZipOutputStream(bos);

        try {
            for (DocumentFQN documentFQN : list) {
                System.out.println("load " + documentFQN);
                zos.putNextEntry(new ZipEntry(userID.getValue() + BucketPath.BUCKET_SEPARATOR + documentFQN.getValue()));
                zos.write(documentSafeService.readDocument(userIDAuth, documentFQN).getDocumentContent().getValue());
                zos.closeEntry();
            }
        } finally {
            zos.close();
        }
        System.out.println("WROTE " + filename);
    }


    private static boolean isKnwon(String arg) {

        for (String knownArg : knownArgs) {
            if (arg.startsWith(knownArg + "=")) {
                return true;
            }
        }
        ;
        return false;
    }
}
