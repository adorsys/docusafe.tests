import { Component, OnInit } from '@angular/core';

@Component({
    selector: 'swagger',
    templateUrl: './swagger.component.html',
    styleUrls: ['./swagger.component.css']
})

export class SwaggerComponent implements OnInit {
    model = {
        left: true,
        middle: false,
        right: false
    };

    focus;
    constructor() { }

    ngOnInit() {}
}
