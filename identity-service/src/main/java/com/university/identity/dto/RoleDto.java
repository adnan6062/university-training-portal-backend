package com.university.identity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.Set;

@Data
public class RoleDto {
    private Long id;
    @NotBlank
    private String name;
    private Set<String> permissions;
}
