import {Input, Component, OnInit} from '@angular/core';
import {TestResultsTYPE, TestResultTYPE} from "../types/test.result.type";
import {TestResultOwner} from "./test.result.owner";
import {TestCaseOwner} from "../app/test.case.owner";
import {TestCaseTYPE} from "../types/test.cases.type";

@Component({
    selector: 'results',
    templateUrl: './results.component.html',
    styleUrls: ['./results.component.css']
})
export class ResultsComponent implements OnInit, TestResultOwner {
    results: TestResultsTYPE = new TestResultsTYPE();
    showTable: boolean = true;
    testcase : TestCaseTYPE = null;
    testcaseString : string = "";
    task: string = "";

    @Input()
    private testCaseOwner: TestCaseOwner;

    constructor() {
        this.results.results = new Array<TestResultTYPE>();
    }

    ngOnInit() {
        this.testCaseOwner.registerResultsHolder(this);
    }

    add(result: TestResultTYPE): void {
        this.results.results.push(result);

    }

    toggleView() : void {
        this.showTable = (this.showTable == true ? false : true);
        console.log("toggle view finished " + this.showTable);
    }

    showTask(i : string) : void {
        this.task = i;
    }

    showTestcase(t:TestResultTYPE) : void {
        if (t == null) {
            this.testcase = null;
            this.testcaseString = "\n";
        } else {
            this.testcase = t.testRequest;
            this.testcaseString = JSON.stringify(t);
        }
    }
}
