import { inject } from '@angular/core';
import { Router, UrlTree } from '@angular/router';
import { Observable, map } from 'rxjs';
import {StorageService} from '../_services/storage.service';

export const IsLoginGuard = (): Observable<boolean | UrlTree> => {
  const storage = inject(StorageService);
  const router = inject(Router);

  return storage.loggedIn$.pipe(
    map(isLoggedIn => {
      if (isLoggedIn) {
        return true;
      }
      return router.createUrlTree(['/auth/login'], {
        queryParams: { redirectUrl: router.url },
      });
    })
  );
};
