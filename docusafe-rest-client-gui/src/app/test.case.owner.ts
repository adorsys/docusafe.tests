import { FileContentHolder } from "../dnd/file.content.holder";
import { TestResultOwner } from "../results/test.result.owner";

export interface TestCaseOwner {
    notifyForChanchedFileContent() : void;
    registerFileContentHolder(fch:FileContentHolder) : void;
    registerResultsHolder(r:TestResultOwner) : void;
};