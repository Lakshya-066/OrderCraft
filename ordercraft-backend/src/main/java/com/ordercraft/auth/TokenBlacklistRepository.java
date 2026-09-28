public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {
    boolean existsByTokenHash(String tokenHash);
    @Modifying
    @Query("delete from TokenBlacklist t where t.expiryDate < :now")
    int deleteExpired(@Param("now") Instant now);
}