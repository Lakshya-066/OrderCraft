@Entity @Table(name = "oc_users")
@Getter @Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_gen")
    @SequenceGenerator(name = "users_gen", sequenceName = "oc_users_seq", allocationSize = 1)
    private Long id;

    private String username;
    private String email;
    @Column(name = "full_name") private String fullName;
    @Column(name = "password_hash") private String passwordHash;
    @Column(name = "is_active") private boolean active = true;
    @Column(name = "must_change_password") private boolean mustChangePassword;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "oc_user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
}