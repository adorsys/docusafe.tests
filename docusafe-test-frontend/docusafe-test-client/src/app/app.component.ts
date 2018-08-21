import {Component} from '@angular/core';
import {TestService} from "../service/test.service";
import {TestCaseTYPE, TestCasesTYPE} from "../types/test.cases.type";
import {TestCaseOwner} from "./test.case.owner";
import {FileContentHolder} from "../dnd/file.content.holder";

var defaultTests:TestCasesTYPE =
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
export class AppComponent implements TestCaseOwner {
    title = 'docusafe-test-client';
    fileContentHolder : FileContentHolder = null;
    testcases: any[] = [
        {"name": "CREATE_DOCUMENTS", "selected": false},
        {"name": "READ_DOCUMENTS", "selected": true}
    ];
    docusafelayer: any[] = [
        {"name": "CACHED_TRANSACTIONAL", "selected": false},
        {"name": "TRANSACTIONAL", "selected": false},
        {"name": "NON_TRANSACTIONAL", "selected": false},
        {"name": "DOCUSAFE_BASE", "selected": true}
    ];
    cachetypes: any[] = [
        {"name": "NO_CACHE", "selected": false},
        {"name": "GUAVA", "selected": false},
        {"name": "HASH_MAP", "selected": true}
    ];

    errormessage: string = "";
    tests: TestCasesTYPE = null;
    me: TestCaseOwner = this;
    currentTestIndex: number = -1;
    numberOfTests: number = 0;

    constructor(private testService: TestService) {
        this.setTestCases(defaultTests);
    }

    requestError(errormessage: string): void {
        this.errormessage = errormessage;
        console.error("an error occured: " + errormessage);
    }

    deleteDBAndCaches(): void {
        console.log("button pressed deleteDBAndCaches");
        this.errormessage = "";
        this.testService.deleteDBAndCaches(this, this.requestError);
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

    registerFileContentHolder(fch:FileContentHolder) : void {
        this.fileContentHolder = fch;
    }

}
