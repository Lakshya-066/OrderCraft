@Entity @Table(name = "oc_token_blacklist") @Getter @Setter
public class TokenBlacklist {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_gen")
    @SequenceGenerator(name = "tb_gen", sequenceName = "oc_token_blacklist_seq", allocationSize = 1)
    private Long id;
    @Column(name = "token_hash") private String tokenHash;
    @Column(name = "expiry_date") private Instant expiryDate;
    @Column(name = "created_at") private Instant createdAt = Instant.now();
}

public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {
    boolean existsByTokenHash(String tokenHash);
    @Modifying
    @Query("delete from TokenBlacklist t where t.expiryDate < :now")
    int deleteExpired(@Param("now") Instant now);
}
