package com.telco.members.tcos.controller;

import com.telco.members.tcos.model.User;
import com.telco.members.tcos.model.bankBalance;
import com.telco.members.tcos.model.PaymentAccountdet;
import com.telco.members.tcos.model.Loan;
import com.telco.members.tcos.model.Surety;
import com.telco.members.tcos.model.transactionHistory;
import com.telco.members.tcos.model.CDShareDeductions;

import com.telco.members.tcos.model.*;
import com.telco.members.tcos.dto.ReceiptReportDTO;
import com.telco.members.tcos.dto.loanDTO;
import com.telco.members.tcos.dto.transactionHistoryDTO;
import com.telco.members.tcos.dto.SuretyloanDTO;
import com.telco.members.tcos.model.LoanVoucher;


import com.telco.members.tcos.service.MisReportService;

import com.telco.members.tcos.service.MemberReceiptService;
//.MemberReceiptService memberReceiptService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/mis")
public class MISController {

    @Autowired
    private MisReportService misReportService;

    @Autowired
	private MemberReceiptService memberReceiptService;
    // -----------------------------------------
    // MEMBERSHIP REPORT
    // -----------------------------------------
    @GetMapping("/membership")
    public String membershipReport(Model model) {
        List<User> membershipList = misReportService.getAllMembership();
        
        model.addAttribute("membership", membershipList);
        System.out.println("membership details in this "+membershipList);
        return "mis/membership";
    }


    // -----------------------------------------
    // LOAN RECOVERY REPORT
    // -----------------------------------------
    @GetMapping("/loan-recovery")
    public String loanRecoveryReport(Model model) {
        List<loanDTO> loans = misReportService.getAllLoanRecovery();
       // misReportService.attachNamesToLoans(loans);
       // System.out.println("Member Voucher details in this "+loans);
        model.addAttribute("loans", loans);
        return "mis/loan-recovery";
    }


    // -----------------------------------------
    // SURETIES REPORT
    // -----------------------------------------
    @GetMapping("/sureties")
    public String suretiesReport(Model model) {
        List<Surety> sureties = misReportService.getAllSureties();
        misReportService.attachNamesToSureties(sureties);
        model.addAttribute("sureties", sureties);
        return "mis/sureties";
    }


    // -----------------------------------------
    // RECEIPT REPORT
    // -----------------------------------------
    @GetMapping("/receipt-report")
    public String receiptReport(Model model) {

        List<ReceiptReportDTO> receipts = misReportService.getAllReceiptReports();
        model.addAttribute("receipts", receipts);

        return "mis/receipt-report";
    }

    @GetMapping("/accountreceipt-report")
    public String accountreceiptReport(Model model) {

        List<ReceiptReportDTO> receipts = misReportService.getAllReceiptReports();
        List<AccountAssign> accountHeadsList = misReportService.getAccountAssign();
        
        model.addAttribute("accountHeadsList", accountHeadsList);
        model.addAttribute("receipts", receipts);

        return "mis/accountreceipt-report";
    }
    @GetMapping("/receiptstatus-report")
    public String receiptstatusReport(Model model) {

        List<ReceiptReportDTO> receipts = misReportService.getAllReceiptstatsReports();
        model.addAttribute("receipts", receipts);

        return "mis/receiptstatus-report";
    }


    // -----------------------------------------
    // PAYMENTS REPORT
    // -----------------------------------------
    @GetMapping("/payments")
    public String paymentsReport(Model model) {
        List<transactionHistory> payments = misReportService.getAllPayments();
        model.addAttribute("payments", payments);
        return "mis/payments";
    }
    
    @GetMapping("/paymentsPosted")
    public String paymentsPostedReport(Model model) {
        List<transactionHistoryDTO> payments = misReportService.getPaymentposted();
        System.out.println("Member payment posted details "+payments);
        model.addAttribute("payments", payments);
        return "mis/paymentsPosted";
    }
   
    @GetMapping("/paymentsNew")
    public String paymentsNewReport(Model model) {
        List<transactionHistory> payments = misReportService.getAllPayments();
        model.addAttribute("payments", payments);
        return "mis/paymentsNew";
    }
    // -----------------------------------------
    // ADVICE REPORT
    // -----------------------------------------
    @GetMapping("/advice")
    public String adviceReport(Model model) {
        List<LoanVoucher> advice = misReportService.getAllAdvice();
        misReportService.attachNamesToLoanVoucher(advice);

        model.addAttribute("advice", advice);
        return "mis/advice";
    }


    // -----------------------------------------
    // CD SHARE REPORT
    // -----------------------------------------
    @GetMapping("/cd-share")
    public String cdShareReport(Model model) {
        List<CDShareDeductions> cdShare = misReportService.getAllData();
        misReportService.attachNamesToCDShare(cdShare);
        model.addAttribute("cdShare", cdShare);
        return "mis/cd-share";
    }
    // -----------------------------------------
    // PAYMENT ACCOUNT DETAILA  REPORT
    // -----------------------------------------
    
    @GetMapping("/paymentsaccount")
    public String bankHistory(Model model){
      // List<PaymentAccountdet> bankBook1 = misReportService.getpaymentaccountdata();
      List<transactionHistory> payments = misReportService.getAllPayments();
    //   List<AccountHeads> accountHeadsList = memberReceiptService.getAccountHeads();
      List<AccountAssign> accountHeadsList = misReportService.getAccountAssign();
       System.out.println("Member payment account details "+accountHeadsList);
//        List<Double> balanceList = bankService.findAllBalance();
//        model.addAttribute("sbiBalance", balanceList.get(0));
//        model.addAttribute("cbiBalance", balanceList.get(1));
          model.addAttribute("accountHeadsList", accountHeadsList);
          model.addAttribute("payments", payments);
        return "mis/payment-account";
    }
    
    // -----------------------------------------
    // SURETY LOAN REPORT
    // -----------------------------------------
    @GetMapping("/sureties-loanreport")
    public String suretiesloanReport(Model model) {

        List<SuretyloanDTO> suretyloans = misReportService.getSuretyReports();
        System.out.println("suretyloans details "+suretyloans);
        model.addAttribute("suretyloans", suretyloans);

        return "mis/sureties-loanreport";
    }
}
