import {Component, OnInit} from '@angular/core';
import {ConfigService} from "../../service/config.service";
import {UrlKeeper} from "../../service/url.keeper";

@Component({
    selector: 'app-main',
    templateUrl: './main.component.html',
    styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit {

    selected :string = 'app';

    constructor(private urlKeeper : UrlKeeper) {
    }

    ngOnInit() {
    }

    getType(w: string): string {
        return this.selected === w ? 'selected' : 'unselected';
    }

    doClick(w: string) {
        this.selected = w;
    }
}
