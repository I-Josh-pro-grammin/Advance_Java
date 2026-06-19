package com.gov.rwanda.erp.payroll.controller;

import com.gov.rwanda.erp.payroll.dto.AppUserRequest;
import com.gov.rwanda.erp.payroll.service.AppUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "App User Management", description = "Endpoints for managing application users and authentication accounts")
public class AppUserController {

    private final AppUserService appUserService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create User Account", description = "Registers a new system user (ADMIN, MANAGER, or EMPLOYEE) and links them to their employee profile if applicable.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request input or username already taken")
    })
    public Map<String, Object> create(@Valid @RequestBody AppUserRequest request) {
        return appUserService.create(request);
    }

    @GetMapping
    @Operation(summary = "Get All Users", description = "Retrieves a list of all registered system user accounts (passwords are excluded).")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved user list")
    public List<Map<String, Object>> findAll() {
        return appUserService.findAll();
    }
}
