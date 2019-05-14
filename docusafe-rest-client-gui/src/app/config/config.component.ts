import {Component, OnInit} from '@angular/core';
import {ConfigService} from "../../service/config.service";
import {AmazonS3TYPE, DFSCredentialsTYPE, FilesystemTYPE, filesystemTYPE} from "../../types/dfs.credentials.type";

@Component({
    selector: 'app-config',
    templateUrl: './config.component.html',
    styleUrls: ['./config.component.css']
})
export class ConfigComponent implements OnInit {

    destinationUrl: string = "http://localhost:9991";

    defaultFilesystemCredentials : DFSCredentialsTYPE;
    defaultAmazonS3Credentials : DFSCredentialsTYPE;
    dfsCredentials: DFSCredentialsTYPE;

    constructor(private configService: ConfigService) {
        console.log("construction");
        this.setupDfsCredentials();
    }

    ngOnInit() {
        console.log("ngOnInit");
        this.setupDfsCredentials();
    }

    private setupDfsCredentials () {
        this.defaultFilesystemCredentials = new DFSCredentialsTYPE();
        this.defaultFilesystemCredentials.filesystem = new FilesystemTYPE();
        this.defaultFilesystemCredentials.filesystem.filesystemRootBucketName = "";

        this.defaultAmazonS3Credentials = new DFSCredentialsTYPE();
        this.defaultAmazonS3Credentials.amazons3 = new AmazonS3TYPE();
        this.defaultAmazonS3Credentials.amazons3.amazonS3RootBucketName = "";
        this.defaultAmazonS3Credentials.amazons3.amazonS3AccessKey = "";
        this.defaultAmazonS3Credentials.amazons3.amazonS3SecretKey = "";
        this.defaultAmazonS3Credentials.amazons3.url = "";
        this.defaultAmazonS3Credentials.amazons3.amazonS3Region = "";

        this.dfsCredentials = this.defaultFilesystemCredentials;
    }

    read() {
        this.configService.getConfig(this.destinationUrl, this);
    }

    write() {
        this.configService.setConfig(this.destinationUrl, this.dfsCredentials);
    }

    public setDFSConfig(dfscredentials : DFSCredentialsTYPE) {
        this.dfsCredentials = dfscredentials;
    }

    public DFSTypes = [
        'amazons3',
        'filesystem'
    ];

    isDFSType(s : string) : boolean{
        var result : boolean;
        if (s === this.DFSTypes[1]) {
            result = this.dfsCredentials.filesystem != null;
        } else {
            result =  this.dfsCredentials.amazons3 != null;
        }
        console.log("isDFSType(" + s  + ") -> "  + result);
        return result;
    }

    checkedDFSType(e) : string {
        if (this.isDFSType(e)) {
            return "checked";
        }
        return "";
    }

    onDFSTypeChange(e) {
        if (e === this.DFSTypes[0]) {
            this.dfsCredentials = this.defaultAmazonS3Credentials;
        } else {
            this.dfsCredentials = this.defaultFilesystemCredentials;
        }
    }

}