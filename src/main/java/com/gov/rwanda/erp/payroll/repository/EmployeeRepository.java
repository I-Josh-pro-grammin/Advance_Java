package com.gov.rwanda.erp.payroll.repository;

import com.gov.rwanda.erp.payroll.entity.Employee;
import com.gov.rwanda.erp.payroll.entity.Employee;
import com.gov.rwanda.erp.payroll.enums.EmploymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);
    List<Employee> findByStatus(EmploymentStatus status);
}
