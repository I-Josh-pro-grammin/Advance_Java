-- Trigger function: fires ERP notification when a payslip is inserted with PAID status
CREATE OR REPLACE FUNCTION notify_payslip_insert()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $func$
DECLARE
    v_first_name TEXT;
    v_emp_code TEXT;
    v_message TEXT;
    v_month_year TEXT;
    v_institution TEXT := 'Government of Rwanda ERP';
BEGIN
    IF NEW.status = 'PAID' THEN
        SELECT e.first_name, emp.emp_code
        INTO v_first_name, v_emp_code
        FROM employees e
        JOIN employment emp ON emp.employee_id = e.id
        WHERE e.id = NEW.employee_id;

        v_month_year := NEW.month::TEXT || '/' || NEW.year::TEXT;

        v_message := 'Dear ' || v_first_name
            || ', Your salary of ' || v_month_year
            || ' from ' || v_institution || ' ' || NEW.net_salary
            || ' has been credited to your ' || v_emp_code || ' account Successfully.';

        INSERT INTO payroll_messages (employee_id, payslip_id, message_content, month, year, created_at)
        VALUES (NEW.employee_id, NEW.id, v_message, NEW.month, NEW.year, NOW());
    END IF;

    RETURN NEW;
END;
$func$;

DROP TRIGGER IF EXISTS trg_payslip_insert_message ON payslips;

CREATE TRIGGER trg_payslip_insert_message
    AFTER INSERT ON payslips
    FOR EACH ROW
    EXECUTE FUNCTION notify_payslip_insert();
