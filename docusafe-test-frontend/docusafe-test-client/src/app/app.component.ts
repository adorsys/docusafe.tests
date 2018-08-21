import { Component } from '@angular/core';
import { TestService } from "../service/test.service";
import { TestCasesTYPE } from "../types/test.cases.type";
import { TestCaseOwner } from "./test.case.owner";


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements TestCaseOwner {
  title = 'docusafe-test-client';
  testcases: string[] = ["CREATE_DOCUMENTS","READ_DOCUMENTS"];
  docusafelayer: string[] = [ "CACHED_TRANSACTIONAL", "TRANSACTIONAL", "NON_TRANSACTIONAL", "DOCUSAFE_BASE"];
  cachetypes: string[] = ["NO_CACHE", "GUAVA", "HASH_MAP"];
  errormessage: string = "";
  tests: TestCasesTYPE = null;
  me: TestCaseOwner = this;

  constructor(private testService: TestService) {}
  requestError(errormessage: string) : void {
    this.errormessage = errormessage;
    console.error("an error occured: " + errormessage);
  }
  deleteDBAndCaches() : void {
    console.log("button pressed deleteDBAndCaches");
    this.errormessage = "";
    this.testService.deleteDBAndCaches(this, this.requestError);
  }
  setTestCases(content: TestCasesTYPE) : void {
    this.tests = content;
    console.log("received tests:");
    if (this.tests != null) {
      console.log("size is " + this.tests.tests.length);
    };
  }

}
