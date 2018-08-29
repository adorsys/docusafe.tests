import {TestRequestTYPE} from "./test.cases.type";

export class TaskTYPE {
    name: string;
    time: number
}

export class TestResultTYPE {
    date: string;
    extendedStoreConnection: string;
    totalTime: number;
    tasks: TaskTYPE[];
}

export class TestResultAndResponseTYPE {
    date: string;
    request: TestRequestTYPE;
    result: TestResultTYPE;
    statusCode: number;
    error: string;
}

export class TestResultsTYPE {
    results: TestResultAndResponseTYPE[];
}
