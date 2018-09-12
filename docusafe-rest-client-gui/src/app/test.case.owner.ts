import { FileContentHolder } from "../dnd/file.content.holder";
import { TestResultOwner } from "../results/test.result.owner";

export interface TestSuiteOwner {
    registerResultsHolder(r:TestResultOwner) : void;
};