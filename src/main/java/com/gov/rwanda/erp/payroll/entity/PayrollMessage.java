package com.gov.rwanda.erp.payroll.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * PayrollMessage entity — stores automated notification text sent to an employee
 * when payroll is processed for a given month/year.
 */
@Entity
@Table(name = "payroll_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayrollMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Many messages can belong to one employee */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    /** Optional link to the payslip that triggered this message */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payslip_id")
    private Payslip payslip;

    /** Notification text sent to the employee */
    @Column(name = "message_content", nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(nullable = false)
    private int month;

    @Column(nullable = false)
    private int year;

    /** Timestamp when the message was generated */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime dateTime;
}
