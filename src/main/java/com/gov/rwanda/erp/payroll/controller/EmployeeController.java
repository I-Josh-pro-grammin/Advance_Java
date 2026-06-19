package com.gov.rwanda.erp.payroll.controller;

import com.gov.rwanda.erp.payroll.dto.EmployeeRequest;
import com.gov.rwanda.erp.payroll.dto.EmployeeResponse;
import com.gov.rwanda.erp.payroll.service.EmployeeService;
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
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Tag(name = "Employee Management", description = "Endpoints for managing employee details and professional status")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create an Employee", description = "Registers a new employee along with their professional employment information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employee created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request input or duplicate resource")
    })
    public EmployeeResponse create(@Valid @RequestBody EmployeeRequest request) {
        return employeeService.create(request);
    }

    @GetMapping
    @Operation(summary = "Get All Employees", description = "Retrieves a list of all registered employees, including active and inactive ones.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the employee list")
    public List<EmployeeResponse> findAll() {
        return employeeService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Employee by DB ID", description = "Retrieves an employee's details using their database primary key.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved employee details"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public EmployeeResponse findById(@PathVariable Long id) {
        return employeeService.findById(id);
    }

    @GetMapping("/by-employee-id/{employeeId}")
    @Operation(summary = "Get Employee by Professional ID", description = "Retrieves an employee's details using their unique professional employee code (e.g. EMP-001).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved employee details"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public EmployeeResponse findByEmployeeId(@PathVariable String employeeId) {
        return employeeService.findByEmployeeId(employeeId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Employee", description = "Updates the personal and professional details of an existing employee by database ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or email already in use"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public EmployeeResponse update(@PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {
        return employeeService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete Employee", description = "Removes an employee and their corresponding employment record from the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Employee deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public void delete(@PathVariable Long id) {
        employeeService.delete(id);
    }
}
