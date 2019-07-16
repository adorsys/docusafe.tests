import {DFSConfigNamesResponseTYPE} from "../types/dfs.config.name.type";

export interface SwitchConfigSender {
    setNames( dfsConfigNames : DFSConfigNamesResponseTYPE) : void;
}
