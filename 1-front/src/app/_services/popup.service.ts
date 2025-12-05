import { Injectable } from '@angular/core';
import {BehaviorSubject, Observable, take} from 'rxjs';
import {PopupConfig, PopupType} from '../_shared/component/popup/popup.model';


@Injectable({
  providedIn: 'root',
})
export class PopupService {
  private popupSubject = new BehaviorSubject<PopupConfig | null>(null);
  popupState$: Observable<PopupConfig | null> = this.popupSubject.asObservable();

  showPopup(config: PopupConfig) {
    this.popupSubject.next(config);
  }

  showConfirmationPopup(title: string, description: string, callback: () => void, confirmButton = 'Ok', closeCallback?: () => void) {
    this.showPopup({
      type: PopupType.Confirmation,
      title,
      description,
      confirmButton,
      callback,
      closeCallback,
    });
  }

  showPopupImage(imageUrl: string) {
    this.showPopup({
      type: PopupType.IMAGE_VIEWER,
      title: 'Visualisation du document',
      imageUrl: imageUrl,
    });
  }


  closePopup() {
    this.popupSubject.getValue()?.closeCallback?.();
    this.hidePopup();  }

  private hidePopup() {
    this.popupSubject.next(null);
  }


  /**
   * Ouvre une confirmation et renvoie un Observable qui émet `true`
   * si l'utilisateur clique sur le bouton confirmer, `false` si il ferme.
   */
  confirm$(
    title: string,
    description: string,
    confirmButton = 'Ok'
  ): Observable<boolean> {
    return new Observable<boolean>(observer => {
      const onConfirm = () => {
        observer.next(true);
        observer.complete();
        this.hidePopup();
      };

      const onCancel = () => {
        observer.next(false);
        observer.complete();
        this.hidePopup();
      };

      this.showConfirmationPopup(
        title,
        description,
        onConfirm,
        confirmButton,
        onCancel
      );
    }).pipe(
      take(1)
    );
  }
}
