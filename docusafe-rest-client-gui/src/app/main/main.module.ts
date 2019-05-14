import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppComponent} from '../app.component';
import {HttpClientModule} from "@angular/common/http";
import {TestService} from "../../service/test.service";

import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {DndComponent} from '../../dnd/dnd.component';
import {DndDirective} from '../../dnd/dnd.directive';
import {ClipboardDirective} from "../../clipboard/clipboard.directive";
import {ClipboardService} from "../../clipboard/clipboard.service";
import {FormsModule} from "@angular/forms";
import {ResultsComponent} from '../../results/results.component';
import {MainComponent} from "./main.component";
import {RouterModule} from "@angular/router";
import {routes} from "./router-config";
import {ConfigComponent} from "../config/config.component";
import {ConfigService} from "../../service/config.service";
import {UrlKeeper} from "../../service/url.keeper";

@NgModule({
    declarations: [
        ConfigComponent,
        MainComponent,
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
        FormsModule,
        RouterModule.forRoot(routes)
    ],
    providers: [TestService, ConfigService, ClipboardService, UrlKeeper],
    bootstrap: [MainComponent]
})
export class MainModule {

}
