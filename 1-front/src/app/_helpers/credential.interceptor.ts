import { HttpClient, HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, catchError, throwError } from 'rxjs';
import {StorageService} from "../_services/storage.service";
import {API_URL} from "../environments/environment";

const isRefreshing = new BehaviorSubject<boolean>(false);

export const credentialInterceptor: HttpInterceptorFn = (req, next) => {
  const storageService = inject(StorageService);
  const router = inject(Router);
  const http = inject(HttpClient);

  // Vérifier si le token va expirer dans moins de 24h
  const expiration = storageService.getTokenRefreshDate();
  const now = Date.now();
  const one_day = 1000 * 60 * 60 * 24;

  if (expiration && expiration - now < one_day && !isRefreshing.value) {
    console.log('🔄 Token proche de l\'expiration, tentative de refresh...');

    isRefreshing.next(true);

    http.post<boolean>(API_URL + '/auth/refresh-token', {
      withCredentials: true,
    }).subscribe({
      next: () => {
        console.log('✅ Token rafraîchi avec succès');
        isRefreshing.next(false);
        storageService.saveTokenRefreshDate();
      },
      error: (err) => {
        console.error('❌ Impossible de rafraîchir le token', err);
        isRefreshing.next(false);
        storageService.removeTokenRefreshDate();
      },
    });
  }

  req = req.clone({
    withCredentials: true,
  });

  return next(req).pipe(
    catchError((error) => {
      if (error instanceof HttpErrorResponse && error.status === 401) {
        storageService.removeUser();

        router.navigate(['/auth/login'], {
          queryParams: { redirectUrl: router.url }, // Rediriger vers la page actuelle après la connexion
        });

        return throwError(() => new Error('Unauthorized'));
      }

      return throwError(() => error);
    }),
  );

};
