package com.gov.rwanda.erp.payroll.dto;

import com.gov.rwanda.erp.payroll.enums.PayslipStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PayslipResponse {
    private Long id;
    private String empId;
    private String name;
    private BigDecimal base;
    private BigDecimal house;
    private BigDecimal transport;
    private BigDecimal gross;
    private BigDecimal tax;
    private BigDecimal pension;
    private BigDecimal medical;
    private BigDecimal other;
    private BigDecimal netSalary;
    private PayslipStatus status;
    private int month;
    private int year;
}
