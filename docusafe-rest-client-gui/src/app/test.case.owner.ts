import { FileContentHolder } from "../dnd/file.content.holder";
import { TestResultOwner } from "../results/test.result.owner";

export interface TestSuiteOwner {
    notifyForChanchedFileContent() : void;
    registerFileContentHolder(fch:FileContentHolder) : void;
    registerResultsHolder(r:TestResultOwner) : void;
};