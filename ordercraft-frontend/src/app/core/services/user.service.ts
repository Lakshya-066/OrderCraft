import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { UserResponse, CreateUserRequest, UpdateUserRequest, UpdateProfileRequest } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  private apiUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  getUsers(page = 0, size = 20, search = ''): Observable<ApiResponse<UserResponse[]>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (search) params = params.set('search', search);
    return this.http.get<ApiResponse<UserResponse[]>>(this.apiUrl, { params });
  }

  getUser(id: number): Observable<UserResponse> {
    return this.http.get<ApiResponse<UserResponse>>(`${this.apiUrl}/${id}`).pipe(map(r => r.data!));
  }

  createUser(req: CreateUserRequest): Observable<UserResponse> {
    return this.http.post<ApiResponse<UserResponse>>(this.apiUrl, req).pipe(map(r => r.data!));
  }

  updateUser(id: number, req: UpdateUserRequest): Observable<UserResponse> {
    return this.http.put<ApiResponse<UserResponse>>(`${this.apiUrl}/${id}`, req).pipe(map(r => r.data!));
  }

  toggleStatus(id: number, active: boolean): Observable<UserResponse> {
    return this.http.patch<ApiResponse<UserResponse>>(`${this.apiUrl}/${id}/status?active=${active}`, {}).pipe(map(r => r.data!));
  }

  getProfile(): Observable<UserResponse> {
    return this.http.get<ApiResponse<UserResponse>>(`${this.apiUrl}/me`).pipe(map(r => r.data!));
  }

  updateProfile(req: UpdateProfileRequest): Observable<UserResponse> {
    return this.http.put<ApiResponse<UserResponse>>(`${this.apiUrl}/me`, req).pipe(map(r => r.data!));
  }
}
