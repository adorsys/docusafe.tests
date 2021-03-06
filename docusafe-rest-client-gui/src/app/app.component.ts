import {ChangeDetectorRef, Component} from '@angular/core';
import {TestService} from "../service/test.service";
import {TestRequestTYPE, TestSuiteTYPE} from "../types/test.cases.type";
import {TestSuiteOwner} from "./test.case.owner";
import {FileContentHolder} from "../dnd/file.content.holder";
import {TestResultOwner} from "../results/test.result.owner";
import {TestResultTYPE} from "../types/test.result.type";
import {RequestSender} from "./request.sender";
import {Consts} from "../environments/consts";
import {TestResultAndResponseTYPE} from "../types/test.result.type";
import {formatDate} from '@angular/common';
import {isUndefined} from "util";
import {ClipboardService} from "../clipboard/clipboard.service";
import {v4 as uuid} from 'uuid';
import * as simpleDatasafeAdapter from "../testsuites/simpleDatasafeAdapter.json";
import * as simpleDatasafeAdapterSingle from "../testsuites/simpleDatasafeAdapterSingle.json";
import * as docusafeBase from "../testsuites/docusafeBase.json";
import * as cachedTransactional from "../testsuites/cachedTransactional.json";
import {SubsumedTestTYPE} from "../types/test.result.type";
import saveAs from 'file-saver';

import {DndOwner} from "../dnd/dnd.owner";
import {UrlKeeper} from "../service/url.keeper";
import {SwitchConfigSender} from "./switch.config.sender";
import {DFSConfigNamesResponseTYPE} from "../types/dfs.config.name.type";
import {DfsSwitchService} from "../service/dfs.switch.service";

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent implements TestSuiteOwner, DndOwner, RequestSender, SwitchConfigSender {
    title = 'docusafe-test-client';
    version = " version 1.1.0 (docusafe 1.1.0, dfs-connection 1.0.0 datasafe 0.4.2)";
    dndForTestSuite: FileContentHolder = null;
    dndForTestResults: FileContentHolder = null;
    testResultOwner: TestResultOwner = null;
    busy: boolean = false;
    doContinue: boolean = false;
    doAbort: boolean = false;
    specialTest: boolean = false;
    errormessage: string = "";
    testSuite: TestSuiteTYPE = null;
    me: TestSuiteOwner = this;
    currentTestIndex: number = -1;
    numberOfTests: number = 0;
    numberOfThreadsThatAnswered: number = 0;
    numberOfRepeatsDone: number = 0;
    testID: string = null;
    lastSendTestRequest: TestRequestTYPE = null;
    lastWriteResult: SubsumedTestTYPE = null;

    private imageURL: string = Consts.INSTANCE.ASSETS_URL_PREFIX + "images/";

    DELETE_DOCUMENT_ACTION_INDEX=1;
    READ_DOCUMENT_ACTION_INDEX=2;
    DOCUMENT_EXISTS_ACTION_INDEX = 3;
    LIST_DOCUMENTS_INDEX = 4;
    testactions: string[] = [
        "CREATE_DOCUMENTS",
        "DELETE_DOCUMENTS",
        "READ_DOCUMENTS",
        "DOCUMENT_EXISTS",
        "LIST_DOCUMENTS",
        "DELETE_DATABASE"
    ];
    docusafelayer: string[] = [
        "CACHED_TRANSACTIONAL",
        "DOCUSAFE_BASE",
        "SIMPLE_DATASAFE_ADAPTER"
    ];

    testSuites: TestSuiteTYPE[] = [<TestSuiteTYPE>simpleDatasafeAdapter.default, <TestSuiteTYPE>docusafeBase.default, <TestSuiteTYPE>cachedTransactional.default, <TestSuiteTYPE>simpleDatasafeAdapterSingle.default];
    currentTestSuiteIndex = 0;
    currentTestSuiteName: string = this.testSuites[this.currentTestSuiteIndex].name;

    dfsNames: string[] = ["default"];
    dfsName: string = "affe";

    constructor(private testService: TestService, private dfsSwitchService: DfsSwitchService, private clipboardService: ClipboardService, private urlKeeper: UrlKeeper) {
        console.log("ANZAHL DER TEST-SUITES: " +  this.testSuites.length);
        console.log("INDEX TEST-SUITES: " +  this.currentTestSuiteIndex);
        for (var i = 0; i<this.testSuites.length; i++) {
            console.log("NAME DER TESTSUITE: " +  this.testSuites[i].name);
            console.log("Anzahl der Test   : " +  this.testSuites[i].testrequests.length);
        }
        console.log("Curernt TestSuite Name: " +  this.currentTestSuiteName);

        this.setTests(this.testSuites[this.currentTestSuiteIndex]);
        console.log("APP CONSTRUCTION");
        console.log("try to load confignames");
        this.getDFSConfigNames();
    }


    changeTestSuite () {
        console.log("current test Suite is now:" + this.currentTestSuiteName);
        for (let i = 0; i<this.testSuites.length; i++) {
            if (this.testSuites[i].name == this.currentTestSuiteName) {
                this.currentTestSuiteIndex = i;
            };
        }
        this.setTests(this.testSuites[this.currentTestSuiteIndex]);
    }

    notifyForChanchedFileContent(id: number): void {
        if (id == 1) {
            var filecontent: string = this.dndForTestSuite.getMessage();
            try {
                console.log("parse testrequests");
                var testCases: TestSuiteTYPE = JSON.parse(filecontent);
                if (isUndefined(testCases)) {
                    throw "dropped element are not json";
                }
                var length = testCases.testrequests.length;
                console.log("anzahl der neu geladenen testfälle:" + length);
                if (length == 0) {
                    throw "dropped element are not json testactions";
                }
                this.setTests(testCases);
            } catch (e) {
                this.dndForTestSuite.setErrorMessage(e);
            }
        }
        if (id == 2) {
            var filecontent: string = this.dndForTestResults.getMessage();
            try {
                console.log("parse testresults");
                var subsumendTests : SubsumedTestTYPE[] = JSON.parse(filecontent);
                if (isUndefined(subsumendTests)) {
                    throw "dropped element are not json";
                }
                let length = subsumendTests.length;
                console.log("anzahl der neu geladenen testergebnisse:" + length);
                if (length == 0) {
                    throw "dropped element are not json testactions";
                }
                this.testResultOwner.loadSubsumedTests(subsumendTests);
            } catch (e) {
                this.dndForTestResults.setErrorMessage(e);
            }
        }
    }

    setTests(newTestSuite: TestSuiteTYPE): void {
        if (this.testSuite == null) {
            console.log("testsuite initializes to " + newTestSuite.name);;
        } else {
            console.log("testsuite changes from : " + this.testSuite.name + " to " + newTestSuite.name);;
        }
        this.testSuite = newTestSuite;
        if (this.testSuite != null) {
            this.currentTestIndex = 0;
            this.numberOfTests = this.testSuite.testrequests.length;
        } else {
            console.error("testsuite received is null");
        }
    }

    registerFileContentHolder(id: number, fch: FileContentHolder): void {
        if (id == 1) {
            this.dndForTestSuite = fch;
        }
        if (id == 2) {
            this.dndForTestResults = fch;
        }
    }

    registerResultsHolder(r: TestResultOwner): void {
        this.testResultOwner = r;
    }


    previousTestcase(): void {
        if (this.currentTestIndex > 0) {
            this.currentTestIndex--;
        }
    }

    nextTestcase(): void {
        if (this.currentTestIndex < this.testSuite.testrequests.length - 1) {
            this.currentTestIndex++;
        }
    }

    doCleanDatabase(): void {
        this.busy = true;
        this.specialTest = true;
        this.errormessage = "";
        this.numberOfRepeatsDone = 1;
        this.numberOfThreadsThatAnswered = 1;
        let deleteTestCase: TestRequestTYPE = JSON.parse(JSON.stringify(this.testSuites[0].testrequests[0]));
        deleteTestCase.testAction = "DELETE_DATABASE";
        deleteTestCase.dynamicClientInfo.testID = uuid();
        deleteTestCase.dynamicClientInfo.threadNumber = 1;
        deleteTestCase.dynamicClientInfo.repetitionNumber = 1;
        deleteTestCase.staticClientInfo.numberOfRepeats = 1;
        deleteTestCase.staticClientInfo.numberOfThreads = 1;
        deleteTestCase.docusafeLayer = this.testSuite.testrequests[this.currentTestIndex].docusafeLayer;
        this.lastSendTestRequest = deleteTestCase;
        this.testService.test(deleteTestCase, this);
    }

    doCurrentTestOnly() : void {
        this.doContinue = false;
        this.doCurrentTest();
    }

    doCurrentTest(): void {

        if (this.testSuite.testrequests[this.currentTestIndex].testAction == this.testactions[this.READ_DOCUMENT_ACTION_INDEX] ||
            this.testSuite.testrequests[this.currentTestIndex].testAction == this.testactions[this.DOCUMENT_EXISTS_ACTION_INDEX] ||
            this.testSuite.testrequests[this.currentTestIndex].testAction == this.testactions[this.DELETE_DOCUMENT_ACTION_INDEX]
        ) {
            this.lastWriteResult = this.testResultOwner.getLastWriteResult();
            // Anzahl der threads und repeats müssen vom WriteTest übernommen werden.
            this.testSuite.testrequests[this.currentTestIndex].staticClientInfo = this.lastWriteResult.staticClientInfo;
        } else {
            if (this.testSuite.testrequests[this.currentTestIndex].userid == "" &&
                this.testSuite.testrequests[this.currentTestIndex].testAction == this.testactions[this.LIST_DOCUMENTS_INDEX]) {
                console.log("userid not set and list documents, do getLastWriteResult");
                this.lastWriteResult = this.testResultOwner.getLastWriteResult();
                // Anzahl der threads und repeats müssen vom WriteTest übernommen werden.
                this.testSuite.testrequests[this.currentTestIndex].staticClientInfo = this.lastWriteResult.staticClientInfo;
            }
        }

        this.specialTest = false;
        this.busy = true;
        this.errormessage = "";
        this.numberOfThreadsThatAnswered = 1;
        this.numberOfRepeatsDone = 1;
        this.testID = uuid();
        this.doAbort = false;

        this.startRepeatTest();
    }

    startRepeatTest(): void {
        let currentTest: TestRequestTYPE = JSON.parse(JSON.stringify(this.testSuite.testrequests[this.currentTestIndex]));
        currentTest.dynamicClientInfo.repetitionNumber = this.numberOfRepeatsDone;
        currentTest.dynamicClientInfo.testID = this.testID;
        this.numberOfThreadsThatAnswered = 1;

        for (let i = 1; i <= currentTest.staticClientInfo.numberOfThreads; i++) {
            let request: TestRequestTYPE = JSON.parse(JSON.stringify(currentTest));
            request.dynamicClientInfo.threadNumber = i;
            request.dynamicClientInfo.requestID = uuid();
            if (this.testSuite.testrequests[this.currentTestIndex].testAction == this.testactions[this.READ_DOCUMENT_ACTION_INDEX] ||
                this.testSuite.testrequests[this.currentTestIndex].testAction == this.testactions[this.DOCUMENT_EXISTS_ACTION_INDEX] ||
                this.testSuite.testrequests[this.currentTestIndex].testAction == this.testactions[this.DELETE_DOCUMENT_ACTION_INDEX]
            ) {
                this.modifyReadRequest(request, this.lastWriteResult);
            } else {
                if (this.testSuite.testrequests[this.currentTestIndex].userid == "" &&
                    this.testSuite.testrequests[this.currentTestIndex].testAction == this.testactions[this.LIST_DOCUMENTS_INDEX]) {
                    console.log("userid not set and list documents, do modifyReadRequest");
                    this.modifyReadRequest(request, this.lastWriteResult);
                }
            }

            this.lastSendTestRequest = request;
            this.testService.test(request, this);
        }
    }

    doRemainingTests(): void {
        this.doContinue = true;
        this.doCurrentTest();
    }

    doAllTests(): void {
        this.currentTestIndex = 0;
        this.doRemainingTests();
    }

    abort(): void {
        this.doAbort = true;
        this.busy = false;
    }

    removeLastTestFromDone(): void {
        this.testResultOwner.removeLastTest();
    }

    receiveRequestResult(statusCode: number, testRequest: TestRequestTYPE, testResult: TestResultTYPE): void {
        console.log("receiveRequestResult");
        var response: TestResultAndResponseTYPE = new TestResultAndResponseTYPE();
        response.result = testResult;
        response.statusCode = statusCode;
        response.request = testRequest;
        response.date = testResult.date;
        response.testOk = true;
        if (testRequest.testAction == this.testactions[this.READ_DOCUMENT_ACTION_INDEX] ||
            testRequest.testAction == this.testactions[this.DOCUMENT_EXISTS_ACTION_INDEX] ||
            testRequest.testAction == this.testactions[this.LIST_DOCUMENTS_INDEX]
        )
        {
            for (var i = 0; i<testResult.listOfReadDocuments.length; i++) {
                if (testResult.listOfReadDocuments[i].readResult != "OK") {
                    response.testOk = false;
                }
            }
        }
        this.continueTesting(response, testRequest);
    }

    receiveRequestError(statusCode: number, testRequest: TestRequestTYPE, errorMessage: string): void {
        console.log("receiveRequestError");
        var response: TestResultAndResponseTYPE = new TestResultAndResponseTYPE();
        response.error = errorMessage;
        response.statusCode = statusCode;
        response.request = testRequest;
        response.date = formatDate(new Date(), 'yyyy-MM-dd-hh:mm:SS', 'en');
        response.testOk = false;
        this.continueTesting(response, testRequest);
    }

    continueTesting(response: TestResultAndResponseTYPE, testRequest: TestRequestTYPE): void {
        this.numberOfThreadsThatAnswered++;
        this.testResultOwner.add(response, this.numberOfRepeatsDone, this.numberOfThreadsThatAnswered);
        if (this.doAbort) {
            console.log("abort is true. do not continue");
            this.busy = false;
            return;
        }
        if (this.numberOfThreadsThatAnswered <= testRequest.staticClientInfo.numberOfThreads) {
            console.log("only " + this.numberOfThreadsThatAnswered + " have ansewred yet. continue to wait");
            return;
        }
        console.log("ALL " + (this.numberOfThreadsThatAnswered-1) + " Threads answered yes");
        this.numberOfRepeatsDone++;
        if (this.numberOfRepeatsDone <= testRequest.staticClientInfo.numberOfRepeats) {
            console.log("erst die " + this.numberOfRepeatsDone + " Wiederholung, test wird wiederholt");
            this.startRepeatTest();
            return;
        }
        console.log("ALL " + (this.numberOfRepeatsDone-1) + " reppetitions done yes");
        this.busy = false;
        console.log("einzelner test beendt");

        if (this.specialTest == true) {
            console.log("TEST FINISHED");
            return;
        }

        if (this.doContinue) {
            this.currentTestIndex++;
            if (this.currentTestIndex == this.testSuite.testrequests.length) {
                this.currentTestIndex--;
                this.doContinue = false;
                console.log("alle tests beendt");
            } else {
                console.log("CONTINUE TESTING");
                this.doRemainingTests();
            }
        }
    }

    saveTestSuite(): void {
        var date:string = formatDate(new Date(), 'yyyyMMdd-hhmmSS', 'en');

        const filename = date + "_testsuite.json"
        const blob = new Blob([JSON.stringify(this.testResultOwner.getTestSuite())], { type: 'text/plain' });
        saveAs(blob, filename);
    }

    saveTestResults(): void {
        var date:string = formatDate(new Date(), 'yyyyMMdd-hhmmSS', 'en');

        const filename =date + "_testresults.json"
        const blob = new Blob([JSON.stringify(this.testResultOwner.getSubsumedTests())], { type: 'text/plain' });
        saveAs(blob, filename);
    }

    private modifyReadRequest(request: TestRequestTYPE, lastWriteResult: SubsumedTestTYPE) {


        console.log("repeats: " + request.dynamicClientInfo.repetitionNumber + " threads:" + request.dynamicClientInfo.threadNumber);
        let result : TestResultAndResponseTYPE = lastWriteResult.repeats[request.dynamicClientInfo.repetitionNumber-1].threads[request.dynamicClientInfo.threadNumber-1];
        if (result.error ==  null) {
            if (request.testAction == this.testactions[this.READ_DOCUMENT_ACTION_INDEX] ||
                request.testAction == this.testactions[this.DOCUMENT_EXISTS_ACTION_INDEX] ||
                request.testAction == this.testactions[this.DELETE_DOCUMENT_ACTION_INDEX]
            ) {
                request.documentsToRead = result.result.listOfCreatedDocuments;
            }
        }
        if (result.result == undefined) {
            console.error("can not set useid");
        } else {
            request.userid = result.result.userID;

        }
    }

    getDFSConfigNames() {
        console.log("ask for available dfs config names");
        this.dfsSwitchService.getNames(this);
        // answer goes to setNames()
    }

    setNames(dfsConfigNames: DFSConfigNamesResponseTYPE): void {
        this.dfsNames = dfsConfigNames.avalailabeNames;

        for (var i = 0; i<this.dfsNames.length; i++) {
            if (this.dfsNames[i].startsWith("DEFAULT")) {
                this.dfsName = this.dfsNames[i];
            }
        }
    }

    changeDFSName() : void {
        console.log("ask to change to dfs:" + this.dfsName);
        this.dfsSwitchService.setName(this.dfsName);
    }

}
