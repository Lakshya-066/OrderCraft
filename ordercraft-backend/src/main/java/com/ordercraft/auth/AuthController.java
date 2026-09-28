@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(new ApiResponse<>(authService.login(req), "Login successful"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String header) {
        return ResponseEntity.ok(new ApiResponse<>(
            authService.refresh(header.substring(7)), "Token refreshed"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String header) {
        authService.logout(header.substring(7));
        return ResponseEntity.ok(new ApiResponse<>(null, "Logged out"));
    }

    @PostMapping("/reset-password")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public ResponseEntity<ApiResponse<ResetPasswordResponse>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest req) {
        return ResponseEntity.ok(new ApiResponse<>(
            authService.resetPassword(req.userId()), "Password reset. User must change it on next login."));
    }
}