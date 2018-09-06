import {TestRequestTYPE} from "./test.cases.type";
import {StaticClientInfoTYPE} from "./test.cases.type";

export class TaskTYPE {
    name: string;
    time: number
}

export class DocumentInfoTYPE {
    documentFQN : string;
    uniqueToken : string;
    size : number;
}

export class TestResultTYPE {
    date: string; // date im Server erzeugt bei beginn des Tests
    extendedStoreConnection: string;
    totalTime: number;
    tasks: TaskTYPE[];
    userID: string;
    listOfCreatedDocuments: DocumentInfoTYPE[];

}

export class TestResultAndResponseTYPE {
    date: string; // date im client erzeugt bei Ende des Tests
    request: TestRequestTYPE;
    result: TestResultTYPE;
    statusCode: number;
    error: string;
    testOk: boolean;
}

export class TestResultsTYPE {
    results: TestResultAndResponseTYPE[];
}

export class TestResultAndResponseThreadsMapTYPE {
    threads: TestResultAndResponseTYPE[];
    averageTime: number = 1;
    maxTime: number = 2;
    minTime: number = 3;
    testOk: boolean;
}

export class SubsumedTestTYPE {
    repeats: TestResultAndResponseThreadsMapTYPE[];
    averageTime: number = 4;
    maxTime: number = 5;
    minTime: number = 6;
    lastReceivedDate: string;
    staticClientInfo: StaticClientInfoTYPE = new StaticClientInfoTYPE();
    testAction: string;
    cacheType: string;
    layer:string;
    testOk: boolean;
}

export class ViewForTests {
    testMap: Map<string, SubsumedTestTYPE> = new Map();
    subsumedTests: SubsumedTestTYPE[] = new Array();

}