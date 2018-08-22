export interface RequestSender {
    setRequestResult(TestResultTYPE) : void;
    setRequestError(string) : void;
}