package com.gov.rwanda.erp.payroll.entity;

import com.gov.rwanda.erp.payroll.enums.EmploymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Employee entity — central record for personal details, base salary, and payroll eligibility.
 * ACTIVE employees are included in payroll; INACTIVE employees are excluded.
 */
@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    /** Primary key for the employee record */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Employee first name (part of full name) */
    @Column(nullable = false)
    private String firstName;

    /** Employee last name (part of full name) */
    @Column(nullable = false)
    private String lastName;

    /** Full employee name used in payslips and notification messages */
    @Column(nullable = false)
    private String names;

    @Column(nullable = false, unique = true)
    private String email;

    private String district;

    private String mobile;

    private LocalDate dateOfBirth;

    /**
     * Base monthly salary in RWF — used to compute House, Transport, Gross, and Net salary.
     * Synced with Employment.salary on create/update.
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal baseSalary;

    /**
     * Employment status — only ACTIVE employees receive payroll.
     * Synced with Employment.status on create/update.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentStatus status;

    /** Professional/employment details (department, position, employee code) */
    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private Employment employment;

    /** One employee may have many payslips (one per month/year) */
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Payslip> payslips = new ArrayList<>();

    /** Notification messages sent to this employee after payroll processing */
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PayrollMessage> messages = new ArrayList<>();

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private AppUser appUser;
}
