package com.university.identity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PermissionDto {
    private Long id;
    @NotBlank
    private String name;
    private String description;
}
