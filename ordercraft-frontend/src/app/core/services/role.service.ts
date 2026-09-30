import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { RoleResponse, RoleRequest, PermissionResponse } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class RoleService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getRoles(): Observable<RoleResponse[]> {
    return this.http.get<ApiResponse<RoleResponse[]>>(`${this.apiUrl}/roles`).pipe(map(r => r.data!));
  }

  createRole(req: RoleRequest): Observable<RoleResponse> {
    return this.http.post<ApiResponse<RoleResponse>>(`${this.apiUrl}/roles`, req).pipe(map(r => r.data!));
  }

  updateRole(id: number, req: RoleRequest): Observable<RoleResponse> {
    return this.http.put<ApiResponse<RoleResponse>>(`${this.apiUrl}/roles/${id}`, req).pipe(map(r => r.data!));
  }

  deleteRole(id: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/roles/${id}`).pipe(map(() => void 0));
  }

  getPermissions(): Observable<PermissionResponse[]> {
    return this.http.get<ApiResponse<PermissionResponse[]>>(`${this.apiUrl}/permissions`).pipe(map(r => r.data!));
  }
}
