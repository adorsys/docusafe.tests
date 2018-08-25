import {TestResultTYPE} from "../types/test.result.type";
import {TestCaseTYPE} from "../types/test.cases.type";

export interface RequestSender {
    receiveRequestResult(statusCode: number, testRequest: TestCaseTYPE, testResult: TestResultTYPE) : void;
    receiveRequestError(statusCode: number, testRequest: TestCaseTYPE, errorMessage: string) : void;
}