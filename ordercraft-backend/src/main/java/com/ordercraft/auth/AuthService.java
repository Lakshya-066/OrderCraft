@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final TokenBlacklistService blacklist;
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final UserDetailsServiceImpl userDetailsService;

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String PW_CHARS =
        "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789@#$%";

    /** UC-1.1 */
    public LoginResponse login(LoginRequest req) {
        Authentication auth = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.username(), req.password()));
        UserPrincipal p = (UserPrincipal) auth.getPrincipal();
        return build(p, jwtService.generateToken(p.getUsername()));
    }

    /** UC-1.3: issue a new token for a still-valid one, retire the old one */
    public LoginResponse refresh(String oldToken) {
        Claims claims = jwtService.parse(oldToken);
        UserPrincipal p = (UserPrincipal) userDetailsService.loadUserByUsername(claims.getSubject());
        if (!p.isEnabled()) throw new DisabledException("Account is inactive");
        blacklist.blacklist(oldToken, claims.getExpiration().toInstant());
        return build(p, jwtService.generateToken(p.getUsername()));
    }

    public void logout(String token) {
        Claims claims = jwtService.parse(token);
        blacklist.blacklist(token, claims.getExpiration().toInstant());
    }

    /** UC-1.5: admin-initiated reset, user must change password on next login */
    @Transactional
    public ResetPasswordResponse resetPassword(Long userId) {
        User u = users.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
        String temp = generateTempPassword(12);
        u.setPasswordHash(encoder.encode(temp));
        u.setMustChangePassword(true);
        return new ResetPasswordResponse(temp);
    }

    private LoginResponse build(UserPrincipal p, String token) {
        return new LoginResponse(token, "Bearer", jwtService.getExpirationMs(), UserProfile.from(p));
    }

    private String generateTempPassword(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(PW_CHARS.charAt(RANDOM.nextInt(PW_CHARS.length())));
        return sb.toString();
    }
}