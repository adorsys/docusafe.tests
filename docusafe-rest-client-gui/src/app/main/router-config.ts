import {Routes} from "@angular/router";
import {AppComponent} from "../app.component";
import {ConfigComponent} from "../config/config.component";

export const routes: Routes = [
  {
    path: 'app',
    component: AppComponent
  },
  {
    path: 'config',
    component: ConfigComponent
  },
  {
    path: '**',
    component: AppComponent
  }
];
