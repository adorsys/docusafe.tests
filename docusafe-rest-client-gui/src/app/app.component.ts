import {Component} from '@angular/core';
import {TestService} from "../service/test.service";
import {TestRequestTYPE, TestSuiteTYPE} from "../types/test.cases.type";
import {TestSuiteOwner} from "./test.case.owner";
import {FileContentHolder} from "../dnd/file.content.holder";
import {TestResultOwner} from "../results/test.result.owner";
import {TestResultTYPE} from "../types/test.result.type";
import {RequestSender} from "./request.sender";
import {Consts} from "../environments/consts";
import {TestResultAndResponseTYPE} from "../types/test.result.type";
import {formatDate} from '@angular/common';
import {isUndefined} from "util";
import {ClipboardService} from "../clipboard/clipboard.service";
import {v4 as uuid} from 'uuid';

var defaultTestSuite: TestSuiteTYPE =
{
    "testrequests": [
        {
            "testAction": "CREATE_DOCUMENTS",
            "docusafeLayer": "DOCUSAFE_BASE",
            "cacheType": "GUAVA",
            "userid": "",
            "sizeOfDocument": 50,
            "documentsPerDirectory": 10,
            "numberOfDocuments": 10,
            staticClientInfo: {
                numberOfThreads: 3,
                numberOfRepeats: 2
            },
            dynamicClientInfo: {
                threadNumber: 0,
                repetitionNumber: 0,
                testID: null
            }
        }
    ]
};

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent implements TestSuiteOwner, RequestSender {
    title = 'docusafe-test-client';
    fileContentHolder: FileContentHolder = null;
    testResultOwner: TestResultOwner = null;
    busy: boolean = false;
    doContinue: boolean = false;
    specialTest: boolean = false;
    errormessage: string = "";
    testSuite: TestSuiteTYPE = null;
    testSuiteDone: TestSuiteTYPE = new TestSuiteTYPE();
    me: TestSuiteOwner = this;
    currentTestIndex: number = -1;
    numberOfTests: number = 0;
    numberOfThreadsThatAnswered: number = 0;
    numberOfRepeatsDone: number = 0;
    testID: string = null;
    lastSendTestRequest: TestRequestTYPE = null;

    private imageURL: string = Consts.INSTANCE.ASSETS_URL_PREFIX + "images/";

    destinationUrls: string[] = [
        "http://docusafe-rest-server-psp-docusafe-performancetest.cloud.adorsys.de",
        "http://localhost:9999"
    ];
    destinationUrl: string = this.destinationUrls[0];
    testactions: string[] = [
        "CREATE_DOCUMENTS",
        "READ_DOCUMENTS",
        "DELETE_DATABASE",
        "DELETE_DATABASE_AND_CACHES",
        "DELETE_CACHES"
    ];
    docusafelayer: string[] = [
        "CACHED_TRANSACTIONAL",
        "TRANSACTIONAL",
        "NON_TRANSACTIONAL",
        "DOCUSAFE_BASE"
    ];
    cachetypes: string[] = [
        "NO_CACHE",
        "GUAVA",
        "HASH_MAP"
    ];

    constructor(private testService: TestService, private clipboardService: ClipboardService) {
        this.setTests(defaultTestSuite);
    }

    notifyForChanchedFileContent(): void {
        var filecontent: string = this.fileContentHolder.getMessage();
        try {
            console.log("parse testrequests");
            var testCases: TestSuiteTYPE = JSON.parse(filecontent);
            if (isUndefined(testCases)) {
                throw "dropped element are not json";
            }
            var length = testCases.testrequests.length;
            console.log("anzahl der neu geladenen testfälle:" + length);
            if (length == 0) {
                throw "dropped element are not json testactions";
            }
            this.setTests(testCases);
        } catch (e) {
            this.fileContentHolder.setErrorMessage(e);
        }
    }

    setTests(testSuite: TestSuiteTYPE): void {
        this.testSuite = testSuite;
        console.log("received testrequests:");
        if (this.testSuite != null) {
            console.log("size is " + this.testSuite.testrequests.length);
            this.currentTestIndex = 0;
            this.numberOfTests = this.testSuite.testrequests.length;
        }
    }

    fromModelToFile() {
        var newfilecontent: string = JSON.stringify(this.testSuite);
        this.fileContentHolder.setMessage(newfilecontent);
        this.currentTestIndex = 0;
    }

    registerFileContentHolder(fch: FileContentHolder): void {
        this.fileContentHolder = fch;
        this.fromModelToFile();
    }

    registerResultsHolder(r: TestResultOwner): void {
        this.testResultOwner = r;
    }


    previousTestcase(): void {
        if (this.currentTestIndex > 0) {
            this.currentTestIndex--;
        }
    }

    nextTestcase(): void {
        if (this.currentTestIndex < this.testSuite.testrequests.length - 1) {
            this.currentTestIndex++;
        }
    }

    doCleanDatabase(): void {
        this.busy = true;
        this.specialTest = true;
        this.errormessage = "";
        this.numberOfRepeatsDone = 1;
        this.numberOfThreadsThatAnswered = 1;
        // var deleteTestCase: TestRequestTYPE  = new TestRequestTYPE();
        var deleteTestCase: TestRequestTYPE = JSON.parse(JSON.stringify(defaultTestSuite.testrequests[0]));
        deleteTestCase.testAction = "DELETE_DATABASE_AND_CACHES";
        deleteTestCase.dynamicClientInfo.testID = uuid();
        deleteTestCase.dynamicClientInfo.threadNumber = 1;
        deleteTestCase.dynamicClientInfo.repetitionNumber = 1;
        deleteTestCase.staticClientInfo.numberOfRepeats = 1;
        deleteTestCase.staticClientInfo.numberOfThreads = 1;
        this.lastSendTestRequest = deleteTestCase;
        this.testService.test(this.destinationUrl, deleteTestCase, this);
    }

    doCurrentTest(): void {


        this.specialTest = false;
        this.busy = true;
        this.errormessage = "";
        this.numberOfThreadsThatAnswered = 1;
        this.numberOfRepeatsDone = 1;
        this.testID = uuid();

        this.startRepeatTest();
    }

    startRepeatTest(): void {
        let currentTest: TestRequestTYPE = JSON.parse(JSON.stringify(this.testSuite.testrequests[this.currentTestIndex]));
        currentTest.dynamicClientInfo.repetitionNumber = this.numberOfRepeatsDone;
        currentTest.dynamicClientInfo.testID = this.testID;

        for (let i = 1; i <= currentTest.staticClientInfo.numberOfThreads; i++) {
            let request: TestRequestTYPE = JSON.parse(JSON.stringify(currentTest));
            request.dynamicClientInfo.threadNumber = i;
            this.lastSendTestRequest = request;
            this.testService.test(this.destinationUrl, request, this);
        }
    }

    doRemainingTests(): void {
        this.doContinue = true;
        this.doCurrentTest();
    }

    doAllTests(): void {
        this.currentTestIndex = 0;
        this.doRemainingTests();
    }

    removeLastTestFromDone(): void {
        this.testResultOwner.remove();
    }

    receiveRequestResult(statusCode: number, testRequest: TestRequestTYPE, testResult: TestResultTYPE): void {
        console.log("receiveRequestResult");
        var response: TestResultAndResponseTYPE = new TestResultAndResponseTYPE();
        response.result = testResult;
        response.statusCode = statusCode;
        response.request = testRequest;
        response.date = testResult.date;
        response.testOk = true;
        this.continueTesting(response, testRequest);
    }

    receiveRequestError(statusCode: number, testRequest: TestRequestTYPE, errorMessage: string): void {
        console.log("receiveRequestError");
        var response: TestResultAndResponseTYPE = new TestResultAndResponseTYPE();
        response.error = errorMessage;
        response.statusCode = statusCode;
        response.request = testRequest;
        response.date = formatDate(new Date(), 'yyyy-MM-dd-hh:mm:SS', 'en');
        response.testOk = false;
        this.continueTesting(response, testRequest);
    }

    continueTesting(response: TestResultAndResponseTYPE, testRequest: TestRequestTYPE): void {
        this.testResultOwner.add(response);
        this.numberOfThreadsThatAnswered++;
        if (this.numberOfThreadsThatAnswered <= testRequest.staticClientInfo.numberOfThreads) {
            console.log("only " + this.numberOfThreadsThatAnswered + " have ansewred yet. continue to wait");
            return;
        }
        this.numberOfRepeatsDone++;
        if (this.numberOfRepeatsDone <= testRequest.staticClientInfo.numberOfRepeats) {
            console.log("erst die " + this.numberOfRepeatsDone + " Wiederholung, test wird wiederholt");
            this.startRepeatTest();
            return;
        }
        this.busy = false;
        console.log("einzelner test beendt");

        if (this.specialTest == true) {
            console.log("TEST FINISHED");
            return;
        }

        if (this.doContinue) {
            this.currentTestIndex++;
            if (this.currentTestIndex == this.testSuite.testrequests.length) {
                this.currentTestIndex--;
                this.doContinue = false;
                console.log("alle tests beendt");
            } else {
                console.log("CONTINUE TESTING");
                this.doRemainingTests();
            }
        }
    }

    copy(): void {
        this.clipboardService.copy(JSON.stringify(this.testResultOwner.getAll()));
    }
}
