@Entity @Table(name = "oc_permissions") @Getter @Setter
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "perm_gen")
    @SequenceGenerator(name = "perm_gen", sequenceName = "oc_permissions_seq", allocationSize = 1)
    private Long id;
    @Column(name = "permission_key") private String permissionKey;
}