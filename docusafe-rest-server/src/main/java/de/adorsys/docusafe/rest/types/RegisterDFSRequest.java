package de.adorsys.docusafe.rest.types;

import de.adorsys.dfs.connection.api.types.connection.*;

import java.net.URL;


public class RegisterDFSRequest {
    private AmazonS3AccessKey amazonS3AccessKey;
    private AmazonS3SecretKey amazonS3SecretKey;
    private AmazonS3Region amazonS3Region;
    private AmazonS3RootBucketName amazonS3RootBucketName;
    private URL url;

    // if set, all amazonstuff is ignored
    private FilesystemRootBucketName filesystemRootBucketName;

    public RegisterDFSRequest(AmazonS3AccessKey amazonS3AccessKey, AmazonS3SecretKey amazonS3SecretKey, AmazonS3Region amazonS3Region, AmazonS3RootBucketName amazonS3RootBucketName, URL url) {
        this.amazonS3AccessKey = amazonS3AccessKey;
        this.amazonS3SecretKey = amazonS3SecretKey;
        this.amazonS3Region = amazonS3Region;
        this.amazonS3RootBucketName = amazonS3RootBucketName;
        this.url = url;
    }

    public RegisterDFSRequest(FilesystemRootBucketName filesystemRootBucketName) {
        this.filesystemRootBucketName = filesystemRootBucketName;
    }

    public AmazonS3AccessKey getAmazonS3AccessKey() {
        return amazonS3AccessKey;
    }

    public AmazonS3SecretKey getAmazonS3SecretKey() {
        return amazonS3SecretKey;
    }

    public AmazonS3Region getAmazonS3Region() {
        return amazonS3Region;
    }

    public AmazonS3RootBucketName getAmazonS3RootBucketName() {
        return amazonS3RootBucketName;
    }

    public URL getUrl() {
        return url;
    }

    public FilesystemRootBucketName getFilesystemRootBucketName() {
        return filesystemRootBucketName;
    }
}
