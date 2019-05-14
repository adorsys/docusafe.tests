import {Injectable} from "@angular/core"
import {HttpHeaders, HttpClient, HttpErrorResponse} from "@angular/common/http"
import {DFSCredentialsTYPE} from "../types/dfs.credentials.type";
import {ConfigComponent} from "../app/config/config.component";
import {UrlKeeper} from "./url.keeper";

const httpOptions = {
    headers: new HttpHeaders({
        'Content-Type': 'application/json'
    })
};

@Injectable()
export class ConfigService {

    constructor(private httpClient: HttpClient, private urlKeeper: UrlKeeper) {
    }

    getConfig(sender: ConfigComponent): void {
        let url = this.urlKeeper.getUrl() + "/config/dfs";
        console.log("GET dfs credentials FROM " + url);
        this.httpClient.get<DFSCredentialsTYPE>(url, httpOptions).subscribe(
            data => sender.setDFSConfig(data),
            error => alert(error)
        );
        console.log("sent GET done to " + url);
    }

    setConfig(dfsCredentials : DFSCredentialsTYPE): void {
        let url = this.urlKeeper.getUrl() + "/config/dfs";
        console.log("PUT dfs credentials FROM " + url + " " + JSON.stringify(dfsCredentials));
        this.httpClient.put<DFSCredentialsTYPE>(url, dfsCredentials, httpOptions).subscribe(
            data =>,
            error => alert(JSON.stringify(error ) +
                " YOU PROBABLY DID NOT PROVIDE CORRECT ACCESS AND SECRET KEY")

        );
        console.log("sent PUT done to " + url);
    }
}
