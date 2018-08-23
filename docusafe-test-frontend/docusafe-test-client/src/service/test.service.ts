import {Injectable} from "@angular/core"
import {HttpHeaders, HttpClient, HttpErrorResponse} from "@angular/common/http"
import {RequestSender} from "../app/request.sender";
import {TestCaseTYPE} from "../types/test.cases.type";

const httpOptions = {
    headers: new HttpHeaders({
        'Content-Type': 'application/json'
    })
};

@Injectable()
export class TestService {

    constructor(private httpClient: HttpClient) {
    }

    test(urlPrefix: string, testCase: TestCaseTYPE, requestSender: RequestSender): void {
        var url = urlPrefix + "/test";
        console.log("PUT " + testCase + " TO " + url);
        this.httpClient.put(url, testCase, httpOptions).
        subscribe(
            data => requestSender.setRequestResult(data),
            error => requestSender.setRequestError(this.getErrorMessage(error))
        );
        console.log("sent get to " + url);
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