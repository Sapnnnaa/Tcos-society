package com.telco.members.tcos.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

import com.telco.members.tcos.dto.*;
import com.telco.members.tcos.model.*;
import com.telco.members.tcos.repository.AccessCtrlRepo;
import com.telco.members.tcos.repository.TcosbankRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.context.Context;

import com.telco.members.tcos.service.BankService;
import com.telco.members.tcos.service.MemberReceiptService;
import com.telco.members.tcos.service.UserService;

@Controller
@RequestMapping("/members")
public class MemberReceiptController {

	@Autowired
	private UserService userService;

	@Autowired
	private BankService bankService;

    @Autowired
    private AccessCtrlRepo accessCtrlRepo;

	@Autowired
	private MemberReceiptService memberReceiptService;

	@Autowired
	private TcosbankRepo tcosbankRepo;


	@GetMapping("/memberReceipt/createReceipt")
	private String createReceipt(Model model) {
		int stage = 0;
		List<ReceiptDto> mr = memberReceiptService.findByStage(stage);
		model.addAttribute("mReceipt", mr);
		return "receipts/receipt_list1";
	}

    @GetMapping("/memberReceipt/checkReceipt")
    private String checkReceipt(Model model) {
        int stage = 0;
        List<ReceiptDto> mr = memberReceiptService.findByStage(stage);
        model.addAttribute("mReceipt", mr);
        return "receipts/receipt_list2";
    }

    @GetMapping("/memberReceipt/checkReceipt/detail/{rId}")
    public String checkmr(@PathVariable String rId, Model model){
        MemberReceiptDTO mr = memberReceiptService.getdata(rId);
        model.addAttribute("mvDto",mr);
        return "receipts/check_receipt";
    }

    @PostMapping("/memberReceipt/checkReceipt/details")
    public String checkeddmr(MemberReceiptDTO mr, Model model){
        mr.setUpdatedDate(Date.valueOf(LocalDate.now()));
        memberReceiptService.savemr(mr);
        return "redirect:/members/memberReceipt/checkReceipt";
    }

    @GetMapping("/memberReceipt/passReceipt")
    private String passReceipt(Model model) {
        int stage = 1;
        List<ReceiptDto> mr = memberReceiptService.findByStage(stage);
        model.addAttribute("mReceipt", mr);
        return "receipts/receipt_list3";
    }

    @GetMapping("/memberReceipt/passReceipt/detail/{rId}")
    public String passmr(@PathVariable String rId, Model model){
        MemberReceiptDTO mr = memberReceiptService.getdata(rId);
        model.addAttribute("mvDto",mr);
        return "receipts/pass_receipt";
    }

    @PostMapping("/memberReceipt/passReceipt/details")
    public String passedmr(MemberReceiptDTO mr, Model model){
        int value = mr.getStage();
        String rid = mr.getRId();
        if(value == 2) {
            mr.setApproveDate(Date.valueOf(LocalDate.now()));
            memberReceiptService.approvemr(mr);
        } else {
            mr.setUpdatedDate(Date.valueOf(LocalDate.now()));
            memberReceiptService.savemr(mr);
        }
        return "redirect:/members/memberReceipt/passReceipt";
    }

    @GetMapping("/memberReceipt/createReceipt/security/{key}")
    public ResponseEntity<Boolean> pendingSecurity(@PathVariable int key){
        Integer accessId = accessCtrlRepo.getAccessId(accessDec.Receipt_Prepare);
        boolean isValid = (accessId != null && key == accessId);
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/memberReceipt/checkReceipt/security/{key}")
    public ResponseEntity<Boolean> checkSecurity(@PathVariable int key){
        Integer accessId = accessCtrlRepo.getAccessId(accessDec.Receipt_Check);
        boolean isValid = (accessId != null && key == accessId);
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/memberReceipt/passReceipt/security/{key}")
    public ResponseEntity<Boolean> verifySecurity(@PathVariable int key){
        Integer accessId = accessCtrlRepo.getAccessId(accessDec.Receipt_Pass);
        boolean isValid = (accessId != null && key == accessId);
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/memberReceipt/approvedReceipt")
    private String approvedReceipt(Model model) {
        int stage = 2;
        List<ReceiptDto> mr = memberReceiptService.findByStage(stage);
        model.addAttribute("mReceipt", mr);
        return "receipts/receipt_list4";
    }

    @GetMapping("/memberReceipt/approvedReceipt/detail/{rId}")
    public String approvedmr(@PathVariable String rId, Model model){
        MemberReceiptDTO mr = memberReceiptService.getdata(rId);
        model.addAttribute("mvDto",mr);
        return "receipts/approved_receipt";
    }

    @GetMapping("/memberReceipt/receiptReport")
    public String receiptReport(Model model){
        List<ReportDTO> readvice = memberReceiptService.findApprovedReport();
        model.addAttribute("mReceipt", readvice);
        return "receipts/receipt_list5";
    }

    @GetMapping("/memberReceipt/createReceipt/add")
    private String addReceipt(Model model) {
        MemberReceiptDTO newVoucher = new MemberReceiptDTO();
        newVoucher.setRId(memberReceiptService.getLatestRId());
        List<String> userIds = userService.findAllUserId();
        List<Tcosbanks> banks = tcosbankRepo.findAll();  
        model.addAttribute("banks", banks);
        model.addAttribute("alluser", userIds);
        model.addAttribute("memberReceiptDTO", newVoucher);
        return "receipts/new_receipt";
    }

    @GetMapping("/memberReceipt/createReceipt/detail/{rId}")
    public String detailsmv(@PathVariable String rId, Model model){
        MemberReceiptDTO mv = memberReceiptService.getdata(rId);
        List<Tcosbanks> banks = tcosbankRepo.findAll();
        model.addAttribute("banks", banks);
        model.addAttribute("mvDto", mv);
        return "receipts/receipt_details";
    }

    @GetMapping("/memberReceipt/createReceipt/delete/{vId}")
    public String deletemv(@PathVariable String vId, Model model){
        memberReceiptService.deletevid(vId);
        return "redirect:/members/memberReceipt/createReceipt";
    }


    @PostMapping("/memberReceipt/createReceipt/details")
    public String updatemvs(@ModelAttribute MemberReceiptDTO mv, Model model){
        memberReceiptService.saveReceipt(mv);
        return "redirect:/members/memberReceipt/createReceipt";
    }

    @GetMapping("/memberReceipt/createReceipt/details/{vId}/add/{vSln}/{amount}")
    public ResponseEntity<Map<String, String>> addVouchers(@PathVariable String vId, @PathVariable String vSln, @PathVariable Double amount, Model model){
        memberReceiptService.addReceipts(vId,vSln, amount);
        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "Voucher added successfully!");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/memberReceipt/createReceipt/details/{vId}/delete/{vSln}")
    public ResponseEntity<String> addVouchers(@PathVariable String vId, @PathVariable String vSln, Model model){
        memberReceiptService.deleteRecipits(vId,vSln);
        return ResponseEntity.ok("Voucher deleted successfully!");
    }

//    public String incrementRId(String latestRid) {
//        // Extract numeric part and trim spaces
//        String numberPart = latestRid.substring(3).trim();
//        int number = Integer.parseInt(numberPart) + 1;
//        String newNumberPart = String.format("%0" + numberPart.length() + "d", number);
//
//        return "rec" + newNumberPart;
//    }

    @PostMapping("/memberReceipt/createReceipt/add")
    public String savingmemberReceiptData(@ModelAttribute MemberReceiptDTO mv, Model model) {
        if (mv.getMemberVoucher() != null && !mv.getMemberVoucher().isEmpty()) {
            for (Receipt mvs : mv.getMemberVoucher()) {
                mvs.setVId(mv.getRId());
                memberReceiptService.Receiptsave(mvs);
            }
        }
        mv.setCreatedDate(mv.getCreatedDate()!= null ? mv.getCreatedDate() : Date.valueOf(LocalDate.now()));
        memberReceiptService.saveit(mv);
        return "redirect:/members/memberReceipt/createReceipt";
    }

    @GetMapping("memberReceipt/fetch/accountHead")
    private ResponseEntity<Map<String,List<AccountHeads>>> fetchGl(){
        Map<String,List<AccountHeads>> response = new HashMap<>();
        List<AccountHeads> accountHeadsList = memberReceiptService.getAccountHeads();
        response.put("accountHead",accountHeadsList);
        return ResponseEntity.ok(response);
    }

    @GetMapping("memberReceipt/add/ticketNumber/{userId}")
    private ResponseEntity<Map<String, MemberReceiptDTO>> seaarchUserId(@PathVariable String userId) {
        Map<String, MemberReceiptDTO> response = new HashMap<String, MemberReceiptDTO>();
        MemberReceiptDTO memberReceipts = memberReceiptService.findByUserId(userId);
        response.put("mvdto", memberReceipts);
        return ResponseEntity.ok(response);
    }

//	@GetMapping("/memberReceipt/createReceipt/detail/{rId}}")
//	private String showMemberReceipt(@PathVariable String rId, Model model) {
//
//		List<MemberReceiptDTO> memberReceiptDTO = new ArrayList<MemberReceiptDTO>();
//		memberReceipts=memberReceiptService.findAll();
//		for(MemberReceipt mr:memberReceipts) {
//			User user=userService.findByUserId(mr.getUserId());
//			Banks bank=bankService.findByUserId(mr.getUserId());
//			memberReceiptDTO.add(new MemberReceiptDTO(user, bank, mr));
//		}
//		System.out.println("Member Receipt details in this "+memberReceiptDTO);
//		model.addAttribute("mreceipts",memberReceiptDTO);
//
//		return "receipts/member_receipts";
//	}
//
//
//	@GetMapping("/memberreceipts/add")
//	private String addMemberReceipt(Model model) {
//
//		List<String> userIds=userService.findAllUserId();
//		model.addAttribute("alluser",userIds);
//		model.addAttribute("memberReceipt",new MemberReceipt());
//
//		return "receipts/add_members_receipts";
//	}
//
//
//	@PostMapping("/memberreceipts/add")
//	private String savingMemberReceiptsData(@ModelAttribute("memberReceipt") MemberReceipt memberReceipt ,Model model) {
//		System.out.println("this is member receipt "+memberReceipt);
//		memberReceipt.setReceiptDate(new Date());
//		memberReceiptService.save(memberReceipt);
//
//		List<MemberReceiptDTO> memberReceiptDTO = new ArrayList<MemberReceiptDTO>();
//		this.memberReceipts=memberReceiptService.findAll();
//		for(MemberReceipt mr:this.memberReceipts) {
//			User user=userService.findByUserId(mr.getUserId());
//			Banks bank=bankService.findByUserId(mr.getUserId());
//			memberReceiptDTO.add(new MemberReceiptDTO(user, bank, mr));
//
//		}
//		System.out.println("Member Receipts details in this "+memberReceiptDTO);
//		model.addAttribute("mreceipts",memberReceiptDTO);
//
//		return "receipts/member_receipts";
//	}
//
//
//	@GetMapping("memberreceipts/ticketNumber/{userId}")
//	private ResponseEntity<Map<String,MemberReceiptDTO>> seaarchUserId(@PathVariable String userId){
//		Map<String, MemberReceiptDTO> response = new HashMap<String, MemberReceiptDTO>();
//
//		User user=userService.findByUserId(userId);
//		Banks bank=bankService.findByUserId(userId);
//		MemberReceipt memberReceipt=memberReceiptService.findByUserId(userId);
//		MemberReceiptDTO memberReceiptDTO=new MemberReceiptDTO(user, bank, memberReceipt);
//		response.put("mrdto", memberReceiptDTO);
//
//		return ResponseEntity.ok(response);
//	}
//
//
//	@GetMapping("/memberreceipts/download")
//	public ResponseEntity<byte[]> MemberReceiptSBI(Model model) {
//	    // Prepare the context for the PDF
//	    Context context = new Context();
//	    context.setVariable("ACCOUNTNO", "10590737320");
//
//	    // Prepare data for the PDF (list of member receipts)
//	    List<MemberReceiptDTO> memberReceiptDTO = new ArrayList<>();
//	    List<MemberReceipt> memberReceipt = memberReceiptService.findByReceiptDownloaded(0);  // Assuming this returns a list of member receipts
//	    double totalAmount = 0;
//	    for (MemberReceipt mr : memberReceipt) {
//	        User user = userService.findByUserId(mr.getUserId()); // Get user by user ID
//	        Banks bank = bankService.findByUserId(mr.getUserId()); // Get bank details by user ID
//	        memberReceiptDTO.add(new MemberReceiptDTO(user, bank, mr)); // Populate DTO list
//	        totalAmount += mr.getAmount();
//	    }
//
//	    // Add the memberReceiptDTO to context for use in Thymeleaf template
//	    context.setVariable("memberReceiptDTO", memberReceiptDTO);
//	    context.setVariable("totalAmount", totalAmount);
//
//	    // Generate the PDF bytes
//	    byte[] pdfBytes = pdfService.generatePdf("receipts/member_receipt_sbi", context);
//
//	    for (MemberReceipt mr : memberReceipt) {
//	        mr.setReceiptDownloaded(1);  // Assuming the field is set for receipts
//	    }
//	    memberReceiptService.update(memberReceipt);
//
//	    // Return the PDF as a download
//	    return ResponseEntity.ok()
//	            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Receipts_Portrait.pdf")
//	            .contentType(MediaType.APPLICATION_PDF)
//	            .body(pdfBytes);
//	}



//	@GetMapping("/memberreceipts/neft-receipts")
//	public ResponseEntity<byte[]> MemberReceiptNEFT() {
//	    // Prepare the context for the PDF
//	    Context context = new Context();
//	    context.setVariable("ACCOUNTNO", "10590737320");
//
//	    // Prepare data for the PDF (list of member receipts)
//	    List<MemberReceiptDTO> memberReceiptDTO = new ArrayList<>();
//	    List<MemberReceipt> memberReceipt = memberReceiptService.findByPaymentType("NEFT", 0);  // Assuming this returns a list of member receipts
//	    double totalAmount = 0;
//	    for (MemberReceipt mr : memberReceipt) {
//	        User user = userService.findByUserId(mr.getUserId()); // Get user by user ID
//	        Banks bank = bankService.findByUserId(mr.getUserId()); // Get bank details by user ID
//	        memberReceiptDTO.add(new MemberReceiptDTO(user, bank, mr)); // Populate DTO list
//
//	        totalAmount += mr.getAmount();
//	    }
//
//	    // Add the memberReceiptDTO to context for use in Thymeleaf template
//	    context.setVariable("memberReceiptDTO", memberReceiptDTO);
//	    context.setVariable("totalAmount", totalAmount);
//
//	    // Generate the portrait PDF bytes
//	    byte[] pdfBytes = pdfService.generatePdf("receipts/member_receipt_neft", context);
//
//	    for (MemberReceipt mr : memberReceipt) {
//	        mr.setReceiptDownloaded(1);  // Assuming the field is set for receipts
//	    }
//	    memberReceiptService.update(memberReceipt);
//
//	    // Return the PDF as a download
//	    return ResponseEntity.ok()
//	            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Receipts_Portrait.pdf")
//	            .contentType(MediaType.APPLICATION_PDF)
//	            .body(pdfBytes);
//	}




}
