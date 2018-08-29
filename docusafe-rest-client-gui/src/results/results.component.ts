import {Input, Component, OnInit} from '@angular/core';
import {TestResultsTYPE, TestResultTYPE} from "../types/test.result.type";
import {TestResultOwner} from "./test.result.owner";
import {TestSuiteOwner} from "../app/test.case.owner";
import {TestRequestTYPE} from "../types/test.cases.type";
import {TestResultAndResponseTYPE} from "../types/test.result.type";

@Component({
    selector: 'results',
    templateUrl: './results.component.html',
    styleUrls: ['./results.component.css']
})
export class ResultsComponent implements OnInit, TestResultOwner {
    results: TestResultsTYPE = new TestResultsTYPE();
    showTable: boolean = true;
    testcase : TestRequestTYPE = null;
    task: string = "";

    @Input()
    private testCaseOwner: TestSuiteOwner;

    constructor() {
        this.results.results = new Array<TestResultAndResponseTYPE>();
    }

    ngOnInit() {
        this.testCaseOwner.registerResultsHolder(this);
    }

    add(response: TestResultAndResponseTYPE): void {
        this.results.results.push(response);

    }

    toggleView() : void {
        this.showTable = (this.showTable == true ? false : true);
        console.log("toggle view finished " + this.showTable);
    }

    showTask(i : string) : void {
        this.task = i;
    }

    showTestcase(t:TestRequestTYPE) : void {
        if (t == null) {
            this.testcase = null;
        } else {
            this.testcase = t;
        }
    }
}
