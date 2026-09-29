import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { TokenService } from '../services/token.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const tokenService = inject(TokenService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        // 401 Unauthorized: clear token, redirect to '/auth/login'
        tokenService.clear();
        router.navigate(['/auth/login']);
      } else if (error.status === 403) {
        // 403 Forbidden: redirect to '/unauthorized'
        router.navigate(['/unauthorized']);
      } else {
        // Other errors: log to console
        console.error('HTTP Error Intercepted:', error);
      }
      
      // Re-throw the error after handling
      return throwError(() => error);
    })
  );
};
