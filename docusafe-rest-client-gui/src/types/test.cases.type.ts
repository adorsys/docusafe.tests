import {DocumentInfoTYPE} from "./test.result.type";
export class StaticClientInfoTYPE {
    numberOfThreads : number;
    numberOfRepeats : number;
};

export class DynamicClientInfoTYPE {
    threadNumber: number;
    repetitionNumber: number;
    testID: string; // fuer alle threads und repeats gleich
    requestID: string;
};

export class TestRequestTYPE {
    testAction: string;
    docusafeLayer: string;
    userid: string;
    sizeOfDocument: number;
    documentsPerDirectory: number;
    numberOfDocuments: number;
    fixFilenames: boolean;
    staticClientInfo: StaticClientInfoTYPE;
    dynamicClientInfo: DynamicClientInfoTYPE;
    documentsToRead : DocumentInfoTYPE[];

};

export class TestSuiteTYPE {
    testrequests: TestRequestTYPE[];
};

