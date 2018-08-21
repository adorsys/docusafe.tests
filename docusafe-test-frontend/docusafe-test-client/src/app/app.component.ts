import {Component} from '@angular/core';
import {TestService} from "../service/test.service";
import {TestCasesTYPE} from "../types/test.cases.type";
import {TestCaseOwner} from "./test.case.owner";
import {default as tests_json} from "./tests.json";


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
        this.setTestCases(tests_json);
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
}
