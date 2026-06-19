package com.gov.rwanda.erp.payroll.dto;

import com.gov.rwanda.erp.payroll.enums.DeductionCategory;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DeductionResponse {
    private Long id;
    private String name;
    private BigDecimal percentage;
    private DeductionCategory category;
    private boolean active;
}
