export interface LoginRequest {
  username?: string;
  password?: string;
}

export interface UserInfo {
  id: number;
  username: string;
  email: string;
  fullName: string;
  roles: string[];
  permissions: string[];
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  expiresIn: number;
  user: UserInfo;
}

export interface TokenRefreshRequest {
  token: string;
}

export interface ResetPasswordRequest {
  userId: number;
  newPassword: string;
}
