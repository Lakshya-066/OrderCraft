@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private final TokenBlacklistRepository repo;

    public void blacklist(String token, Instant expiry) {
        TokenBlacklist t = new TokenBlacklist();
        t.setTokenHash(hash(token));
        t.setExpiryDate(expiry);
        repo.save(t);
    }

    public boolean isBlacklisted(String token) {
        return repo.existsByTokenHash(hash(token));
    }

    @Scheduled(cron = "0 0 * * * *")   // hourly purge; add @EnableScheduling on main class
    @Transactional
    public void purgeExpired() {
        repo.deleteExpired(Instant.now());
    }

    private static String hash(String token) {
        try {
            byte[] d = MessageDigest.getInstance("SHA-256")
                .digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(d);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}