package com.gov.rwanda.erp.payroll.service;

import com.gov.rwanda.erp.payroll.dto.AppUserRequest;
import com.gov.rwanda.erp.payroll.entity.AppUser;
import com.gov.rwanda.erp.payroll.entity.Employee;
import com.gov.rwanda.erp.payroll.enums.UserRole;
import com.gov.rwanda.erp.payroll.exception.DuplicateResourceException;
import com.gov.rwanda.erp.payroll.exception.ResourceNotFoundException;
import com.gov.rwanda.erp.payroll.repository.AppUserRepository;
import com.gov.rwanda.erp.payroll.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public Map<String, Object> create(AppUserRequest request) {
        appUserRepository.findByUsername(request.getUsername()).ifPresent(u -> {
            throw new DuplicateResourceException("Username already exists: " + request.getUsername());
        });

        if (request.getRole() == UserRole.EMPLOYEE && request.getEmployeeId() == null) {
            throw new IllegalArgumentException("Employee users must be linked to an employee record (employeeId is required).");
        }

        AppUser.AppUserBuilder builder = AppUser.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .role(request.getRole());

        if (request.getEmployeeId() != null) {
            if (appUserRepository.existsByEmployee_Id(request.getEmployeeId())) {
                throw new DuplicateResourceException(
                        "Employee already has a linked user account (employee id: "
                                + request.getEmployeeId() + ").");
            }

            Employee employee = employeeRepository.findById(request.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Employee not found with id: " + request.getEmployeeId()));
            builder.employee(employee);
        }

        AppUser saved = appUserRepository.save(builder.build());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> findAll() {
        return appUserRepository.findAll().stream().map(this::toResponse).toList();
    }

    private Map<String, Object> toResponse(AppUser user) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("role", user.getRole());
        response.put("employeeId", user.getEmployee() != null ? user.getEmployee().getId() : null);
        return response;
    }
}
