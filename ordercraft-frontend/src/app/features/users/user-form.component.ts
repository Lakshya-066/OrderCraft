import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { UserService } from '../../core/services/user.service';
import { RoleService } from '../../core/services/role.service';
import { RoleResponse } from '../../core/models/user.model';

@Component({
  selector: 'app-user-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  template: `
    <div style="padding: 20px; max-width: 600px;">
      <h2>{{ isEdit ? 'Edit User' : 'Create User' }}</h2>

      <div *ngIf="errorMessage" style="color: red; margin-bottom: 10px;">{{ errorMessage }}</div>

      <form [formGroup]="form" (ngSubmit)="onSubmit()">
        <div style="margin-bottom: 15px;">
          <label style="display: block; margin-bottom: 5px;">Username</label>
          <input type="text" formControlName="username" style="width: 100%; padding: 5px;" />
        </div>

        <div style="margin-bottom: 15px;">
          <label style="display: block; margin-bottom: 5px;">Email</label>
          <input type="email" formControlName="email" style="width: 100%; padding: 5px;" />
        </div>

        <div style="margin-bottom: 15px;">
          <label style="display: block; margin-bottom: 5px;">Full Name</label>
          <input type="text" formControlName="fullName" style="width: 100%; padding: 5px;" />
        </div>

        <div *ngIf="!isEdit" style="margin-bottom: 15px;">
          <label style="display: block; margin-bottom: 5px;">Password</label>
          <input type="password" formControlName="password" style="width: 100%; padding: 5px;" />
        </div>

        <div style="margin-bottom: 15px;">
          <label style="display: block; margin-bottom: 5px;">Roles</label>
          <div *ngFor="let role of roles">
            <label>
              <input type="checkbox" [value]="role.id" (change)="onRoleChange($event, role.id)" [checked]="hasRole(role.roleName)" />
              {{ role.roleName }}
            </label>
          </div>
        </div>

        <div style="margin-top: 20px;">
          <button type="submit" [disabled]="form.invalid" style="padding: 5px 15px; margin-right: 10px;">Save</button>
          <button type="button" routerLink="/users" style="padding: 5px 15px;">Cancel</button>
        </div>
      </form>
    </div>
  `
})
export class UserFormComponent implements OnInit {
  form: FormGroup;
  isEdit = false;
  userId: number | null = null;
  roles: RoleResponse[] = [];
  errorMessage = '';
  selectedRoleIds: number[] = [];
  currentUserRoles: string[] = [];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private userService: UserService,
    private roleService: RoleService
  ) {
    this.form = this.fb.group({
      username: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      fullName: ['', Validators.required],
      password: ['']
    });
  }

  ngOnInit() {
    this.userId = Number(this.route.snapshot.paramMap.get('id')) || null;
    this.isEdit = !!this.userId;

    if (this.isEdit) {
      this.form.get('username')?.disable();
      this.form.get('password')?.clearValidators();
      this.form.get('password')?.updateValueAndValidity();
    } else {
      this.form.get('password')?.setValidators([Validators.required]);
      this.form.get('password')?.updateValueAndValidity();
    }

    this.roleService.getRoles().subscribe({
      next: (roles) => {
        this.roles = roles;
        if (this.isEdit && this.userId) {
          this.loadUser();
        }
      },
      error: (err) => this.errorMessage = 'Failed to load roles'
    });
  }

  loadUser() {
    if (!this.userId) return;
    this.userService.getUser(this.userId).subscribe({
      next: (user) => {
        this.form.patchValue({
          username: user.username,
          email: user.email,
          fullName: user.fullName
        });
        this.currentUserRoles = user.roles;
        this.selectedRoleIds = this.roles.filter(r => user.roles.includes(r.roleName)).map(r => r.id);
      },
      error: (err) => this.errorMessage = 'Failed to load user'
    });
  }

  hasRole(roleName: string): boolean {
    return this.currentUserRoles.includes(roleName);
  }

  onRoleChange(event: any, roleId: number) {
    if (event.target.checked) {
      if (!this.selectedRoleIds.includes(roleId)) {
        this.selectedRoleIds.push(roleId);
      }
    } else {
      this.selectedRoleIds = this.selectedRoleIds.filter(id => id !== roleId);
    }
  }

  onSubmit() {
    if (this.form.invalid) return;

    const val = this.form.getRawValue();
    const req = {
      ...val,
      roleIds: this.selectedRoleIds
    };

    const request$ = this.isEdit && this.userId
      ? this.userService.updateUser(this.userId, req)
      : this.userService.createUser(req);

    request$.subscribe({
      next: () => this.router.navigate(['/users']),
      error: (err) => this.errorMessage = err.error?.message || 'Failed to save user'
    });
  }
}
