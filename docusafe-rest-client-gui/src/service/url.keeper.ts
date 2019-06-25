import {Injectable} from "@angular/core";

@Injectable()
export class UrlKeeper {

    private destinationUrls: string[] = [
        "http://docusafe-rest-server-psp-docusafe-performancetest.cloud.adorsys.de",
        "http://localhost:9991",
    ];
    private url = this.destinationUrls[0];

    constructor() {
    }

    public setUrl(url : string) {
        console.log("url set to " + url);
        this.url = url;
    }

    public getUrl() : string {
        return this.url;
    }

    public getUrls() : string[] {
        return this.destinationUrls;
    }


}
