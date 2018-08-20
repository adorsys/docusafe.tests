import {Injectable} from "@angular/core"
import {Consts} from "../environments/consts"
import {HttpHeaders, HttpClient} from "@angular/common/http"

const httpOptions = {
    headers: new HttpHeaders({
        'Content-Type':  'application/json'
    })
};

@Injectable()
export class TestService {

    constructor(private httpClient: HttpClient) {
    }

    deleteDBAndCaches(obj: any, finished: (obj: any) => void) : void {
        var url = Consts.INSTANCE.URL_PREFIX + "/deleteDBAndCaches";
        this.httpClient.get(url,httpOptions).subscribe(data => console.log("get done"));
        console.log("sent get to " + url);
    }
}