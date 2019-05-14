export class FilesystemTYPE {
    filesystemRootBucketName: string;
};

export class AmazonS3TYPE {
    amazonS3AccessKey: string;
    amazonS3Region: string;
    amazonS3RootBucketName: string;
    amazonS3SecretKey: string;
    url: string;
};

export class DFSCredentialsTYPE {
    amazons3: AmazonS3TYPE;
    filesystem: FilesystemTYPE;
};
