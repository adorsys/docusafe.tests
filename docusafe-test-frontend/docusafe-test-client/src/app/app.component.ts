import { Component } from '@angular/core';
import { TestService } from "../service/test.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'docusafe-test-client';
  testcases: string[] = ["CREATE_DOCUMENTS","READ_DOCUMENTS"];
  docusafelayer: string[] = [ "CACHED_TRANSACTIONAL", "TRANSACTIONAL", "NON_TRANSACTIONAL", "DOCUSAFE_BASE"];
  cachetypes: string[] = ["NO_CACHE", "GUAVA", "HASH_MAP"];
  errormessage: string = "";

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



}
