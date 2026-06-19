package com.gov.rwanda.erp.payroll.exception;

import org.springframework.dao.DataIntegrityViolationException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DatabaseErrorMessageResolver {

    private static final Pattern DUPLICATE_KEY_FIELD =
            Pattern.compile("Key \\(([^)]+)\\)=\\(([^)]*)\\) already exists");

    private DatabaseErrorMessageResolver() {
    }

    public static String resolve(DataIntegrityViolationException ex) {
        String message = ex.getMostSpecificCause() != null
                ? ex.getMostSpecificCause().getMessage()
                : ex.getMessage();

        if (message == null) {
            return "A database constraint was violated. Please check your input.";
        }

        if (message.contains("duplicate key value violates unique constraint")) {
            return resolveDuplicateKey(message);
        }

        if (message.contains("violates foreign key constraint")) {
            return "Referenced record does not exist or cannot be deleted because it is still in use.";
        }

        if (message.contains("violates not-null constraint")) {
            return "A required field is missing.";
        }

        return "A database constraint was violated. Please check your input.";
    }

    private static String resolveDuplicateKey(String message) {
        Matcher matcher = DUPLICATE_KEY_FIELD.matcher(message);
        if (matcher.find()) {
            String field = matcher.group(1);
            String value = matcher.group(2);

            return switch (field) {
                case "username" -> "Username already exists: " + value;
                case "employee_id" -> "Employee already has a linked user account (employee id: " + value + ").";
                case "email" -> "Email already exists: " + value;
                case "emp_code" -> "Employee ID already exists: " + value;
                case "name" -> "Deduction name already exists: " + value;
                default -> "Duplicate value for '" + field + "': " + value;
            };
        }

        if (message.contains("(employee_id, month, year)")) {
            return "Payroll already exists for this employee in the selected month and year.";
        }

        return "A record with the same unique value already exists.";
    }
}
