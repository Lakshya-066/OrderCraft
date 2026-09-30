package com.ordercraft.user.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    @NotBlank @Size(max = 100)
    private String fullName;
    
    @NotBlank @Email @Size(max = 100)
    private String email;
    
    private String currentPassword;  // required only if changing password
    
    @Size(min = 6, max = 100)
    private String newPassword;      // optional
}
