package com.ordercraft.user.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    @NotBlank @Size(min = 3, max = 50)
    private String username;
    
    @NotBlank @Email @Size(max = 100)
    private String email;
    
    @NotBlank @Size(max = 100)
    private String fullName;
    
    @NotBlank @Size(min = 6, max = 100)
    private String password;
    
    @NotEmpty
    private Set<Long> roleIds;
}
