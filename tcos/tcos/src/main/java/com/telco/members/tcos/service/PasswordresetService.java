package com.telco.members.tcos.service;

import com.telco.members.tcos.model.PasswordResetToken;
import com.telco.members.tcos.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PasswordresetService {

    @Autowired
    private PasswordResetTokenRepository passreset;

    public void deleteByEmployeeId(String employeeId) {
        passreset.deleteByEmployeeId(employeeId);
    }

    public void saveit(PasswordResetToken resetToken) {
        passreset.save(resetToken);
    }

    public Optional<PasswordResetToken> findByToken(String token) {
        return passreset.findByToken(token);
    }

    public void delete(PasswordResetToken passwordResetToken) {
        passreset.delete(passwordResetToken);
    }
}
