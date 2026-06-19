package com.gov.rwanda.erp.payroll.repository;

import com.gov.rwanda.erp.payroll.entity.Payslip;
import com.gov.rwanda.erp.payroll.enums.PayslipStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PayslipRepository extends JpaRepository<Payslip, Long> {
    boolean existsByEmployeeIdAndMonthAndYear(Long employeeId, int month, int year);
    List<Payslip> findByMonthAndYear(int month, int year);
    List<Payslip> findByEmployeeIdAndMonthAndYear(Long employeeId, int month, int year);
    List<Payslip> findByEmployeeId(Long employeeId);
    List<Payslip> findByMonthAndYearAndStatus(int month, int year, PayslipStatus status);
    Optional<Payslip> findByEmployeeIdAndMonthAndYearAndStatus(Long employeeId, int month, int year, PayslipStatus status);
}
