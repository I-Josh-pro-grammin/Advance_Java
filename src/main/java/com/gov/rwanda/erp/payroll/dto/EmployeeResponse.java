package com.gov.rwanda.erp.payroll.dto;

import com.gov.rwanda.erp.payroll.enums.EmploymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class EmployeeResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String names;
    private String email;
    private String district;
    private String mobile;
    private LocalDate dateOfBirth;
    private String employeeId;
    private String department;
    private String position;
    private BigDecimal salary;
    private EmploymentStatus status;
    private LocalDate joiningDate;
}
