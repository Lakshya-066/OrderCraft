import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, map, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TokenService } from './token.service';
import { AuthResponse, LoginRequest, ResetPasswordRequest, TokenRefreshRequest, UserInfo } from '../models/auth.model';
import { ApiResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  
  public currentUser$ = new BehaviorSubject<UserInfo | null>(null);

  constructor(
    private http: HttpClient,
    private tokenService: TokenService
  ) {
    this.currentUser$.next(this.tokenService.getUser());
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.apiUrl}/login`, request).pipe(
      map(response => response.data!),
      tap(authResponse => {
        this.tokenService.setToken(authResponse.token);
        this.tokenService.setUser(authResponse.user);
        this.currentUser$.next(authResponse.user);
      })
    );
  }

  refreshToken(): Observable<AuthResponse> {
    const currentToken = this.tokenService.getToken();
    const request: TokenRefreshRequest = { token: currentToken || '' };
    return this.http.post<ApiResponse<AuthResponse>>(`${this.apiUrl}/refresh`, request).pipe(
      map(response => response.data!),
      tap(authResponse => {
        this.tokenService.setToken(authResponse.token);
        // The refresh endpoint might or might not return the user info again.
        if (authResponse.user) {
          this.tokenService.setUser(authResponse.user);
          this.currentUser$.next(authResponse.user);
        }
      })
    );
  }

  logout(): Observable<void> {
    const token = this.tokenService.getToken();
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.post<ApiResponse<void>>(`${this.apiUrl}/logout`, {}, { headers }).pipe(
      map(() => void 0),
      tap(() => {
        this.tokenService.clear();
        this.currentUser$.next(null);
      })
    );
  }

  resetPassword(request: ResetPasswordRequest): Observable<void> {
    return this.http.post<ApiResponse<void>>(`${this.apiUrl}/reset-password`, request).pipe(
      map(() => void 0)
    );
  }

  isLoggedIn(): boolean {
    return this.tokenService.isLoggedIn();
  }

  getCurrentUser(): UserInfo | null {
    return this.currentUser$.value;
  }

  hasPermission(permission: string): boolean {
    const user = this.getCurrentUser();
    if (!user || !user.permissions) return false;
    return user.permissions.includes(permission);
  }

  hasAnyPermission(permissions: string[]): boolean {
    if (!permissions || permissions.length === 0) return true;
    const user = this.getCurrentUser();
    if (!user || !user.permissions) return false;
    return permissions.some(p => user.permissions.includes(p));
  }

  hasRole(role: string): boolean {
    const user = this.getCurrentUser();
    if (!user || !user.roles) return false;
    return user.roles.includes(role);
  }
}
