import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class SpinnerService {
  private spinnerSubject = new BehaviorSubject<boolean>(false);
  spinnerState$ = this.spinnerSubject.asObservable();

  showSpinner() {
    this.spinnerSubject.next(true);
  }

  hideSpinner() {
    this.spinnerSubject.next(false);
  }
}
