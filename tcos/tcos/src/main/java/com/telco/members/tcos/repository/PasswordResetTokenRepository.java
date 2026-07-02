package com.telco.members.tcos.repository;

import com.telco.members.tcos.model.PasswordResetToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
    Optional<PasswordResetToken> findByToken(String token);

    @Transactional
    void deleteByEmployeeId(String employeeId);
}
