@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository users;
    private final RoleRepository roles;
    private final PermissionRepository permissions;
    private final PasswordEncoder encoder;

    private static final List<String> ALL_PERMS = List.of(
        "USER_MANAGE","ROLE_MANAGE","CUSTOMER_VIEW","CUSTOMER_MANAGE","VENDOR_VIEW","VENDOR_MANAGE",
        "PRODUCT_VIEW","PRODUCT_MANAGE","SO_VIEW","SO_CREATE","SO_EDIT","SO_APPROVE",
        "PO_VIEW","PO_CREATE","PO_APPROVE","INVENTORY_VIEW","INVENTORY_ADJUST",
        "INVOICE_VIEW","INVOICE_CREATE","PAYMENT_VIEW","PAYMENT_RECORD",
        "APPROVAL_CONFIG","DASHBOARD_VIEW","REPORT_EXPORT","CONFIG_MANAGE","AUDIT_VIEW");

    @Override @Transactional
    public void run(String... args) {
        if (users.findByUsername("admin").isPresent()) return;

        Set<Permission> perms = ALL_PERMS.stream().map(k -> {
            Permission p = new Permission(); p.setPermissionKey(k); return permissions.save(p);
        }).collect(Collectors.toSet());

        Role admin = new Role(); admin.setRoleName("ADMIN"); admin.setPermissions(perms);
        roles.save(admin);

        User u = new User();
        u.setUsername("admin"); u.setEmail("admin@ordercraft.local"); u.setFullName("System Admin");
        u.setPasswordHash(encoder.encode("Admin@123"));
        u.setMustChangePassword(true);
        u.getRoles().add(admin);
        users.save(u);
    }
}