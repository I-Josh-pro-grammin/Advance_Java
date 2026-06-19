package com.gov.rwanda.erp.payroll.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request body payload for generating or approving payroll for a specific calendar period")
public class PayrollRunRequest {

    @NotNull
    @Min(1)
    @Max(12)
    @Schema(description = "Calendar month number (1 to 12)", example = "6")
    private Integer month;

    @NotNull
    @Min(2000)
    @Schema(description = "Calendar year (minimum 2000)", example = "2026")
    private Integer year;
}
