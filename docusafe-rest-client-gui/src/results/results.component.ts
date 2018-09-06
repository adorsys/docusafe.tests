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
    viewForTests: ViewForTests = new ViewForTests();
    showTable: boolean = true;
    task: string = "";
    singleThread: TestResultAndResponseTYPE = null;

    @Input()
    private testCaseOwner: TestSuiteOwner;

    constructor() {
    }

    ngOnInit() {
        this.testCaseOwner.registerResultsHolder(this);
    }

    add(response: TestResultAndResponseTYPE): void {
        let subsumedTest = this.viewForTests.testMap[response.request.dynamicClientInfo.testID];
        if (subsumedTest == null) {
            console.log("create new testresult for " + response.request.dynamicClientInfo.testID);
            subsumedTest = new SubsumedTestTYPE();
            subsumedTest.staticClientInfo = response.request.staticClientInfo;
            subsumedTest.testAction = response.request.testAction;
            subsumedTest.cacheType = response.request.cacheType;
            subsumedTest.layer = response.request.docusafeLayer;
            subsumedTest.repeats = new Array<TestResultAndResponseThreadsMapTYPE>(subsumedTest.staticClientInfo.numberOfRepeats);
            for (var i = 0; i < subsumedTest.repeats.length; i++) {
                subsumedTest.repeats[i] = null;
            }

            this.viewForTests.testMap[response.request.dynamicClientInfo.testID] = subsumedTest;
            this.viewForTests.subsumedTests.push(subsumedTest);
            subsumedTest.testOk = true;
        }
//        console.log("found subsumed test for " + response.request.dynamicClientInfo.testID);

        subsumedTest.lastReceivedDate = response.date;

        let testResultAndResponseThreadsMap: TestResultAndResponseThreadsMapTYPE = subsumedTest.repeats[(response.request.dynamicClientInfo.repetitionNumber - 1)];
        if (testResultAndResponseThreadsMap == null) {
//            console.log("create repeat for " + response.request.dynamicClientInfo.repetitionNumber);
            testResultAndResponseThreadsMap = new TestResultAndResponseThreadsMapTYPE();
            testResultAndResponseThreadsMap.threads = new Array<TestResultAndResponseTYPE>(subsumedTest.staticClientInfo.numberOfThreads);
            for (var i = 0; i < testResultAndResponseThreadsMap.threads.length; i++) {
                testResultAndResponseThreadsMap.threads[i] = null;
            }
            subsumedTest.repeats[(response.request.dynamicClientInfo.repetitionNumber - 1)] = testResultAndResponseThreadsMap;
        }
//        console.log("found repeat for " + response.request.dynamicClientInfo.repetitionNumber);
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
//                        console.log("time ist " + time);
                        totalTime = totalTime + time;
//                        console.log("total time ist " + totalTime);
                        numberOfRepeats++;
//                        console.log("number of repeats ist " + numberOfRepeats);
//                        console.log("average time ist " + (totalTime / numberOfRepeats).toFixed(0));
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

    removeLastTest(): void {
        this.viewForTests.subsumedTests.pop();

        // delete funktioniert nicht, da put nicht benutzt wird
        // put wird nicht benutzt, weil sonst die json Darstellung immer leer ist
        // daher wird die map neu angelegt, ohne das zu löschende element

        let newTestMap: Map<string, SubsumedTestTYPE> = new Map();
        for (let i = 0; i <this.viewForTests.subsumedTests.length; i++) {
            let t : SubsumedTestTYPE = this.viewForTests.subsumedTests[i];
            let key: string = t.repeats[0].threads[0].request.dynamicClientInfo.testID;
            newTestMap[key] = this.viewForTests.testMap[key];
        }
        this.viewForTests.testMap = newTestMap;
    }

    getAll(): TestSuiteTYPE {
        var testSuite: TestSuiteTYPE = new TestSuiteTYPE();
        testSuite.testrequests = new Array<TestRequestTYPE>();
        for (var i = 0; i < this.viewForTests.subsumedTests.length; i++) {
            let tr: TestRequestTYPE = JSON.parse(JSON.stringify(this.viewForTests.subsumedTests[i].repeats[0].threads[0].request));
            tr.dynamicClientInfo.repetitionNumber = 0;
            tr.dynamicClientInfo.threadNumber = 0;
            tr.dynamicClientInfo.testID = null;
            testSuite.testrequests.push(tr);
        }
        return testSuite;
    }

    getLastWriteResult(): SubsumedTestTYPE {
        if (this.viewForTests.subsumedTests.length == 0) {
            throw "no previous test found";
        }
        let subsumendTest: SubsumedTestTYPE = this.viewForTests.subsumedTests[this.viewForTests.subsumedTests.length - 1];
        if (subsumendTest.testAction == "CREATE_DOCUMENTS") {
            return subsumendTest;
        }
        throw "did not find last write test";
    }

    toggleView(): void {
        this.showTable = (this.showTable == true ? false : true);
        console.log("toggle view finished " + this.showTable);
    }

    showTask(i: string): void {
        this.task = i;
    }

    showSingleThread(thread: TestResultAndResponseTYPE): void {
        this.singleThread = thread;
    }

    createRange(size: number): number[] {
        return Array.from(new Array(size), (val, index)=>index);
    }

}
