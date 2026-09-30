export interface UserResponse {
  id: number;
  username: string;
  email: string;
  fullName: string;
  isActive: boolean;
  roles: string[];
  createdAt: string;
}

export interface CreateUserRequest {
  username: string;
  email: string;
  fullName: string;
  password: string;
  roleIds: number[];
}

export interface UpdateUserRequest {
  email: string;
  fullName: string;
  roleIds: number[];
}

export interface UpdateProfileRequest {
  fullName: string;
  email: string;
  currentPassword?: string;
  newPassword?: string;
}

export interface RoleResponse {
  id: number;
  roleName: string;
  description: string;
  isSystem: boolean;
  permissions: PermissionResponse[];
  createdAt: string;
}

export interface RoleRequest {
  roleName: string;
  description: string;
  permissionIds: number[];
}

export interface PermissionResponse {
  id: number;
  permissionKey: string;
  description: string;
}
