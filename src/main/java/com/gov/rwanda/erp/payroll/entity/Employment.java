package com.gov.rwanda.erp.payroll.entity;

import com.gov.rwanda.erp.payroll.enums.EmploymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Employment entity — professional details linked one-to-one with Employee.
 * Salary and status are mirrored on Employee for payroll queries.
 */
@Entity
@Table(name = "employment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false, unique = true)
    private Employee employee;

    /** Business employee code e.g. EMP-001 */
    @Column(name = "emp_code", nullable = false, unique = true)
    private String employeeId;

    private String department;

    private String position;

    /** Base salary — kept in sync with Employee.baseSalary */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal salary;

    /** Status — kept in sync with Employee.status */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentStatus status;

    private LocalDate joiningDate;
}
