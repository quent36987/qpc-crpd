import { Component } from '@angular/core';
import {AuthApi, DcisionsDeFiltrageQPCApi, DcisionsQPCCCApi} from "../../_services/generated-api";
import {StorageService} from "../../_services/storage.service";
import {SpinnerService} from "../../_services/spinner.service";
import {NotificationService} from "../../_services/notification.service";
import {ActivatedRoute, Router} from "@angular/router";
import {take} from "rxjs";
import {apiWrapper} from "../../_services/api-wrapper";
import {CommonModule} from "@angular/common";

@Component({
  selector: 'app-import',
  imports: [
    CommonModule,
  ],
  templateUrl: './import.html',
  styleUrl: './import.css',
})
export class Import {

  fileToUpload: File | null = null;

  constructor(
    private authService: AuthApi,
    private storageService: StorageService,
    private spinnerService: SpinnerService,
    private notifService: NotificationService,
    private router: Router,
    private route: ActivatedRoute,
    private decisionFiltrageApi: DcisionsDeFiltrageQPCApi,
    private decisionQpcCcService: DcisionsQPCCCApi,
  ) {

  }

  onFileSelected(event: Event) {
    const target = event.target as HTMLInputElement;
    const file = target.files && target.files[0];

    if (file) {
      this.fileToUpload = file;
    }
  }


  uploadFileFiltrage() {
    this.decisionFiltrageApi.importDecisionFiltrageXls(this.fileToUpload!)
      .pipe(apiWrapper(this.spinnerService, this.notifService))
      .subscribe();
  }

  uploadFileQpcCc() {
    this.decisionQpcCcService.importQpcCcXls(this.fileToUpload!)
      .pipe(apiWrapper(this.spinnerService, this.notifService))
      .subscribe();
  }


}
