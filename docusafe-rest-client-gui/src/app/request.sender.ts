import {TestResultTYPE} from "../types/test.result.type";
import {TestRequestTYPE} from "../types/test.cases.type";

export interface RequestSender {
    receiveRequestResult(statusCode: number, testRequest: TestRequestTYPE, testResult: TestResultTYPE) : void;
    receiveRequestError(statusCode: number, testRequest: TestRequestTYPE, errorMessage: string) : void;
}