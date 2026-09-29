import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { TokenService } from '../services/token.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const tokenService = inject(TokenService);

  // Skip adding header for login and refresh endpoints
  if (req.url.includes('/login') || req.url.includes('/refresh')) {
    return next(req);
  }

  const token = tokenService.getToken();
  // If token exists and is not expired, clone request and add Authorization header
  if (token && !tokenService.isTokenExpired()) {
    const authReq = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`)
    });
    return next(authReq);
  }

  // Pass through otherwise
  return next(req);
};
