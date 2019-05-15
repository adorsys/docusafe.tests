import {Routes} from "@angular/router";
import {AppComponent} from "../app.component";
import {ConfigComponent} from "../config/config.component";
import {SwaggerComponent} from "../../swagger/swagger.component";

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
    path: 'swagger',
    component: SwaggerComponent
  },
  {
    path: '**',
    component: AppComponent
  }
];
