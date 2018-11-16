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
import * as fulltestJson from "../testsuites/fulltest.json";
import {SubsumedTestTYPE} from "../types/test.result.type";
import { saveAs } from "file-saver/FileSaver";

import {TestResultAndResponseThreadsMapTYPE} from "../types/test.result.type";
import {DocumentInfoTYPE} from "../types/test.result.type";
import {DndOwner} from "../dnd/dnd.owner";


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
            },
            documentsToRead: []
        }
    ]
};

var fullTestSuite = <TestSuiteTYPE> fulltestJson.default;
// var fullTestSuite = defaultTestSuite;

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent implements TestSuiteOwner, DndOwner, RequestSender {
    title = 'docusafe-test-client';
    dndForTestSuite: FileContentHolder = null;
    dndForTestResults: FileContentHolder = null;
    testResultOwner: TestResultOwner = null;
    busy: boolean = false;
    doContinue: boolean = false;
    doAbort: boolean = false;
    specialTest: boolean = false;
    errormessage: string = "";
    testSuite: TestSuiteTYPE = null;
    me: TestSuiteOwner = this;
    currentTestIndex: number = -1;
    numberOfTests: number = 0;
    numberOfThreadsThatAnswered: number = 0;
    numberOfRepeatsDone: number = 0;
    testID: string = null;
    lastSendTestRequest: TestRequestTYPE = null;
    lastWriteResult: SubsumedTestTYPE = null;

    private imageURL: string = Consts.INSTANCE.ASSETS_URL_PREFIX + "images/";

    destinationUrls: string[] = [
        "http://docusafe-rest-server-psp-docusafe-performancetest.cloud.adorsys.de",
        "http://localhost:9999",
        "http://localhost:9991",
        "http://localhost:9992",
        "http://localhost:9993",
        "http://localhost:9994",
        "http://localhost:9995",
    ];
    destinationUrl: string = this.destinationUrls[0];
    testactions: string[] = [
        "CREATE_DOCUMENTS",
        "READ_DOCUMENTS",
        "DOCUMENT_EXISTS",
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
        this.setTests(fullTestSuite);
    }

    notifyForChanchedFileContent(id: number): void {
        if (id == 1) {
            var filecontent: string = this.dndForTestSuite.getMessage();
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
                this.dndForTestSuite.setErrorMessage(e);
            }
        }
        if (id == 2) {
            var filecontent: string = this.dndForTestResults.getMessage();
            try {
                console.log("parse testresults");
                var subsumendTests : SubsumedTestTYPE[] = JSON.parse(filecontent);
                if (isUndefined(subsumendTests)) {
                    throw "dropped element are not json";
                }
                var length = subsumendTests.length;
                console.log("anzahl der neu geladenen testergebnisse:" + length);
                if (length == 0) {
                    throw "dropped element are not json testactions";
                }
                this.testResultOwner.loadSubsumedTests(subsumendTests);
            } catch (e) {
                this.dndForTestResults.setErrorMessage(e);
            }
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

    registerFileContentHolder(id: number, fch: FileContentHolder): void {
        if (id == 1) {
            this.dndForTestSuite = fch;
        }
        if (id == 2) {
            this.dndForTestResults = fch;
        }
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

    doCurrentTestOnly() : void {
        this.doContinue = false;
        this.doCurrentTest();
    }

    doCurrentTest(): void {

        if (this.testSuite.testrequests[this.currentTestIndex].testAction == this.testactions[1] ||
            this.testSuite.testrequests[this.currentTestIndex].testAction == this.testactions[2]
        ) {
            this.lastWriteResult = this.testResultOwner.getLastWriteResult();
            // Anzahl der threads und repeats müssen vom WriteTest übernommen werden.
            this.testSuite.testrequests[this.currentTestIndex].staticClientInfo = this.lastWriteResult.staticClientInfo;
        }

        this.specialTest = false;
        this.busy = true;
        this.errormessage = "";
        this.numberOfThreadsThatAnswered = 1;
        this.numberOfRepeatsDone = 1;
        this.testID = uuid();
        this.doAbort = false;

        this.startRepeatTest();
    }

    startRepeatTest(): void {
        let currentTest: TestRequestTYPE = JSON.parse(JSON.stringify(this.testSuite.testrequests[this.currentTestIndex]));
        currentTest.dynamicClientInfo.repetitionNumber = this.numberOfRepeatsDone;
        currentTest.dynamicClientInfo.testID = this.testID;
        this.numberOfThreadsThatAnswered = 1;

        for (let i = 1; i <= currentTest.staticClientInfo.numberOfThreads; i++) {
            let request: TestRequestTYPE = JSON.parse(JSON.stringify(currentTest));
            request.dynamicClientInfo.threadNumber = i;
            if (this.testSuite.testrequests[this.currentTestIndex].testAction == this.testactions[1] ||
                this.testSuite.testrequests[this.currentTestIndex].testAction == this.testactions[2]
            ) {
                this.modifyReadRequest(request, this.lastWriteResult);
            }

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

    abort(): void {
        this.doAbort = true;
        this.busy = false;
    }

    removeLastTestFromDone(): void {
        this.testResultOwner.removeLastTest();
    }

    receiveRequestResult(statusCode: number, testRequest: TestRequestTYPE, testResult: TestResultTYPE): void {
        console.log("receiveRequestResult");
        var response: TestResultAndResponseTYPE = new TestResultAndResponseTYPE();
        response.result = testResult;
        response.statusCode = statusCode;
        response.request = testRequest;
        response.date = testResult.date;
        response.testOk = true;
        if (testRequest.testAction == this.testactions[1] ||
            testRequest.testAction == this.testactions[2])
        {
            for (var i = 0; i<testResult.listOfReadDocuments.length; i++) {
                if (testResult.listOfReadDocuments[i].readResult != "OK") {
                    response.testOk = false;
                }
            }
        }
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
        if (this.doAbort) {
            console.log("abort is true. do not continue");
            this.busy = false;
            return;
        }
        this.numberOfThreadsThatAnswered++;
        if (this.numberOfThreadsThatAnswered <= testRequest.staticClientInfo.numberOfThreads) {
            console.log("only " + this.numberOfThreadsThatAnswered + " have ansewred yet. continue to wait");
            return;
        }
        console.log("ALL " + (this.numberOfThreadsThatAnswered-1) + " Threads answered yes");
        this.numberOfRepeatsDone++;
        if (this.numberOfRepeatsDone <= testRequest.staticClientInfo.numberOfRepeats) {
            console.log("erst die " + this.numberOfRepeatsDone + " Wiederholung, test wird wiederholt");
            this.startRepeatTest();
            return;
        }
        console.log("ALL " + (this.numberOfRepeatsDone-1) + " reppetitions done yes");
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

    copyTestSuite(): void {
        this.clipboardService.copy(JSON.stringify(this.testResultOwner.getTestSuite()));
    }

    saveTestSuite(): void {
        var date:string = formatDate(new Date(), 'yyyyMMdd-hhmmSS', 'en');

        const filename = date + "_testsuite.json"
        const blob = new Blob([JSON.stringify(this.testResultOwner.getTestSuite())], { type: 'text/plain' });
        saveAs(blob, filename);
    }

    saveTestResults(): void {
        var date:string = formatDate(new Date(), 'yyyyMMdd-hhmmSS', 'en');

        const filename =date + "_testresults.json"
        const blob = new Blob([JSON.stringify(this.testResultOwner.getSubsumedTests())], { type: 'text/plain' });
        saveAs(blob, filename);
    }

    loadTestResults(): void {
        // this.testResultOwner.loadSubsumedTests(subsumedTests);
    }
    private modifyReadRequest(request: TestRequestTYPE, lastWriteResult: SubsumedTestTYPE) {
        let result : TestResultAndResponseTYPE = lastWriteResult.repeats[request.dynamicClientInfo.repetitionNumber-1].threads[request.dynamicClientInfo.threadNumber-1];
        if (result.error ==  null) {
            request.documentsToRead = result.result.listOfCreatedDocuments;
        }
        request.userid = result.result.userID;
    }
}
