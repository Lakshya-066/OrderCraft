package com.ordercraft.user.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResponse {
    private Long id;
    private String permissionKey;
    private String description;
}
