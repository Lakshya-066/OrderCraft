import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { RoleService } from '../../core/services/role.service';
import { PermissionResponse } from '../../core/models/user.model';

@Component({
  selector: 'app-role-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  template: `
    <div style="padding: 20px; max-width: 600px;">
      <h2>{{ isEdit ? 'Edit Role' : 'Create Role' }}</h2>

      <div *ngIf="errorMessage" style="color: red; margin-bottom: 10px;">{{ errorMessage }}</div>

      <form [formGroup]="form" (ngSubmit)="onSubmit()">
        <div style="margin-bottom: 15px;">
          <label style="display: block; margin-bottom: 5px;">Role Name</label>
          <input type="text" formControlName="roleName" style="width: 100%; padding: 5px;" />
        </div>

        <div style="margin-bottom: 15px;">
          <label style="display: block; margin-bottom: 5px;">Description</label>
          <textarea formControlName="description" style="width: 100%; padding: 5px; height: 80px;"></textarea>
        </div>

        <div style="margin-bottom: 15px;">
          <label style="display: block; margin-bottom: 5px;">Permissions</label>
          <div *ngFor="let perm of permissions" style="margin-bottom: 3px;">
            <label>
              <input type="checkbox" [value]="perm.id" (change)="onPermissionChange($event, perm.id)" [checked]="hasPermission(perm.id)" />
              {{ perm.permissionKey }} - <small style="color: #666;">{{ perm.description }}</small>
            </label>
          </div>
        </div>

        <div style="margin-top: 20px;">
          <button type="submit" [disabled]="form.invalid" style="padding: 5px 15px; margin-right: 10px;">Save</button>
          <button type="button" routerLink="/roles" style="padding: 5px 15px;">Cancel</button>
        </div>
      </form>
    </div>
  `
})
export class RoleFormComponent implements OnInit {
  form: FormGroup;
  isEdit = false;
  roleId: number | null = null;
  permissions: PermissionResponse[] = [];
  errorMessage = '';
  selectedPermissionIds: number[] = [];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private roleService: RoleService
  ) {
    this.form = this.fb.group({
      roleName: ['', Validators.required],
      description: ['']
    });
  }

  ngOnInit() {
    this.roleId = Number(this.route.snapshot.paramMap.get('id')) || null;
    this.isEdit = !!this.roleId;

    this.roleService.getPermissions().subscribe({
      next: (perms) => {
        this.permissions = perms;
        if (this.isEdit && this.roleId) {
          this.loadRole();
        }
      },
      error: (err) => this.errorMessage = 'Failed to load permissions'
    });
  }

  loadRole() {
    if (!this.roleId) return;
    this.roleService.getRoles().subscribe({
      next: (roles) => {
        const role = roles.find(r => r.id === this.roleId);
        if (role) {
          this.form.patchValue({
            roleName: role.roleName,
            description: role.description
          });
          if (role.isSystem) {
            this.form.get('roleName')?.disable();
          }
          this.selectedPermissionIds = role.permissions?.map(p => p.id) || [];
        } else {
          this.errorMessage = 'Role not found';
        }
      },
      error: (err) => this.errorMessage = 'Failed to load role'
    });
  }

  hasPermission(permId: number): boolean {
    return this.selectedPermissionIds.includes(permId);
  }

  onPermissionChange(event: any, permId: number) {
    if (event.target.checked) {
      if (!this.selectedPermissionIds.includes(permId)) {
        this.selectedPermissionIds.push(permId);
      }
    } else {
      this.selectedPermissionIds = this.selectedPermissionIds.filter(id => id !== permId);
    }
  }

  onSubmit() {
    if (this.form.invalid) return;

    const val = this.form.getRawValue();
    const req = {
      ...val,
      permissionIds: this.selectedPermissionIds
    };

    const request$ = this.isEdit && this.roleId
      ? this.roleService.updateRole(this.roleId, req)
      : this.roleService.createRole(req);

    request$.subscribe({
      next: () => this.router.navigate(['/roles']),
      error: (err) => this.errorMessage = err.error?.message || 'Failed to save role'
    });
  }
}
