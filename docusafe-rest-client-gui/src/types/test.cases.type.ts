export class StaticClientInfoTYPE {
    numberOfThreads : number;
    numberOfRepeats : number;
};

export class DynamicClientInfoTYPE {
    threadNumber: number;
    repetitionNumber: number;
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
};

export class TestSuiteTYPE {
    testrequests: TestRequestTYPE[];
};

