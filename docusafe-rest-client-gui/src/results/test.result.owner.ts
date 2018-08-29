import {TestResultAndResponseTYPE} from "../types/test.result.type";
import {TestSuiteTYPE} from "../types/test.cases.type";
export interface TestResultOwner {
    add(response: TestResultAndResponseTYPE) : void;
    remove () : void;
    getAll() : TestSuiteTYPE;
}