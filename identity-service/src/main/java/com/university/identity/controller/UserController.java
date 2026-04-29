package com.university.identity.controller;

import com.university.identity.dto.UserDto;
import com.university.identity.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management endpoints")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserDto> getUserById(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Get user by username")
    public ResponseEntity<UserDto> getUserByUsername(
            @Parameter(description = "Username", required = true, example = "johndoe")
            @PathVariable("username") String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user")
    public ResponseEntity<UserDto> updateUser(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable("id") Long id,
            @RequestBody UserDto dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Change user status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> changeStatus(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable("id") Long id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(userService.changeStatus(id, body.get("status")));
    }

    @PostMapping("/{userId}/roles/{roleId}")
    @Operation(summary = "Assign role to user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> assignRole(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable("userId") Long userId,
            @Parameter(description = "Role ID", required = true, example = "1")
            @PathVariable("roleId") Long roleId) {
        return ResponseEntity.ok(userService.assignRole(userId, roleId));
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    @Operation(summary = "Remove role from user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> removeRole(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable("userId") Long userId,
            @Parameter(description = "Role ID", required = true, example = "1")
            @PathVariable("roleId") Long roleId) {
        return ResponseEntity.ok(userService.removeRole(userId, roleId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
