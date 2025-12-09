import {Component, LOCALE_ID} from '@angular/core';
import {bootstrapApplication, provideClientHydration} from '@angular/platform-browser';
import { provideRouter, RouterOutlet } from '@angular/router';
import {registerLocaleData} from "@angular/common";
import localeFr from '@angular/common/locales/fr';
import {provideHttpClient, withFetch, withInterceptors} from "@angular/common/http";
import {provideAnimationsAsync} from "@angular/platform-browser/animations/async";
import {credentialInterceptor} from "./app/_helpers/credential.interceptor";
import {MAT_DATE_LOCALE} from "@angular/material/core";
import {providePrimeNG} from "primeng/config";
import {MessageService} from "primeng/api";
import {Configuration} from "./app/_services/generated-api";
import {API_URL_OPENAPI} from "./app/environments/environment";
import Aura from '@primeng/themes/aura';
import {HeaderComponent} from "./app/component/header/header";
import {SearchComponent} from "./app/component/decision-filtrage/decision-filtrage";
import {DecisionCCComponent} from "./app/component/decision-cc/decision-cc";
import {HomeComponent} from "./app/component/home/home";
import {LoginComponent} from "./app/component/login/login";
import {Import} from "./app/component/import/import";
import {IsLoginGuard} from "./app/_helpers/IsLoginGuard";


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, HeaderComponent],
  template: `
    <app-header></app-header>
    <router-outlet></router-outlet>
  `,
})
export class App {}

export function apiConfigFactory(): Configuration {
  return new Configuration({ basePath: API_URL_OPENAPI });
}

registerLocaleData(localeFr, 'fr');

bootstrapApplication(App, {
  providers: [
    provideClientHydration(),
    provideHttpClient(withFetch()),
    provideAnimationsAsync(),
    provideHttpClient(withInterceptors([credentialInterceptor]), withFetch()),
    provideRouter([
      { path: '', component: HomeComponent },
      { path: 'recherche', component: SearchComponent },
      { path: 'decisions-cc', component: DecisionCCComponent },
      { path: 'login', component: LoginComponent },
      { path: 'import', component: Import , canActivate: [IsLoginGuard] }
    ]),
    { provide: LOCALE_ID, useValue: 'fr-FR' },
    { provide: MAT_DATE_LOCALE, useValue: 'fr-FR' },
    providePrimeNG({
      theme: {
        preset: Aura
      }
    }),
    { provide: MessageService, useClass: MessageService },
    { provide: Configuration, useFactory: apiConfigFactory },
  ]
});
