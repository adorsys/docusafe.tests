import {TestResultTYPE} from "../types/test.result.type";
export interface TestResultOwner {
    add(result: TestResultTYPE) : void;
}