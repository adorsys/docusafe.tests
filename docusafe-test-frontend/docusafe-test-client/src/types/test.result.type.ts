import {TestCaseTYPE} from "./test.cases.type";

export class TestResultTYPE {
    testRequest: TestCaseTYPE;
    serversExtendedStoreConnection: string;
    totalTime: number;
    tasks: TaskTYPE[];
}

export class TaskTYPE {
    name: string;
    time: number
}
