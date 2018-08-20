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

  constructor(private testService: TestService) {}
  deleteDBAndCachesFinished() : void {
    console.log("deleteDBAndCaches finished confirmation from server");
  }
  deleteDBAndCaches() : void {
    console.log("button pressed deleteDBAndCaches");
    this.testService.deleteDBAndCaches(this, this.deleteDBAndCachesFinished);
  }



}
