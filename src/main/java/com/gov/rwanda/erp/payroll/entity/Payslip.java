package com.gov.rwanda.erp.payroll.entity;

import com.gov.rwanda.erp.payroll.enums.PayslipStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Payslip entity — stores computed salary breakdown for one employee in a given month/year.
 * Unique constraint prevents duplicate payroll for the same employee and period.
 */
@Entity
@Table(name = "payslips", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"employee_id", "month", "year"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payslip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Many payslips belong to one employee */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal baseSalary;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal houseAllowance;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal transportAllowance;

    /** Calculated: BaseSalary + House + Transport */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal grossSalary;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal employeeTax;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal pension;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal medicalInsurance;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal others;

    /** Calculated: GrossSalary - (45% of GrossSalary) */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal netSalary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PayslipStatus status = PayslipStatus.GENERATED;

    /** Payroll month (1-12) */
    @Column(nullable = false)
    private int month;

    /** Payroll year e.g. 2026 */
    @Column(nullable = false)
    private int year;
}
