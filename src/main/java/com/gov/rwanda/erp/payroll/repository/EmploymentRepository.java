package com.gov.rwanda.erp.payroll.repository;

import com.gov.rwanda.erp.payroll.entity.Employment;
import com.gov.rwanda.erp.payroll.enums.EmploymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmploymentRepository extends JpaRepository<Employment, Long> {
    Optional<Employment> findByEmployeeId(String employeeId);
    List<Employment> findByStatus(EmploymentStatus status);
    boolean existsByEmployeeId(String employeeId);
}
