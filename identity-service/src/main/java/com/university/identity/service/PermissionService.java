package com.university.identity.service;

import com.university.identity.dto.PermissionDto;
import com.university.identity.entity.Permission;
import com.university.identity.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public List<PermissionDto> getAllPermissions() {
        return permissionRepository.findAll().stream().map(this::toDto).toList();
    }

    public PermissionDto getPermissionById(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + id));
        return toDto(permission);
    }

    @Transactional
    public PermissionDto createPermission(PermissionDto dto) {
        if (permissionRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Permission already exists: " + dto.getName());
        }
        Permission permission = new Permission();
        permission.setName(dto.getName());
        permission.setDescription(dto.getDescription());
        return toDto(permissionRepository.save(permission));
    }

    @Transactional
    public PermissionDto updatePermission(Long id, PermissionDto dto) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + id));
        permission.setName(dto.getName());
        permission.setDescription(dto.getDescription());
        return toDto(permissionRepository.save(permission));
    }

    @Transactional
    public void deletePermission(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new RuntimeException("Permission not found with id: " + id);
        }
        permissionRepository.deleteById(id);
    }

    private PermissionDto toDto(Permission permission) {
        PermissionDto dto = new PermissionDto();
        dto.setId(permission.getId());
        dto.setName(permission.getName());
        dto.setDescription(permission.getDescription());
        return dto;
    }
}
