import {Injectable} from "@angular/core"
import {HttpHeaders, HttpClient, HttpErrorResponse} from "@angular/common/http"
import {RequestSender} from "../app/request.sender";
import {TestRequestTYPE} from "../types/test.cases.type";
import {TestResultTYPE} from "../types/test.result.type";
import {UrlKeeper} from "./url.keeper";

const httpOptions = {
    headers: new HttpHeaders({
        'Content-Type': 'application/json'
    })
};

@Injectable()
export class TestService {

    constructor(private httpClient: HttpClient, private urlKeeper: UrlKeeper) {
    }

    test(testRequeset: TestRequestTYPE, requestSender: RequestSender): void {
        var url = this.urlKeeper.getUrl() + "/test";
        console.log("PUT " + JSON.stringify(testRequeset) + " TO " + url);
        this.httpClient.put<TestResultTYPE>(url, testRequeset, httpOptions).subscribe(
            data => requestSender.receiveRequestResult(1, testRequeset, data),
            error => requestSender.receiveRequestError(0, testRequeset, this.getErrorMessage(error))
        );
        console.log("sent PUT done to " + url);
    }

    private getErrorMessage(error: HttpErrorResponse): string {
        if (error.error != null && error.error.message != null) {
            return "Backend returned code " + error.status + " message was: " + error.error.message;
        }
        console.log("ERROR of SERVER can not be grabbed from result:" + JSON.stringify(error));
        // The backend returned an unsuccessful response code.
        // The response body may contain clues as to what went wrong,
        return "Backend returned code " + error.status;
    }
}
