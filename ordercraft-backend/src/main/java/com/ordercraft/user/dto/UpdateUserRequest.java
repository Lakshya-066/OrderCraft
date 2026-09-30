package com.ordercraft.user.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    @NotBlank @Email @Size(max = 100)
    private String email;
    
    @NotBlank @Size(max = 100)
    private String fullName;
    
    @NotEmpty
    private Set<Long> roleIds;
}
