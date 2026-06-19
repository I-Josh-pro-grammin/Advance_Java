package com.gov.rwanda.erp.payroll.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Ensures Employee table has required payroll columns before seeders and sync runners execute.
 * Prevents startup failure when ddl-auto=update does not alter existing tables.
 */
@Component
@Order(0)
@RequiredArgsConstructor
@Slf4j
public class SchemaMigrationRunner implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        jdbcTemplate.execute("ALTER TABLE employees ADD COLUMN IF NOT EXISTS base_salary NUMERIC(15,2)");
        jdbcTemplate.execute("ALTER TABLE employees ADD COLUMN IF NOT EXISTS status VARCHAR(255)");
        jdbcTemplate.execute("ALTER TABLE employees ADD COLUMN IF NOT EXISTS names VARCHAR(255)");

        jdbcTemplate.update("""
                UPDATE employees e
                SET base_salary = emp.salary,
                    status = emp.status,
                    names = COALESCE(NULLIF(TRIM(e.names), ''), e.first_name || ' ' || e.last_name)
                FROM employment emp
                WHERE emp.employee_id = e.id
                  AND (e.base_salary IS NULL OR e.status IS NULL OR e.names IS NULL)
                """);

        jdbcTemplate.execute("ALTER TABLE employees ALTER COLUMN base_salary SET NOT NULL");
        jdbcTemplate.execute("ALTER TABLE employees ALTER COLUMN status SET NOT NULL");
        jdbcTemplate.execute("ALTER TABLE employees ALTER COLUMN names SET NOT NULL");

        log.info("Employee schema columns verified (base_salary, status, names)");
    }
}
