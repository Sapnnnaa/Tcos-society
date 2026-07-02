package com.telco.members.tcos.controller;

import com.telco.members.tcos.dto.MemberVoucherDTO;
import com.telco.members.tcos.dto.TableDto;
import com.telco.members.tcos.dto.reAdviceDTO;
import com.telco.members.tcos.model.*;
import com.telco.members.tcos.repository.AccessCtrlRepo;
import com.telco.members.tcos.repository.TcosbankRepo;
import com.telco.members.tcos.service.*;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.telco.members.tcos.dto.loanDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/members/advice")
public class AdviceController {

    @Autowired
    private UserService userService;

    @Autowired
    private BankService bankService;

    @Autowired
    private LoanService loanService;
    @Autowired
    private AccessCtrlRepo accessCtrlRepo;
    @Autowired
    private PdfService pdfService;

    @Autowired
    private TcosbankRepo tcosbankRepo;

    @Value("${my.api.key}")
    private String apiKey;

    private List<reAdviceDTO> memberVoucher;

    private List<reAdviceDTO> readvice;

    @GetMapping("/createAdvice")
    private String showMemberVouchers(Model model) {
        int download = 0;
        int approved = 0;
        memberVoucher = loanService.findBydownloanded(download, approved);
        model.addAttribute("mvoucher",memberVoucher);
        return "Advice/loan_vouchers";
    }

    @GetMapping("/createAdvice/details/{Id}")
    private String addMemberVoucher(@PathVariable int Id,Model model) {
        loanDTO detail= loanService.getbyAdviceId(Id);
        List<Tcosbanks> banks = tcosbankRepo.findAll();
        model.addAttribute("banks", banks);
        model.addAttribute("mv",detail);
        return "Advice/add_voucher";
    }

    @PostMapping("/createAdvice/details/update")
    private String savingMemberVoucherData(@ModelAttribute("memberVoucher") loanDTO memberVouchers , Model model) {
        loanService.updateadvice(memberVouchers);
        return "redirect:/members/advice/createAdvice";
    }

    @GetMapping("/createAdvice/security/{key}")
    public ResponseEntity<Boolean> pendingSecurity(@PathVariable int key){
        Integer accessId = accessCtrlRepo.getAccessId(accessDec.Advice_Prepare);
        boolean isValid = (accessId != null && key == accessId);
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/checkAdvice/security/{key}")
    public ResponseEntity<Boolean> checkSecurity(@PathVariable int key){
        Integer accessId = accessCtrlRepo.getAccessId(accessDec.Advice_Check);
        boolean isValid = (accessId != null && key == accessId);
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/passAdvice/security/{key}")
    public ResponseEntity<Boolean> verifySecurity(@PathVariable int key){
        Integer accessId = accessCtrlRepo.getAccessId(accessDec.Advice_Pass);
        boolean isValid = (accessId != null && key == accessId);
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/checkAdvice")
    private String checkAdvice(Model model) {
        int download = 1;
        int approved = 0;
        memberVoucher = loanService.findBydownloanded(download, approved);
//        System.out.println("Member Voucher details in this "+loanVoucherDTO);
        model.addAttribute("mvoucher",memberVoucher);
        return "Advice/advice_monitor";
    }

    @GetMapping("/checkAdvice/details/{Id}")
    private String monitoradvice(@PathVariable int Id,Model model) {

        reAdviceDTO detail= loanService.getbyAdviceId1(Id);
        advicedate = detail.getAdviceDate();
        model.addAttribute("mv",detail);

        return "Advice/advice_details";
    }

    @PostMapping("/checkAdvice/details/update")
    private String updateVoucherData(@ModelAttribute("memberVoucher") reAdviceDTO loanVouchers , Model model) {
        if(loanVouchers.getAdviceApproved() == 1 && loanVouchers.getAdviceDownloaded() == 1){
            loanVouchers.setUpdatedAt(Date.valueOf(LocalDate.now()));
            loanService.checkAdvice(loanVouchers);
        } else if (loanVouchers.getAdviceDownloaded() == 0 && loanVouchers.getAdviceApproved() == 0) {
            loanService.updatedownload(loanVouchers);
        }
        return "redirect:/members/advice/checkAdvice";
    }

    @GetMapping("/passAdvice")
    private String approvedAdvice(Model model) {
        int download = 1;
        int approved = 1;
        memberVoucher = loanService.findBydownloanded(download, approved);
//        System.out.println("Member Voucher details in this "+loanVoucherDTO);
        model.addAttribute("mvoucher",memberVoucher);
        return "Advice/advice_passed";
    }

    @GetMapping("/passAdvice/details/{Id}")
    private String passadvice(@PathVariable int Id,Model model) {
        reAdviceDTO detail= loanService.getbyAdviceId1(Id);
        advicedate = detail.getAdviceDate();
        model.addAttribute("mv",detail);

        return "Advice/pass_details";
    }

    @PostMapping("/passAdvice/details/update")
    private String passMemberVoucherData(@ModelAttribute("memberVoucher") reAdviceDTO loanVouchers , Model model) {
        if(loanVouchers.getAdviceApproved() == 2){
            loanVouchers.setUpdatedAt(Date.valueOf(LocalDate.now()));
            loanService.cahiers(loanVouchers);
        } else if (loanVouchers.getAdviceApproved() == 0) {
            loanService.updatepass(loanVouchers);
        }
        return "redirect:/members/advice/passAdvice";
    }

    @GetMapping("/approvedAdvice")
    private String showMemberAdvice(Model model) {
        int download = 1;
        int approved = 3;
        memberVoucher = loanService.findBydownloanded(download, approved);
//        System.out.println("Member Voucher details in this "+loanVoucherDTO);
        model.addAttribute("mvoucher",memberVoucher);
        return "Advice/advice_approved";
    }

    @GetMapping("/approvedAdvice/details/{Id}")
    private String approveadvice(@PathVariable int Id,Model model) {

        reAdviceDTO detail= loanService.getbyAdviceId1(Id);
        advicedate = detail.getAdviceDate();
        model.addAttribute("mv",detail);

        return "Advice/approved_details";
    }

    @GetMapping("/bankClearing")
    private String bankClearing(Model model) {
        return "Advice/adviceClearing";
    }

    @GetMapping("/bankClearing/security/{key}/bank/{selectedBank}")
    public ResponseEntity<Map<String, Object>> bankSecurity(@PathVariable int key, @PathVariable String selectedBank){
        Integer accessId = accessCtrlRepo.getAccessId(accessDec.AdviceClearing);
        boolean isValid = (accessId != null && key == accessId);
        Map<String, Object> response = new HashMap<>();
        response.put("valid", isValid);

        if (isValid) {
            int download = 1;
            int approved = 2;
            memberVoucher = loanService.findBydownloandedandapproved(download, approved, selectedBank);
            response.put("access", memberVoucher);  // attach voucher list
        } else {
            response.put("access", Collections.emptyList());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/bankClearing/update")
    private String BankUpdate(@RequestParam("id") String Id , @RequestParam("status") int status, Model model) {
        if(status == 3){
            System.out.println("Approving advice for bondId = " + Id);
            loanService.approveAdvice(Id,paymentStatus.Cleared);
        }else {
            System.out.println("Approving advice for bondId = " + Id);
            loanService.approveAdvice(Id,paymentStatus.Reversed);
        }
        return "Advice/adviceClearing";
    }

//    @GetMapping("/reGeneratedAdvice")
//    private String reAdvice(Model model){
//        readvice = loanService.fetchALLAdvice();
//        model.addAttribute("mvoucher",readvice);
//        return "Advice/reAdvice";
//    }

    java.util.Date advicedate;

    @GetMapping("/createAdvice/sbi-vouchers/{chequeNo}")
    public ResponseEntity<byte[]> MemberVoucherSBI(@RequestParam String fullName, @PathVariable String chequeNo, Model model) {
        // Prepare the context for the PDF
        Context context = new Context();
        context.setVariable("ACCOUNTNO", "10590737320");
        // Prepare data for the PDF (list of member vouchers)
        String bankname = "State Bank Of India, Telco";
        List<loanDTO> memberVouchers = loanService.findBydownloandednbankwithchequeAndpreparedBy(0,Paymenttype.ADVICE, bankname, fullName);  // Assuming this returns a list of member vouchers
        double totalAmount = 0;
        for (loanDTO mv : memberVouchers) {
            totalAmount += mv.getLoanAmount();
        }
     //   System.out.println("Member Voucher details in this "+chequeNo);
      //  System.out.println("Member Voucher details in this "+fullName);
        // Add the LoanVoucherDTO to context for use in Thymeleaf template
        context.setVariable("LoanVoucherDTO", memberVouchers);
        context.setVariable("totalAmount", totalAmount);
        context.setVariable("ChequeNo", chequeNo);
        context.setVariable("Name",fullName);

        // Generate the PDF bytes
        byte[] pdfBytes = pdfService.generatePdf("Advice/voucher_sbi", context);

//        for (loanDTO mv : memberVouchers) {
//            mv.setChequeNo(chequeNo);
//            mv.setAdviceDownloaded(1);
//            mv.setAdviceDate(Date.valueOf(LocalDate.now()));
//        }
//        loanService.updateAdviceDownload(memberVouchers);
        // Return the PDF as a download
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Vouchers_Portrait.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/createAdvice/jscb-vouchers/{chequeNo}")
    public ResponseEntity<byte[]> MemberVoucherJSCB(@RequestParam String fullName, @PathVariable String chequeNo, Model model) {
        // Prepare the context for the PDF
        Context context = new Context();
        context.setVariable("ACCOUNTNO", "1126-3521000060");
        // Prepare data for the PDF (list of member vouchers)
        String bankname = "Jharkhand State Co-operative Bank, Telco";
        List<loanDTO> memberVouchers = loanService.findBydownloandednbankwithchequeAndpreparedBy(0,Paymenttype.ADVICE, bankname, fullName);  // Assuming this returns a list of member vouchers
        double totalAmount = 0;
        for (loanDTO mv : memberVouchers) {
            totalAmount += mv.getLoanAmount();
        }

        // Add the LoanVoucherDTO to context for use in Thymeleaf template
        context.setVariable("LoanVoucherDTO", memberVouchers);
        context.setVariable("totalAmount", totalAmount);
        context.setVariable("ChequeNo", chequeNo);
        context.setVariable("Name",fullName);

        // Generate the PDF bytes
        byte[] pdfBytes = pdfService.generatePdf("Advice/voucher_jscb", context);

//        for (loanDTO mv : memberVouchers) {
//            mv.setChequeNo(chequeNo);
//            mv.setAdviceDownloaded(1);
//            mv.setAdviceDate(Date.valueOf(LocalDate.now()));
//        }
//        loanService.updateAdviceDownload(memberVouchers);
        // Return the PDF as a download
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Vouchers_Portrait.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/createAdvice/cbi-vouchers/{chequeNo}")
    public ResponseEntity<byte[]> MemberVoucherCBI(@RequestParam String fullName, @PathVariable String chequeNo, Model model) {
        // Prepare the context for the PDF
        Context context = new Context();
        context.setVariable("ACCOUNTNO", "1231489568");
        // Prepare data for the PDF (list of member vouchers)
        String bankname = "Central Bank Of India, Telco";
        List<loanDTO> memberVouchers = loanService.findBydownloandednbankwithchequeAndpreparedBy(0,Paymenttype.ADVICE, bankname, fullName);  // Assuming this returns a list of member vouchers
        double totalAmount = 0;
        for (loanDTO mv : memberVouchers) {
            totalAmount += mv.getLoanAmount();
        }

        // Add the LoanVoucherDTO to context for use in Thymeleaf template
        context.setVariable("LoanVoucherDTO", memberVouchers);
        context.setVariable("totalAmount", totalAmount);
        context.setVariable("ChequeNo", chequeNo);
        context.setVariable("Name",fullName);

        // Generate the PDF bytes
        byte[] pdfBytes = pdfService.generatePdf("Advice/voucher_cbi", context);

//        for (loanDTO mv : memberVouchers) {
//            mv.setChequeNo(chequeNo);
//            mv.setAdviceDownloaded(1);
//            mv.setAdviceDate(Date.valueOf(LocalDate.now()));
//        }
//        loanService.updateAdviceDownload(memberVouchers);
        // Return the PDF as a download
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Vouchers_Portrait.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }


    @GetMapping("/createAdvice/neft-vouchers")
    public ResponseEntity<byte[]> MemberVoucherNEFT(@RequestParam String fullName, Model model) {
        // Prepare the context for the PDF
        Context context = new Context();
//        context.setVariable("ACCOUNTNO", "10590737320");

        // Prepare data for the PDF (list of member vouchers)
        List<loanDTO> memberVouchers = loanService.findBydownloandednbankAndPreparedBy(0,Paymenttype.NEFT, fullName);  // Assuming this returns a list of member vouchers
       String accountNumbers = "";
        double totalAmount = 0;
        for (loanDTO mv : memberVouchers) {
            totalAmount += mv.getLoanAmount();
            accountNumbers = mv.getAccountNumbers();
        }
        System.out.println("Member Voucher details in this "+memberVouchers);

        // Add the LoanVoucherDTO to context for use in Thymeleaf template
        context.setVariable("LoanVoucherDTO", memberVouchers);
        context.setVariable("accountNumbers", accountNumbers);         
        context.setVariable("totalAmount", totalAmount);
        context.setVariable("Name",fullName);

        // Generate the portrait PDF bytes
        byte[] pdfBytes = pdfService.generatePdf("Advice/voucher_neft", context);

//        for (loanDTO mv : memberVouchers) {
//            mv.setAdviceDownloaded(1);
//            mv.setAdviceDate(Date.valueOf(LocalDate.now()));
//        }
//        loanService.updateAdviceNeft(memberVouchers);
        // Return the PDF as a download
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Vouchers_Portrait.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/markAdviceDownloaded/{selectedType}/{chequeNo}")
    public ResponseEntity<?> markAdviceDownloaded(@RequestParam String fullName, @PathVariable String selectedType, @PathVariable String chequeNo, Model model) {
        String bank = "";
        Paymenttype paymentType;

        // Determine bank and type
        if (selectedType.startsWith("NEFT")) {
            paymentType = Paymenttype.NEFT;
        }else {
            paymentType = Paymenttype.ADVICE;
        }

        if (selectedType.startsWith("SBI")) bank = "State Bank Of India, Telco";
        else if (selectedType.startsWith("JSCB")) bank = "Jharkhand State Co-operative Bank, Telco";
        else if (selectedType.startsWith("CBI")) bank = "Central Bank Of India, Telco";

        List<loanDTO> loans = (paymentType == Paymenttype.NEFT)
                ? loanService.findBydownloandednbankAndPreparedBy(0, paymentType, fullName)
                : loanService.findBydownloandednbankwithchequeAndpreparedBy(0, paymentType, bank, fullName);
        if (loans == null || loans.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No eligible loans to mark.");
        }
        for (loanDTO mv : loans) {
            if (paymentType == Paymenttype.ADVICE) {
                mv.setChequeNo(chequeNo);
            }
            mv.setAdviceDownloaded(1);
            mv.setAdviceDate(Date.valueOf(LocalDate.now()));
        }

        if (paymentType == Paymenttype.NEFT) {
            loanService.updateAdviceNeft(loans);
        } else {
            loanService.updateAdviceDownload(loans);
        }

        return ResponseEntity.ok("Advice marked as downloaded");
    }

}

