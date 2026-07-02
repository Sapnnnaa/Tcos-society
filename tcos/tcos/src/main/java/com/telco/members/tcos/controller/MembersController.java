package com.telco.members.tcos.controller;

import com.telco.members.tcos.dto.MembersRegistrationDTO;
import com.telco.members.tcos.model.Members;
import com.telco.members.tcos.model.PasswordResetToken;
import com.telco.members.tcos.service.PasswordresetService;
import com.telco.members.tcos.service.mailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.telco.members.tcos.service.MembersService;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Controller
public class MembersController {

    @Autowired
    private MembersService membersService;
    @Autowired
    private mailSenderService mailSenderService;
    @Autowired
    private PasswordresetService passwordresetService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("members", new MembersRegistrationDTO());
        return "register";
    }

    @PostMapping("/register")
    public String registerUserAccount(@ModelAttribute("members") MembersRegistrationDTO members, Model model) {
        membersService.save(members);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String checkPassword(@RequestParam String mid, @RequestParam String password, Model model, HttpSession session) {
        System.out.println("Login attempt for mid: " + mid);
        boolean isPasswordValid  =membersService.checkPassword(mid, password);
        Members m1 = membersService.findByMid(mid);
        String name = m1.getFirstName()+ " " + m1.getLastName();
        if (isPasswordValid) {
            session.setAttribute("LoginName",name);
            System.out.println("LoginName: " + session.getAttribute("LoginName"));
            return "redirect:/members/index";
        } else {
            model.addAttribute("errorMessage", "Invalid mid or password.");
            return "login";
        }
    }
    
    @GetMapping("/logout")
    public String logoutMembers() {
        return "redirect:/login";
    }
    
    @PostMapping("/logout")
    public String loggedOut() {

    	return "redirect:/login";
    }

    @PostMapping("/forgotpassword")
    public String forgotPasswordPage(@RequestParam("employeeId") String employeeId, RedirectAttributes redirectAttributes) {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Employee ID is required.");
            return "redirect:/login";
        }
        String emailId = membersService.findEmailId(employeeId);
        if (emailId.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Employee ID is not correct.");
        }
        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(30);

        passwordresetService.deleteByEmployeeId(employeeId);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setEmployeeId(employeeId);
        resetToken.setToken(token);
        resetToken.setExpiryTime(expiry);
        passwordresetService.saveit(resetToken);

        String resetLink = "https://tcos.sigmaworld.in/tcos/resetpassword?token=" + token + "&mid=" + employeeId;
        //for war - https://tcos.sigmaworld.in/tcos,http://localhost:6969/tcos

        mailSenderService.sendPasswordResetMail(emailId, employeeId, resetLink);

        redirectAttributes.addFlashAttribute("success", "A reset link has been sent to your registered email.");
        return "redirect:/login";
    }

    @GetMapping("/resetpassword")
    public String showResetPasswordForm(@RequestParam("token") String token, @RequestParam("mid") String employeeId, Model model,
                                        RedirectAttributes redirectAttributes) {
        Optional<PasswordResetToken> tokenData = passwordresetService.findByToken(token);
        if (tokenData.isEmpty() ||
                !tokenData.get().getEmployeeId().equals(employeeId) ||
                tokenData.get().getExpiryTime().isBefore(LocalDateTime.now())) {

            redirectAttributes.addFlashAttribute("error", "Invalid or expired password reset link.");
            return "redirect:/login";
        }

        // valid → show reset password form
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("token", token);
        return "forgot_password"; // thymeleaf page with new password fields
    }

    @PostMapping("/resetpassword")
    public String processResetPassword(@RequestParam String token, @RequestParam String employeeId, @RequestParam String newPassword, RedirectAttributes redirectAttributes) {

        Optional<PasswordResetToken> tokenData = passwordresetService.findByToken(token);

        if (tokenData.isEmpty() ||
                !tokenData.get().getEmployeeId().equals(employeeId) ||
                tokenData.get().getExpiryTime().isBefore(LocalDateTime.now())) {

            redirectAttributes.addFlashAttribute("error", "Invalid or expired token.");
            return "redirect:/login";
        }

        membersService.updatePassword(employeeId, newPassword);

        passwordresetService.delete(tokenData.get());

        redirectAttributes.addFlashAttribute("success", "Password reset successful. Please log in.");
        return "redirect:/login";
    }
    
}
