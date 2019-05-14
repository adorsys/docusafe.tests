import {Injectable} from "@angular/core";

@Injectable()
export class UrlKeeper {

    private url: string;
    constructor() {
    }

    public setUrl(url : string) {
        console.log("url set to " + url);
        this.url = url;
    }

    public getUrl() : string {
        return this.url;
    }
}
