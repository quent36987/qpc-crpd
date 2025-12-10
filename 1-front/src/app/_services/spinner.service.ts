import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class SpinnerService {
  private spinnerSubject = new BehaviorSubject<boolean>(false);
  spinnerState$ = this.spinnerSubject.asObservable();
  private showTimeout: any; // pour stocker le timer

  /** Affiche le spinner après un court délai */
  showSpinner(delay = 150) {
    clearTimeout(this.showTimeout);

    this.showTimeout = setTimeout(() => {
      this.spinnerSubject.next(true);
    }, delay);
  }

  /** Cache immédiatement le spinner */
  hideSpinner() {
    clearTimeout(this.showTimeout); // stoppe le timer si le spinner n'est pas encore affiché
    this.spinnerSubject.next(false);
  }
}
