package com.ordercraft.auth.service;

import com.ordercraft.auth.dto.AuthResponse;
import com.ordercraft.auth.dto.LoginRequest;
import com.ordercraft.auth.dto.UserInfo;
import com.ordercraft.auth.entity.TokenBlacklist;
import com.ordercraft.auth.jwt.JwtTokenProvider;
import com.ordercraft.auth.repository.TokenBlacklistRepository;
import com.ordercraft.common.exception.EntityNotFoundException;
import com.ordercraft.user.entity.User;
import com.ordercraft.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Core authentication service handling login, token refresh, logout, and password reset.
 */
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider,
                       TokenBlacklistRepository tokenBlacklistRepository,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenBlacklistRepository = tokenBlacklistRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authenticate user and return JWT token with user info.
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(userDetails);
        User user = userDetails.getUser();

        return buildAuthResponse(token, user);
    }

    /**
     * Validate existing token, blacklist it, and issue a new one.
     */
    @Transactional
    public AuthResponse refreshToken(String oldToken) {
        if (!jwtTokenProvider.validateToken(oldToken)) {
            throw new BadCredentialsException("Invalid or expired token");
        }

        String username = jwtTokenProvider.getUsernameFromToken(oldToken);

        // Blacklist the old token
        blacklistToken(oldToken);

        // Load user and generate new token
        User user = userRepository.findByUsernameWithRolesAndPermissions(username)
                .orElseThrow(() -> new EntityNotFoundException("User", username));

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String newToken = jwtTokenProvider.generateToken(userDetails);

        return buildAuthResponse(newToken, user);
    }

    /**
     * Invalidate a token by adding its hash to the blacklist.
     */
    @Transactional
    public void logout(String token) {
        blacklistToken(token);
    }

    /**
     * Admin-initiated password reset for a specific user.
     */
    @Transactional
    public void resetPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private AuthResponse buildAuthResponse(String token, User user) {
        List<String> roleNames = user.getRoles().stream()
                .map(role -> role.getRoleName())
                .collect(Collectors.toList());

        List<String> permissions = user.getEffectivePermissions().stream()
                .sorted()
                .collect(Collectors.toList());

        UserInfo userInfo = UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(roleNames)
                .permissions(permissions)
                .build();

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationMs() / 1000)
                .user(userInfo)
                .build();
    }

    private void blacklistToken(String token) {
        String tokenHash = hashToken(token);
        Date expiryDate = jwtTokenProvider.getExpirationDateFromToken(token);
        LocalDateTime expiry = expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        TokenBlacklist blacklistEntry = TokenBlacklist.builder()
                .tokenHash(tokenHash)
                .expiryDate(expiry)
                .build();

        tokenBlacklistRepository.save(blacklistEntry);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
