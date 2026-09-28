@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    private final TokenBlacklistService blacklist;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        String header = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtService.isValid(token) && !blacklist.isBlacklisted(token)
                    && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    String username = jwtService.parse(token).getSubject();
                    UserDetails ud = userDetailsService.loadUserByUsername(username);
                    if (ud.isEnabled()) {
                        var auth = new UsernamePasswordAuthenticationToken(
                            ud, null, ud.getAuthorities());
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                } catch (UsernameNotFoundException ignored) { /* falls through to 401 */ }
            }
        }
        chain.doFilter(req, res);
    }
}