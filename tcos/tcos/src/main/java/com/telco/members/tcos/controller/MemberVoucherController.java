package com.telco.members.tcos.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.telco.members.tcos.dto.*;
import com.telco.members.tcos.model.*;
import com.telco.members.tcos.repository.*;
import com.telco.members.tcos.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.thymeleaf.context.Context;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Controller
@RequestMapping("/members")
public class MemberVoucherController {

	@Autowired
	private UserService userService;

	@Autowired
	private BankService bankService;

	@Autowired
	private AccessCtrlRepo accessCtrlRepo;

	@Autowired
	private TcosbankRepo tcosbankRepo;

	@Autowired
	private MemberVoucherService memberVoucherService;

	@Autowired
	private MemberVoucherRepository memberVoucherRepository;

	@Autowired
	private PdfService pdfService;

	@Autowired
	private CashierRepo cashierRepo;

	@GetMapping("/membervoucher/createVoucher")
	private String showMemberVouchers(Model model) {
		int download = 0;
		int approved = 0;
		List<TableDto> mv = memberVoucherService.tableView(download, approved);
		model.addAttribute("mvoucher", mv);
		return "vouchers/voucher_step01";
	}

	@GetMapping("/membervoucher/createVoucher/add")
	private String addMemberVoucher(Model model) {
		MemberVoucherDTO newVoucher = new MemberVoucherDTO();
		newVoucher.setVId(memberVoucherService.getLatestVId());
		List<String> userIds = userService.findAllUserId();
		List<Tcosbanks> banks = tcosbankRepo.findAll();
		model.addAttribute("banks", banks);
		model.addAttribute("alluser", userIds);
		model.addAttribute("memberVoucherDTO", newVoucher);
		return "vouchers/voucher_step2";
	}

	@PostMapping("/membervoucher/createVoucher/add")
	public String savingMemberVoucherData(@ModelAttribute MemberVoucherDTO mv, Model model) {
		String vId = mv.getVId();
		if (mv.getMemberVoucher() != null && !mv.getMemberVoucher().isEmpty()) {
			for (Voucher mvs : mv.getMemberVoucher()) {
				mvs.setVId(vId);
				memberVoucherService.savevoucher(mvs);
			}
		}
		mv.setCreatedDate(mv.getCreatedDate()!= null ? mv.getCreatedDate() : Date.valueOf(LocalDate.now()));
		memberVoucherService.saveit(mv);
		return "redirect:/members/membervoucher/createVoucher";
	}

	@GetMapping("/membervoucher/checkVoucher")
	private String checkVouchers(Model model) {
		int download = 1;
		int approved = 0;
		List<TableDto> mv = memberVoucherService.tableView(download, approved);
		model.addAttribute("mvoucher", mv);
		return "vouchers/voucher_step3";
	}

	@GetMapping("/membervoucher/checkVoucher/details/{vId}")
	public String checkmv(@PathVariable String vId, Model model){
		MemberVoucherDTO mv = memberVoucherService.getdata(vId);
		model.addAttribute("mvDto",mv);
		return "vouchers/voucher_step4";
	}

	@PostMapping("/membervoucher/checkVoucher/details")
	public String checkmvs(@ModelAttribute MemberVoucherDTO mv, Model model){
		int generatedTimes = memberVoucherService.findGenerated(mv.getVId());
		int downloaded = mv.getVoucherDownloaded();
		int approved = mv.getVoucherApproved();
		if(downloaded == 0 && approved == 0){
			memberVoucherService.rolbackGid(mv.getVId());}
//		} else if (downloaded == 1 && approved == 1) {
//			memberVoucherService.updateByGid(mv.getVId());
//		}
		mv.setUpdatedDate(Date.valueOf(LocalDate.now()));
		memberVoucherService.checkmv(mv);
		return "redirect:/members/membervoucher/checkVoucher";
	}

	@GetMapping("/membervoucher/createVoucher/security/{key}")
	public ResponseEntity<Boolean> pendingSecurity(@PathVariable int key){
		Integer accessId = accessCtrlRepo.getAccessId(accessDec.Voucher_Prepare);
		boolean isValid = (accessId != null && key == accessId);
		return ResponseEntity.ok(isValid);
	}

	@GetMapping("/membervoucher/checkVoucher/security/{key}")
	public ResponseEntity<Boolean> checkSecurity(@PathVariable int key){
		Integer accessId = accessCtrlRepo.getAccessId(accessDec.Voucher_Check);
		boolean isValid = (accessId != null && key == accessId);
		return ResponseEntity.ok(isValid);
	}

	@GetMapping("/membervoucher/passVoucher/security/{key}")
	public ResponseEntity<Boolean> verifySecurity(@PathVariable int key){
		Integer accessId = accessCtrlRepo.getAccessId(accessDec.Voucher_Pass);
		boolean isValid = (accessId != null && key == accessId);
		return ResponseEntity.ok(isValid);
	}

//	@GetMapping("/reports/{userId}")
//	public ResponseEntity<ReportingDTO> findAllDetails(@PathVariable("userId") String userId) {
//		ReportingDTO data = new ReportingDTO();
//		userIdsave = userId;
//		User user = userService.findByUserId(userIdsave);
//		List<loanHistory> loans = loanHistoryService.getAllLoans(user.getUserId());
//		CDShareDeductions emis = cdShareDeductionService.getLatestRecord(userIdsave);
//		data.setUser(user);
//		data.setLoans(loans);
//		data.setEmis(emis);
//		return ResponseEntity.ok(data);
//	}

	@GetMapping("/membervoucher/passVoucher")
	private String passedVouchers(Model model) {
		int download = 1;
		int approved = 1;
		List<TableDto> mv = memberVoucherService.tableView(download, approved);
		model.addAttribute("mvoucher", mv);
		return "vouchers/voucher_step6";
	}

	@GetMapping("/membervoucher/passVoucher/details/{vId}")
	public String passmv(@PathVariable String vId, Model model){
		MemberVoucherDTO mv = memberVoucherService.getdata(vId);
		model.addAttribute("mvDto",mv);
		return "vouchers/voucher_step7";
	}

	@PostMapping("/membervoucher/passVoucher/details")
	public String passmvs(@ModelAttribute MemberVoucherDTO mv, Model model){
		int approved = mv.getVoucherApproved();
		if(approved == 0){
			memberVoucherService.rolbackGid2(mv.getVId());
		} else if (approved == 2) {
			memberVoucherService.updateByGid(mv.getVId());
		}
		mv.setUpdatedDate(Date.valueOf(LocalDate.now()));
		memberVoucherService.passmv(mv);
		return "redirect:/members/membervoucher/passVoucher";
	}

	@GetMapping("/membervoucher/bankClearing")
	private String bankClearing(Model model) {
		return "vouchers/voucherClearing";
	}

	@GetMapping("/membervoucher/bankClearing/security/{key}/bank/{selectedBank}")
	public ResponseEntity<Map<String, Object>> bankSecurity(@PathVariable int key,  @PathVariable String selectedBank) {
		Integer accessId = accessCtrlRepo.getAccessId(accessDec.VoucherClearing);
		boolean isValid = (accessId != null && key == accessId);

		Map<String, Object> response = new HashMap<>();
		response.put("valid", isValid);

		if (isValid) {
			int download = 1;
			int approved = 2;
			List<TableDto> mv = memberVoucherService.bankView(download, approved, selectedBank);
			response.put("access", mv);  // attach voucher list
		} else {
			response.put("access", Collections.emptyList());
		}

		return ResponseEntity.ok(response);
	}


//	@GetMapping("/membervoucher/bankClearing/security/{key}")
//	public ResponseEntity<Boolean> bankSecurity(@PathVariable int key){
//		Integer accessId = accessCtrlRepo.getAccessId(accessDec.VoucherClearing);
//		boolean isValid = (accessId != null && key == accessId);
//		return ResponseEntity.ok(isValid);
//	}

	@GetMapping("/membervoucher/bankClearing/update")
	@ResponseBody
	public ResponseEntity<String> updateVoucher(@RequestParam String id,
												@RequestParam int status) {
		if (status == 0) {
			MemberVoucher mv = memberVoucherRepository.findByvId(id);
			String cashStatus = "Cancelled";
			cashierRepo.updateBondCancelled(id, cashStatus);
			mv.setUpdatedDate(Date.valueOf(LocalDate.now()));
			mv.setPaymentStatus(paymentStatus.Reversed);
			mv.setVoucherDownloaded(0);
			mv.setVoucherApproved(0);
			memberVoucherService.rollbackmv(mv);
		} else if (status == 3) {
			MemberVoucher mv = memberVoucherRepository.findByvId(id);
			mv.setVoucherApprovedDate(Date.valueOf(LocalDate.now()));
			mv.setPaymentStatus(paymentStatus.Cleared);
			mv.setVoucherApproved(3);
			memberVoucherService.bankmv(mv);
		}
		return ResponseEntity.ok("Voucher updated successfully");
	}

	@GetMapping("/membervoucher/approvedVoucher")
	private String approvedVouchers(Model model) {
		int download = 1;
		int approved = 3;
		List<TableDto> mv = memberVoucherService.tableView(download, approved);
		model.addAttribute("mvoucher", mv);
		return "vouchers/voucher_step8";
	}

	@GetMapping("/membervoucher/approvedVoucher/details/{vId}")
	public String approvemv(@PathVariable String vId, Model model){
		MemberVoucherDTO mv = memberVoucherService.getdata(vId);
		model.addAttribute("mvDto",mv);
		return "vouchers/voucher_step9";
	}

	@GetMapping("/membervoucher/reGenerated")
	private String reAdvice(Model model){
		int regenrated = 1;
		List<MemberVoucher> readvice = memberVoucherService.findByregenrated(regenrated);
		model.addAttribute("mvoucher",readvice);
		return "vouchers/voucher_step5";
	}

	@GetMapping("/membervoucher/voucherReport")
	private String reports(Model model){
		List<ReportDTO> readvice = memberVoucherService.findApprovedReport();
		model.addAttribute("mvoucher",readvice);
		return "vouchers/voucher_step10";
	}

	@GetMapping("/membervoucher/createVoucher/details/{vId}/add/{vSln}/{amount}")
	public ResponseEntity<Map<String, String>> addVouchers(@PathVariable String vId, @PathVariable String vSln, @PathVariable Double amount, Model model){
		memberVoucherService.addVouchers(vId,vSln, amount);
		Map<String, String> response = new HashMap<>();
		response.put("success", "true");
		response.put("message", "Voucher added successfully!");

		return ResponseEntity.ok(response);
	}

//	public String incrementVId(String latestVid) {
//		// Extract numeric part and trim spaces
//		String numberPart = latestVid.substring(3).trim();
//		int number = Integer.parseInt(numberPart) + 1;
//		String newNumberPart = String.format("%0" + numberPart.length() + "d", number);
//		return "voc" + newNumberPart;
//	}

	@GetMapping("/membervoucher/createVoucher/print/{vId}")
	public ResponseEntity<byte[]> generatePdf(@PathVariable String vId, Model model){
		MemberVoucherDTO mv = memberVoucherService.getdata(vId);
		Context context = new Context();
		context.setVariable("mvDto",mv);
		byte[] pdfBytes = pdfService.generatePdf("vouchers/general_voucher", context);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=General_voucher.pdf")
				.contentType(MediaType.APPLICATION_PDF)
				.body(pdfBytes);
	}

	@GetMapping("/membervoucher/createVoucher/delete/{vId}")
	public String deletemv(@PathVariable String vId, Model model){
		memberVoucherService.deletevid(vId);
		return "redirect:/members/membervoucher/createVoucher";
	}

	@GetMapping("/membervoucher/createVoucher/details/{vId}")
	public String detailsmv(@PathVariable String vId, Model model){
		MemberVoucherDTO mv = memberVoucherService.getdata(vId);
		List<Tcosbanks> banks = tcosbankRepo.findAll();
		model.addAttribute("banks", banks);
		model.addAttribute("mvDto",mv);
		return "vouchers/voucher_step2_2";
	}

	@PostMapping("/membervoucher/createVoucher/details")
	public String updatemvs(@ModelAttribute MemberVoucherDTO mv, Model model){
		memberVoucherService.savemv(mv);
		return "redirect:/members/membervoucher/createVoucher";
	}

	@GetMapping("/membervoucher/createVoucher/details/{vId}/delete/{vSln}")
	public ResponseEntity<String> addVouchers(@PathVariable String vId, @PathVariable String vSln, Model model){
		memberVoucherService.deleteVouchers(vId,vSln);
		return ResponseEntity.ok("Voucher deleted successfully!");
	}


	@GetMapping("membervoucher/fetch/accountHead")
	private ResponseEntity<Map<String,List<AccountHeads>>> fetchGl(){
		Map<String,List<AccountHeads>> response = new HashMap<>();
		List<AccountHeads> accountHeadsList = memberVoucherService.getAccountHeads();
		response.put("accountHead",accountHeadsList);
		return ResponseEntity.ok(response);
	}

	@GetMapping("membervoucher/add/ticketNumber/{userId}")
	private ResponseEntity<Map<String, MemberVoucherDTO>> seaarchUserId(@PathVariable String userId) {
		Map<String, MemberVoucherDTO> response = new HashMap<String, MemberVoucherDTO>();
		MemberVoucherDTO memberVouchers = memberVoucherService.findByUserId(userId);
		response.put("mvdto", memberVouchers);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/membervoucher/createVoucher/advice/cbi/{chequeNo}")
	public ResponseEntity<byte[]> MemberVoucherCBI(@RequestParam String fullName, @PathVariable String chequeNo, Model model) {
		// Prepare the context for the PDF
		Context context = new Context();
		context.setVariable("Cheque", chequeNo);
		context.setVariable("ACCOUNTNO", "1231489568");
		double totalAmount = 0;
		String prepared="";
		// Prepare data for the PDF (list of member vouchers)
		List<printDto> memberVoucherDTO = new ArrayList<>();
		String bankname = "Central Bank Of India, Telco";
		List<MemberVoucher> mvs = memberVoucherService.findByPaymentTypeAndPreparedBy(Paymenttype.ADVICE, bankname, 0, fullName);
		for (MemberVoucher mv : mvs) {
			totalAmount += mv.getTotalAmount();
			printDto dto = new printDto();
			dto.setAccountNum(mv.getAccountNum());
			dto.setName(mv.getName());
			dto.setVId(mv.getVId());
			dto.setUserId(mv.getUserId());
			dto.setCategory(mv.getCategory());
			dto.setAmount(mv.getTotalAmount());
			dto.setBranchName(mv.getBranchName());
			if (prepared == null || prepared.isEmpty()) {
				prepared = mv.getPreparedBy();
			}
			memberVoucherDTO.add(dto);
		}
		// Group and sum amounts for each userId
		// Add the memberVoucherDTO to context for use in Thymeleaf template
		context.setVariable("memberVoucherDTO", memberVoucherDTO);
		context.setVariable("totalAmount", totalAmount);
		context.setVariable("BankName", bankname);
		context.setVariable("prepared", fullName);

		// Generate the PDF bytes
		byte[] pdfBytes = pdfService.generatePdf("vouchers/voucher_advice", context);

//		for (MemberVoucher mv : mvs) {
//			mv.setVoucherDownloaded(1);
//			mv.setUpdatedDate(Date.valueOf(LocalDate.now()));
//		}
//		memberVoucherService.update(mvs);

		LocalDate currentDate = LocalDate.now();
		String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		String filename = "Vouchers_cbi_" + formattedDate + ".pdf";

		// Return the PDF as a download
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment;  filename=" + filename)
				.contentType(MediaType.APPLICATION_PDF).body(pdfBytes);
	}
	@GetMapping("/membervoucher/createVoucher/advice/sbi/{chequeNo}")
	public ResponseEntity<byte[]> MemberVoucherSBI(@RequestParam String fullName, @PathVariable String chequeNo, Model model) {
		// Prepare the context for the PDF
		Context context = new Context();
		context.setVariable("Cheque", chequeNo);
		context.setVariable("ACCOUNTNO", "10590737320");
		double totalAmount = 0;
		String prepared="";
		// Prepare data for the PDF (list of member vouchers)
		List<printDto> memberVoucherDTO = new ArrayList<>();
		String bankname = "State Bank Of India, Telco";
		List<MemberVoucher> mvs = memberVoucherService.findByPaymentTypeAndPreparedBy(Paymenttype.ADVICE, bankname, 0, fullName);
		for (MemberVoucher mv : mvs) {
			totalAmount += mv.getTotalAmount();
			printDto dto = new printDto();
			dto.setAccountNum(mv.getAccountNum());
			dto.setName(mv.getName());
			dto.setVId(mv.getVId());
			dto.setUserId(mv.getUserId());
			dto.setCategory(mv.getCategory());
			dto.setAmount(mv.getTotalAmount());
			dto.setBranchName(mv.getBranchName());
			dto.setAccountNum(mv.getAccountNum());
			if (prepared == null || prepared.isEmpty()) {
				prepared = mv.getPreparedBy();
			}
			memberVoucherDTO.add(dto);
		}
		// Group and sum amounts for each userId
		// Add the memberVoucherDTO to context for use in Thymeleaf template
		context.setVariable("memberVoucherDTO", memberVoucherDTO);
		context.setVariable("totalAmount", totalAmount);
		context.setVariable("BankName", bankname);
		context.setVariable("prepared", fullName);

		// Generate the PDF bytes
		byte[] pdfBytes = pdfService.generatePdf("vouchers/voucher_advice", context);

//		for (MemberVoucher mv : mvs) {
//			mv.setVoucherDownloaded(1);
//			mv.setUpdatedDate(Date.valueOf(LocalDate.now()));
//		}
//		memberVoucherService.update(mvs);

		LocalDate currentDate = LocalDate.now();
		String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		String filename = "Vouchers_sbi_" + formattedDate + ".pdf";

		// Return the PDF as a download
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment;  filename=" + filename)
				.contentType(MediaType.APPLICATION_PDF).body(pdfBytes);
	}

	@GetMapping("/membervoucher/createVoucher/advice/jscb/{chequeNo}")
	public ResponseEntity<byte[]> MemberVoucherJSCB(@RequestParam String fullName, @PathVariable String chequeNo, Model model) {
		// Prepare the context for the PDF
		Context context = new Context();
		context.setVariable("Cheque", chequeNo);
		context.setVariable("ACCOUNTNO", "1126-3521000060");
		double totalAmount = 0;
		String prepared="";
		// Prepare data for the PDF (list of member vouchers)
		List<printDto> memberVoucherDTO = new ArrayList<>();
		String bankname = "Jharkhand State Co-operative Bank, Telco";
		List<MemberVoucher> mvs = memberVoucherService.findByPaymentTypeAndPreparedBy(Paymenttype.ADVICE, bankname, 0, fullName);
		for (MemberVoucher mv : mvs) {
			totalAmount += mv.getTotalAmount();
			printDto dto = new printDto();
			dto.setAccountNum(mv.getAccountNum());
			dto.setName(mv.getName());
			dto.setVId(mv.getVId());
			dto.setUserId(mv.getUserId());
			dto.setCategory(mv.getCategory());
			dto.setAmount(mv.getTotalAmount());
			dto.setBranchName(mv.getBranchName());
			dto.setAccountNum(mv.getAccountNum());
			if (prepared == null || prepared.isEmpty()) {
				prepared = mv.getPreparedBy();
			}
			memberVoucherDTO.add(dto);
		}
		// Group and sum amounts for each userId
		// Add the memberVoucherDTO to context for use in Thymeleaf template
		context.setVariable("memberVoucherDTO", memberVoucherDTO);
		context.setVariable("totalAmount", totalAmount);
		context.setVariable("BankName", bankname);
		context.setVariable("prepared", fullName);

		// Generate the PDF bytes
		byte[] pdfBytes = pdfService.generatePdf("vouchers/voucher_advice", context);

//		for (MemberVoucher mv : mvs) {
//			mv.setVoucherDownloaded(1);
//			mv.setUpdatedDate(Date.valueOf(LocalDate.now()));
//		}
//		memberVoucherService.update(mvs);

		LocalDate currentDate = LocalDate.now();
		String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		String filename = "Vouchers_jscb_" + formattedDate + ".pdf";

		// Return the PDF as a download
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment;  filename=" + filename)
				.contentType(MediaType.APPLICATION_PDF).body(pdfBytes);
	}

	@GetMapping("/membervoucher/createVoucher/neft/cbi")
	public ResponseEntity<byte[]> neftCBI(@RequestParam String fullName, Model model) {
		// Prepare the context for the PDF
		Context context = new Context();
		context.setVariable("ACCOUNTNO", "1231489568");
		double totalAmount = 0;
//		String prepared="";
		// Prepare data for the PDF (list of member vouchers)
		List<printDto> memberVoucherDTO = new ArrayList<>();
		String bankname = "Central Bank Of India, Telco";
		List<MemberVoucher> mvs = memberVoucherService.findByPaymentTypeAndPreparedBy(Paymenttype.NEFT, bankname, 0, fullName);
		for (MemberVoucher mv : mvs) {
			totalAmount += mv.getTotalAmount();
			printDto dto = new printDto();
			dto.setAccountNum(mv.getAccountNum());
			dto.setName(mv.getName());
			dto.setVId(mv.getVId());
			dto.setUserId(mv.getUserId());
			dto.setCategory(mv.getCategory());
			dto.setAmount(mv.getTotalAmount());
			dto.setBranchName(mv.getBranchName());
			dto.setBankName(mv.getBankName());
			dto.setIfscCode(mv.getIfscCode());
			dto.setPayableAt(mv.getPayableAt());
//
			memberVoucherDTO.add(dto);
		}
		// Group and sum amounts for each userId
		// Add the memberVoucherDTO to context for use in Thymeleaf template
		context.setVariable("memberVoucherDTO", memberVoucherDTO);
		context.setVariable("totalAmount", totalAmount);
		context.setVariable("BankName", bankname);
		context.setVariable("prepared", fullName);

		// Generate the PDF bytes
		byte[] pdfBytes = pdfService.generatePdf("vouchers/voucher_neft", context);

//		for (MemberVoucher mv : mvs) {
//			mv.setVoucherDownloaded(1);
//			mv.setUpdatedDate(Date.valueOf(LocalDate.now()));
//		}
//		memberVoucherService.update(mvs);

		LocalDate currentDate = LocalDate.now();
		String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		String filename = "Vouchers_neftCBI_" + formattedDate + ".pdf";

		// Return the PDF as a download
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment;  filename=" + filename)
				.contentType(MediaType.APPLICATION_PDF).body(pdfBytes);
	}

	@GetMapping("/membervoucher/createVoucher/neft/sbi")
	public ResponseEntity<byte[]> neftSBI(@RequestParam String fullName, Model model) {
		// Prepare the context for the PDF
		Context context = new Context();
		context.setVariable("ACCOUNTNO", "10590737320");
		double totalAmount = 0;
//		String prepared="";
		// Prepare data for the PDF (list of member vouchers)
		List<printDto> memberVoucherDTO = new ArrayList<>();
		String bankname = "State Bank Of India, Telco";
		List<MemberVoucher> mvs = memberVoucherService.findByPaymentTypeAndPreparedBy(Paymenttype.NEFT, bankname, 0, fullName);
		for (MemberVoucher mv : mvs) {
			totalAmount += mv.getTotalAmount();
			printDto dto = new printDto();
			dto.setAccountNum(mv.getAccountNum());
			dto.setName(mv.getName());
			dto.setVId(mv.getVId());
			dto.setUserId(mv.getUserId());
			dto.setCategory(mv.getCategory());
			dto.setAmount(mv.getTotalAmount());
			dto.setBranchName(mv.getBranchName());
			dto.setBankName(mv.getBankName());
			dto.setIfscCode(mv.getIfscCode());
			dto.setPayableAt(mv.getPayableAt());
//			if (prepared == null || prepared.isEmpty()) {
//				prepared = mv.getPreparedBy();
//			}
			memberVoucherDTO.add(dto);
		}
		// Group and sum amounts for each userId
		// Add the memberVoucherDTO to context for use in Thymeleaf template
		context.setVariable("memberVoucherDTO", memberVoucherDTO);
		context.setVariable("totalAmount", totalAmount);
		context.setVariable("BankName", bankname);
		context.setVariable("prepared", fullName);

		// Generate the PDF bytes
		byte[] pdfBytes = pdfService.generatePdf("vouchers/voucher_neft", context);

//		for (MemberVoucher mv : mvs) {
//			mv.setVoucherDownloaded(1);
//			mv.setUpdatedDate(Date.valueOf(LocalDate.now()));
//		}
//		memberVoucherService.update(mvs);

		LocalDate currentDate = LocalDate.now();
		String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		String filename = "Vouchers_neftSBI_" + formattedDate + ".pdf";

		// Return the PDF as a download
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment;  filename=" + filename)
				.contentType(MediaType.APPLICATION_PDF).body(pdfBytes);
	}

	@GetMapping("/membervoucher/createVoucher/neft/jscb")
	public ResponseEntity<byte[]> neftJSCB(@RequestParam String fullName, Model model) {
		// Prepare the context for the PDF
		Context context = new Context();
		context.setVariable("ACCOUNTNO", "1126-3521000060");
		double totalAmount = 0;
//		String prepared="";
		// Prepare data for the PDF (list of member vouchers)
		List<printDto> memberVoucherDTO = new ArrayList<>();
		String bankname = "Jharkhand State Co-operative Bank, Telco";
		List<MemberVoucher> mvs = memberVoucherService.findByPaymentTypeAndPreparedBy(Paymenttype.NEFT, bankname, 0, fullName);
		for (MemberVoucher mv : mvs) {
			totalAmount += mv.getTotalAmount();
			printDto dto = new printDto();
			dto.setAccountNum(mv.getAccountNum());
			dto.setName(mv.getName());
			dto.setVId(mv.getVId());
			dto.setUserId(mv.getUserId());
			dto.setCategory(mv.getCategory());
			dto.setAmount(mv.getTotalAmount());
			dto.setBranchName(mv.getBranchName());
			dto.setBankName(mv.getBankName());
			dto.setIfscCode(mv.getIfscCode());
			dto.setPayableAt(mv.getPayableAt());
//			if (prepared == null || prepared.isEmpty()) {
//				prepared = mv.getPreparedBy();
//			}
			memberVoucherDTO.add(dto);
		}
		// Group and sum amounts for each userId
		// Add the memberVoucherDTO to context for use in Thymeleaf template
		context.setVariable("memberVoucherDTO", memberVoucherDTO);
		context.setVariable("totalAmount", totalAmount);
		context.setVariable("BankName", bankname);
		context.setVariable("prepared", fullName);

		// Generate the PDF bytes
		byte[] pdfBytes = pdfService.generatePdf("vouchers/voucher_neft", context);

//		for (MemberVoucher mv : mvs) {
//			mv.setVoucherDownloaded(1);
//			mv.setUpdatedDate(Date.valueOf(LocalDate.now()));
//		}
//		memberVoucherService.update(mvs);

		LocalDate currentDate = LocalDate.now();
		String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		String filename = "Vouchers_neftJSCB_" + formattedDate + ".pdf";

		// Return the PDF as a download
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment;  filename=" + filename)
				.contentType(MediaType.APPLICATION_PDF).body(pdfBytes);
	}

	@GetMapping("/membervoucher/createVoucher/cheque")
	public ResponseEntity<byte[]> cheques(@RequestParam String fullName, Model model) {
		// Prepare the context for the PDF
		Context context = new Context();
		double totalAmount = 0;
		String prepared="";
		// Prepare data for the PDF (list of member vouchers)
		List<printDto> memberVoucherDTO = new ArrayList<>();
		List<MemberVoucher> mvs = memberVoucherService.findByCheque(Paymenttype.CHEQUE, 0, fullName);
		for (MemberVoucher mv : mvs) {
			totalAmount += mv.getTotalAmount();
			printDto dto = new printDto();
			dto.setAccountNum(mv.getAccountNum());
			dto.setName(mv.getName());
			dto.setVId(mv.getVId());
			dto.setUserId(mv.getUserId());
			dto.setCategory(mv.getCategory());
			dto.setAmount(mv.getTotalAmount());
			dto.setBranchName(mv.getBranchName());
			dto.setBankName(mv.getBankName());
			dto.setChequeNo(mv.getChequeNo());
			dto.setIfscCode(mv.getIfscCode());
			dto.setPayableAt(mv.getPayableAt());
			dto.setPaymentBank(mv.getPaymentBank());
			if (prepared == null || prepared.isEmpty()) {
				prepared = mv.getPreparedBy();
			}
			memberVoucherDTO.add(dto);
		}
		// Group and sum amounts for each userId
		// Add the memberVoucherDTO to context for use in Thymeleaf template
		context.setVariable("memberVoucherDTO", memberVoucherDTO);
		context.setVariable("totalAmount", totalAmount);
		context.setVariable("prepared", fullName);

		// Generate the PDF bytes
		byte[] pdfBytes = pdfService.generatePdf("vouchers/voucher_cheque", context);

//		for (MemberVoucher mv : mvs) {
//			mv.setVoucherDownloaded(1);
//			mv.setUpdatedDate(Date.valueOf(LocalDate.now()));
//		}
//		memberVoucherService.update(mvs);

		LocalDate currentDate = LocalDate.now();
		String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		String filename = "Vouchers_cheque_" + formattedDate + ".pdf";

		// Return the PDF as a download
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment;  filename=" + filename)
				.contentType(MediaType.APPLICATION_PDF).body(pdfBytes);
	}

	@GetMapping("membervoucher/markAdviceDownloaded")
	public ResponseEntity<?> markAdviceDownloaded(@RequestParam String fullName, @RequestParam String chequeNo,
												  @RequestParam String type) {
		String bank = "";
		Paymenttype paymentType;
		if (type.startsWith("ADVICE")) {
			paymentType = Paymenttype.ADVICE;

			// Determine bank name
			if (type.contains("SBI")) {
				bank = "State Bank Of India, Telco";
			} else if (type.contains("JSCB")) {
				bank = "Jharkhand State Co-operative Bank, Telco";
			} else if (type.contains("CBI")) {
				bank = "Central Bank Of India, Telco";
			} else {
				return ResponseEntity.badRequest().body("Unknown bank for ADVICE");
			}
			List<MemberVoucher> loans = memberVoucherService.findByPaymentTypeAndPreparedBy(Paymenttype.ADVICE, bank, 0, fullName);
			for (MemberVoucher mv : loans) {
				mv.setChequeNo(chequeNo);
				mv.setVoucherDownloaded(1);
				mv.setUpdatedDate(Date.valueOf(LocalDate.now()));
			}
			memberVoucherService.updateAdvice(loans);
			return ResponseEntity.ok("ADVICE marked as downloaded");

		}
		else if(type.startsWith("NEFT")){
			paymentType = Paymenttype.NEFT;

			// Determine bank name
			if (type.contains("SBI")) {
				bank = "State Bank Of India, Telco";
			} else if (type.contains("JSCB")) {
				bank = "Jharkhand State Co-operative Bank, Telco";
			} else if (type.contains("CBI")) {
				bank = "Central Bank Of India, Telco";
			} else {
				return ResponseEntity.badRequest().body("Unknown bank for ADVICE");
			}
			List<MemberVoucher> loans = memberVoucherService.findByPaymentTypeAndPreparedBy(Paymenttype.NEFT, bank, 0,fullName);
			for (MemberVoucher mv : loans) {
				mv.setVoucherDownloaded(1);
				mv.setUpdatedDate(Date.valueOf(LocalDate.now()));
			}
			memberVoucherService.update(loans);
			return ResponseEntity.ok("NEFT marked as downloaded");
		}
		else if (type.startsWith("CHEQUE")) {
			paymentType = Paymenttype.CHEQUE;

			List<MemberVoucher> loans = memberVoucherService.findByCheque(Paymenttype.CHEQUE, 0, fullName);
			for (MemberVoucher mv : loans) {
				mv.setVoucherDownloaded(1);
				mv.setUpdatedDate(Date.valueOf(LocalDate.now()));
			}
			memberVoucherService.update(loans); // Assuming same method used for CHEQUE too
			return ResponseEntity.ok("CHEQUE marked as downloaded");

		} else {
			return ResponseEntity.badRequest().body("Invalid payment type");
		}

	}

/*	@GetMapping("/membervouchers/delete")
	public ResponseEntity<Map<String, String>> deleteMemberVoucher(@RequestParam int mvId) {
		Map<String, String> response = new HashMap<>();
		MemberVoucher mv = memberVoucherService.findById(mvId);

		if (mv != null) {
			Integer deleted = memberVoucherService.delete(mvId);
			if (deleted != 0) {
				response.put("status", "deleted");
			} else {
				response.put("status", "unable to delete");
			}
			return ResponseEntity.ok(response);
		}

		response.put("status", "not available");
		return ResponseEntity.ok(response);
	}*/

}
