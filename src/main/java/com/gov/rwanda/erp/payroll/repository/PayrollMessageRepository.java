package com.gov.rwanda.erp.payroll.repository;

import com.gov.rwanda.erp.payroll.entity.PayrollMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayrollMessageRepository extends JpaRepository<PayrollMessage, Long> {
    List<PayrollMessage> findByEmployeeId(Long employeeId);
    List<PayrollMessage> findByMonthAndYear(int month, int year);
}
