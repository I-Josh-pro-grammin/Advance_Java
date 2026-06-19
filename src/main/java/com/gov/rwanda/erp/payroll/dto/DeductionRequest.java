package com.gov.rwanda.erp.payroll.dto;

import com.gov.rwanda.erp.payroll.enums.DeductionCategory;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DeductionRequest {

    @NotBlank
    private String name;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal percentage;

    @NotNull
    private DeductionCategory category;
}
