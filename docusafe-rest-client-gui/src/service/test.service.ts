import {Injectable} from "@angular/core"
import {HttpHeaders, HttpClient, HttpErrorResponse} from "@angular/common/http"
import {RequestSender} from "../app/request.sender";
import {TestRequestTYPE} from "../types/test.cases.type";
import {TestResultTYPE} from "../types/test.result.type";

const httpOptions = {
    headers: new HttpHeaders({
        'Content-Type': 'application/json'
    })
};

@Injectable()
export class TestService {

    constructor(private httpClient: HttpClient) {
    }

    test(urlPrefix: string, testRequeset: TestRequestTYPE, requestSender: RequestSender): void {
        var url = urlPrefix + "/test";
        if (testRequeset.docusafeLayer.indexOf("MY_CACHED_TRANSACTIONAL") == 0) {
            url = urlPrefix + "/testtx";
        }
        console.log("PUT " + JSON.stringify(testRequeset) + " TO " + url);
        this.httpClient.put<TestResultTYPE>(url, testRequeset, httpOptions).subscribe(
            data => requestSender.receiveRequestResult(1, testRequeset, data),
            error => requestSender.receiveRequestError(0, testRequeset, this.getErrorMessage(error))
        );
        console.log("sent PUT done to " + url);
    }

    private getErrorMessage(error: HttpErrorResponse): string {
        if (error.error instanceof ErrorEvent) {
            // A client-side or network error occurred. Handle it accordingly.
            return error.error.message;
        } else {
            // The backend returned an unsuccessful response code.
            // The response body may contain clues as to what went wrong,
            return "Backend returned code " + error.status + " message was: " + error.message;
        }
    }
}