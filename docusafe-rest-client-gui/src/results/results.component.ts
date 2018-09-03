import {Input, Component, OnInit} from '@angular/core';
import {TestResultsTYPE, TestResultTYPE} from "../types/test.result.type";
import {TestResultOwner} from "./test.result.owner";
import {TestSuiteOwner} from "../app/test.case.owner";
import {TestRequestTYPE} from "../types/test.cases.type";
import {TestResultAndResponseTYPE} from "../types/test.result.type";
import {TestSuiteTYPE} from "../types/test.cases.type";
import {ViewForTests} from "../types/test.result.type";
import {SubsumedTestTYPE} from "../types/test.result.type";
import {TestResultAndResponseThreadsMapTYPE} from "../types/test.result.type";

@Component({
    selector: 'results',
    templateUrl: './results.component.html',
    styleUrls: ['./results.component.css']
})
export class ResultsComponent implements OnInit, TestResultOwner {
    results: TestResultsTYPE = new TestResultsTYPE();
    viewForTests: ViewForTests = new ViewForTests();
    showTable: boolean = true;
    testresult: TestResultTYPE = null;
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

        let subsumedTest = this.viewForTests.testMap[response.request.dynamicClientInfo.testID];
        if (subsumedTest == null) {
            console.log("create new testresult for " + response.request.dynamicClientInfo.testID);
            subsumedTest = new SubsumedTestTYPE();
            subsumedTest.staticClientInfo = response.request.staticClientInfo;
            subsumedTest.testAction = response.request.testAction;
            subsumedTest.repeats = new Array<TestResultAndResponseThreadsMapTYPE>(subsumedTest.staticClientInfo.numberOfRepeats);
            for (var i = 0; i < subsumedTest.repeats.length; i++) {
                subsumedTest.repeats[i] = null;
            }

            this.viewForTests.testMap[response.request.dynamicClientInfo.testID] = subsumedTest;
            this.viewForTests.subsumedTests.push(subsumedTest);
            subsumedTest.testOk = true;
        }
        console.log("found subsumed test for " + response.request.dynamicClientInfo.testID);

        subsumedTest.lastReceivedDate = response.date;

        let testResultAndResponseThreadsMap: TestResultAndResponseThreadsMapTYPE = subsumedTest.repeats[(response.request.dynamicClientInfo.repetitionNumber - 1)];
        if (testResultAndResponseThreadsMap == null) {
            console.log("create repeat for " + response.request.dynamicClientInfo.repetitionNumber);
            testResultAndResponseThreadsMap = new TestResultAndResponseThreadsMapTYPE();
            testResultAndResponseThreadsMap.threads = new Array<TestResultAndResponseTYPE>(subsumedTest.staticClientInfo.numberOfThreads);
            for (var i = 0; i < testResultAndResponseThreadsMap.threads.length; i++) {
                testResultAndResponseThreadsMap.threads[i] = null;
            }
            subsumedTest.repeats[(response.request.dynamicClientInfo.repetitionNumber - 1)] = testResultAndResponseThreadsMap;
        }
        console.log("found repeat for " + response.request.dynamicClientInfo.repetitionNumber);
        testResultAndResponseThreadsMap.threads[(response.request.dynamicClientInfo.threadNumber - 1)] = response;

        {
            // condense all threads

            let numberOfThreads: number = 0;
            let totalTime: number = 0;
            let minTime: number = -1;
            let maxTime: number = 0;
            let testOk: boolean = true;
            for (var i = 0; i < testResultAndResponseThreadsMap.threads.length; i++) {
                if (testResultAndResponseThreadsMap.threads[i] != null) {
                    if (testResultAndResponseThreadsMap.threads[i].testOk) {
                        let time: number = testResultAndResponseThreadsMap.threads[i].result.totalTime;
                        totalTime += time;
                        numberOfThreads++;
                        if (time > maxTime) {
                            maxTime = time;
                        }
                        if (minTime > time) {
                            minTime = time;
                        }
                        if (minTime == -1) {
                            minTime = time;
                        }
                    } else {
                        testOk = false;
                    }
                }
            }
            if (numberOfThreads > 0) {
                testResultAndResponseThreadsMap.minTime = minTime;
                testResultAndResponseThreadsMap.maxTime = maxTime;
                testResultAndResponseThreadsMap.averageTime = parseInt((totalTime / numberOfThreads).toFixed(0));
            } else {
                testResultAndResponseThreadsMap.minTime = 0;
                testResultAndResponseThreadsMap.maxTime = 0;
                testResultAndResponseThreadsMap.averageTime = 0;
            }
            testResultAndResponseThreadsMap.testOk = testOk;
        }
        {
            // condense all repeats

            let numberOfRepeats: number = 0;
            let totalTime: number = 0;
            let minTime: number = -1;
            let maxTime: number = 0;
            let testOk: boolean = true;
            for (var i = 0; i < subsumedTest.repeats.length; i++) {
                if (subsumedTest.repeats[i] != null) {
                    if (subsumedTest.repeats[i].testOk) {

                        let time: number = subsumedTest.repeats[i].averageTime;
                        console.log("time ist " + time);
                        totalTime = totalTime + time;
                        console.log("total time ist " + totalTime);
                        numberOfRepeats++;
                        console.log("number of repeats ist " + numberOfRepeats);
                        console.log("average time ist " + (totalTime / numberOfRepeats).toFixed(0));
                        if (subsumedTest.repeats[i].maxTime > maxTime) {
                            maxTime = subsumedTest.repeats[i].maxTime;
                        }
                        if (minTime > subsumedTest.repeats[i].minTime) {
                            minTime = subsumedTest.repeats[i].minTime;
                        }
                        if (minTime == -1) {
                            minTime = subsumedTest.repeats[i].minTime;
                        }
                    }
                    else {
                        testOk = false;
                    }
                }
            }
            subsumedTest.minTime = minTime;
            subsumedTest.maxTime = maxTime;
            subsumedTest.testOk = testOk;
            subsumedTest.averageTime = parseInt((totalTime / numberOfRepeats).toFixed(0));
        }

    }

    remove(): void {
        this.results.results.pop();
    }

    getAll(): TestSuiteTYPE {
        var testSuite: TestSuiteTYPE = new TestSuiteTYPE();
        testSuite.testrequests = new Array<TestRequestTYPE>(this.results.results.length);
        for (var i = 0; i < this.results.results.length; i++) {
            testSuite.testrequests.push(this.results.results[i].request);
        }
        return testSuite;
    }


    toggleView(): void {
        this.showTable = (this.showTable == true ? false : true);
        console.log("toggle view finished " + this.showTable);
    }

    showTask(i: string): void {
        this.task = i;
    }

    showTestcase(t: TestResultTYPE): void {
        if (t == null) {
            this.testresult = null;
        } else {
            this.testresult = t;
        }
    }

    createRange(size: number): number[] {
        return Array.from(new Array(size), (val, index)=>index);
    }

}
