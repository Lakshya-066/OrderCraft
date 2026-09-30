package com.ordercraft.user.controller;

import com.ordercraft.common.dto.ApiResponse;
import com.ordercraft.common.dto.PagedResponse;
import com.ordercraft.user.dto.CreateUserRequest;
import com.ordercraft.user.dto.UpdateProfileRequest;
import com.ordercraft.user.dto.UpdateUserRequest;
import com.ordercraft.user.dto.UserResponse;
import com.ordercraft.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> listUsers(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        
        String[] sortParts = sort.split(",");
        Sort.Direction direction = Sort.Direction.fromString(sortParts[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParts[0]));
        
        Page<UserResponse> result = userService.listUsers(search, pageable);
        return ResponseEntity.ok(PagedResponse.of(result));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(userService.createUser(request), "User created successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateUser(id, request), "User updated successfully"));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public ResponseEntity<ApiResponse<UserResponse>> toggleStatus(@PathVariable Long id, @RequestParam boolean active) {
        return ResponseEntity.ok(ApiResponse.success(userService.toggleUserStatus(id, active), "User status updated"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(userService.getCurrentUserProfile(authentication.getName())));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(Authentication authentication, @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateProfile(authentication.getName(), request), "Profile updated successfully"));
    }
}
