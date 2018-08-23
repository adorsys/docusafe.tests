import {TestCaseTYPE} from "./test.cases.type";


export class TaskTYPE {
    name: string;
    time: number
}

export class TestResultTYPE {
    date: string;
    testRequest: TestCaseTYPE;
    serversExtendedStoreConnection: string;
    totalTime: number;
    tasks: TaskTYPE[];
}

export class TestResultsTYPE {
    results: TestResultTYPE[];
}
