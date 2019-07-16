import {Injectable} from "@angular/core"
import {HttpClient, HttpHeaders} from "@angular/common/http"
import {UrlKeeper} from "./url.keeper";
import {DFSConfigNameRequestTYPE, DFSConfigNamesResponseTYPE} from "../types/dfs.config.name.type";
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
        this.httpClient.get<DFSConfigNamesResponseTYPE>(url, httpOptions).subscribe(
            data => sender.setNames(data),
            error => alert(error)
        );
        console.log("sent GET done to " + url);
    }

    setName(name : string): void {
        let url = this.urlKeeper.getUrl() + "/switch/dfs";
        let request = new DFSConfigNameRequestTYPE();
        request.name = name;
        console.log("PUT DFS NAME TO " + url + " " + JSON.stringify(request));
        this.httpClient.put<DFSConfigNameRequestTYPE>(url, request, httpOptions).subscribe(
            data => this.doNothing(),
            error => alert("ERROR " + JSON.stringify(error ) +
                " can not set DFS Name in server ")

        );
        console.log("sent PUT done to " + url);
    }

    doNothing() {

    }
}
