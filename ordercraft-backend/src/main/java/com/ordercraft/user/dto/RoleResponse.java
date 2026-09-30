package com.ordercraft.user.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {
    private Long id;
    private String roleName;
    private String description;
    private Boolean isSystem;
    private List<PermissionResponse> permissions;
    private LocalDateTime createdAt;
}
