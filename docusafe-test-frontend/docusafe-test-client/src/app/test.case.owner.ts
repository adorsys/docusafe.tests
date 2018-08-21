import { TestCasesTYPE } from "../types/test.cases.type";

export interface TestCaseOwner {
    setTestCases(content: TestCasesTYPE) : void;
};