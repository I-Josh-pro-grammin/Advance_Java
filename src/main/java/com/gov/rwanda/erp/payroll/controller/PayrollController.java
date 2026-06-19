package com.gov.rwanda.erp.payroll.controller;

import com.gov.rwanda.erp.payroll.dto.PayrollMessageResponse;
import com.gov.rwanda.erp.payroll.dto.PayrollRunRequest;
import com.gov.rwanda.erp.payroll.dto.PayslipResponse;
import com.gov.rwanda.erp.payroll.service.PayrollService;
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
@RequestMapping("/api/payroll")
@RequiredArgsConstructor
@Tag(name = "Payroll & Payslip Management", description = "Endpoints for generating, approving, and viewing payrolls, payslips, and payroll notification messages")
public class PayrollController {

    private final PayrollService payrollService;

    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Generate Payroll", description = "Generates payslips for all active employees for the specified month and year. Inactive employees are excluded.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payroll generated successfully"),
            @ApiResponse(responseCode = "400", description = "Duplicate payroll run or invalid period parameters"),
            @ApiResponse(responseCode = "404", description = "No active employees found to generate payroll")
    })
    public List<PayslipResponse> generatePayroll(@Valid @RequestBody PayrollRunRequest request) {
        return payrollService.generatePayroll(request);
    }

    @PostMapping("/approve")
    @Operation(summary = "Approve Payroll (Admin)", description = "Updates all GENERATED payslips to PAID for the given month and year.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payroll approved — payslip status updated to PAID"),
            @ApiResponse(responseCode = "404", description = "No generated payslips found to approve for the given period")
    })
    public List<PayslipResponse> approvePayroll(@Valid @RequestBody PayrollRunRequest request) {
        return payrollService.approvePayroll(request.getMonth(), request.getYear());
    }

    @GetMapping("/payslips")
    @Operation(summary = "Get Payslips by Period", description = "Retrieves all payslips generated for a specific month and year.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved payslips")
    public List<PayslipResponse> getPayslipsByPeriod(
            @RequestParam int month,
            @RequestParam int year) {
        return payrollService.getPayslipsByPeriod(month, year);
    }

    @GetMapping("/payslips/employee/{employeeId}")
    @Operation(summary = "Get Employee Payslip History", description = "Retrieves the full payroll payslip history of a specific employee by their database ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved payslip history"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public List<PayslipResponse> getEmployeePayslips(@PathVariable Long employeeId) {
        return payrollService.getPayslipsForEmployee(employeeId);
    }

    @GetMapping("/payslips/employee/{employeeId}/{month}/{year}")
    @Operation(summary = "Get Specific Employee Payslip", description = "Retrieves a single payslip for an employee for a specific month and year.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the payslip"),
            @ApiResponse(responseCode = "404", description = "Payslip not found for the specified criteria")
    })
    public PayslipResponse getEmployeePayslip(
            @PathVariable Long employeeId,
            @PathVariable int month,
            @PathVariable int year) {
        return payrollService.getEmployeePayslip(employeeId, month, year);
    }

    @GetMapping("/messages/employee/{employeeId}")
    @Operation(summary = "Get Employee Notification Messages", description = "Retrieves all notifications/messages sent to a specific employee related to their payroll approvals.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved message history")
    public List<PayrollMessageResponse> getEmployeeMessages(@PathVariable Long employeeId) {
        return payrollService.getMessagesForEmployee(employeeId);
    }

    @GetMapping("/messages")
    @Operation(summary = "Get All Notification Messages by Period", description = "Retrieves all notification messages processed in the system for a given month and year.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved period messages")
    public List<PayrollMessageResponse> getMessagesByPeriod(
            @RequestParam int month,
            @RequestParam int year) {
        return payrollService.getMessagesByPeriod(month, year);
    }
}
