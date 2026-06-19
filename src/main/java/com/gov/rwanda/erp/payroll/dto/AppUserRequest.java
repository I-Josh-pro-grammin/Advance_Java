package com.gov.rwanda.erp.payroll.dto;

import com.gov.rwanda.erp.payroll.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AppUserRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotNull
    private UserRole role;

    private Long employeeId;
}
