import {DocumentInfoTYPE} from "./test.result.type";
export class StaticClientInfoTYPE {
    numberOfThreads : number;
    numberOfRepeats : number;
};

export class DynamicClientInfoTYPE {
    threadNumber: number;
    repetitionNumber: number;
    testID: string; // fuer alle threads und repeats gleich
};

export class TestRequestTYPE {
    testAction: string;
    docusafeLayer: string;
    cacheType: string;
    userid: string;
    sizeOfDocument: number;
    documentsPerDirectory: number;
    numberOfDocuments: number;
    staticClientInfo: StaticClientInfoTYPE;
    dynamicClientInfo: DynamicClientInfoTYPE;
    documentsToRead : DocumentInfoTYPE[];

};

export class TestSuiteTYPE {
    testrequests: TestRequestTYPE[];
};

