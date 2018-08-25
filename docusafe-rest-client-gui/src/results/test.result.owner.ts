import {TestResultAndResponseTYPE} from "../types/test.result.type";
export interface TestResultOwner {
    add(response: TestResultAndResponseTYPE) : void;
}