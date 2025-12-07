import { Observable, finalize, tap, catchError, throwError } from 'rxjs';
import { SpinnerService } from './spinner.service';
import {NotificationService} from "./notification.service";

export function apiWrapper<T>(
  spinner: SpinnerService,
  notification: NotificationService,
  errorMessage?: string,
  successMessage?: string
) {
  return (source$: Observable<T>): Observable<T> => {
    spinner.showSpinner();

    return source$.pipe(
      tap(() => {
        if (successMessage) {
          notification.success(successMessage);
        }
      }),
      finalize(() => spinner.hideSpinner()),
      catchError(error => {
        if (errorMessage) {
          notification.error(errorMessage);
        } else {
          notification.error(error.error.message);
        }
        return throwError(() => error);
      })
    );
  };
}

