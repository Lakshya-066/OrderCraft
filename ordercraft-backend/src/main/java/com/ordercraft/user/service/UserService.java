package com.ordercraft.user.service;

import com.ordercraft.common.exception.BusinessRuleException;
import com.ordercraft.common.exception.DuplicateResourceException;
import com.ordercraft.common.exception.EntityNotFoundException;
import com.ordercraft.user.dto.CreateUserRequest;
import com.ordercraft.user.dto.UpdateProfileRequest;
import com.ordercraft.user.dto.UpdateUserRequest;
import com.ordercraft.user.dto.UserResponse;
import com.ordercraft.user.entity.Role;
import com.ordercraft.user.entity.User;
import com.ordercraft.user.repository.RoleRepository;
import com.ordercraft.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<UserResponse> listUsers(String search, Pageable pageable) {
        Page<User> users;
        if (search == null || search.trim().isEmpty()) {
            users = userRepository.findAll(pageable);
        } else {
            users = userRepository.searchUsers(search, pageable);
        }
        return users.map(this::toUserResponse);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findByIdWithRolesAndPermissions(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id.toString()));
        return toUserResponse(user);
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new DuplicateResourceException("User", "username", req.getUsername());
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new DuplicateResourceException("User", "email", req.getEmail());
        }

        List<Role> roles = roleRepository.findAllById(req.getRoleIds());
        if (roles.isEmpty() && !req.getRoleIds().isEmpty()) {
            throw new EntityNotFoundException("Roles", req.getRoleIds().toString());
        }

        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .fullName(req.getFullName())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .isActive(true)
                .roles(new HashSet<>(roles))
                .build();

        User savedUser = userRepository.save(user);
        return toUserResponse(savedUser);
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest req) {
        User user = userRepository.findByIdWithRolesAndPermissions(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id.toString()));

        if (!user.getEmail().equals(req.getEmail()) && userRepository.existsByEmail(req.getEmail())) {
            throw new DuplicateResourceException("User", "email", req.getEmail());
        }

        List<Role> roles = roleRepository.findAllById(req.getRoleIds());
        if (roles.isEmpty() && !req.getRoleIds().isEmpty()) {
            throw new EntityNotFoundException("Roles", req.getRoleIds().toString());
        }

        user.setEmail(req.getEmail());
        user.setFullName(req.getFullName());
        user.setRoles(new HashSet<>(roles));

        User savedUser = userRepository.save(user);
        return toUserResponse(savedUser);
    }

    @Transactional
    public UserResponse toggleUserStatus(Long id, boolean active) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id.toString()));
        user.setIsActive(active);
        return toUserResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUserProfile(String username) {
        User user = userRepository.findByUsernameWithRolesAndPermissions(username)
                .orElseThrow(() -> new EntityNotFoundException("User", username));
        return toUserResponse(user);
    }

    @Transactional
    public UserResponse updateProfile(String username, UpdateProfileRequest req) {
        User user = userRepository.findByUsernameWithRolesAndPermissions(username)
                .orElseThrow(() -> new EntityNotFoundException("User", username));

        if (!user.getEmail().equals(req.getEmail()) && userRepository.existsByEmail(req.getEmail())) {
            throw new DuplicateResourceException("User", "email", req.getEmail());
        }

        if (req.getNewPassword() != null && !req.getNewPassword().trim().isEmpty()) {
            if (req.getCurrentPassword() == null || req.getCurrentPassword().trim().isEmpty()) {
                throw new BusinessRuleException("Current password is required to set a new password");
            }
            if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPasswordHash())) {
                throw new BusinessRuleException("Invalid current password");
            }
            user.setPasswordHash(passwordEncoder.encode(req.getNewPassword()));
        }

        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());

        User savedUser = userRepository.save(user);
        return toUserResponse(savedUser);
    }

    private UserResponse toUserResponse(User user) {
        List<String> roleNames = user.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .isActive(user.getIsActive())
                .roles(roleNames)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
