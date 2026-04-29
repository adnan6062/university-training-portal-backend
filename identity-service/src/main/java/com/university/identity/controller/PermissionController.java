package com.university.identity.controller;

import com.university.identity.dto.PermissionDto;
import com.university.identity.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@Tag(name = "Permissions", description = "Permission management endpoints")
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    @Operation(summary = "Get all permissions")
    public ResponseEntity<List<PermissionDto>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get permission by ID")
    public ResponseEntity<PermissionDto> getPermissionById(
            @Parameter(description = "Permission ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(permissionService.getPermissionById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new permission")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PermissionDto> createPermission(@Valid @RequestBody PermissionDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(permissionService.createPermission(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update permission")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PermissionDto> updatePermission(
            @Parameter(description = "Permission ID", required = true, example = "1")
            @PathVariable("id") Long id,
            @Valid @RequestBody PermissionDto dto) {
        return ResponseEntity.ok(permissionService.updatePermission(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete permission")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePermission(
            @Parameter(description = "Permission ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        permissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }
}
