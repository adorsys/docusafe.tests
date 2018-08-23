import {Input, Component, OnInit} from '@angular/core';
import {TestResultsTYPE, TestResultTYPE} from "../types/test.result.type";
import {TestResultOwner} from "./test.result.owner";
import {TestCaseOwner} from "../app/test.case.owner";

@Component({
    selector: 'results',
    templateUrl: './results.component.html',
    styleUrls: ['./results.component.css']
})
export class ResultsComponent implements OnInit, TestResultOwner {
    results: TestResultsTYPE = new TestResultsTYPE();

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


}
