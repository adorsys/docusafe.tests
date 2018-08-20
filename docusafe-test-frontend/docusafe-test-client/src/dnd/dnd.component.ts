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


}
