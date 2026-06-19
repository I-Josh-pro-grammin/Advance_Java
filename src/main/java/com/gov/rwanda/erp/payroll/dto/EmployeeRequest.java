package com.gov.rwanda.erp.payroll.dto;

import com.gov.rwanda.erp.payroll.enums.EmploymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "Request body payload for creating or updating an employee's personal and professional details")
public class EmployeeRequest {

    @NotBlank
    @Schema(description = "Employee's first name", example = "Peter")
    private String firstName;

    @NotBlank
    @Schema(description = "Employee's last name", example = "Mugisha")
    private String lastName;

    @NotBlank
    @Email
    @Schema(description = "Employee's official unique email address", example = "peter.mugisha@gov.rw")
    private String email;

    @Schema(description = "District where the employee resides", example = "Gasabo")
    private String district;

    @Schema(description = "Active mobile telephone number", example = "+250788000001")
    private String mobile;

    @Schema(description = "Date of birth of the employee", example = "1990-05-15")
    private LocalDate dateOfBirth;

    @NotBlank
    @Schema(description = "Unique professional code / account number assigned to the employee", example = "EMP-001")
    private String employeeId;

    @NotBlank
    @Schema(description = "Department where the employee is deployed", example = "Finance")
    private String department;

    @NotBlank
    @Schema(description = "Professional role / position held", example = "Accountant")
    private String position;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Schema(description = "Base salary of the employee in RWF. Used to compute allowances and deductions.", example = "700000.00")
    private BigDecimal salary;

    @NotNull
    @Schema(description = "Current employment status (ACTIVE, INACTIVE)", example = "ACTIVE")
    private EmploymentStatus status;

    @Schema(description = "Date when the employee officially joined the institution", example = "2018-03-01")
    private LocalDate joiningDate;
}
