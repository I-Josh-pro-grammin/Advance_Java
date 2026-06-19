package com.gov.rwanda.erp.payroll.service;

import com.gov.rwanda.erp.payroll.dto.*;
import com.gov.rwanda.erp.payroll.entity.*;
import com.gov.rwanda.erp.payroll.exception.DuplicateResourceException;
import com.gov.rwanda.erp.payroll.exception.ResourceNotFoundException;
import com.gov.rwanda.erp.payroll.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * EmployeeService — manages employee registration and keeps baseSalary/status
 * synchronized between Employee and Employment records.
 */
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmploymentRepository employmentRepository;

    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {
        if (employeeRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Employee with email already exists: " + request.getEmail());
        }
        if (employmentRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new DuplicateResourceException("Employee ID already exists: " + request.getEmployeeId());
        }

        Employee employee = Employee.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .names(request.getFirstName() + " " + request.getLastName())
                .email(request.getEmail())
                .district(request.getDistrict())
                .mobile(request.getMobile())
                .dateOfBirth(request.getDateOfBirth())
                .baseSalary(request.getSalary())
                .status(request.getStatus())
                .build();

        Employment employment = Employment.builder()
                .employee(employee)
                .employeeId(request.getEmployeeId())
                .department(request.getDepartment())
                .position(request.getPosition())
                .salary(request.getSalary())
                .status(request.getStatus())
                .joiningDate(request.getJoiningDate())
                .build();

        employee.setEmployment(employment);
        Employee saved = employeeRepository.save(employee);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> findAll() {
        return employeeRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EmployeeResponse findById(Long id) {
        return toResponse(getEmployee(id));
    }

    @Transactional(readOnly = true)
    public EmployeeResponse findByEmployeeId(String employeeId) {
        Employment employment = employmentRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));
        return toResponse(employment.getEmployee());
    }

    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        Employee employee = getEmployee(id);

        employeeRepository.findByEmail(request.getEmail())
                .filter(e -> !e.getId().equals(id))
                .ifPresent(e -> {
                    throw new DuplicateResourceException("Email already in use: " + request.getEmail());
                });

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setNames(request.getFirstName() + " " + request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setDistrict(request.getDistrict());
        employee.setMobile(request.getMobile());
        employee.setDateOfBirth(request.getDateOfBirth());
        employee.setBaseSalary(request.getSalary());
        employee.setStatus(request.getStatus());

        Employment employment = employee.getEmployment();
        if (!employment.getEmployeeId().equals(request.getEmployeeId())
                && employmentRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new DuplicateResourceException("Employee ID already exists: " + request.getEmployeeId());
        }

        employment.setEmployeeId(request.getEmployeeId());
        employment.setDepartment(request.getDepartment());
        employment.setPosition(request.getPosition());
        employment.setSalary(request.getSalary());
        employment.setStatus(request.getStatus());
        employment.setJoiningDate(request.getJoiningDate());

        return toResponse(employeeRepository.save(employee));
    }

    @Transactional
    public void delete(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee not found with id: " + id);
        }
        employeeRepository.deleteById(id);
    }

    private Employee getEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

    private EmployeeResponse toResponse(Employee employee) {
        Employment employment = employee.getEmployment();
        return EmployeeResponse.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .names(employee.getNames())
                .email(employee.getEmail())
                .district(employee.getDistrict())
                .mobile(employee.getMobile())
                .dateOfBirth(employee.getDateOfBirth())
                .employeeId(employment != null ? employment.getEmployeeId() : null)
                .department(employment != null ? employment.getDepartment() : null)
                .position(employment != null ? employment.getPosition() : null)
                .salary(employee.getBaseSalary())
                .status(employee.getStatus())
                .joiningDate(employment != null ? employment.getJoiningDate() : null)
                .build();
    }
}
