import {Directive, HostListener, HostBinding} from '@angular/core';

@Directive({
  selector: '[appDnd]'
})
export class DndDirective {

  @HostBinding('style.background') private background = '#eee';

  constructor() { }

  @HostListener('dragover', ['$event']) public onDragOver(evt){
    evt.preventDefault();
    evt.stopPropagation();
    this.background = '#999';
  }

  @HostListener('dragleave', ['$event']) public onDragLeave(evt){
    evt.preventDefault();
    evt.stopPropagation();
    this.background = '#eee'
  }

  @HostListener('drop', ['$event']) public onDrop(evt){
    evt.preventDefault();
    evt.stopPropagation();
    this.background = '#eee';
    let files = evt.dataTransfer.files;
    for (var i = 0; i< files.length; i++) {
      console.log("droped file " + files[i].name + " -> " + files[i].size)

      var reader = new FileReader();
      reader.onload = (function(theFile) {
        return function(e) {
          console.log("result: " + e.target.result);
        };
      })(files[i]);
      reader.readAsText(files[i]);
    }
  }

}
