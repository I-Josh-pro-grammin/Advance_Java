package com.gov.rwanda.erp.payroll.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Installs PostgreSQL procedure (with CURSOR) and trigger for payroll approval messaging.
 * Executes each SQL script as a single block so $$ delimiters are preserved.
 */
@Component
@Order(50)
@RequiredArgsConstructor
@Slf4j
public class DatabaseRoutineInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        installScript("db/procedure_approve_payroll.sql");
        installScript("db/trigger_payslip_message.sql");
        verifyInstallation();
    }

    private void installScript(String classpathResource) {
        try {
            String sql = StreamUtils.copyToString(
                    new ClassPathResource(classpathResource).getInputStream(),
                    StandardCharsets.UTF_8);

            jdbcTemplate.execute((Connection connection) -> {
                try (Statement statement = connection.createStatement()) {
                    statement.execute(sql);
                }
                return null;
            });

            log.info("Installed database script: {}", classpathResource);
        } catch (Exception ex) {
            log.error("Failed to install database script {}: {}", classpathResource, ex.getMessage(), ex);
        }
    }

    private void verifyInstallation() {
        Integer procedures = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM pg_proc WHERE proname = 'approve_payroll'", Integer.class);
        Integer triggers = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM pg_trigger WHERE tgname = 'trg_payslip_insert_message'", Integer.class);

        if (procedures != null && procedures > 0 && triggers != null && triggers > 0) {
            log.info("Database routines verified: approve_payroll procedure + trg_payslip_insert_message trigger");
        } else {
            log.error("Database routine verification failed (procedures={}, triggers={})", procedures, triggers);
        }
    }
}
