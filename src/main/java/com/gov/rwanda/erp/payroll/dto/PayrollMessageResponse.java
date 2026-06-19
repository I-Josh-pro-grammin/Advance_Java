package com.gov.rwanda.erp.payroll.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PayrollMessageResponse {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String text;
    private int month;
    private int year;
    private LocalDateTime dateTime;
}
