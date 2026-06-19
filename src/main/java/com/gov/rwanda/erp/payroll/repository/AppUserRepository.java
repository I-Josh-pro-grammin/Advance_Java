package com.gov.rwanda.erp.payroll.repository;

import com.gov.rwanda.erp.payroll.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
    boolean existsByEmployee_Id(Long employeeId);
    Optional<AppUser> findByEmployee_Id(Long employeeId);
}
