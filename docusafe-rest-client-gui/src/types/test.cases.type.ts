export class TestRequestTYPE {
    testAction: string;
    docusafeLayer: string;
    cacheType: string;
    userid: string;
    sizeOfDocument: number;
    documentsPerDirectory: number;
    numberOfDocuments: number
};

export class TestSuiteTYPE {
    testrequests: TestRequestTYPE[];
};