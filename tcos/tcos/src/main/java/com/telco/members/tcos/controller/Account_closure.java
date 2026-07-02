package com.telco.members.tcos.controller;


import com.telco.members.tcos.dto.*;
import com.telco.members.tcos.model.CDShareDeductions;
import com.telco.members.tcos.model.User;
import com.telco.members.tcos.model.accessDec;
import com.telco.members.tcos.model.loanHistory;
import com.telco.members.tcos.repository.AccessCtrlRepo;
import com.telco.members.tcos.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.List;


@Controller
@RequestMapping("/members")
public class Account_closure {

    @Autowired
    private LoanHistoryService loanHistoryService;
    @Autowired
    private CDShareDeductionService cdShareDeductionService;
    @Autowired
    private UserService userService;
    @Autowired
    private AccessCtrlRepo accessCtrlRepo;
    @Autowired
    private GuarantorService guarantorService;
    @Autowired
    private LoanService loanService;

    @GetMapping("/account_closure")
    public String stage1(Model model){
        model.addAttribute("accountClosureDto", new closureDTO());
        return "closure/Stage1";
    }

    @GetMapping("/account_closure/security/{key}")
    public ResponseEntity<Boolean> verifySecurity(@PathVariable int key){
        Integer accessId = accessCtrlRepo.getAccessId(accessDec.Account_Closure);
        boolean isValid = (accessId != null && key == accessId);
        return ResponseEntity.ok(isValid);
    }

    private String userIdsave = "";

    @GetMapping("/account_closure/{userId}")
    public ResponseEntity<ReportingDTO> findAllDetails(@PathVariable("userId") String userId) {
        ReportingDTO data = new ReportingDTO();
        userIdsave = userId;
        User user = userService.findByUserId(userIdsave);
        List<loanInfoDto> loans1 = loanHistoryService.getAllLoansAC(user.getUserId());
        CDShareDeductions emis = cdShareDeductionService.getLatestRecord(userIdsave);
        List<LoanapproveDTO> loan2 = loanService.getAllNotApprovedLoan(userIdsave);
        int Surety = guarantorService.getGuarantorCount(userIdsave);
        data.setSurety(Surety);
        data.setUser(user);
        data.setLoans1(loans1);
        data.setLoans2(loan2);
        data.setEmis(emis);
        return ResponseEntity.ok(data);
    }
    @GetMapping("/audit_settlement")
    public String listUsersaudit(Model model) {
        List<UserregisterDTO> userss = userService.getAllUserclosure();
        model.addAttribute("users", userss);
        return "closure/audit_settlements"; // Return the Thymeleaf template view
    }
    


    @GetMapping("/settlement")
    public String listUsers(Model model) {
        List<UserregisterDTO> userss = userService.getAllUserclosure();
        model.addAttribute("users", userss);
        return "closure/account_closure"; // Return the Thymeleaf template view
    }

    @GetMapping("/closed")
    public String listUsersClosed(Model model) {
        List<UserregisterDTO> userss = userService.getAllUserclosed();
        model.addAttribute("users", userss);
        return "closure/Accountclosure"; // Return the Thymeleaf template view
    }
    
}
