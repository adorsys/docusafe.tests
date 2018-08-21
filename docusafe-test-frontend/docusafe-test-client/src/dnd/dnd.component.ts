import {Component, OnInit, Input} from '@angular/core';
import {TestCaseOwner} from "../app/test.case.owner";
import {TestCasesTYPE} from "../types/test.cases.type";
import {FileContentHolder} from "./file.content.holder";

@Component({
    selector: 'app-dnd',
    templateUrl: './dnd.component.html',
    styleUrls: ['./dnd.component.css']
})
export class DndComponent implements OnInit, FileContentHolder {
    message: string = "Drop your file here!";
    fehler: string = "";

    @Input()
    private testCaseOwner: TestCaseOwner;

    constructor() {
    }

    ngOnInit() {
        this.testCaseOwner.registerFileContentHolder(this);
    }

    onFilesChange(files: FileList) {
        this.fehler = "";
        for (var i = 0; i < files.length; i++) {
            console.log("droped file " + files[i].name + " -> " + files[i].size);
            this.message = files[i].name;

            var reader = new FileReader();
            reader.onload = (function (affe, o, m) {
                return function (e) {
                    m.call(o, e.target.result);
                };
            })(files[i], this, this.setMessageAndPropagate);
            reader.readAsText(files[i]);
        }
    }

    setMessageAndPropagate(m: string) {
        this.setMessage(m);
        try {
            var testCases: TestCasesTYPE = JSON.parse(m);
            this.testCaseOwner.setTestCases(testCases);
        } catch (e ) {
            console.log("Fehler:" + e.message);
            this.fehler = e.message;
        }
    }

    setMessage(m: string) : void {
        this.message = m;
        console.log("setMessage mit neuem Wert:" + m);
    }

    getMessage() : string {
        console.log("getMessage mit Wert:" + this.message);
        return this.message;
    }
}
