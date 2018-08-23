import { TestCasesTYPE } from "../types/test.cases.type";
import { FileContentHolder } from "../dnd/file.content.holder";

export interface TestCaseOwner {
    notifyForChanchedFileContent() : void;
    registerFileContentHolder(fch:FileContentHolder) : void;
};