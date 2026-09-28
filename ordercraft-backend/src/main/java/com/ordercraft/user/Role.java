@Entity @Table(name = "oc_roles") @Getter @Setter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roles_gen")
    @SequenceGenerator(name = "roles_gen", sequenceName = "oc_roles_seq", allocationSize = 1)
    private Long id;
    @Column(name = "role_name") private String roleName;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "oc_role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions = new HashSet<>();
}