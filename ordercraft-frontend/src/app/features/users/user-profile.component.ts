import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { UserService } from '../../core/services/user.service';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  template: `
    <div style="padding: 20px; max-width: 600px;">
      <h2>My Profile</h2>

      <div *ngIf="errorMessage" style="color: red; margin-bottom: 10px;">{{ errorMessage }}</div>
      <div *ngIf="successMessage" style="color: green; margin-bottom: 10px;">{{ successMessage }}</div>

      <form [formGroup]="form" (ngSubmit)="onSubmit()">
        <div style="margin-bottom: 15px;">
          <label style="display: block; margin-bottom: 5px;">Full Name</label>
          <input type="text" formControlName="fullName" style="width: 100%; padding: 5px;" />
        </div>

        <div style="margin-bottom: 15px;">
          <label style="display: block; margin-bottom: 5px;">Email</label>
          <input type="email" formControlName="email" style="width: 100%; padding: 5px;" />
        </div>

        <h3 style="margin-top: 20px;">Change Password (Optional)</h3>

        <div style="margin-bottom: 15px;">
          <label style="display: block; margin-bottom: 5px;">Current Password</label>
          <input type="password" formControlName="currentPassword" style="width: 100%; padding: 5px;" />
        </div>

        <div style="margin-bottom: 15px;">
          <label style="display: block; margin-bottom: 5px;">New Password</label>
          <input type="password" formControlName="newPassword" style="width: 100%; padding: 5px;" />
        </div>

        <div style="margin-top: 20px;">
          <button type="submit" [disabled]="form.invalid" style="padding: 5px 15px; margin-right: 10px;">Update Profile</button>
          <button type="button" routerLink="/dashboard" style="padding: 5px 15px;">Back to Dashboard</button>
        </div>
      </form>
    </div>
  `
})
export class UserProfileComponent implements OnInit {
  form: FormGroup;
  errorMessage = '';
  successMessage = '';

  constructor(private fb: FormBuilder, private userService: UserService) {
    this.form = this.fb.group({
      fullName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      currentPassword: [''],
      newPassword: ['']
    });
  }

  ngOnInit() {
    this.userService.getProfile().subscribe({
      next: (user) => {
        this.form.patchValue({
          fullName: user.fullName,
          email: user.email
        });
      },
      error: (err) => this.errorMessage = 'Failed to load profile'
    });
  }

  onSubmit() {
    if (this.form.invalid) return;

    this.errorMessage = '';
    this.successMessage = '';

    const req = this.form.value;
    if (!req.currentPassword) delete req.currentPassword;
    if (!req.newPassword) delete req.newPassword;

    this.userService.updateProfile(req).subscribe({
      next: (user) => {
        this.successMessage = 'Profile updated successfully!';
        this.form.get('currentPassword')?.reset();
        this.form.get('newPassword')?.reset();
      },
      error: (err) => this.errorMessage = err.error?.message || 'Failed to update profile'
    });
  }
}
