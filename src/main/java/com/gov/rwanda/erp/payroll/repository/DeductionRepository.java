package com.gov.rwanda.erp.payroll.repository;

import com.gov.rwanda.erp.payroll.entity.Deduction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeductionRepository extends JpaRepository<Deduction, Long> {
    Optional<Deduction> findByNameIgnoreCase(String name);
    List<Deduction> findByActiveTrue();
}
