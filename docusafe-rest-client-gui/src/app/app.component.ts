import {Component} from '@angular/core';
import {TestService} from "../service/test.service";
import {TestCaseTYPE, TestCasesTYPE} from "../types/test.cases.type";
import {TestCaseOwner} from "./test.case.owner";
import {FileContentHolder} from "../dnd/file.content.holder";
import {TestResultOwner} from "../results/test.result.owner";
import {TestResultTYPE} from "../types/test.result.type";
import {RequestSender} from "./request.sender";
import {Consts} from "../environments/consts";
import {TestResultAndResponseTYPE} from "../types/test.result.type";
import {formatDate} from '@angular/common';
import {isUndefined} from "util";

var defaultTests: TestCasesTYPE =
{
    "tests": [
        {
            "testcase": "CREATE_DOCUMENTS",
            "docusafeLayer": "DOCUSAFE_BASE",
            "cacheType": "NO_CACHE",
            "userid": "dummy01",
            "sizeOfDocument": 50,
            "documentsPerDirectory": 1,
            "numberOfDocuments": 1
        }
    ]
};

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent implements TestCaseOwner, RequestSender {
    title = 'docusafe-test-client';
    fileContentHolder: FileContentHolder = null;
    testResultOwner: TestResultOwner = null;
    busy: boolean = false;
    doContinue: boolean = false;
    specialTest: boolean = false;
    errormessage: string = "";
    tests: TestCasesTYPE = null;
    testsDone: TestCasesTYPE = new TestCasesTYPE();
    me: TestCaseOwner = this;
    currentTestIndex: number = -1;
    numberOfTests: number = 0;


    private imageURL: string = Consts.INSTANCE.ASSETS_URL_PREFIX + "images/";

    destinationUrls: string[] = [
        "http://docusafe-rest-server-psp-docusafe-performancetest.cloud.adorsys.de",
        "http://localhost:9999"
    ];
    destinationUrl: string = this.destinationUrls[0];
    testcases: string[] = [
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

    constructor(private testService: TestService) {
        this.setTests(defaultTests);
        this.testsDone.tests = new Array<TestCaseTYPE>();
    }

    notifyForChanchedFileContent(): void {
        var filecontent: string = this.fileContentHolder.getMessage();
        try {
            console.log("parse tests");
            var testCases: TestCasesTYPE = JSON.parse(filecontent);
            if (isUndefined(testCases)) {
                throw "dropped element are not json";
            }
            var length = testCases.tests.length;
            console.log("anzahl der neu geladenen testfälle:" + length);
            if (length == 0) {
                throw "dropped element are not json testcases";
            }
            this.setTests(testCases);
        } catch (e) {
            this.fileContentHolder.setErrorMessage(e);
        }
    }

    setTests(testCases: TestCasesTYPE): void {
        this.tests = testCases;
        console.log("received tests:");
        if (this.tests != null) {
            console.log("size is " + this.tests.tests.length);
            this.currentTestIndex = 0;
            this.numberOfTests = this.tests.tests.length;
        }
    }

    show() {
        console.log(this.tests.tests[this.currentTestIndex]);
        console.log(this.destinationUrl);
    }

    fromModelToFile() {
        var newfilecontent: string = JSON.stringify(this.tests);
        this.fileContentHolder.setMessage(newfilecontent);
        this.currentTestIndex = 0;
    }

    appendToFile() {
        console.log("am2f 1");
        var filecontent: string = this.fileContentHolder.getMessage();
        console.log("am2f 2");
        var fileTestCases: TestCasesTYPE = JSON.parse(filecontent);
        console.log("am2f 3");
        fileTestCases.tests.push(this.tests.tests[this.currentTestIndex]);
        console.log("am2f 4");
        this.tests = fileTestCases;
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
        if (this.currentTestIndex < this.tests.tests.length - 1) {
            this.currentTestIndex++;
        }
    }

    doCleanDatabase(): void {
        this.busy = true;
        this.specialTest = true;
        this.errormessage = "";
        // var deleteTestCase: TestCaseTYPE  = new TestCaseTYPE();
        var deleteTestCase: TestCaseTYPE = Object.assign({}, defaultTests.tests[0]);
        deleteTestCase.testcase = "DELETE_DATABASE_AND_CACHES";
        this.testService.test(this.destinationUrl, deleteTestCase, this);
    }

    doCurrentTest(): void {
        this.busy = true;
        this.errormessage = "";
        this.testService.test(this.destinationUrl, this.tests.tests[this.currentTestIndex], this);
    }

    doRemainingTests(): void {
        this.doContinue = true;
        this.doCurrentTest();
    }

    doAllTests(): void {
        this.currentTestIndex = 0;
        this.doRemainingTests();
    }

    receiveRequestResult(statusCode: number, testRequest: TestCaseTYPE, testResult: TestResultTYPE): void {
        this.busy = false;
        this.testsDone.tests.push(testRequest);
        if (this.specialTest == true) {
            // do not increment counter
        } else {
            console.log("test erfolgreich");
            this.currentTestIndex++;
            if (this.currentTestIndex == this.tests.tests.length) {
                this.currentTestIndex--;
            } else {
                if (this.doContinue) {
                    this.doRemainingTests();
                }
            }
        }
        this.specialTest = false;
        var response: TestResultAndResponseTYPE = new TestResultAndResponseTYPE();
        response.result = testResult;
        response.statusCode = statusCode;
        response.request = testRequest;
        response.date = testResult.date;
        this.testResultOwner.add(response);
    }

    removeLastTestFromDone() : void {
        this.testsDone.tests.pop();
    }

    receiveRequestError(statusCode: number, testRequest: TestCaseTYPE, errorMessage: string): void {
        this.busy = false;
        this.specialTest = false;
        this.errormessage = errorMessage;
        this.testsDone.tests.push(testRequest);
        this.doContinue = false;
        var response: TestResultAndResponseTYPE = new TestResultAndResponseTYPE();
        response.error = errorMessage;
        response.statusCode = statusCode;
        response.request = testRequest;
        response.date = formatDate(new Date(), 'yyyy-MM-dd-hh:mm:SS', 'en');
        console.error("an error occured: " + errorMessage);
        this.testResultOwner.add(response);
    }


}
