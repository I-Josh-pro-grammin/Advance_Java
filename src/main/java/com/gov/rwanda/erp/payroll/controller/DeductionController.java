package com.gov.rwanda.erp.payroll.controller;

import com.gov.rwanda.erp.payroll.dto.DeductionRequest;
import com.gov.rwanda.erp.payroll.dto.DeductionResponse;
import com.gov.rwanda.erp.payroll.service.DeductionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deductions")
@RequiredArgsConstructor
@Tag(name = "Deductions & Allowances Configuration", description = "Endpoints for configuring dynamic taxes, pension rates, medical insurances, house and transport allowances")
public class DeductionController {

    private final DeductionService deductionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create Deduction/Allowance Config", description = "Registers a new deduction or allowance percentage configuration.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Configuration created successfully"),
            @ApiResponse(responseCode = "400", description = "Duplicate configuration name or invalid payload parameters")
    })
    public DeductionResponse create(@Valid @RequestBody DeductionRequest request) {
        return deductionService.create(request);
    }

    @GetMapping
    @Operation(summary = "Get All Configurations", description = "Retrieves all registered deduction and allowance configurations (both active and inactive).")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    public List<DeductionResponse> findAll() {
        return deductionService.findAll();
    }

    @GetMapping("/active")
    @Operation(summary = "Get Active Configurations", description = "Retrieves only the active deduction and allowance configurations currently used for calculations.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved active list")
    public List<DeductionResponse> findActive() {
        return deductionService.findActive();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Configuration by ID", description = "Retrieves details of a deduction/allowance configuration by its database ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved configuration details"),
            @ApiResponse(responseCode = "404", description = "Configuration not found")
    })
    public DeductionResponse findById(@PathVariable Long id) {
        return deductionService.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Configuration", description = "Updates the details (name, percentage, category) of an existing configuration.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuration updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or name conflict"),
            @ApiResponse(responseCode = "404", description = "Configuration not found")
    })
    public DeductionResponse update(@PathVariable Long id, @Valid @RequestBody DeductionRequest request) {
        return deductionService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deactivate Configuration", description = "Soft-deletes/deactivates a configuration so it won't be used in future payroll generations.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Configuration deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Configuration not found")
    })
    public void deactivate(@PathVariable Long id) {
        deductionService.deactivate(id);
    }
}
