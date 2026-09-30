import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { RoleService } from '../../core/services/role.service';
import { RoleResponse } from '../../core/models/user.model';

@Component({
  selector: 'app-role-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div style="padding: 20px;">
      <h2>Roles</h2>
      <div style="margin-bottom: 20px;">
        <button routerLink="/roles/new" style="padding: 5px 10px;">Create Role</button>
      </div>

      <div *ngIf="errorMessage" style="color: red; margin-bottom: 10px;">{{ errorMessage }}</div>

      <table border="1" cellpadding="8" style="width: 100%; border-collapse: collapse;">
        <thead>
          <tr>
            <th>Role Name</th>
            <th>Description</th>
            <th>System?</th>
            <th>Permissions Count</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let role of roles">
            <td>{{ role.roleName }}</td>
            <td>{{ role.description }}</td>
            <td>{{ role.isSystem ? 'Yes' : 'No' }}</td>
            <td>{{ role.permissions?.length || 0 }}</td>
            <td>
              <button [routerLink]="['/roles', role.id, 'edit']" [disabled]="role.isSystem" style="margin-right: 5px;">Edit</button>
              <button (click)="deleteRole(role)" [disabled]="role.isSystem">Delete</button>
            </td>
          </tr>
          <tr *ngIf="roles.length === 0">
            <td colspan="5">No roles found.</td>
          </tr>
        </tbody>
      </table>
    </div>
  `
})
export class RoleListComponent implements OnInit {
  roles: RoleResponse[] = [];
  errorMessage = '';

  constructor(private roleService: RoleService) {}

  ngOnInit() {
    this.loadRoles();
  }

  loadRoles() {
    this.roleService.getRoles().subscribe({
      next: (res) => this.roles = res || [],
      error: (err) => this.errorMessage = err.error?.message || 'Failed to load roles'
    });
  }

  deleteRole(role: RoleResponse) {
    if (role.isSystem) return;
    if (window.confirm(`Are you sure you want to delete role \${role.roleName}?`)) {
      this.roleService.deleteRole(role.id).subscribe({
        next: () => this.loadRoles(),
        error: (err) => this.errorMessage = err.error?.message || 'Failed to delete role'
      });
    }
  }
}
