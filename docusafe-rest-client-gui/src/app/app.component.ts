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
                numberOfThreads: 1,
                numberOfRepeats: 1
            },
            dynamicClientInfo: {
                threadNumber: 0,
                repetitionNumber: 0
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
    numberOfThreadsThatAnswered : number = 0;
    numberOfRepeatsDone : number = 0;

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
        this.testSuiteDone.testrequests = new Array<TestRequestTYPE>();
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

    show() {
        console.log(this.testSuite.testrequests[this.currentTestIndex]);
        console.log(this.destinationUrl);
    }

    fromModelToFile() {
        var newfilecontent: string = JSON.stringify(this.testSuite);
        this.fileContentHolder.setMessage(newfilecontent);
        this.currentTestIndex = 0;
    }

    appendToFile() {
        console.log("am2f 1");
        var filecontent: string = this.fileContentHolder.getMessage();
        console.log("am2f 2");
        var fileTestCases: TestSuiteTYPE = JSON.parse(filecontent);
        console.log("am2f 3");
        fileTestCases.testrequests.push(this.testSuite.testrequests[this.currentTestIndex]);
        console.log("am2f 4");
        this.testSuite = fileTestCases;
        console.log("am2f 5");
        this.fromModelToFile();
        console.log("am2f 6");
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
        this.testService.test(this.destinationUrl, deleteTestCase, this);
    }

    doCurrentTest(): void {


        this.busy = true;
        this.errormessage = "";
        this.numberOfThreadsThatAnswered = 1;
        this.numberOfRepeatsDone = 1;


        this.startRepeatTest();
    }

    startRepeatTest() : void {
        let currentTest:TestRequestTYPE = JSON.parse(JSON.stringify(this.testSuite.testrequests[this.currentTestIndex]));
        currentTest.dynamicClientInfo.repetitionNumber = this.numberOfRepeatsDone;

        for (let i = 1; i<=currentTest.staticClientInfo.numberOfThreads; i++) {
            let request : TestRequestTYPE = JSON.parse(JSON.stringify(currentTest));
            request.dynamicClientInfo.threadNumber = i;
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
        console.log("continue testsing");
        this.testSuiteDone.testrequests.push(testRequest);
        this.busy = false;
        if (this.specialTest == true) {
            // do not increment counter
        } else {
            console.log("test erfolgreich");
            this.currentTestIndex++;
            if (this.currentTestIndex == this.testSuite.testrequests.length) {
                this.currentTestIndex--;
            } else {
                if (this.doContinue) {
                    this.doRemainingTests();
                }
            }
        }
        this.specialTest = false;
    }

    receiveRequestError(statusCode: number, testRequest: TestRequestTYPE, errorMessage: string): void {
        console.log("receiveRequestError");
        var response: TestResultAndResponseTYPE = new TestResultAndResponseTYPE();
        response.error = errorMessage;
        response.statusCode = statusCode;
        response.request = testRequest;
        response.date = formatDate(new Date(), 'yyyy-MM-dd-hh:mm:SS', 'en');
        console.error("an error occured: " + errorMessage);
        this.testResultOwner.add(response);
        this.numberOfThreadsThatAnswered++;
        if (this.numberOfThreadsThatAnswered < testRequest.staticClientInfo.numberOfThreads) {
            console.log("error only " + this.numberOfThreadsThatAnswered + " have ansewred yet. continue to wait");
            return;
        }
        this.numberOfRepeatsDone++;
        if (this.numberOfRepeatsDone < testRequest.staticClientInfo.numberOfRepeats) {
            console.log("error erst die " + this.numberOfRepeatsDone + " Wiederholung, test wird wiederholt");
            this.startRepeatTest();
            return;
        }
        console.log("continue testsing");
        this.busy = false;
        this.specialTest = false;
        this.errormessage = errorMessage;
        this.doContinue = false;
        this.testSuiteDone.testrequests.push(testRequest);
    }

    copy(): void {
        this.clipboardService.copy(JSON.stringify(this.testResultOwner.getAll()));
    }
}
