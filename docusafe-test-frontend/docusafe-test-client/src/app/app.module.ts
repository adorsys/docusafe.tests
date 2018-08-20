import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {HttpClientModule} from "@angular/common/http";
import {TestService} from "../service/test.service";

import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {DndComponent} from '../dnd/dnd.component';
import {DndDirective} from '../dnd/dnd.directive';

@NgModule({
    declarations: [
        AppComponent,
        DndComponent,
        DndDirective
    ],
    imports: [
        BrowserModule,
        HttpClientModule,
        NgbModule
    ],
    providers: [TestService],
    bootstrap: [AppComponent]
})
export class AppModule {

}
