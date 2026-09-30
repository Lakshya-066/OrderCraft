import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then(m => m.AUTH_ROUTES)
  },
  {
    path: 'unauthorized',
    loadComponent: () => import('./features/auth/unauthorized/unauthorized.component').then(m => m.UnauthorizedComponent)
  },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () => import('./features/dashboard/dashboard-placeholder.component').then(m => m.DashboardPlaceholderComponent)
  },
  {
    path: 'users',
    canActivate: [authGuard],
    loadChildren: () => import('./features/users/users.routes').then(m => m.USERS_ROUTES)
  },
  {
    path: 'roles',
    canActivate: [authGuard],
    loadChildren: () => import('./features/roles/roles.routes').then(m => m.ROLES_ROUTES)
  },
  {
    path: '',
    redirectTo: "auth/login",
    pathMatch: 'full'
  },
  {
    path: '**',
    redirectTo: "auth/login"
  }
];
