package com.gov.rwanda.erp.payroll.config;

import com.gov.rwanda.erp.payroll.entity.Employee;
import com.gov.rwanda.erp.payroll.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Syncs baseSalary, status, and names from Employment/firstName+lastName for existing database rows.
 */
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class EmployeeDataSyncRunner implements ApplicationRunner {

    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        for (Employee employee : employeeRepository.findAll()) {
            if (employee.getEmployment() == null) {
                continue;
            }
            boolean changed = false;
            if (employee.getBaseSalary() == null) {
                employee.setBaseSalary(employee.getEmployment().getSalary());
                changed = true;
            }
            if (employee.getStatus() == null) {
                employee.setStatus(employee.getEmployment().getStatus());
                changed = true;
            }
            if (employee.getNames() == null || employee.getNames().isBlank()) {
                employee.setNames(employee.getFirstName() + " " + employee.getLastName());
                changed = true;
            }
            if (changed) {
                employeeRepository.save(employee);
                log.info("Synced employee fields for employee id {}", employee.getId());
            }
        }
    }
}
