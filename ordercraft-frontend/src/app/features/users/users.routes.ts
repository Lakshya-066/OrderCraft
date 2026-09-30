import { Routes } from '@angular/router';

export const USERS_ROUTES: Routes = [
  { path: '', loadComponent: () => import('./user-list.component').then(m => m.UserListComponent) },
  { path: 'new', loadComponent: () => import('./user-form.component').then(m => m.UserFormComponent) },
  { path: ':id/edit', loadComponent: () => import('./user-form.component').then(m => m.UserFormComponent) },
  { path: 'profile', loadComponent: () => import('./user-profile.component').then(m => m.UserProfileComponent) }
];
