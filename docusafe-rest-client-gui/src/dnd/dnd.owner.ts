import {FileContentHolder} from "./file.content.holder";
export interface DndOwner {
    notifyForChanchedFileContent(id: number) : void;
    registerFileContentHolder(id: number, fch:FileContentHolder) : void;
}