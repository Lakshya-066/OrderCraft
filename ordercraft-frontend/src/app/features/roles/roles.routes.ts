import { Routes } from '@angular/router';

export const ROLES_ROUTES: Routes = [
  { path: '', loadComponent: () => import('./role-list.component').then(m => m.RoleListComponent) },
  { path: 'new', loadComponent: () => import('./role-form.component').then(m => m.RoleFormComponent) },
  { path: ':id/edit', loadComponent: () => import('./role-form.component').then(m => m.RoleFormComponent) }
];
