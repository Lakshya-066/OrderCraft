import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const permissionGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const permissions = route.data['permissions'] as string[];
  
  if (!permissions || permissions.length === 0) {
    return true;
  }

  if (authService.hasAnyPermission(permissions)) {
    return true;
  }
  
  router.navigate(['/unauthorized']);
  return false;
};
