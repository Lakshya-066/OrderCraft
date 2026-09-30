package com.ordercraft.user.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private Boolean isActive;
    private List<String> roles;
    private LocalDateTime createdAt;
}
