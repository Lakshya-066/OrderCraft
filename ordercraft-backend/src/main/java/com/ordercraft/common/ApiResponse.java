public record LoginRequest(@NotBlank String username, @NotBlank String password) {}

public record UserProfile(Long id, String username, String fullName, String email,
                          boolean mustChangePassword, Set<String> permissions) {
    public static UserProfile from(UserPrincipal p) {
        return new UserProfile(p.getId(), p.getUsername(), p.getFullName(), p.getEmail(),
            p.isMustChangePassword(),
            p.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet()));
    }
}

public record LoginResponse(String token, String tokenType, long expiresInMs, UserProfile user) {}

public record ResetPasswordRequest(@NotNull Long userId) {}
public record ResetPasswordResponse(String temporaryPassword) {}

public record ApiResponse<T>(T data, String message) {}