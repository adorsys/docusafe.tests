import {Component, OnInit} from '@angular/core';
import {Consts} from "../../environments/consts";

@Component({
    selector: 'app-main',
    templateUrl: './main.component.html',
    styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit {

    selected :string = 'app';
    private imageURL: string = Consts.INSTANCE.ASSETS_URL_PREFIX + "images/";

    constructor() {
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
