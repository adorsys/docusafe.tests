import {Injectable} from "@angular/core"
import {HttpClient, HttpHeaders} from "@angular/common/http"
import {UrlKeeper} from "./url.keeper";
import {DFSConfigNameTYPE} from "../types/dfs.config.name.type";
import {SwitchConfigSender} from "../app/switch.config.sender";

const httpOptions = {
    headers: new HttpHeaders({
        'Content-Type': 'application/json'
    })
};

@Injectable()
export class DfsSwitchService {

    constructor(private httpClient: HttpClient, private urlKeeper: UrlKeeper) {
    }

    getNames(sender: SwitchConfigSender): void {
        let url = this.urlKeeper.getUrl() + "/switch/dfs";
        console.log("GET DFS NAMES FROM " + url);
        this.httpClient.get<DFSConfigNameTYPE>(url, httpOptions).subscribe(
            data => sender.setNames(data),
            error => alert(error)
        );
        console.log("sent GET done to " + url);
    }

    setName(name : String): void {
        let url = this.urlKeeper.getUrl() + "/switch/dfs";
        console.log("PUT DFS NAME TO " + url + " " + JSON.stringify(name));
        this.httpClient.put<String>(url, name, httpOptions).subscribe(
            data => this.doNothing(),
            error => alert(JSON.stringify(error ) +
                " can not set DFS Name in server ")

        );
        console.log("sent PUT done to " + url);
    }

    doNothing() {

    }
}
