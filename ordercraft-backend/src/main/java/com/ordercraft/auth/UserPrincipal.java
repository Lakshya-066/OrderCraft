public class UserPrincipal implements UserDetails {
    @Getter private final Long id;
    @Getter private final String fullName;
    @Getter private final String email;
    @Getter private final boolean mustChangePassword;
    private final String username, password;
    private final boolean active;
    private final Set<GrantedAuthority> authorities;

    public UserPrincipal(User u) {
        this.id = u.getId();
        this.username = u.getUsername();
        this.password = u.getPasswordHash();
        this.fullName = u.getFullName();
        this.email = u.getEmail();
        this.active = u.isActive();
        this.mustChangePassword = u.isMustChangePassword();
        this.authorities = u.getRoles().stream()
            .flatMap(r -> r.getPermissions().stream())
            .map(p -> new SimpleGrantedAuthority(p.getPermissionKey()))
            .collect(Collectors.toSet());
    }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return username; }
    @Override public boolean isEnabled() { return active; }   // inactive users cannot log in
}