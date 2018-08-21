import {Component} from '@angular/core';
import {TestService} from "../service/test.service";
import {TestCasesTYPE} from "../types/test.cases.type";
import {TestCaseOwner} from "./test.case.owner";


@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent implements TestCaseOwner {
    title = 'docusafe-test-client';
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
        { "name":"NO_CACHE", "selected":false},
        { "name":"GUAVA", "selected":false},
        { "name":"HASH_MAP", "selected":true}
    ];
    userID:string = "";
    numberOfDocuments:number = 1;
    sizeOfDocuments:number = 100000;
    documentsPerDirectory:number = 1;

    errormessage: string = "";
    tests: TestCasesTYPE = null;
    me: TestCaseOwner = this;
    currentTestIndex: number = -1;
    numberOfTests: number = 0;

    constructor(private testService: TestService) {
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

    fillCurrentTest() {
        for (var i = 0; i < this.testcases.length; i++) {
            this.testcases[i].selected = (this.testcases[i].name == this.tests.tests[this.currentTestIndex].testcase);
        }
        for (var i = 0; i < this.docusafelayer.length; i++) {
            this.docusafelayer[i].selected = (this.docusafelayer[i].name == this.tests.tests[this.currentTestIndex].docusafeLayer);
        }
        for (var i = 0; i < this.cachetypes.length; i++) {
            this.cachetypes[i].selected = (this.cachetypes[i].name == this.tests.tests[this.currentTestIndex].cacheType);
        }
        this.userID = this.tests.tests[this.currentTestIndex].userid;
        this.numberOfDocuments = this.tests.tests[this.currentTestIndex].numberOfDocuments;
        this.documentsPerDirectory = this.tests.tests[this.currentTestIndex].documentsPerDirectory;
        this.sizeOfDocuments = this.tests.tests[this.currentTestIndex].sizeOfDocument;
    }

    setTestCases(content: TestCasesTYPE): void {
        this.tests = content;
        console.log("received tests:");
        if (this.tests != null) {
            console.log("size is " + this.tests.tests.length);
            this.currentTestIndex = 0;
            this.numberOfTests = this.tests.tests.length;
            this.fillCurrentTest();
        }
        ;
    }

}
