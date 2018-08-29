import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {HttpClientModule} from "@angular/common/http";
import {TestService} from "../service/test.service";

import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {DndComponent} from '../dnd/dnd.component';
import {DndDirective} from '../dnd/dnd.directive';
import {ClipboardDirective } from "../clipboard/clipboard.directive";
import {ClipboardService } from "../clipboard/clipboard.service";
import {FormsModule} from "@angular/forms";
import { ResultsComponent } from '../results/results.component';

@NgModule({
    declarations: [
        AppComponent,
        DndComponent,
        DndDirective,
        ClipboardDirective,
        ResultsComponent
    ],
    imports: [
        BrowserModule,
        HttpClientModule,
        NgbModule,
        FormsModule
    ],
    providers: [TestService, ClipboardService],
    bootstrap: [AppComponent]
})
export class AppModule {

}
