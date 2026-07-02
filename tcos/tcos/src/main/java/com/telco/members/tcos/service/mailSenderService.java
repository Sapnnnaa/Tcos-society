package com.telco.members.tcos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class mailSenderService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordResetMail(String toEmail, String employeeId, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Password Reset Request [TCOS]");
        message.setText(
                "Hello " + employeeId + ",\n\n" +
                        "We received a request to reset your password. " +
                        "Please click the link below to reset it:\n\n" +
                        resetLink + "\n\n" +
                        "If you did not request this, please ignore this email.\n\n" +
                        "Thanks,\nSigma eSolutions Pvt. Ltd. & TCOS"
        );
        mailSender.send(message);
    }
}
