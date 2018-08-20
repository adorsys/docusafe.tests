import {Injectable} from "@angular/core"
import {Consts} from "../environments/consts"
import {HttpHeaders, HttpClient, HttpErrorResponse} from "@angular/common/http"

const httpOptions = {
    headers: new HttpHeaders({
        'Content-Type':  'application/json'
    })
};

@Injectable()
export class TestService {

    constructor(private httpClient: HttpClient) {
    }

    deleteDBAndCaches(obj: any, finished: (errormessage: string) => void) : void {
        var url = Consts.INSTANCE.URL_PREFIX + "/deleteDBAndCaches";
        this.httpClient.get(url,httpOptions).subscribe(
            data => console.log("get done"),
            error => finished.call(obj, this.getErrorMessage(error))
        );
        console.log("sent get to " + url);
    }

    private getErrorMessage(error: HttpErrorResponse):string {
        if (error.error instanceof ErrorEvent) {
            // A client-side or network error occurred. Handle it accordingly.
            return error.error.message;
        } else {
            // The backend returned an unsuccessful response code.
            // The response body may contain clues as to what went wrong,
            return "Backend returned code " + error.status + " body was: " +error.error;
        }
    }
}