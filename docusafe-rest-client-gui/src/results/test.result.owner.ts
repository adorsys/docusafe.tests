import {TestResultAndResponseTYPE} from "../types/test.result.type";
import {TestSuiteTYPE} from "../types/test.cases.type";
import {SubsumedTestTYPE} from "../types/test.result.type";
export interface TestResultOwner {
    add(response: TestResultAndResponseTYPE) : void;
    removeLastTest () : void;
    getTestSuite() : TestSuiteTYPE;
    getSubsumedTests() : SubsumedTestTYPE[];
    loadSubsumedTests(subsumedTests : SubsumedTestTYPE[]) : void;
    getLastWriteResult() : SubsumedTestTYPE;
}