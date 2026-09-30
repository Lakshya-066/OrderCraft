import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-dashboard-placeholder',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div style="padding: 2rem; max-width: 800px; margin: 0 auto;">
      <h1>Welcome to OrderCraft Dashboard</h1>
      <p style="font-size: 1.2rem; margin-top: 1rem;">Hello, {{ fullName }}!</p>
      
      <div style="margin-top: 2rem; padding: 1.5rem; background: #fff; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
        <h3>Your Access</h3>
        <p><strong>Roles:</strong> {{ roles.join(', ') || 'None' }}</p>
        <p><strong>Permissions:</strong></p>
        <ul style="margin-left: 1.5rem; margin-top: 0.5rem; margin-bottom: 1rem;">
          <li *ngFor="let perm of permissions">{{ perm }}</li>
        </ul>
        <div style="margin-bottom: 1rem;">
          <a routerLink="/users" style="margin-right: 1rem; color: #007bff; text-decoration: none; font-weight: bold;">Manage Users</a>
          <a routerLink="/roles" style="margin-right: 1rem; color: #007bff; text-decoration: none; font-weight: bold;">Manage Roles</a>
        </div>
        <button (click)="logout()" style="padding: 0.5rem 1rem; background: #dc3545; color: white; border: none; border-radius: 4px; cursor: pointer;">
          Logout
        </button>
      </div>
    </div>
  `
})
export class DashboardPlaceholderComponent {
  fullName = '';
  roles: string[] = [];
  permissions: string[] = [];

  constructor(private authService: AuthService, private router: Router) {
    const user = this.authService.getCurrentUser();
    if (user) {
      this.fullName = user.fullName || user.username;
      this.roles = user.roles || [];
      this.permissions = user.permissions || [];
    }
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => this.router.navigate(['/auth/login']),
      error: () => {
        // Fallback navigate
        this.router.navigate(['/auth/login']);
      }
    });
  }
}
