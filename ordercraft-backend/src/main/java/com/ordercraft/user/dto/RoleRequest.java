package com.ordercraft.user.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequest {
    @NotBlank @Size(max = 50)
    private String roleName;
    
    @Size(max = 255)
    private String description;
    
    @NotEmpty
    private Set<Long> permissionIds;
}
