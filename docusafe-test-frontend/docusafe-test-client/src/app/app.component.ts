import {Component} from '@angular/core';
import {TestService} from "../service/test.service";
import {TestCaseTYPE, TestCasesTYPE} from "../types/test.cases.type";
import {TestCaseOwner} from "./test.case.owner";
import {FileContentHolder} from "../dnd/file.content.holder";
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
    busy: boolean = false;
    doContinue: boolean = false;
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
        this.setTestCases(defaultTests);
    }

    setTestCases(content: TestCasesTYPE): void {
        this.tests = content;
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
        var filecontent: string = this.fileContentHolder.getMessage();
        var testCases: TestCasesTYPE = JSON.parse(filecontent);
        this.setTestCases(testCases);
    }

    fromModelToFile() {
        var newfilecontent: string = JSON.stringify(this.tests);
        this.fileContentHolder.setMessage(newfilecontent);
        this.currentTestIndex = 0;
    }

    appendToFile() {
        var filecontent: string = this.fileContentHolder.getMessage();
        var fileTestCases: TestCasesTYPE = JSON.parse(filecontent);
        fileTestCases.tests.push(this.tests.tests[this.currentTestIndex]);
        this.tests = fileTestCases;
        this.fromModelToFile();
    }

    registerFileContentHolder(fch: FileContentHolder): void {
        this.fileContentHolder = fch;
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

    setRequestResult(TestResultTYPE): void {
        this.busy = false;
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

    setRequestError(errormessage: string): void {
        this.busy = false;
        this.errormessage = errormessage;
        console.error("an error occured: " + errormessage);
    }

}
