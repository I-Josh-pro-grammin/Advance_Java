package com.gov.rwanda.erp.payroll.service;

import com.gov.rwanda.erp.payroll.entity.Employee;
import com.gov.rwanda.erp.payroll.entity.Payslip;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * PayrollCalculator — applies Rwanda ERP salary formulas:
 * Gross = Base + 14% House + 14% Transport
 * Net  = Gross - (45% of Gross)
 */
@Component
public class PayrollCalculator {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;
    private static final BigDecimal GROSS_DEDUCTION_RATE = new BigDecimal("45");
    private static final BigDecimal HOUSE_RATE = new BigDecimal("14");
    private static final BigDecimal TRANSPORT_RATE = new BigDecimal("14");

    /**
     * Computes a payslip for the given employee and payroll period.
     * Uses Employee.baseSalary and the mandated 14% allowance rates.
     */
    public Payslip computePayslip(Employee employee, int month, int year) {
        BigDecimal baseSalary = employee.getBaseSalary();

        // Gross Salary = BaseSalary + (BaseSalary * 0.14) + (BaseSalary * 0.14)
        BigDecimal house = baseSalary.multiply(HOUSE_RATE)
                .divide(BigDecimal.valueOf(100), SCALE, ROUNDING);
        BigDecimal transport = baseSalary.multiply(TRANSPORT_RATE)
                .divide(BigDecimal.valueOf(100), SCALE, ROUNDING);
        BigDecimal gross = baseSalary.add(house).add(transport);

        // Net Salary = GrossSalary - (45% of GrossSalary)
        BigDecimal totalDeductions = gross
                .multiply(GROSS_DEDUCTION_RATE)
                .divide(BigDecimal.valueOf(100), SCALE, ROUNDING);
        BigDecimal netSalary = gross.subtract(totalDeductions);

        // Split total deductions across payslip columns (30:6:5:4 ratio = 45 parts)
        BigDecimal employeeTax = allocateDeduction(totalDeductions, 30);
        BigDecimal pension = allocateDeduction(totalDeductions, 6);
        BigDecimal medical = allocateDeduction(totalDeductions, 5);
        BigDecimal others = allocateDeduction(totalDeductions, 4);

        return Payslip.builder()
                .employee(employee)
                .baseSalary(baseSalary.setScale(SCALE, ROUNDING))
                .houseAllowance(house)
                .transportAllowance(transport)
                .grossSalary(gross.setScale(SCALE, ROUNDING))
                .employeeTax(employeeTax)
                .pension(pension)
                .medicalInsurance(medical)
                .others(others)
                .netSalary(netSalary.setScale(SCALE, ROUNDING))
                .month(month)
                .year(year)
                .build();
    }

    /** Allocates a portion of total deductions by ratio part (e.g. 30 of 45) */
    private BigDecimal allocateDeduction(BigDecimal totalDeductions, int ratioPart) {
        return totalDeductions
                .multiply(BigDecimal.valueOf(ratioPart))
                .divide(BigDecimal.valueOf(45), SCALE, ROUNDING);
    }
}
