package com.gov.rwanda.erp.payroll.service;

import com.gov.rwanda.erp.payroll.dto.PayrollMessageResponse;
import com.gov.rwanda.erp.payroll.dto.PayrollRunRequest;
import com.gov.rwanda.erp.payroll.dto.PayslipResponse;
import com.gov.rwanda.erp.payroll.entity.Payslip;
import com.gov.rwanda.erp.payroll.enums.EmploymentStatus;
import com.gov.rwanda.erp.payroll.enums.PayslipStatus;
import com.gov.rwanda.erp.payroll.exception.DuplicateResourceException;
import com.gov.rwanda.erp.payroll.exception.ResourceNotFoundException;
import com.gov.rwanda.erp.payroll.repository.EmployeeRepository;
import com.gov.rwanda.erp.payroll.repository.PayrollMessageRepository;
import com.gov.rwanda.erp.payroll.repository.PayslipRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * PayrollService — orchestrates payroll generation, approval, messaging, and payslip retrieval.
 * Only ACTIVE employees receive payroll. Messages are created on approval via DB procedure.
 */
@Service
@RequiredArgsConstructor
public class PayrollService {

    private final EmployeeRepository employeeRepository;
    private final PayslipRepository payslipRepository;
    private final PayrollMessageRepository payrollMessageRepository;
    private final PayrollCalculator payrollCalculator;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${app.institution.name}")
    private String institutionName;

    /**
     * Generates payslips for all ACTIVE employees for the given month/year (status = GENERATED).
     * Notification messages are created when admin approves payroll.
     */
    @Transactional
    public List<PayslipResponse> generatePayroll(PayrollRunRequest request) {
        int month = request.getMonth();
        int year = request.getYear();

        var activeEmployees = employeeRepository.findByStatus(EmploymentStatus.ACTIVE);
        if (activeEmployees.isEmpty()) {
            throw new ResourceNotFoundException("No active employees found for payroll generation");
        }

        List<Payslip> generated = new ArrayList<>();
        for (var employee : activeEmployees) {
            if (payslipRepository.existsByEmployeeIdAndMonthAndYear(employee.getId(), month, year)) {
                throw new DuplicateResourceException(
                        "Payroll already exists for employee "
                                + employee.getEmployment().getEmployeeId()
                                + " for " + month + "/" + year);
            }

            Payslip payslip = payrollCalculator.computePayslip(employee, month, year);
            generated.add(payslipRepository.save(payslip));
        }

        return generated.stream().map(this::toResponse).toList();
    }

    /**
     * Admin approves payroll — calls PostgreSQL approve_payroll procedure (CURSOR) which
     * inserts ERP notification messages and updates payslip status to PAID.
     */
    @Transactional
    public List<PayslipResponse> approvePayroll(int month, int year) {
        List<Payslip> payslips = payslipRepository.findByMonthAndYearAndStatus(month, year, PayslipStatus.GENERATED);
        if (payslips.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No generated payslips found for " + month + "/" + year + " to approve");
        }

        entityManager.createNativeQuery("CALL approve_payroll(:month, :year, :institution)")
                .setParameter("month", month)
                .setParameter("year", year)
                .setParameter("institution", institutionName)
                .executeUpdate();

        entityManager.flush();
        entityManager.clear();

        return payslipRepository.findByMonthAndYear(month, year).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PayslipResponse> getPayslipsByPeriod(int month, int year) {
        return payslipRepository.findByMonthAndYear(month, year).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PayslipResponse> getPayslipsForEmployee(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }
        return payslipRepository.findByEmployeeId(employeeId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PayslipResponse getEmployeePayslip(Long employeeId, int month, int year) {
        Payslip payslip = payslipRepository.findByEmployeeIdAndMonthAndYear(employeeId, month, year)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payslip not found for employee " + employeeId + " in " + month + "/" + year));
        return toResponse(payslip);
    }

    @Transactional(readOnly = true)
    public List<PayrollMessageResponse> getMessagesForEmployee(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }
        return payrollMessageRepository.findByEmployeeId(employeeId).stream()
                .map(this::toMessageResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PayrollMessageResponse> getMessagesByPeriod(int month, int year) {
        return payrollMessageRepository.findByMonthAndYear(month, year).stream()
                .map(this::toMessageResponse)
                .toList();
    }

    private PayslipResponse toResponse(Payslip payslip) {
        var employee = payslip.getEmployee();
        var employment = employee.getEmployment();
        return PayslipResponse.builder()
                .id(payslip.getId())
                .empId(employment.getEmployeeId())
                .name(employee.getNames())
                .base(payslip.getBaseSalary())
                .house(payslip.getHouseAllowance())
                .transport(payslip.getTransportAllowance())
                .gross(payslip.getGrossSalary())
                .tax(payslip.getEmployeeTax())
                .pension(payslip.getPension())
                .medical(payslip.getMedicalInsurance())
                .other(payslip.getOthers())
                .netSalary(payslip.getNetSalary())
                .status(payslip.getStatus())
                .month(payslip.getMonth())
                .year(payslip.getYear())
                .build();
    }

    private PayrollMessageResponse toMessageResponse(com.gov.rwanda.erp.payroll.entity.PayrollMessage message) {
        var employee = message.getEmployee();
        return PayrollMessageResponse.builder()
                .id(message.getId())
                .employeeId(employee.getId())
                .employeeName(employee.getNames())
                .text(message.getText())
                .month(message.getMonth())
                .year(message.getYear())
                .dateTime(message.getDateTime())
                .build();
    }
}
