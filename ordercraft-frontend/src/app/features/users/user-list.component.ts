import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../core/services/user.service';
import { UserResponse } from '../../core/models/user.model';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  template: `
    <div style="padding: 20px;">
      <h2>Users</h2>
      <div style="margin-bottom: 20px;">
        <input type="text" [(ngModel)]="searchTerm" placeholder="Search users..." style="padding: 5px; margin-right: 10px;" />
        <button (click)="search()" style="padding: 5px 10px; margin-right: 10px;">Search</button>
        <button routerLink="/users/new" style="padding: 5px 10px;">Create User</button>
      </div>

      <div *ngIf="errorMessage" style="color: red; margin-bottom: 10px;">{{ errorMessage }}</div>

      <table border="1" cellpadding="8" style="width: 100%; border-collapse: collapse;">
        <thead>
          <tr>
            <th>Username</th>
            <th>Full Name</th>
            <th>Email</th>
            <th>Roles</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let user of users">
            <td>{{ user.username }}</td>
            <td>{{ user.fullName }}</td>
            <td>{{ user.email }}</td>
            <td>{{ user.roles.join(', ') }}</td>
            <td>{{ user.isActive ? 'Active' : 'Inactive' }}</td>
            <td>
              <button [routerLink]="['/users', user.id, 'edit']" style="margin-right: 5px;">Edit</button>
              <button (click)="toggleStatus(user)">{{ user.isActive ? 'Deactivate' : 'Activate' }}</button>
            </td>
          </tr>
          <tr *ngIf="users.length === 0">
            <td colspan="6">No users found.</td>
          </tr>
        </tbody>
      </table>

      <div style="margin-top: 20px;">
        <button (click)="prevPage()" [disabled]="page === 0" style="margin-right: 10px;">Previous</button>
        <span>Page {{ page + 1 }} of {{ totalPages }}</span>
        <button (click)="nextPage()" [disabled]="page >= totalPages - 1" style="margin-left: 10px;">Next</button>
      </div>
    </div>
  `
})
export class UserListComponent implements OnInit {
  users: UserResponse[] = [];
  page = 0;
  size = 20;
  searchTerm = '';
  totalPages = 1;
  errorMessage = '';

  constructor(private userService: UserService) {}

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.userService.getUsers(this.page, this.size, this.searchTerm).subscribe({
      next: (res) => {
        this.users = res.data || [];
        this.totalPages = res.totalPages || 1;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to load users';
      }
    });
  }

  search() {
    this.page = 0;
    this.loadUsers();
  }

  prevPage() {
    if (this.page > 0) {
      this.page--;
      this.loadUsers();
    }
  }

  nextPage() {
    if (this.page < this.totalPages - 1) {
      this.page++;
      this.loadUsers();
    }
  }

  toggleStatus(user: UserResponse) {
    this.userService.toggleStatus(user.id, !user.isActive).subscribe({
      next: (res) => {
        user.isActive = res.isActive;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to toggle status';
      }
    });
  }
}
