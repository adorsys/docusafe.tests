import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-dnd',
  templateUrl: './dnd.component.html',
  styleUrls: ['./dnd.component.css']
})
export class DndComponent implements OnInit {
  message: string = "Drop your file here!";

  constructor() { }

  ngOnInit() {
  }

  onFilesChange(files : FileList){
    for (var i = 0; i< files.length; i++) {
      console.log("droped file " + files[i].name + " -> " + files[i].size);
      this.message=files[i].name;

      var reader = new FileReader();
      reader.onload = (function(affe, o, m) {
        return function(e) {
          console.log("result: " + e.target.result);
            m.call(o, e.target.result);
        };
      })(files[i], this, this.setMessage);
      reader.readAsText(files[i]);
    }
  }

  setMessage(m:string) {
    this.message = m;
  }

}
