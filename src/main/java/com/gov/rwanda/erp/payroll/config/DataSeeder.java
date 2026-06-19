package com.gov.rwanda.erp.payroll.config;

import com.gov.rwanda.erp.payroll.entity.AppUser;
import com.gov.rwanda.erp.payroll.entity.Deduction;
import com.gov.rwanda.erp.payroll.entity.Employee;
import com.gov.rwanda.erp.payroll.entity.Employment;
import com.gov.rwanda.erp.payroll.enums.DeductionCategory;
import com.gov.rwanda.erp.payroll.enums.EmploymentStatus;
import com.gov.rwanda.erp.payroll.enums.UserRole;
import com.gov.rwanda.erp.payroll.repository.AppUserRepository;
import com.gov.rwanda.erp.payroll.repository.DeductionRepository;
import com.gov.rwanda.erp.payroll.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Seeds default deductions, sample employees, and system users on first startup */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final DeductionRepository deductionRepository;
    private final EmployeeRepository employeeRepository;
    private final AppUserRepository appUserRepository;

    @Override
    public void run(String... args) {
        seedDeductions();
        seedEmployees();
        seedUsers();
    }

    private void seedDeductions() {
        if (deductionRepository.count() > 0) {
            return;
        }

        createDeduction("EmployeeTax", "30.00", DeductionCategory.DEDUCTION);
        createDeduction("Pension", "6.00", DeductionCategory.DEDUCTION);
        createDeduction("MedicalInsurance", "5.00", DeductionCategory.DEDUCTION);
        createDeduction("Others", "4.00", DeductionCategory.DEDUCTION);
        createDeduction("House", "14.00", DeductionCategory.ALLOWANCE);
        createDeduction("Transport", "14.00", DeductionCategory.ALLOWANCE);

        log.info("Seeded deduction/tax configuration");
    }

    private void createDeduction(String name, String percentage, DeductionCategory category) {
        deductionRepository.save(Deduction.builder()
                .name(name)
                .percentage(new BigDecimal(percentage))
                .category(category)
                .active(true)
                .build());
    }

    private void seedEmployees() {
        if (employeeRepository.count() > 0) {
            return;
        }

        Employee peter = buildEmployee(
                "Peter", "Mugisha", "peter.mugisha@gov.rw", "Gasabo", "+250788000001",
                LocalDate.of(1990, 5, 15), "EMP-001", "Finance", "Accountant",
                new BigDecimal("700000.00"), EmploymentStatus.ACTIVE, LocalDate.of(2018, 3, 1));

        Employee alice = buildEmployee(
                "Alice", "Uwase", "alice.uwase@gov.rw", "Kicukiro", "+250788000002",
                LocalDate.of(1988, 8, 20), "EMP-002", "Human Resources", "HR Officer",
                new BigDecimal("550000.00"), EmploymentStatus.ACTIVE, LocalDate.of(2019, 6, 15));

        Employee inactive = buildEmployee(
                "Jean", "Habimana", "jean.habimana@gov.rw", "Nyarugenge", "+250788000003",
                LocalDate.of(1985, 1, 10), "EMP-003", "IT", "Systems Analyst",
                new BigDecimal("600000.00"), EmploymentStatus.INACTIVE, LocalDate.of(2015, 1, 10));

        employeeRepository.save(peter);
        employeeRepository.save(alice);
        employeeRepository.save(inactive);

        log.info("Seeded sample employees (Peter base salary = 700,000 RWF)");
    }

    private Employee buildEmployee(String firstName, String lastName, String email, String district,
                                   String mobile, LocalDate dob, String employeeId, String department,
                                   String position, BigDecimal salary, EmploymentStatus status,
                                   LocalDate joiningDate) {
        Employee employee = Employee.builder()
                .firstName(firstName)
                .lastName(lastName)
                .names(firstName + " " + lastName)
                .email(email)
                .district(district)
                .mobile(mobile)
                .dateOfBirth(dob)
                .baseSalary(salary)
                .status(status)
                .build();

        Employment employment = Employment.builder()
                .employee(employee)
                .employeeId(employeeId)
                .department(department)
                .position(position)
                .salary(salary)
                .status(status)
                .joiningDate(joiningDate)
                .build();

        employee.setEmployment(employment);
        return employee;
    }

    private void seedUsers() {
        if (appUserRepository.count() > 0) {
            return;
        }

        Employee peter = employeeRepository.findByEmail("peter.mugisha@gov.rw").orElseThrow();

        appUserRepository.save(AppUser.builder()
                .username("admin")
                .password("admin123")
                .role(UserRole.ADMIN)
                .build());

        appUserRepository.save(AppUser.builder()
                .username("manager")
                .password("manager123")
                .role(UserRole.MANAGER)
                .build());

        appUserRepository.save(AppUser.builder()
                .username("peter.mugisha")
                .password("employee123")
                .role(UserRole.EMPLOYEE)
                .employee(peter)
                .build());

        log.info("Seeded users: admin, manager, peter.mugisha");
    }
}
