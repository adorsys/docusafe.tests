export interface RequestSender {
    receiveRequestResult(TestResultTYPE) : void;
    receiveRequestError(string) : void;
}