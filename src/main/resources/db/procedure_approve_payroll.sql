-- Procedure: cursor iterates GENERATED payslips, inserts ERP notification message, sets PAID
CREATE OR REPLACE PROCEDURE approve_payroll(p_month INT, p_year INT, p_institution TEXT)
LANGUAGE plpgsql
AS $proc$
DECLARE
    payslip_cursor CURSOR FOR
        SELECT p.id,
               p.employee_id,
               p.net_salary,
               p.month,
               p.year,
               e.first_name,
               emp.emp_code
        FROM payslips p
        JOIN employees e ON e.id = p.employee_id
        JOIN employment emp ON emp.employee_id = e.id
        WHERE p.month = p_month
          AND p.year = p_year
          AND p.status = 'GENERATED';

    rec RECORD;
    v_message TEXT;
    v_month_year TEXT;
BEGIN
    FOR rec IN payslip_cursor LOOP
        v_month_year := rec.month::TEXT || '/' || rec.year::TEXT;

        v_message := 'Dear ' || rec.first_name
            || ', Your salary of ' || v_month_year
            || ' from ' || p_institution || ' ' || rec.net_salary
            || ' has been credited to your ' || rec.emp_code || ' account Successfully.';

        INSERT INTO payroll_messages (employee_id, payslip_id, message_content, month, year, created_at)
        VALUES (rec.employee_id, rec.id, v_message, rec.month, rec.year, NOW());

        UPDATE payslips SET status = 'PAID' WHERE id = rec.id;
    END LOOP;
END;
$proc$;
