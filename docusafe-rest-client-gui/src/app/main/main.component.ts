import {Component, OnInit} from '@angular/core';
import {Consts} from "../../environments/consts";
import {UrlKeeper} from "../../service/url.keeper";

@Component({
    selector: 'app-main',
    templateUrl: './main.component.html',
    styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit {

    destinationUrls: string[] = [
        "http://docusafe-rest-server-psp-docusafe-performancetest.cloud.adorsys.de",
        "http://localhost:9991",
    ];
    destinationUrl: string = this.destinationUrls[0];

    selectUrl() {
        this.urlKeeper.setUrl(this.destinationUrl);
    }

    selected :string = 'app';
    private imageURL: string = Consts.INSTANCE.ASSETS_URL_PREFIX + "images/";

    constructor(private urlKeeper : UrlKeeper) {
        this.selectUrl();
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
