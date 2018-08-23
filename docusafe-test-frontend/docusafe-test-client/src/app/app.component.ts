import {Component} from '@angular/core';
import {TestService} from "../service/test.service";
import {TestCaseTYPE, TestCasesTYPE} from "../types/test.cases.type";
import {TestCaseOwner} from "./test.case.owner";
import {FileContentHolder} from "../dnd/file.content.holder";
import {TestResultOwner} from "../results/test.result.owner";
import {TestResultTYPE} from "../types/test.result.type";
import {RequestSender} from "./request.sender";

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
    destinationUrls: string[] = [
        "http://docusafeserver-psp-docusafe-performancetest.cloud.adorsys.de",
        "http://localhost:9999"
    ];
    destinationUrl: string = this.destinationUrls[1];
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

    errormessage: string = "";
    tests: TestCasesTYPE = null;
    me: TestCaseOwner = this;
    currentTestIndex: number = -1;
    numberOfTests: number = 0;

    constructor(private testService: TestService) {
        this.setTests(defaultTests);
    }

    notifyForChanchedFileContent(): void {
        console.log("nfcfc 1");
        var filecontent: string = this.fileContentHolder.getMessage();
        console.log("nfcfc 2");
        var testCases: TestCasesTYPE = JSON.parse(filecontent);
        console.log("nfcfc 3");
        this.setTests(testCases);
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

    fromFileToModel() {
        console.log("ff2m 1");
        var filecontent: string = this.fileContentHolder.getMessage();
        console.log("ff2m 2");
        try {
            var testCases: TestCasesTYPE = JSON.parse(filecontent);
            console.log("ff2m 3");
            this.setTests(testCases);
            console.log("ff2m 4");
        } catch (e) {
            this.fileContentHolder.setErrorMessage(e);
        }
    }

    fromModelToFile() {
        console.log("fm2f 1");
        var newfilecontent: string = JSON.stringify(this.tests);
        console.log("fm2f 2");
        this.fileContentHolder.setMessage(newfilecontent);
        console.log("fm2f 3");
        this.currentTestIndex = 0;
        console.log("fm2f 4");
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

    registerResultsHolder(r:TestResultOwner) : void {
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
        var deleteTestCase: TestCaseTYPE = defaultTests.tests[0];
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

    receiveRequestResult(testResult: TestResultTYPE): void {
        this.busy = false;
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
        this.testResultOwner.add(testResult);
    }

    receiveRequestError(errormessage: string): void {
        this.busy = false;
        this.specialTest = false;
        this.errormessage = errormessage;
        console.error("an error occured: " + errormessage);
    }

}
