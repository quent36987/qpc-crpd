import { Injectable } from '@angular/core';
import { MessageService } from 'primeng/api';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  constructor(
    private messageService: MessageService
  ) { }

  openSnackBarError(message: string) {
    this.messageService.add({ severity: 'error', summary: 'Error', detail: message});
  }

  openSnackBarSuccess(message: string) {
    this.messageService.add({ severity: 'success', summary: 'Success', detail: message});
  }

  openSnackBarInfo(message: string) {
    this.messageService.add({ severity: 'info', summary: 'Info', detail: message});
  }

  openSnackBarWarn(message: string) {
    this.messageService.add({ severity: 'warn', summary: 'Warn', detail: message});
  }
}
