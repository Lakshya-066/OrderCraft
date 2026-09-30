package com.ordercraft.user.controller;

import com.ordercraft.common.dto.ApiResponse;
import com.ordercraft.user.dto.PermissionResponse;
import com.ordercraft.user.dto.RoleRequest;
import com.ordercraft.user.dto.RoleResponse;
import com.ordercraft.user.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> listRoles() {
        return ResponseEntity.ok(ApiResponse.success(roleService.listRoles()));
    }

    @PostMapping("/roles")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(@Valid @RequestBody RoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(roleService.createRole(request), "Role created successfully"));
    }

    @PutMapping("/roles/{id}")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    public ResponseEntity<ApiResponse<RoleResponse>> updateRole(@PathVariable Long id, @Valid @RequestBody RoleRequest request) {
        return ResponseEntity.ok(ApiResponse.success(roleService.updateRole(id, request), "Role updated successfully"));
    }

    @DeleteMapping("/roles/{id}")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Role deleted successfully"));
    }

    @GetMapping("/permissions")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> listPermissions() {
        return ResponseEntity.ok(ApiResponse.success(roleService.listPermissions()));
    }
}
