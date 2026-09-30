package com.ordercraft.user.service;

import com.ordercraft.common.exception.BusinessRuleException;
import com.ordercraft.common.exception.DuplicateResourceException;
import com.ordercraft.common.exception.EntityNotFoundException;
import com.ordercraft.user.dto.PermissionResponse;
import com.ordercraft.user.dto.RoleRequest;
import com.ordercraft.user.dto.RoleResponse;
import com.ordercraft.user.entity.Permission;
import com.ordercraft.user.entity.Role;
import com.ordercraft.user.repository.PermissionRepository;
import com.ordercraft.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    public List<RoleResponse> listRoles() {
        return roleRepository.findAll().stream()
                .map(this::toRoleResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoleResponse createRole(RoleRequest req) {
        if (roleRepository.existsByRoleName(req.getRoleName())) {
            throw new DuplicateResourceException("Role", "roleName", req.getRoleName());
        }

        List<Permission> permissions = permissionRepository.findAllById(req.getPermissionIds());
        if (permissions.isEmpty() && !req.getPermissionIds().isEmpty()) {
            throw new EntityNotFoundException("Permissions", req.getPermissionIds().toString());
        }

        Role role = Role.builder()
                .roleName(req.getRoleName())
                .description(req.getDescription())
                .isSystem(false)
                .permissions(new HashSet<>(permissions))
                .build();

        Role savedRole = roleRepository.save(role);
        return toRoleResponse(savedRole);
    }

    @Transactional
    public RoleResponse updateRole(Long id, RoleRequest req) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role", id.toString()));

        if (Boolean.TRUE.equals(role.getIsSystem())) {
            throw new BusinessRuleException("System roles cannot be modified");
        }

        if (!role.getRoleName().equals(req.getRoleName()) && roleRepository.existsByRoleName(req.getRoleName())) {
            throw new DuplicateResourceException("Role", "roleName", req.getRoleName());
        }

        List<Permission> permissions = permissionRepository.findAllById(req.getPermissionIds());
        if (permissions.isEmpty() && !req.getPermissionIds().isEmpty()) {
            throw new EntityNotFoundException("Permissions", req.getPermissionIds().toString());
        }

        role.setRoleName(req.getRoleName());
        role.setDescription(req.getDescription());
        role.setPermissions(new HashSet<>(permissions));

        Role savedRole = roleRepository.save(role);
        return toRoleResponse(savedRole);
    }

    @Transactional
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role", id.toString()));

        if (Boolean.TRUE.equals(role.getIsSystem())) {
            throw new BusinessRuleException("System roles cannot be deleted");
        }

        long count = roleRepository.countUsersWithRole(id);
        if (count > 0) {
            throw new BusinessRuleException("Cannot delete role assigned to users");
        }

        roleRepository.delete(role);
    }

    @Transactional(readOnly = true)
    public List<PermissionResponse> listPermissions() {
        return permissionRepository.findAll().stream()
                .map(this::toPermissionResponse)
                .collect(Collectors.toList());
    }

    private RoleResponse toRoleResponse(Role role) {
        List<PermissionResponse> permissions = role.getPermissions().stream()
                .map(this::toPermissionResponse)
                .collect(Collectors.toList());

        return RoleResponse.builder()
                .id(role.getId())
                .roleName(role.getRoleName())
                .description(role.getDescription())
                .isSystem(role.getIsSystem())
                .permissions(permissions)
                .createdAt(role.getCreatedAt())
                .build();
    }

    private PermissionResponse toPermissionResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .permissionKey(permission.getPermissionKey())
                .description(permission.getDescription())
                .build();
    }
}
