import {Component, OnInit, Input} from '@angular/core';
import {FileContentHolder} from "./file.content.holder";
import {Consts} from "../environments/consts";
import {DndOwner} from "./dnd.owner";

@Component({
    selector: 'app-dnd',
    templateUrl: './dnd.component.html',
    styleUrls: ['./dnd.component.css']
})
export class DndComponent implements OnInit, FileContentHolder {
    message: string = "";
    errorMessage: string = "";

    @Input()
    private labelText : string;

    @Input()
    private dndOwner: DndOwner;

    @Input()
    private id: number;

    constructor() {
        console.log("dnd constructor");
    }

    ngOnInit() {
        console.log("ngOnInit dnd")
        this.dndOwner.registerFileContentHolder(this.id, this);
    }

    onFilesChange(files: FileList) {
        this.errorMessage = "";
        for (var i = 0; i < files.length; i++) {
            console.log("droped file " + files[i].name + " -> " + files[i].size);
            this.message = files[i].name;

            var reader = new FileReader();
            reader.onload = (function (affe, o, m) {
                return function (e) {
                    m.call(o, e.target.result);
                };
            })(files[i], this, this.setMessage);
            reader.readAsText(files[i]);
        }
    }

    setMessage(m: string) : void {
        this.message = m;
        this.errorMessage = "";
        this.dndOwner.notifyForChanchedFileContent(this.id);
    }

    setErrorMessage(em: string) : void {
        this.errorMessage = em;
    }

    getMessage() : string {
        console.log("getMessage mit Wert:" + this.message);
        return this.message;
    }
}
