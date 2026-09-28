@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository users;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        return users.findByUsername(username)
            .map(UserPrincipal::new)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}