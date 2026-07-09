package com.telco.members.tcos.controller;
import com.telco.members.tcos.dto.UserregisterDTO;
import com.telco.members.tcos.dto.loanDTO;
import com.telco.members.tcos.dto.LoanUpdateDTO;
import com.telco.members.tcos.repository.loanRepository;
import com.telco.members.tcos.model.*;
import com.telco.members.tcos.repository.AccessCtrlRepo;
import com.telco.members.tcos.service.GuarantorService;
import com.telco.members.tcos.service.LoanService;
import com.telco.members.tcos.service.UserService;

import com.telco.members.tcos.service.PdfService;
import com.telco.members.tcos.service.CompanyService;
import com.telco.members.tcos.service.BankService;
import com.telco.members.tcos.service.CDShareDeductionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.springframework.stereotype.Controller;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.ibm.icu.text.RuleBasedNumberFormat;
import java.util.Locale;


@Controller
@RequestMapping("/members/loan")
public class LoanController {

    @Autowired
    private LoanService loanService;

	@Autowired
	private loanRepository loanRepository;
    @Autowired
    private AccessCtrlRepo accessCtrlRepo;
    @Autowired
    private UserService userService;   
   
    @Autowired
    private GuarantorService guarantorService;
    
    @Autowired
	private CDShareDeductionService cdShareDeductionService; 
    
    @Autowired
	private BankService bankService;
    
	@Autowired
	private CompanyService companyService;
	
	@Autowired
	private PdfService pdfService;

    //private final ConcurrentHashMap<String, List<Guarantor>> userGuarantorMap = new ConcurrentHashMap<>();

    @Value("${my.api.key}")
    private String apiKey;
    
 // Helper method to get current authenticated user with request-level caching
 	private User getCurrentUser(HttpServletRequest request, HttpSession session) {
 	    // Check session first
 	    User sessionUser = (User) session.getAttribute("user");
 	    if (sessionUser != null) {
 	        return sessionUser;
 	    }
 	    
 	    // Then check request cache
 	    User cachedUser = (User) request.getAttribute("currentUser");
 	    if (cachedUser != null) {
 	        return cachedUser;
 	    }
 	    
 	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
 	    if (authentication == null || !authentication.isAuthenticated()) {
 	        throw new RuntimeException("User not authenticated");
 	    }
 	    
 	    String uid = authentication.getName();
 	    System.out.println("User Details " + uid);  // 17-03-2026 bashishtha
 	    User user = userService.findByUserId(uid);
 	    
 	    if (user == null) {
 	        throw new RuntimeException("User not found");
 	    }
 	    
 	    // Cache in both request and session
 	    request.setAttribute("currentUser", user);
 	    session.setAttribute("user", user);
 	    
 	    return user;
 	}
	// Helper method to check account status
	//private boolean checkAccountStatus(AccountStatus status) {
	//	return status == AccountStatus.ACTIVE;
	//}

	// Helper method to update session user data
	private void updateSessionUser(HttpSession session, User user) {
		session.setAttribute("user", user);
	}
	// session attributes
	private static final String SESSION_USER = "sessionUser";
	private static final String SESSION_CD_SHARE = "sessionCDShare";
	private static final String SESSION_BANKS = "sessionBanks";
	private static final String SESSION_COMPANY = "sessionCompany";
	private static final String SESSION_LOAN_AMT = "sessionLoanAmt";
	private static final String SESSION_INSTALMENT_AMT = "sessionInstalmentAmt";
	private static final String SESSION_APPLICATION_TYPE = "sessionApplicationType";
	private static final String SESSION_DUE_LOAN = "sessionDueLoan";
	private static final String SESSION_ELIGIBLE = "sessionEligible";
	private static final String SESSION_LOAN_APPLIED_AMT = "sessionLoanAppliedAmt";
	private static final String SESSION_LOAN_REASON = "sessionLoanReason";
	private static final String SESSION_NEW_MEMBER = "sessionNewMember";
	private static final String SESSION_LOAN_INFO = "sessionLoanInfo";
	private static final String SESSION_LOAN_APPLIED = "sessionLoanApplied";
	private static final String SESSION_PREV_LOAN = "sessionPrevLoan";
	private static final String SESSION_INSTALMENT_LEFT = "sessionInstalmentLeft";
	private static final String SESSION_DESCRIPTION = "sessionDescription";
	
	// initialize session attributes
	private void initializeSession(HttpSession session) {
		session.setAttribute(SESSION_LOAN_APPLIED, false);
	}

	// clear loan application session data
	private void clearLoanSession(HttpSession session) {
		session.removeAttribute(SESSION_LOAN_AMT);
		session.removeAttribute(SESSION_INSTALMENT_AMT);
		session.removeAttribute(SESSION_APPLICATION_TYPE);
		session.removeAttribute(SESSION_DUE_LOAN);
		session.removeAttribute(SESSION_ELIGIBLE);
		session.removeAttribute(SESSION_LOAN_APPLIED_AMT);
		session.removeAttribute(SESSION_LOAN_REASON);
		session.removeAttribute(SESSION_NEW_MEMBER);
		session.removeAttribute(SESSION_LOAN_INFO);
		session.removeAttribute(SESSION_PREV_LOAN);
		session.removeAttribute(SESSION_INSTALMENT_LEFT);
		session.removeAttribute(SESSION_DESCRIPTION);
	}
	
	private String validateLoanApplication(User user, CDShareDeductions cdShareDeductions, double loanAmount,
			Model model) {
		if (user == null) {
			return "User information not found";
		}

		if (cdShareDeductions == null) {
			return "Share deduction information not found";
		}

		String userType = user.getUserType();
		if (userType == null || userType.trim().isEmpty()) {
			return "User type not defined";
		}
		userType = userType.trim();

		double shareTotal = cdShareDeductions.getShareDeductionTotal();
		if (shareTotal <= 0) {
			return "Invalid share amount: " + shareTotal;
		}

		double multiplier;
		boolean isNewMember;
		if ("New Member".equalsIgnoreCase(userType)) {
			multiplier = 5.0;
			isNewMember = true;
		} else if ("Existing Member".equalsIgnoreCase(userType)) {
			multiplier = 10.0;
			isNewMember = false;
		} else {
			multiplier = 5.0;
			isNewMember = true;
		}

		double maxAllowedLoan = shareTotal * multiplier;

		if (loanAmount <= 0) {
			return "Loan amount must be greater than 0";
		}

		if (loanAmount > maxAllowedLoan) {
			return String.format(
					"Loan amount ₹%.2f exceeds maximum allowed ₹%.2f for %s. You can get %.0f times your share amount (₹%.2f)",
					loanAmount, maxAllowedLoan, userType, multiplier, shareTotal);
		}

		if (shareTotal < 16000) {
			return "Minimum share amount of ₹16,000 required to apply for loan. Current share amount: ₹" + shareTotal;
		}

		return null; // No validation errors
	}
    @GetMapping("/loan_status")
    public String listLoans(Model model) {
        List<loanDTO> loans = loanService.getAllLoans(); // Corrected method name
        System.out.println("Member Voucher details in this "+loans);
        model.addAttribute("loans", loans); // Changed attribute name to plural
        return "Loan/loan_approval"; // Return the Thymeleaf template view
    }

    @GetMapping("/checkedLoan")
    public String checkLoans(Model model) {
        List<loanDTO> loans = loanService.getLoanByloanStatus(LoanStatus.CHECKED); // Corrected method name
        model.addAttribute("loans", loans); // Changed attribute name to plural
        return "Loan/checkloan"; // Return the Thymeleaf template view
    }

    @GetMapping("/verifiedLoan")
    public String verifiedLoans(Model model) {
        List<loanDTO> loans = loanService.getLoanByloanStatus(LoanStatus.VERIFIED); // Corrected method name
        model.addAttribute("loans", loans); // Changed attribute name to plural
        return "Loan/verifyloan"; // Return the Thymeleaf template view
    }

    @GetMapping("/loan_status/details/{Id}")
    public String loanDetails(@PathVariable int Id, Model model) {
        List<Guarantor> gua = guarantorService.getGuarantors(Id);
        loanDTO loanDTO = loanService.getLoanByLoanIdType1(Id);
        model.addAttribute("gua", gua);
        model.addAttribute("loan", loanDTO); // Add the loan details to the model
        return "Loan/loan_saction"; // Return the Thymeleaf template for loan details
    }
    
    @GetMapping("/loan_status/sendSmsToGarantor")
    public ResponseEntity<Map<String, Boolean>> loanSurities(@RequestParam("userId")String userId){
    	
    	List<Guarantor> lst = guarantorService.findByUserId(userId);
		User usr = userService.getUserByUserId(userId);

		List<String> nums = new ArrayList<>();
		for (Guarantor s : lst) {
			nums.add(s.getPhone_num() + "");
		}
		String allNumbers = String.join(",", nums);

		Map<String, Boolean> responseMap = new HashMap<>();
		OkHttpClient client = new OkHttpClient();

		String senderId = "TCOSMS";

		String message = "Dear Sir/Mam,\r\n"
				+ "You have been added as Loan Surety in "+usr.getName()+" Ticket no. "+userId+".\r\n"
				+ "\r\n"
				+ "-Sigma e Solution Pvt. Ltd.";

		RequestBody formBody = new FormBody.Builder().add("module", "TRANS_SMS")
				.add("apikey", apiKey).add("to", allNumbers).add("from", senderId)
				.add("msg", message).build();

		Request request = new Request.Builder().url("https://2factor.in/API/R1/").post(formBody).build();
		
		try (Response response = client.newCall(request).execute()) {
			if (response.isSuccessful()) {
					responseMap.put("msg", true);
				} else {
					responseMap.put("msg", false);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
    	
    	return ResponseEntity.ok(responseMap);
    }

    @GetMapping("/checkedLoan/details/{Id}")
    public String checkedDetails(@PathVariable int Id, Model model) {
        List<Guarantor> gua = guarantorService.getGuarantors(Id);
        loanDTO loanDTO = loanService.getLoanByloanId(Id);
        model.addAttribute("guarantor", gua);
        model.addAttribute("loan", loanDTO); // Add the loan details to the model
        return "Loan/checkedloan"; // Return the Thymeleaf template for loan details
    }

    @GetMapping("/verifiedLoan/details/{Id}")
    public String verifiedDetails(@PathVariable int Id, Model model) {
        List<Guarantor> gua = guarantorService.getGuarantors(Id);
        loanDTO loanDTO = loanService.getLoanByloanId(Id);
        model.addAttribute("loan", loanDTO);
        model.addAttribute("guarantor", gua);
        return "Loan/Verifiedloan"; // Return the Thymeleaf template for loan details
    }

    @PostMapping("/checkedLoan/update")
    public String verifyLoan(@ModelAttribute loanDTO loandTO, Model model){
        loanService.updateLoans(loandTO);
        return "redirect:/members/loan/checkedLoan";
    }

    @GetMapping("/loan_status/security/{key}")
    public ResponseEntity<Boolean> pendingSecurity(@PathVariable int key){
        Integer accessId = accessCtrlRepo.getAccessId(accessDec.Loan_Pending);
        boolean isValid = (accessId != null && key == accessId);
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/checkedLoan/security/{key}")
    public ResponseEntity<Boolean> checkSecurity(@PathVariable int key){
        Integer accessId = accessCtrlRepo.getAccessId(accessDec.Loan_Check);
        boolean isValid = (accessId != null && key == accessId);
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/verifiedLoan/security/{key}")
    public ResponseEntity<Boolean> verifySecurity(@PathVariable int key){
        Integer accessId = accessCtrlRepo.getAccessId(accessDec.Loan_Verify);
        boolean isValid = (accessId != null && key == accessId);
        return ResponseEntity.ok(isValid);
    }

    @PostMapping("/verifiedLoan/update")
    public String approveLoan(@ModelAttribute loanDTO loandTO){
        LoanStatus status = loandTO.getLoanStatus();
        if(status == LoanStatus.APPROVED){
            LoanType types = loandTO.getLoanType();
            if (types == LoanType.COMMODITY) {
                loanService.approvedLoansType2(loandTO);
            } else {
                loanService.approvedLoans(loandTO);
            }
        }
        else if(status == LoanStatus.CHECKED){
            loanService.updateLoans(loandTO);
        }
        return "redirect:/members/loan/verifiedLoan";
    }

    @GetMapping("/loan_status/details/{userId}/fetchUser/{ticketNo}")
    private ResponseEntity<UserregisterDTO> seaarchUserId(@PathVariable String userId, @PathVariable String ticketNo){
        User users = userService.getUsers(ticketNo);
        if (users != null) {
            UserregisterDTO userDTO = userService.convertToDTO(users);
            return ResponseEntity.ok(userDTO);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/loan_status/details/{Id}/{userId}/addUser/{userIds}")
    public ResponseEntity<String> addGuarantor(@PathVariable int Id, @PathVariable String userId, @PathVariable String userIds){
            guarantorService.addGuarantor(userId,Id,userIds);
        return ResponseEntity.ok("Guarantor added successfully!");
    }

    @PostMapping("/loan_status/details/updated")
    public String updateLoan(@ModelAttribute loanDTO loandTO){
        loanService.updateLoans(loandTO);
        return "redirect:/members/loan/loan_status";
    }

    @GetMapping("/loan_status/details/{Id}/{userId}/deleteUser/{userIds}")
    public ResponseEntity<String> removeGuarantor(@PathVariable int Id, @PathVariable String userId, @PathVariable String userIds) {
        guarantorService.removeGuarantor(Id, userId , userIds);
        return ResponseEntity.ok("Guarantor removed successfully");
    }

    @GetMapping("/commodityloan")
    public String listLoans_c(Model model) {
        List<loanDTO> loans = loanService.getAllLoanstype1(); // Corrected method name      
        model.addAttribute("loans", loans); // Changed attribute name to plural
        return "Loan/cl_details"; // Return the Thymeleaf template view
    }

    @GetMapping("/commodityLoan/details/{Id}")
    private String monitorCLoan(@PathVariable int Id,Model model) {

        loanDTO detail= loanService.getByloanId(Id);
        model.addAttribute("mv",detail);

        return "Loan/approvedCLoanDetails";
    }

    @GetMapping("/normalloan")
    public String listLoans_p(Model model) {
        List<loanDTO> loans = loanService.getAllLoanstype2(); // Corrected method name
        System.out.println("Member Voucher details in this "+loans);
        model.addAttribute("loans", loans); // Changed attribute name to plural
        return "Loan/pl_details"; // Return the Thymeleaf template view
    }

    @GetMapping("/personalLoan/details/{Id}")
    private String monitorPLoan(@PathVariable int Id,Model model) {

        loanDTO detail= loanService.getByloanId(Id);
        model.addAttribute("mv",detail);

        return "Loan/approvedPLoanDetails";
    }

	@GetMapping("/loanUpdate")
	public String loanUpdatePage() {

		return "Loan/loan_update";

	}

	@GetMapping("/getLoan/{userId}")
	@ResponseBody
	public ResponseEntity<?> getLoan(
			@PathVariable String userId) {

		Loan personalLoan =
				loanRepository.findTop1ByUserIdAndLoanTypeOrderByLoanApproveDateDesc(
						userId,
						LoanType.PERSONAL);

		Loan commodityLoan =
				loanRepository.findTop1ByUserIdAndLoanTypeOrderByLoanApproveDateDesc(
						userId,
						LoanType.COMMODITY);

		if(personalLoan == null && commodityLoan == null){

			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body("User not found");
		}

		LoanUpdateDTO dto = new LoanUpdateDTO();

		dto.setUserId(userId);

		if(personalLoan != null){

			dto.setPersonalBalance(personalLoan.getLoanBalance());

			dto.setPersonalLoanApproveDate(personalLoan.getLoanApproveDate());

		}

		if(commodityLoan != null){

			dto.setCommodityBalance(commodityLoan.getLoanBalance());

			dto.setCommodityLoanApproveDate(commodityLoan.getLoanApproveDate());

		}

		return ResponseEntity.ok(dto);
	}
	@PostMapping("/updateLoan")
	@ResponseBody
	public String updateLoan(
			@org.springframework.web.bind.annotation.RequestBody LoanUpdateDTO dto){

		// PERSONAL loan
		Loan personalLoan =
				loanRepository.findTop1ByUserIdAndLoanTypeOrderByLoanApproveDateDesc(
						dto.getUserId(),
						LoanType.PERSONAL);

		if(personalLoan != null){

			personalLoan.setLoanBalance(dto.getPersonalBalance());

			personalLoan.setLoanApproveDate(dto.getPersonalLoanApproveDate());

			loanRepository.save(personalLoan);
		}


		// COMMODITY loan
		Loan commodityLoan =
				loanRepository.findTop1ByUserIdAndLoanTypeOrderByLoanApproveDateDesc(
						dto.getUserId(),
						LoanType.COMMODITY);

		if(commodityLoan != null){

			commodityLoan.setLoanBalance(dto.getCommodityBalance());

			commodityLoan.setLoanApproveDate(dto.getCommodityLoanApproveDate());

			loanRepository.save(commodityLoan);
		}

		return "Updated Successfully";
	}
  
    @GetMapping("/loan_apply")
    public String loancdapply(Model model) {
         return "Loan/LoanApply"; // Return the Thymeleaf template view
    }
	@GetMapping("/cd_share")
	public String cdShare(@RequestParam("userId") String userId, HttpSession session, Model model, HttpServletRequest request) {
	   // User currentUser = getCurrentUser(request,session);
		//String uid = authentication.getName();
	 //   System.out.println("User Details " + userId);  // 18-03-2026 bashishtha
	    User user = userService.findByUserId(userId);
	    session.setAttribute("userId", userId);
	//   System.out.println("User Details " + user);
//	//	boolean allAccess = checkAccountStatus(user.getAccountStatus());
//
	CDShareDeductions cdShareDeductions = cdShareDeductionService.findLatestRecordByUserId(userId);
	// System.out.println("cdShareDeductions Details " + cdShareDeductions);
		if (cdShareDeductions == null) {
			cdShareDeductions = new CDShareDeductions();
		}
		model.addAttribute("user", user);
		model.addAttribute("cdShareDeductions", cdShareDeductions);
		model.addAttribute("activePage", "CD Share");
//
//		// Update session
    	updateSessionUser(session, user);
//
//		// return allAccess ? "cd_share" : "account_pending";
		return "Loan/cd_share";
	}
	
	@PostMapping("/cd_share")
	public String changeCdShare(@RequestParam("amt") double amt, HttpSession session, Model model, HttpServletRequest request) {
		
		User currentUser = getCurrentUser(request,session);
		model.addAttribute("user", currentUser);
		LocalDate today = LocalDate.now();
		// Update session with modified user
		 	updateSessionUser(session, currentUser);
		boolean isEditableDate = today.getDayOfMonth() >= 1 && today.getDayOfMonth() <= 15;
		
		if (isEditableDate && amt >= loanService.findLoanInfo().getCdMinimum()) {
			try {
				
				currentUser.setCdCut(amt);
				
				// Update user and refresh cache
				
		 	    currentUser = userService.updateUser(currentUser);
			    request.setAttribute("currentUser", currentUser); // Update cache with fresh data

				// Update session with modified user
			 //	updateSessionUser(session, currentUser);

				model.addAttribute("message", "CD Cut amount updated successfully!");
			} catch (Exception e) {
				model.addAttribute("messageerr", "Failed to update CD Cut amount. Please try again.");
				System.out.println("error -> " + e);
			}
		} else {
			model.addAttribute("messageerr", "CD Cut amount can only be changed between the 1st and 15th of the month "
					+ "and amount should not be more than or equal to 2000.");
		}

		// Reload CD share data
		CDShareDeductions cdShareDeductions = cdShareDeductionService.findLatestRecordByUserId(currentUser.getUserId());
		if (cdShareDeductions == null) {
			cdShareDeductions = new CDShareDeductions();
		}

		model.addAttribute("cdShareDeductions", cdShareDeductions);
		model.addAttribute("isEditableDate", isEditableDate);

		return "Loan/cd_share";
	}
	// For personal loan
	@GetMapping("/termsper")
	public String loanTerms(Model model, HttpSession session) {
	model.addAttribute("activePage", "Personal Loans");
	initializeSession(session);
//
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		String userId = authentication.getName();
//
//		// Fetch user details and store in session
//		User user = userService.findByUserId(userId);
//		session.setAttribute(SESSION_USER, user);
//
//		AccountStatus status = user.getAccountStatus();
//		boolean allAccess = checkAccountStatus(status);
//		if (!allAccess)
//			return "account_pending";
//		else
			return "Loan/personal_loan_tc";
	}
	@GetMapping("/termsper/personalloan")
	public String personalLoan(Model model, HttpSession session) {
		model.addAttribute("activePage", "Personal Loans");
		String userId = session.getAttribute("userId").toString();		
	User user = userService.findByUserId(userId);   //(User) session.getAttribute(SESSION_USER);
	
		if (user == null) {
			return "redirect:/login";
		}

		CDShareDeductions cdShareDeductions = cdShareDeductionService.findLatestRecordByUserId(user.getUserId());
		if (cdShareDeductions == null) {
			cdShareDeductions = new CDShareDeductions();
		}
		session.setAttribute(SESSION_CD_SHARE, cdShareDeductions);

		Banks banks = bankService.getByUserId(user.getUserId());
		session.setAttribute(SESSION_BANKS, banks);

		model.addAttribute("user", user);
		model.addAttribute("cdShare", cdShareDeductions);

		double loanDue = loanService.findByOutStandingLoan(user.getUserId(), LoanType.PERSONAL);
		session.setAttribute(SESSION_DUE_LOAN, loanDue);
		model.addAttribute("loandue", loanDue);

		// for instalment
		System.out.println("testing for getDateOfRetirement");
		java.util.Date utilDateOfRetirement = user.getDateOfRetirement();
		if(utilDateOfRetirement == null)
		{
			model.addAttribute("message", "You need to fill date of birth and  the process.");
			return "Loan/LoanApply";
		}
		java.sql.Date sqlDateOfRetirement = new java.sql.Date(utilDateOfRetirement.getTime());	
		
		java.util.Date utilDate = new Date();
		java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
		
		int monthsDiff = loanService.findInstalment(sqlDateOfRetirement, sqlDate);
		session.setAttribute(SESSION_INSTALMENT_LEFT, monthsDiff);
		model.addAttribute("monthsDiff", monthsDiff);

		LoanInfo loanInfo = loanService.findLoanInfo();
		session.setAttribute(SESSION_LOAN_INFO, loanInfo);
		// testing stat added 28-02-2026
		Loan prevLoan = loanService.findByLastLoanTypeAndStatus(user.getUserId(), LoanType.PERSONAL,
				LoanStatus.PENDING);
		if (prevLoan == null)
			prevLoan = loanService.findByLastLoanTypeAndStatus(user.getUserId(), LoanType.PERSONAL,
					LoanStatus.APPROVED);
		// System.out.println(prevLoan); // added 28-02-2026
		
		// testing end
		

		String userType = user.getUserType();
		// System.out.println(userType);
		boolean isNewMember = "New Member".equalsIgnoreCase(userType != null ? userType.trim() : "");
		session.setAttribute(SESSION_NEW_MEMBER, isNewMember ? 1 : 0);
      
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		Calendar cal3 = Calendar.getInstance();
		
		cal1.setTime(new Date());
		try {
			cal2.setTime(user.getApprovedAt());
		     cal3.setTime(prevLoan.getLoanApproveDate());  // Added 28-02-2026
		} catch (Exception e) {
			model.addAttribute("message", "You need to get your account approved and complete the process.");
			return "Loan/personal_loan";
		}

		int yearDiff = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
		double monthDiff = cal1.get(Calendar.MONTH) - cal2.get(Calendar.MONTH);
		double totalMonths = yearDiff * 12 + monthDiff;
		
		// // 28-02-2026 start
		int yearDiff1 = cal1.get(Calendar.YEAR) - cal3.get(Calendar.YEAR);
		double monthDiff1 = cal1.get(Calendar.MONTH) - cal3.get(Calendar.MONTH);  // 28-02-2026
		double totalMonths1 = yearDiff1 * 12 + monthDiff1;
		if ("Existing Member".equalsIgnoreCase(userType)) {
			if (totalMonths1 < 4.0 || cdShareDeductions.getShareDeductionTotal() < 16000) {
				model.addAttribute("message", "You have to wait till 4 months from date of enrollment to apply for loan."
						+ " You also have to make sure your share amount is paid full. After you have completed the process you can apply");
			}
			
		//	System.out.println(totalMonths1); 
		}
		
		 // testing 28-02-2026 end
		else
	  {
		if (totalMonths < 4.0 || cdShareDeductions.getShareDeductionTotal() < 16000) {
			model.addAttribute("message", "You have to wait till 10 months from date of enrollment to apply for loan."
					+ " You also have to make sure your share amount is paid full. After you have completed the process you can apply");
		} else if (totalMonths >= 4.0) {

			Date applyDate = new Date();

			Date lDate = null;
			try {
				lDate = loanService.findByLastLoanApproveDateByUserId(user.getUserId(), LoanType.PERSONAL);
			} catch (Exception e) {
				System.out.println(e);
			}

			ApplicationType applicationType = ApplicationType.NA;
			String description= "";
			if (lDate != null) {

				cal1 = Calendar.getInstance();
				cal2 = Calendar.getInstance();

				cal1.setTime(applyDate);
				cal2.setTime(lDate);

				yearDiff = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
				monthDiff = cal1.get(Calendar.MONTH) - cal2.get(Calendar.MONTH);
				totalMonths = yearDiff * 12 + monthDiff;

				if (cdShareDeductions.getShareDeductionTotal() >= 16000 && totalMonths >= 12) {
					applicationType = ApplicationType.NA;
					model.addAttribute("aplimsgna", "No Application needed");
					description = "No Application needed.";
				} else if (cdShareDeductions.getShareDeductionTotal() >= 16000 && totalMonths >= 4
						&& totalMonths < 12) {
					applicationType = ApplicationType.Application;
					model.addAttribute("aplimsga", "<strong>Application needed</strong> for this loan");
					description = "Application to the society is needed in this"
							+ " condition as last loan taken is between 4 and 12 months.";
				} else if(totalMonths < 4) {
					applicationType = ApplicationType.Management_approval;
					model.addAttribute("aplimsgma",
							"There is a need of an <strong>appllication to the management</strong> for this loan approval.");
					description = "Application to management is needed as last loan taken was within 4 months.";
				}
			} else {
				if (cdShareDeductions.getShareDeductionTotal() >= 16000 && totalMonths >= 12.0 
			            && cdShareDeductions.getShareDeductionTotal() <= loanInfo.getShareAmount()) {
			        model.addAttribute("aplimsgna", "There is no requirement for application in this loan.");
			        applicationType = ApplicationType.NA;
			        description = "No Application needed.";
			    } else if (cdShareDeductions.getShareDeductionTotal() > 16000 && totalMonths >= 4.0) {
			        model.addAttribute("aplimsga","This loan requires <strong>Application</strong> for approval.");
			        applicationType = ApplicationType.Application;
			        description = "There is a need of application as joining date criteria is not fulfilled.";
			    } else { 
			    	model.addAttribute("aplimsgma","Cannot get loan now.");
			    }
			}
			session.setAttribute(SESSION_APPLICATION_TYPE, applicationType);
			session.setAttribute(SESSION_DESCRIPTION, description);
		}
	 }
		return "Loan/personal_loan";
	}
	
	@PostMapping("/termsper/personalloan")
	public String applyPersonalLoan(Model model, @RequestParam("loanamt") double loanamt,
			@RequestParam("instalmentAmt") double instalmentAmt, @RequestParam("loanReason") String loanReason,
			@RequestParam("monthsDiff") int monthsDiff, // This stays the same
			HttpSession session) {
		model.addAttribute("activePage", "Personal Loans");
		String userId = session.getAttribute("userId").toString();
		// System.out.println(userId);
	   User user = userService.findByUserId(userId); 

//		User user = (User) session.getAttribute(SESSION_USER);
//		 System.out.println(user); // 03-03-2026 dueLoan
		if (user == null) {
			return "redirect:/login";
		}
     
		CDShareDeductions cdShareDeductions = (CDShareDeductions) session.getAttribute(SESSION_CD_SHARE);

		String validationError = validateLoanApplication(user, cdShareDeductions, loanamt, model);
		if (validationError != null) {
			model.addAttribute("message", validationError);
			model.addAttribute("user", user);
			model.addAttribute("cdShare", cdShareDeductions);
			model.addAttribute("monthsDiff", monthsDiff);
			model.addAttribute("loanamt", loanamt);

			Double loanDue = (Double) session.getAttribute(SESSION_DUE_LOAN);
			if (loanDue != null) {
				model.addAttribute("loandue", loanDue);
			}

			return "Loan/personal_loan";
		}
		String userType = user.getUserType();
		//  03-03-2026 start for testing
		if ("Existing Member".equalsIgnoreCase(userType != null ? userType.trim() : ""))
		{
			Double loanDue = (Double) session.getAttribute(SESSION_DUE_LOAN);
			// System.out.println(loanamt);  // 03-03-2026 dueLoan
			// System.out.println(dueLoan); // 03-03-2026 dueLoan
//			int loantotalamt = (int) Math.ceil((double) (loanamt + loanDue));
//			int loanamtact = 400000 -  (int) Math.ceil((double) (loanDue));
//			
			double maxLoan = 0.0;
			double shareTotal = cdShareDeductions != null ? cdShareDeductions.getShareDeductionTotal() : 0;
			// double multiplier = isNewMember ? 5.0 : 10.0;
			// 08-03-2026
			 // 1. Define the target date (01-01-2025)
	        Calendar targetCal = Calendar.getInstance();
	        targetCal.set(2024, Calendar.DECEMBER, 31, 0, 0, 0); // Month is 0-based for Calendar

	        // 2. Define a date to compare (e.g., 15-12-2024)
	        Calendar cal3 = Calendar.getInstance();
	        cal3.setTime(user.getCreatedAt());
	     //   Calendar compareCal =Calendar.getInstance();  // cal3.setTime(User.createdAt()); 
	     //   compareCal.set(2024, Calendar.DECEMBER, 15, 0, 0, 0);
	     
	        // 3. Compare the dates using before()
	        if (cal3.before(targetCal)) {
	        	 maxLoan = shareTotal * 10.0;
	        } else {
	        	 maxLoan = shareTotal * 5.0;
	        }

	     // 08-03-2026

			double overhead = loanDue + loanamt - maxLoan;
			double applied = loanamt;
			double eligible;
		//	System.out.println(overhead);  // 07-03-2026
		//	System.out.println(loanDue);  // 07-03-2026
		//	System.out.println(loanamt);  // 07-03-2026
			System.out.println(maxLoan);  // 07-03-2026
			
//			if (overhead < 0) {
//				eligible = maxLoan - loanDue;
//			} else {
//				loanamt = loanamt - overhead;
//				eligible = loanamt;
//			}
			eligible = maxLoan - loanDue;
			if (eligible <= 0) {
				session.setAttribute(SESSION_LOAN_APPLIED, true);
				model.addAttribute("messageerr", "You are not eligible for loan. Eligible amount is less.");
			}
			
		// System.out.println(eligible);
		// System.out.println(loanamt);
			if (loanamt > eligible) {				
			model.addAttribute("messageerr", "You are not eligible for loan. Eligible amount is less maximum amount " + eligible);
		model.addAttribute("loanamt", loanamt);
		model.addAttribute("loandue", loanDue);
		model.addAttribute("monthsDiff", monthsDiff);
		return "Loan/personal_loan";
		
			}
		}
	
		// 03-03-2026 end for testing
		session.setAttribute(SESSION_LOAN_REASON, loanReason);
		session.setAttribute(SESSION_LOAN_APPLIED_AMT, loanamt);
		session.setAttribute(SESSION_LOAN_AMT, loanamt);
		session.setAttribute(SESSION_INSTALMENT_AMT, instalmentAmt);
		session.setAttribute(SESSION_INSTALMENT_LEFT, monthsDiff);

		model.addAttribute("monthsDiff", monthsDiff);
		
		

		ApplicationType applicationType = (ApplicationType) session.getAttribute(SESSION_APPLICATION_TYPE);
		if (applicationType == ApplicationType.NA) {
			model.addAttribute("aplimsgna", "No Application needed");
		} else if (applicationType == ApplicationType.Application) {
			model.addAttribute("aplimsga", "Application needed for this loan");
		} else {
			model.addAttribute("aplimsgma",
					"There is a need of an appllication to the management for this loan approval.");
		}

		
		boolean isNewMember = "New Member".equalsIgnoreCase(userType != null ? userType.trim() : "");
		session.setAttribute(SESSION_NEW_MEMBER, isNewMember ? 1 : 0);

		double dueLoan = (Double) session.getAttribute(SESSION_DUE_LOAN);
		int currInstalment = (int) Math.ceil((double) (loanamt + dueLoan) / instalmentAmt);
	

		if (currInstalment > monthsDiff) {
			model.addAttribute("messageerr",
					"EMI period (" + currInstalment + " months) exceeds time until retirement (" + monthsDiff
							+ " months). " + "Please increase your installment amount to at least ₹"
							+ Math.ceil((loanamt + dueLoan) / monthsDiff));
			model.addAttribute("loanamt", loanamt);
			return "Loan/personal_loan";
		}

		if (currInstalment > 50) {
			model.addAttribute("messageerr", "Emi should be less than 50 months. "
					+ "Current amount entered makes instalments more than 50, enter another higher amount.");
			model.addAttribute("loanamt", loanamt);
			return "Loan/personal_loan";
		}


		return "redirect:personalloan/check";
	}
	
	@GetMapping("/termsper/personalloan/check")
	public String finalApplypersonal(Model model, HttpSession session) {
		model.addAttribute("activePage", "Personal Loans");
		session.setAttribute(SESSION_LOAN_APPLIED, false);

	//	User user = (User) session.getAttribute(SESSION_USER);
		String userId = session.getAttribute("userId").toString();
		// System.out.println(userId);
	   User user = userService.findByUserId(userId); 
		if (user == null) {
			return "redirect:/login";
		}
		CDShareDeductions cdShareDeductions = (CDShareDeductions) session.getAttribute(SESSION_CD_SHARE);
		Double loanamt = (Double) session.getAttribute(SESSION_LOAN_AMT);
		Loan prevLoan = loanService.findByLastLoanTypeAndStatus(user.getUserId(), LoanType.PERSONAL,
				LoanStatus.PENDING);
		if (prevLoan == null)
			prevLoan = loanService.findByLastLoanTypeAndStatus(user.getUserId(), LoanType.PERSONAL,
					LoanStatus.APPROVED);
		if (prevLoan == null)
			prevLoan = loanService.findByLastLoanTypeAndStatus(user.getUserId(), LoanType.PERSONAL, 
					LoanStatus.CHECKED);
		if (prevLoan == null)
			prevLoan = loanService.findByLastLoanTypeAndStatus(user.getUserId(), LoanType.PERSONAL,
					LoanStatus.VERIFIED);
		
		model.addAttribute("prevLoan", prevLoan);
		session.setAttribute(SESSION_PREV_LOAN, prevLoan);

		if (loanamt != null) {
			String validationError = validateLoanApplication(user, cdShareDeductions, loanamt, model);
			if (validationError != null) {
				model.addAttribute("messageerr", "Validation failed: " + validationError);
				session.setAttribute(SESSION_LOAN_APPLIED, true);
				return "Loan/personal_loan_chk";
			}
		}

		LoanStatus prevLoanStatus = loanService.findLastLoanStatus(user.getUserId(), LoanType.PERSONAL);

		if (prevLoanStatus == LoanStatus.PENDING || prevLoanStatus == LoanStatus.CHECKED
				|| prevLoanStatus == LoanStatus.VERIFIED) {
			session.setAttribute(SESSION_LOAN_APPLIED, true);
			model.addAttribute("messageerr",
					"Previous loan is not approved, " + "you can apply for new loan after previous loan is approved. "
							+ "You can check in your loan history for the loans you applied.");
		}

		double loanDue = (Double) session.getAttribute(SESSION_DUE_LOAN);
		Integer newMemberInt = (Integer) session.getAttribute(SESSION_NEW_MEMBER);
		boolean isNewMember = (newMemberInt != null && newMemberInt == 1);

		double shareTotal = cdShareDeductions != null ? cdShareDeductions.getShareDeductionTotal() : 0;
		double multiplier = isNewMember ? 5.0 : 10.0;
		double maxLoan = shareTotal * multiplier;

		double overhead = loanDue + loanamt - maxLoan;
		double applied = loanamt;
		double eligible;
	//	System.out.println(overhead);  // 07-03-2026
	//	System.out.println(loanDue);  // 07-03-2026
	//	System.out.println(loanamt);  // 07-03-2026
	//	System.out.println(maxLoan);  // 07-03-2026
		
		if (overhead < 0) {
			eligible = maxLoan - loanDue;
		} else {
			loanamt = loanamt - overhead;
			eligible = loanamt;
		}

		if (eligible <= 0) {
			session.setAttribute(SESSION_LOAN_APPLIED, true);
			model.addAttribute("messageerr", "You are not eligible for loan. Eligible amount is less.");
		}
		
		String userType = user.getUserType();
		if ("Existing Member".equalsIgnoreCase(userType != null ? userType.trim() : "")) {
			if (eligible > shareTotal * 10) {
				eligible = shareTotal * 10;
			}
		} else {
			if (eligible > shareTotal * 5) {
				eligible = shareTotal * 5;
			}
		}
	//	System.out.println(eligible);  // 07-03-2026
		session.setAttribute(SESSION_ELIGIBLE, eligible);
		model.addAttribute("user", user);
		model.addAttribute("eligibleloan", eligible);
		model.addAttribute("loandue", loanDue);
		model.addAttribute("appliedloan", applied);

		Banks banks = (Banks) session.getAttribute(SESSION_BANKS);
		model.addAttribute("bank", banks);

		return "Loan/personal_loan_chk";
	}
	
	@PostMapping("/termsper/personalloan/check")
	public String personalApply(Model model, HttpSession session) {
		model.addAttribute("activePage", "Personal Loans");

	//	User user = (User) session.getAttribute(SESSION_USER);
		String userId = session.getAttribute("userId").toString();
		// System.out.println(userId);
	   User user = userService.findByUserId(userId); 
		if (user == null) {
			return "redirect:/login";
		}

		Loan prevLoan = (Loan) session.getAttribute(SESSION_PREV_LOAN);
		System.out.println(prevLoan);  // 28-02-2026
		
		model.addAttribute("prevLoan", prevLoan);

		Double loanamt = (Double) session.getAttribute(SESSION_LOAN_AMT);
		Double instalmentAmt = (Double) session.getAttribute(SESSION_INSTALMENT_AMT);
		String loanReason = (String) session.getAttribute(SESSION_LOAN_REASON);
		Double loanAppliedAmt = (Double) session.getAttribute(SESSION_LOAN_APPLIED_AMT);
		ApplicationType applicationType = (ApplicationType) session.getAttribute(SESSION_APPLICATION_TYPE);
		CDShareDeductions cdShareDeductions = (CDShareDeductions) session.getAttribute(SESSION_CD_SHARE);
		LoanInfo loanInfo = (LoanInfo) session.getAttribute(SESSION_LOAN_INFO);
		Boolean loanApplied = (Boolean) session.getAttribute(SESSION_LOAN_APPLIED);
		String description = (String) session.getAttribute(SESSION_DESCRIPTION);
		double eligible = (double) session.getAttribute(SESSION_ELIGIBLE);

		if (loanamt != null) {
			String validationError = validateLoanApplication(user, cdShareDeductions, loanamt, model);
			if (validationError != null) {
				model.addAttribute("messageerr", "Final validation failed: " + validationError);
				session.setAttribute(SESSION_LOAN_APPLIED, true);
				Banks banks = (Banks) session.getAttribute(SESSION_BANKS);
				model.addAttribute("bank", banks);
				return "Loan/personal_loan_chk";
			}
		}

		// Create new Loan object
		Loan personalLoan = new Loan();
		personalLoan.setUserId(user.getUserId());
		
		if(loanamt<=eligible)
			personalLoan.setLoanAmount(loanamt);
		else
			personalLoan.setLoanAmount(eligible);
		
		personalLoan.setLoanApplyDate(new Date());
		personalLoan.setLoanStatus(LoanStatus.PENDING);
		personalLoan.setLoanType(LoanType.PERSONAL);
		personalLoan.setIntrestRate(loanInfo.getInterestOnNormal());
		personalLoan.setApplicationType(applicationType);
		personalLoan.setUpdatedAt(new Date());
		personalLoan.setInstalmentAmount(instalmentAmt);
		personalLoan.setEmiBalance(50);
		personalLoan.setEmiPaid(0);
		personalLoan.setAppliedLoan(loanAppliedAmt);
		personalLoan.setLoanReason(loanReason);
		personalLoan.setDescription(description);

		// Calculate time difference
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		Calendar cal3 = Calendar.getInstance();

		cal1.setTime(new Date());
		cal2.setTime(user.getApprovedAt());
		if (prevLoan != null) 
		{
			cal3.setTime(prevLoan.getLoanApproveDate());  // Added 28-02-2026  prevLoan
		}
		

		int yearDiff = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
		double monthDiff = cal1.get(Calendar.MONTH) - cal2.get(Calendar.MONTH);
		double totalMonths = yearDiff * 12 + monthDiff;
		
		System.out.println(totalMonths);  // 28-02-2026
		 // 28-02-2026 start
		int yearDiff1 = cal1.get(Calendar.YEAR) - cal3.get(Calendar.YEAR);
		double monthDiff1 = cal1.get(Calendar.MONTH) - cal3.get(Calendar.MONTH);  // 28-02-2026
		double totalMonths1 = yearDiff1 * 12 + monthDiff1;
	
		
	
		String userType = user.getUserType();
		if ("Existing Member".equalsIgnoreCase(userType)) {
			if (prevLoan == null ) 
			{
				totalMonths1 = 12 ; //System.out.println("total month"+totalMonths1); 
			//	System.out.println(shareAmount); // totalMonths1 = 12.0; // Added 02-04-2026  prevLoan
			}
			if (totalMonths1 < 4.0 || cdShareDeductions.getShareDeductionTotal() < 16000) {
				model.addAttribute("message", "You have to wait till 4 months from date of enrollment to apply for loan."
						+ " You also have to make sure your share amount is paid full. After you have completed the process you can apply");
			}
		double shareAmount = cdShareDeductions.getShareDeductionTotal();
		//System.out.println(totalMonths1); 
		//System.out.println(shareAmount); 
			if (totalMonths1 >= 4.0 && shareAmount > 0) {
				loanService.save(personalLoan);
				model.addAttribute("message", "Loan application submitted successfully! "
						+ "You can download your loan application PDF.");
				model.addAttribute("downloadUrl", "/tcos/members/loan/termsper/personalloan/downloadPdf");
				personalLoan.setApplicationType(ApplicationType.NA);
			} else {
				model.addAttribute("messageerr",
						"You need to meet the minimum 4-month enrollment period and have some share amount before applying for a loan.");
			}
			session.setAttribute(SESSION_LOAN_APPLIED, true);
		//	System.out.println(totalMonths1); 
		
		}
		else
		{
			try {
				if (loanApplied != null && loanApplied) {
					model.addAttribute("messageerr",
							"You can go back. " + "You can check in your loan history for the loans you applied.");
				} else if (totalMonths < 4.0 || cdShareDeductions.getShareDeductionTotal() < 16000) {
					model.addAttribute("messageerr",
							"You have to wait till 10 months from date of enrollment to apply for a loan."
									+ " You also have to make sure your share amount is paid in full. After you have completed the process, you can apply.");
				} else if (cdShareDeductions.getShareDeductionTotal() >= 16000 && totalMonths >= 4.0) {

					Date applyDate = personalLoan.getLoanApplyDate();

					try {
						prevLoan = loanService.findByLastLoanTypeAndStatus(user.getUserId(), LoanType.PERSONAL,
								LoanStatus.PAID);
					} catch (Exception e) {
						System.out.println(e);
					}

					if (prevLoan != null) {
						// Previous loan exists
						Calendar repaymentCalendar = Calendar.getInstance();
						repaymentCalendar.setTime(prevLoan.getEndDate());
						repaymentCalendar.add(Calendar.DAY_OF_MONTH, 60);
						Date sixtyDaysFromRepayment = repaymentCalendar.getTime();

						cal2.setTime(prevLoan.getLoanApproveDate());
						cal2.add(Calendar.MONTH, 4);
						Date fourMonthsFromLastLoan = cal2.getTime();

						Date earliestEligibilityDate = (fourMonthsFromLastLoan.before(sixtyDaysFromRepayment))
								? fourMonthsFromLastLoan
								: sixtyDaysFromRepayment;

						if (applyDate.before(earliestEligibilityDate)) {
							model.addAttribute("messageerr",
									"You are not eligible to apply for a loan yet. "
											+ "Eligibility starts after either 60 days from the repayment date "
											+ "or 4 months from the last loan date, whichever is earlier.");
						} else {
							handleLoanConditions(model, personalLoan, totalMonths, session);
						}
					} else {
						// First time loan application - Check based on current share amount multiplier
						double shareAmount = cdShareDeductions.getShareDeductionTotal();

						if (totalMonths >= 4.0 && shareAmount > 0) {
							loanService.save(personalLoan);
							model.addAttribute("message", "Loan application submitted successfully! "
									+ "You can download your loan application PDF.");
							model.addAttribute("downloadUrl", "/tcos/members/loan/termsper/personalloan/downloadPdf");
							personalLoan.setApplicationType(ApplicationType.NA);
						} else {
							model.addAttribute("messageerr",
									"You need to meet the minimum 4-month enrollment period and have some share amount before applying for a loan.");
						}
					}
				}
				session.setAttribute(SESSION_LOAN_APPLIED, true);
			} catch (Exception e) {
				model.addAttribute("messageerr", "Failed to submit loan application. Please try again.");
				e.printStackTrace();
			}
		}

	

		Banks banks = (Banks) session.getAttribute(SESSION_BANKS);
		model.addAttribute("user", user);
		model.addAttribute("bank", banks);
		model.addAttribute("personalloan", personalLoan);
		model.addAttribute("eligibleloan",eligible);

		return "Loan/personal_loan_chk";
	}
	
	// Helper method to handle conditions based on share deductions and total months
	private void handleLoanConditions(Model model, Loan personalLoan, double totalMonths, HttpSession session) {
		CDShareDeductions cdShareDeductions = (CDShareDeductions) session.getAttribute(SESSION_CD_SHARE);
		LoanInfo loanInfo = (LoanInfo) session.getAttribute(SESSION_LOAN_INFO);

		if (cdShareDeductions.getShareDeductionTotal() >= loanInfo.getShareAmount() && totalMonths >= 12) {
			loanService.save(personalLoan);
			personalLoan.setApplicationType(ApplicationType.NA);
			model.addAttribute("message", "Loan application submitted successfully! "
					+ "You can check in your loan history for the loans you applied.");
		} else if (cdShareDeductions.getShareDeductionTotal() >= loanInfo.getShareAmount() && totalMonths >= 4
				&& totalMonths < 12) {
			loanService.save(personalLoan);
			personalLoan.setApplicationType(ApplicationType.Application);
			model.addAttribute("message", "Loan application submitted successfully but "
					+ "you need to submit an application to the society. Once the application is cleared, you can get the loan. "
					+ "You can check in your loan history for the loans you applied.");
		} else {
			loanService.save(personalLoan);
			personalLoan.setApplicationType(ApplicationType.Management_approval);
			model.addAttribute("message", "Loan application submitted successfully but "
					+ "you need to submit an application to the society for management approval. If cleared, you will get your loan. "
					+ "You can check in your loan history for the loans you applied.");
		}
	}
	
	@GetMapping("/termsper/personalloan/downloadPdf")
	public ResponseEntity<byte[]> downloadPdf(HttpSession session) {
	  //  User user = (User) session.getAttribute(SESSION_USER);
		String userId = session.getAttribute("userId").toString();		
	    User user = userService.findByUserId(userId); 
	    if (user == null) {
	        System.out.println("Error on PDF generation - User not found in session");
	        return ResponseEntity.badRequest().build();
	    }

	    // Get application type from session
	    ApplicationType applicationType = (ApplicationType) session.getAttribute(SESSION_APPLICATION_TYPE);
	    String description = (String) session.getAttribute(SESSION_DESCRIPTION);
	    
	    if (applicationType == null) {
	        applicationType = ApplicationType.NA;
	    }

	    Company company = companyService.findbyCompanyId(user.getCompanyId());
	    if (company == null) {
	        company = new Company();
	    }
	    session.setAttribute(SESSION_COMPANY, company);

	    Loan lloan = loanService.findLastApproveLoan(user.getUserId(), LoanType.PERSONAL);
	    CDShareDeductions cdShareDeductions = (CDShareDeductions) session.getAttribute(SESSION_CD_SHARE);
	    Banks banks = (Banks) session.getAttribute(SESSION_BANKS);
	    Double loanamt = (Double) session.getAttribute(SESSION_LOAN_AMT);
	    Double instalmentAmt = (Double) session.getAttribute(SESSION_INSTALMENT_AMT);
	    Double dueLoan = (Double) session.getAttribute(SESSION_DUE_LOAN);
	    Double eligible = (Double) session.getAttribute(SESSION_ELIGIBLE);
	    String loanReason = (String) session.getAttribute(SESSION_LOAN_REASON);
	    LoanInfo loanInfo = (LoanInfo) session.getAttribute(SESSION_LOAN_INFO);

	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	    DecimalFormat df = new DecimalFormat("#.00");
	    
	    loanamt = loanamt + dueLoan;
	    // Current date and year for letter references
	    Date currentDate = new Date();
	    String currentDateStr = dateFormat.format(currentDate);
	    String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

	    Context context = new Context();
	    
	    // Common variables for all templates
	    context.setVariable("userName", safeStr(user.getName()));
	    context.setVariable("ticketNumber", safeStr(user.getUserId()));
	    context.setVariable("address", safeStr(user.getCurrentAddr()));
	    context.setVariable("companyName", safeStr(company.getCompanyName()));
	    context.setVariable("deptNo", safeStr(user.getBcNumber()));
	    context.setVariable("servicePeriod", user.getSerivcePeriod() != ""? safeStr(user.getSerivcePeriod()):"              ");
	    context.setVariable("dateOfRetirement", 
	        user.getDateOfRetirement() != null ? dateFormat.format(user.getDateOfRetirement()) : "              ");
	    context.setVariable("loanAmount", df.format(safeDouble(loanamt)));
	   // context.setVariable("dueLoan", df.format(safeDouble(dueLoan)));
	    context.setVariable("monthlyInstalment", df.format(safeDouble(instalmentAmt)));
	    context.setVariable("interestRate", loanInfo != null ? safeStr(loanInfo.getInterestOnNormal()) : "             ");
	    context.setVariable("loanReason", safeStr(loanReason));
	    context.setVariable("shareTotal", 
	        cdShareDeductions != null ? df.format(safeDouble(cdShareDeductions.getShareDeductionTotal())) : "0");
	    context.setVariable("outstandingLoan", df.format(safeDouble(dueLoan)));
	    context.setVariable("mobileNumber", safeStr(user.getPhoneNum()));
	    context.setVariable("email", safeStr(user.getEmail()));
	    context.setVariable("currentDate", currentDateStr);
	    context.setVariable("year", currentYear);
	    context.setVariable("approvalReason", safeStr(description));
	    context.setVariable("fathersname", safeStr(user.getFathersName()));
	    
	 // Add additional variables needed for the original form
        context.setVariable("loanType", "NORMAL");
        context.setVariable("bankName", banks != null ? safeStr(banks.getBankName()) : "                  ");
        context.setVariable("osLoan", df.format(safeDouble(dueLoan)));
        context.setVariable("dateOfLastLoan",
            (lloan != null && lloan.getLoanApproveDate() != null) ? dateFormat.format(lloan.getLoanApproveDate()) : "				");
        context.setVariable("loanTaken", lloan != null ? df.format(safeDouble(lloan.getLoanAmount())) : "0");
        context.setVariable("noOfEmiPaid", lloan != null ? safeStr(lloan.getEmiPaid()) : "0");
        context.setVariable("amountRecovered", lloan != null ? df.format(safeDouble(lloan.getLoanRepaid())) : "0");
        context.setVariable("loanOs", lloan != null ? df.format(safeDouble(lloan.getLoanBalance())) : "0");
        context.setVariable("cdBalance",
            cdShareDeductions != null ? df.format(safeDouble(cdShareDeductions.getCdDeductionTotal())) : "0");
        context.setVariable("accountNumber", banks != null ? safeStr(banks.getAccountNum()) : "					");
        context.setVariable("branch", banks != null ? safeStr(banks.getBranchName()) : "			");
        context.setVariable("ifsc", banks != null ? safeStr(banks.getIfscCode()) : "			");
        context.setVariable("eligible", df.format(safeDouble(eligible)));
        context.setVariable("dos", user.getDateOfRetirement() != null ? dateFormat.format(user.getDateOfRetirement()) : "			");

	    String imagePath = "";
	    try {
	        imagePath = getClass().getResource("/static/dist/images/company_logo.png").toExternalForm();
//	        System.out.println(imagePath);
	    } catch (Exception e) {
	        System.out.println(e);
	    }
	    context.setVariable("logoPath", imagePath);

	    // Determine which template to use and filename based on application type
	    String templateName;
	    String filename;
	    
	    switch (applicationType) {
	        case Management_approval:
	            templateName = "management_approval_letter";
	            filename = "management_approval_letter.pdf";
	            break;
	        case Application:
	            templateName = "society_approval_letter";
	            filename = "society_approval_letter.pdf";
	            break;
	        case NA:
	        default:
	            // Use the original loan application form for direct processing
	            templateName = "loan_application_normal";
	            filename = "loan_application.pdf";
	            
	            
	            break;
	    }

	    // Generate PDF
	    byte[] pdfBytes = pdfService.generatePdf(templateName, context);

	    return ResponseEntity.ok()
	        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
	        .contentType(MediaType.APPLICATION_PDF)
	        .body(pdfBytes);
	}
	
	// Helper method to avoid nulls
	String safeStr(Object val) {
		return val != null ? val.toString() : "";
	}

	double safeDouble(Double val) {
		return val != null ? val : 0.0;
	}
	
	@GetMapping("/termscom")
	public String loancTerms(Model model, HttpSession session) {
		model.addAttribute("activePage", "Commodity Loans");
		initializeSession(session);
		clearLoanSession(session);

		// Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		// String userId = authentication.getName();
		
		String userId = session.getAttribute("userId").toString();
		System.out.println(userId);
	    User user = userService.findByUserId(userId); 

		// User user = userService.findByUserId(userId);
		session.setAttribute(SESSION_USER, user);

	//	AccountStatus status = user.getAccountStatus();
	//	boolean allAccess = checkAccountStatus(status);
//		if (!allAccess)
//			return "account_pending";
//		else
			return "Loan/commodity_loan_tc";
	}
	
	@GetMapping("/termscom/commodityloan")
	public String commLoan(Model model, HttpSession session) {
		model.addAttribute("activePage", "Commodity Loans");

		// User user = (User) session.getAttribute(SESSION_USER);
		String userId = session.getAttribute("userId").toString();
		// System.out.println(userId);
	   User user = userService.findByUserId(userId); 
		if (user == null) {
			return "redirect:/login";
		}

		CDShareDeductions cdShareDeductions = cdShareDeductionService.findLatestRecordByUserId(user.getUserId());
		session.setAttribute(SESSION_CD_SHARE, cdShareDeductions);

		Banks banks = bankService.getByUserId(user.getUserId());
		session.setAttribute(SESSION_BANKS, banks);

		model.addAttribute("user", user);
		model.addAttribute("cdShare", cdShareDeductions);

		LoanInfo loanInfo = loanService.findLoanInfo();
		session.setAttribute(SESSION_LOAN_INFO, loanInfo);
		session.setAttribute(SESSION_LOAN_APPLIED, false); // added 22-04-2026

		if (user.getUserType().equals("New Member"))
			model.addAttribute("maxcomloan", loanInfo.getMaxCommodityLoan());
		else
			model.addAttribute("maxcomloan", 100000);

		return "Loan/comm_loan";
	}
	
	@PostMapping("/termscom/commodityloan")
	public String applyCommodityLoan(Model model, @RequestParam("instalmentAmt") double instalmentAmt,
			@RequestParam("loanReason") String loanReason, HttpSession session) {
		model.addAttribute("activePage", "Commodity Loans");

		// User user = (User) session.getAttribute(SESSION_USER);
		String userId = session.getAttribute("userId").toString();
		// System.out.println(userId);
	   User user = userService.findByUserId(userId); 
		if (user == null) {
			return "redirect:/login";
		}

		LoanInfo loanInfo = (LoanInfo) session.getAttribute(SESSION_LOAN_INFO);
		CDShareDeductions cdShareDeductions = (CDShareDeductions) session.getAttribute(SESSION_CD_SHARE);
		Boolean loanApplied = (Boolean) session.getAttribute(SESSION_LOAN_APPLIED);
		model.addAttribute("user", user);
		
		System.out.println("commodity loan test" + loanApplied);  // test 05-04-2026
		System.out.println("commodity loan test1" + loanInfo);
		System.out.println("commodity loan test1" + cdShareDeductions);
		double loanamt;
		if (user.getUserType().equals("New Member"))
			loanamt = loanInfo.getMaxCommodityLoan();
		else
			loanamt = 100000;

		session.setAttribute(SESSION_LOAN_REASON, loanReason);
		session.setAttribute(SESSION_LOAN_AMT, loanamt);
		session.setAttribute(SESSION_INSTALMENT_AMT, instalmentAmt);
		session.setAttribute(SESSION_LOAN_APPLIED_AMT, loanamt);

		double share;
		try {
			share = cdShareDeductions.getShareDeductionTotal();
		} catch (Exception e) {
			share = 0;
		}
		if (loanApplied != null && loanApplied) {
			model.addAttribute("messageerr",
					"You can go back. " + "You can check in your loan history for the loans you applied.");
		} else if (share < 16000) {
			model.addAttribute("messageerr",
					"You have to wait till 10 months from date of enrollment and complete share amount to 16000 to apply for loan."
							+ " You also have to make sure your share amount is paid full. After you have completed the process you can apply");
			session.setAttribute(SESSION_LOAN_APPLIED, true);
		} else if (share >= 16000) {
			double loan = loanService.findByLastLoanOutStandingByUserId(user.getUserId(), LoanType.COMMODITY);
			if (loan > 0.0) {
				model.addAttribute("messageerr", "You cannot get loan now. "
						+ "Clear your previous due amount and then you can apply for loan.");
				session.setAttribute(SESSION_LOAN_APPLIED, true);
			}
		}

		if (session.getAttribute(SESSION_LOAN_APPLIED) != null
				&& (Boolean) session.getAttribute(SESSION_LOAN_APPLIED)) {
			return "Loan/comm_loan";
		}

		int currInstalment = (int) Math.ceil((double) loanamt / instalmentAmt);

		if (currInstalment > 50) {
			model.addAttribute("messageerr", "Emi should be less than 50 months. "
					+ "Current amount entered makes instalments more than 50, enter another higher amount.");
			model.addAttribute("loanamt", loanamt);
			return "Loan/comm_loan";
		}

		return "redirect:commodityloan/check";
	}
	
	@GetMapping("/termscom/commodityloan/check")
	public String finalApplycommodity(Model model, HttpSession session) {
		model.addAttribute("activePage", "Commodity Loans");
		session.setAttribute(SESSION_LOAN_APPLIED, false);

	//	User user = (User) session.getAttribute(SESSION_USER);
		String userId = session.getAttribute("userId").toString();
		// System.out.println(userId);
	   User user = userService.findByUserId(userId); 
		if (user == null) {
			return "redirect:/login";
		}

		if (loanService.findLastLoanStatus(user.getUserId(), LoanType.COMMODITY) == LoanStatus.PENDING) {
			session.setAttribute(SESSION_LOAN_APPLIED, true);
			model.addAttribute("messageerr",
					"Previous loan is on pending status, "
							+ "you can apply for new loan after previous loan is approved. "
							+ "You can check in your loan history for the loans you applied.");
		}

		Double loanamt = (Double) session.getAttribute(SESSION_LOAN_AMT);
		session.setAttribute(SESSION_LOAN_APPLIED_AMT, loanamt);

		Loan prevLoan = loanService.findByLastLoanTypeAndStatus(user.getUserId(), LoanType.COMMODITY,
				LoanStatus.PENDING);
		if (prevLoan == null) {
			prevLoan = loanService.findByLastLoanTypeAndStatus(user.getUserId(), LoanType.COMMODITY,
					LoanStatus.APPROVED);
		}
		session.setAttribute(SESSION_PREV_LOAN, prevLoan);
		model.addAttribute("prevLoan", prevLoan);

		Banks banks = (Banks) session.getAttribute(SESSION_BANKS);
		model.addAttribute("bank", banks);
		model.addAttribute("user", user);
		return "Loan/commodity_loan_chk";
	}
	@PostMapping("/termscom/commodityloan/check")
	public String commodityApply(Model model, HttpSession session) {
		model.addAttribute("activePage", "Commodity Loans");

		// User user = (User) session.getAttribute(SESSION_USER);
		String userId = session.getAttribute("userId").toString();
		// System.out.println(userId);
	   User user = userService.findByUserId(userId); 
		if (user == null) {
			return "redirect:/login";
		}

		Double loanamt = (Double) session.getAttribute(SESSION_LOAN_AMT);
		Double instalmentAmt = (Double) session.getAttribute(SESSION_INSTALMENT_AMT);
		String loanReason = (String) session.getAttribute(SESSION_LOAN_REASON);
		Double loanAppliedAmt = (Double) session.getAttribute(SESSION_LOAN_APPLIED_AMT);
		CDShareDeductions cdShareDeductions = (CDShareDeductions) session.getAttribute(SESSION_CD_SHARE);
		Boolean loanApplied = (Boolean) session.getAttribute(SESSION_LOAN_APPLIED);
		LoanInfo loanInfo = (LoanInfo) session.getAttribute(SESSION_LOAN_INFO);

		// Create new Loan object
		Loan commodityLoan = new Loan();
		commodityLoan.setUserId(user.getUserId());
		commodityLoan.setLoanAmount(loanamt);
		commodityLoan.setLoanApplyDate(new Date());
		commodityLoan.setLoanStatus(LoanStatus.PENDING);
		commodityLoan.setLoanType(LoanType.COMMODITY);
		commodityLoan.setIntrestRate(loanInfo.getInterestOnCommodity());
		commodityLoan.setUpdatedAt(new Date());
		commodityLoan.setApplicationType(ApplicationType.NA);
		commodityLoan.setInstalmentAmount(instalmentAmt);
		commodityLoan.setEmiBalance(50);
		commodityLoan.setEmiPaid(0);
		commodityLoan.setAppliedLoan(loanAppliedAmt);
		commodityLoan.setLoanReason(loanReason);

		try {
			double share;
			try {
				share = cdShareDeductions.getShareDeductionTotal();
			} catch (Exception e) {
				share = 0;
			}
			if (loanApplied != null && loanApplied) {
				model.addAttribute("messageerr",
						"You can go back. " + "You can check in your loan history for the loans you applied.");
			} else if (share < 16000) {
				model.addAttribute("messageerr",
						"You have to wait till 4 months from date of enrollment and complete share amount to 16000 to apply for loan."
								+ " You also have to make sure your share amount is paid full. After you have completed the process you can apply");
			} else if (share >= 16000) {
				double loan = loanService.findByLastLoanOutStandingByUserId(user.getUserId(), LoanType.COMMODITY);
				if (loan <= 0.0) {
					loanService.save(commodityLoan);
					model.addAttribute("message", "Loan application submitted successfully! "
							+ "You can check in your loan history for the loans you applied.");
					model.addAttribute("downloadUrl", "/tcos/members/loan/termscom/commodityloan/downloadPdf");
				} else {
					model.addAttribute("messageerr", "You cannot get loan now. "
							+ "Clear your previous due amount and then you can apply for loan.");
				}
			}
			session.setAttribute(SESSION_LOAN_APPLIED, true);
		} catch (Exception e) {
			model.addAttribute("messageerr", "Failed to submit loan application. Please try again.");
		}
       
		model.addAttribute("user", user);
		model.addAttribute("personalloan", commodityLoan);
		Banks banks = (Banks) session.getAttribute(SESSION_BANKS);
		model.addAttribute("bank", banks);
		Loan prevLoan = (Loan) session.getAttribute(SESSION_PREV_LOAN);
		model.addAttribute("prevLoan", prevLoan);
		

		return "Loan/commodity_loan_chk";
	}
	

	@GetMapping("/termscom/commodityloan/downloadPdf")
	public ResponseEntity<byte[]> downloadPdfComm(HttpSession session) {
		// User user = (User) session.getAttribute(SESSION_USER);
		String userId = session.getAttribute("userId").toString();		
	    User user = userService.findByUserId(userId); 
		if (user == null) {
			return ResponseEntity.badRequest().build();
		}

		Company company = (Company) session.getAttribute(SESSION_COMPANY);
		if (company == null) {
			company = companyService.findbyCompanyId(user.getCompanyId());
			if (company == null) {
				company = new Company();
			}
			session.setAttribute(SESSION_COMPANY, company);
		}

		Loan lloan = loanService.findLastApproveLoan(user.getUserId(), LoanType.PERSONAL);
		CDShareDeductions cdShareDeductions = (CDShareDeductions) session.getAttribute(SESSION_CD_SHARE);
		Banks banks = (Banks) session.getAttribute(SESSION_BANKS);
		Double loanamt = (Double) session.getAttribute(SESSION_LOAN_AMT);
		Double instalmentAmt = (Double) session.getAttribute(SESSION_INSTALMENT_AMT);
		Double dueLoan = (Double) session.getAttribute(SESSION_DUE_LOAN);
		String loanReason = (String) session.getAttribute(SESSION_LOAN_REASON);
		LoanInfo loanInfo = (LoanInfo) session.getAttribute(SESSION_LOAN_INFO);

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		DecimalFormat df = new DecimalFormat("#.00");

		Context context = new Context();

		context.setVariable("userName", safeStr(user.getName()));
		context.setVariable("bankName", banks != null ? safeStr(banks.getBankName()) : "			");
		context.setVariable("loanAmount", df.format(safeDouble(loanamt)));
		context.setVariable("ticketNumber", safeStr(user.getUserId()));
		context.setVariable("Address", safeStr(user.getCurrentAddr()));
		context.setVariable("companyName", safeStr(company.getCompanyName()));
		context.setVariable("osLoan", safeDouble(dueLoan));
		context.setVariable("dateOfLastLoan",
				(lloan != null && lloan.getLoanApproveDate() != null) ? dateFormat.format(lloan.getLoanApproveDate())
						: "			");
		context.setVariable("loanTaken", lloan != null ? df.format(safeDouble(lloan.getLoanAmount())) : "0");
		context.setVariable("noOfEmiPaid", lloan != null ? safeStr(lloan.getEmiPaid()) : "0");
		context.setVariable("amountRecovered", lloan != null ? df.format(safeDouble(lloan.getLoanRepaid())) : "0");
		context.setVariable("loanOs", lloan != null ? df.format(safeDouble(lloan.getLoanBalance())) : "0");

		context.setVariable("cdBalance",
				cdShareDeductions != null ? df.format(safeDouble(cdShareDeductions.getCdDeductionTotal())) : "0");

		context.setVariable("monthlyInstalment", df.format(safeDouble(instalmentAmt)));
		context.setVariable("interestRate", loanInfo != null ? safeStr(loanInfo.getInterestOnCommodity()) : "			");
		context.setVariable("deptNo", safeStr(user.getBcNumber()));
		context.setVariable("mobileNumber", safeStr(user.getPhoneNum()));
		context.setVariable("email", safeStr(user.getEmail()));
		context.setVariable("accountNumber", banks != null ? safeStr(banks.getAccountNum()) : "				");
		context.setVariable("branch", banks != null ? safeStr(banks.getBranchName()) : "");
		context.setVariable("ifsc", banks != null ? safeStr(banks.getIfscCode()) : "				");
		RuleBasedNumberFormat formatter = 
			    new RuleBasedNumberFormat(Locale.of("en", "IN"), RuleBasedNumberFormat.SPELLOUT);
		if (user.getUserType().equals("New Member"))
		{
			context.setVariable("eligible", loanInfo != null ? df.format(safeDouble(loanInfo.getMaxCommodityLoan())) : "0");
		context.setVariable("wordrupee", loanInfo != null ? formatter.format(df.format(safeDouble(loanInfo.getMaxCommodityLoan()))) : "0");
		}
		else
		{
			context.setVariable("eligible","100000");
			context.setVariable("wordeligible",formatter.format(100000));
		}
		
		
		context.setVariable("fathersname", safeStr(user.getFathersName()));

		context.setVariable("sharebalance",
				cdShareDeductions != null ? df.format(safeDouble(cdShareDeductions.getShareDeductionTotal())) : "0");

		context.setVariable("dos",
				user.getDateOfRetirement() != null ? dateFormat.format(user.getDateOfRetirement()) : "				");
		context.setVariable("serviceperiod", user.getSerivcePeriod() != ""? safeStr(user.getSerivcePeriod()):"              ");
		context.setVariable("loanReason", safeStr(loanReason));			
		
	
		Calendar dob = Calendar.getInstance();
		Calendar today = Calendar.getInstance();
		if (user.getDob() != null) 
		{
			dob.setTime(user.getDob());  // Added 22-04-2026  prevLoan
		}
		context.setVariable("age",user.getDob() != null ? today.get(Calendar.YEAR) - dob.get(Calendar.YEAR) : "				");
				
		
		
		String imagePath = "";
		try {
			imagePath = getClass().getResource("/static/dist/images/company_logo.png").toExternalForm();
		} catch (Exception e) {
			System.out.println(e);
		}
		context.setVariable("logoPath", imagePath);

		byte[] pdfBytes = pdfService.generatePdf("loan_application_commodity", context);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=loan_application.pdf")
				.contentType(MediaType.APPLICATION_PDF).body(pdfBytes);
	}
	
	@GetMapping("/user-data/{userId}")
	public ResponseEntity<Map<String, Object>> getUserData(@PathVariable("userId") String userId, HttpSession session) {
		Map<String, Object> response = new HashMap<>();

		try {
			// Call service to get user entity by userId  testing by bashishtha  getUserByUserId
		//	User user = userService.findByUserIdAndAccountStatus(userId);
			User user = userService.getUserByUserId(userId);
			 System.out.println("Member user details in this "+userId);
			
			if (user != null) {
				// Convert User entity to UserregisterDTO
				UserregisterDTO userData = convertUserToDTO(user);

				// Fetch Banks data for this user
				Banks bankData = bankService.getByUserId(userId);
				 System.out.println("Member bank details in this "+bankData);
				if (bankData != null) {
					populateBankData(userData, bankData);
				}

//				// Fetch Nominees data for this user
//				Nominiees nomineeData = nominieesService.findByUserId(userId);
//				if (nomineeData != null) {
//					populateNomineeData(userData, nomineeData);
//				}

     			response.put("exists", true);
     			// response.put("data", user);
     			 response.put("data", userData);
			} else {
				response.put("exists", false);
				response.put("message", "No data found for this ticket number");
			}

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("exists", false);
			response.put("message", "Error retrieving user data");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	// Helper method to populate bank data
	private void populateBankData(UserregisterDTO dto, Banks bankData) {
		dto.setAccountNum(bankData.getAccountNum());
		dto.setBname(bankData.getBankName());
		dto.setBranch(bankData.getBranchName());
		dto.setIfscode(bankData.getIfscCode());
		dto.setPayableAt(bankData.getPayableAt());
	}

	// Helper method to populate nominee data
	private void populateNomineeData(UserregisterDTO dto, Nominiees nomineeData) {
		dto.setNomineeName(nomineeData.getNomineeName());
		dto.setRelation(nomineeData.getRelation());
		// dto.setNomineeAddress(nomineeData.getAddr());
		dto.setAadharNum(nomineeData.getAadharNum());
		dto.setMobileNum(nomineeData.getMobileNum());
	}


	private UserregisterDTO convertUserToDTO(User user) {
		UserregisterDTO dto = new UserregisterDTO();

		// Basic Information
		dto.setId(user.getId()); // Important: Include the primary key for updates
		dto.setUserId(user.getUserId());
		dto.setEmpId(user.getEmpId());
		dto.setName(user.getName());
		dto.setBcNumber(user.getBcNumber());
		dto.setGender(user.getGender());
		dto.setFathersName(user.getFathersName());
		dto.setDob(user.getDob());
		dto.setDor(user.getDateOfRetirement());
		dto.setAadharNumber(user.getAadharNumber());
		dto.setPanCard(user.getPanCard());
		dto.setEmail(user.getEmail());
		dto.setPhoneNum(user.getPhoneNum());
		dto.setWhatsappNumber(user.getWhatsappNumber());
		dto.setSerivcePeriod(user.getSerivcePeriod());

		// Address Information
		dto.setPermanentAddr(user.getPermanentAddr());
		dto.setPermanentCity(user.getPermanentCity());
		dto.setPermanentState(user.getPermanentState());
		dto.setPermanentPinCode(user.getPermanentPinCode());
		dto.setCurrentAddr(user.getCurrentAddr());
		dto.setCurrentCity(user.getCurrentCity());
		dto.setCurrentState(user.getCurrentState());
		dto.setCurrentPinCode(user.getCurrentPinCode());

		// Company and Status
		dto.setCompanyId(user.getCompanyId());
		dto.setAccountStatus(user.getAccountStatus());
		dto.setUserType(user.getUserType());
		dto.setCreatedAt(user.getCreatedAt());

		return dto;
	}
	
	@PostMapping("/registerOld")
	public String registerUserAccountOld(
			@ModelAttribute("UserregisterDTO") UserregisterDTO userregisterDTOS, Model model, HttpSession session,@RequestParam("userId1") String userId1) {
      
		System.out.println(userregisterDTOS);
//		// Check if OTP was verified (optional security check)
//		Boolean otpVerified = (Boolean) session.getAttribute("otpVerified");
//		if (otpVerified == null || !otpVerified) {
//			System.out.println("Warning: Registration attempted without OTP verification");
//		}
   String usreid = userId1;
 //  System.out.println(usreid);
		try {
				
		//	UserIdGenerator generator = new UserIdGenerator();
			userregisterDTOS.setAccountStatus(AccountStatus.PENDING);
			userregisterDTOS.setUserType("Existing Member");
			 userregisterDTOS.setUserId(usreid);
			userService.updateRetirementdt(userregisterDTOS);
			userService.Insertorupdatebanks(userregisterDTOS);
			model.addAttribute("successMessage", "User Update successfully!");
		//	String encryptedPassword = passwordEncoder.encode(userregisterDTOS.getPassword());
		//	userregisterDTOS.setPassword(encryptedPassword);
//			userregisterDTOS.setEmpId(
//					generator.generateNextUserId(userregisterDTOS.getUserId(), userregisterDTOS.getCompanyId()));
//			userService.newUser(userregisterDTOS);
  		//model.addAttribute("successMessage", "User saved successfully!");

			Context context = new Context();
			Company company = companyService.findbyCompanyId(userregisterDTOS.getCompanyId());
//
//			try {
//				userregisterDTOS.getEmail();
//				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
//
//				context.setVariable("date", userregisterDTOS.getCreatedAt());
//				context.setVariable("ticketNumber", userregisterDTOS.getUserId());
//				context.setVariable("EMP_ID", userregisterDTOS.getEmpId());
//				context.setVariable("NAME", userregisterDTOS.getName());
//				context.setVariable("BC_NUMBER", userregisterDTOS.getBcNumber());
//				context.setVariable("GENDER", userregisterDTOS.getGender());
//				context.setVariable("FATHERS_NAME", userregisterDTOS.getFathersName());
//				context.setVariable("DOB", dateFormat.format(userregisterDTOS.getDob()));
//				context.setVariable("DATE_OF_RETIREMENT", dateFormat.format(userregisterDTOS.getDor()));
//				context.setVariable("AADHAR_NUMBER", userregisterDTOS.getAadharNumber());
//				context.setVariable("PAN_CARD", userregisterDTOS.getPanCard());
//				context.setVariable("EMAIL", userregisterDTOS.getEmail());
//				context.setVariable("PHONE_NUM", String.valueOf(userregisterDTOS.getPhoneNum()));
//				context.setVariable("WHATSAPP_NUMBER", String.valueOf(userregisterDTOS.getWhatsappNumber()));
//				context.setVariable("SERVICE_PERIOD", userregisterDTOS.getSerivcePeriod());
//				context.setVariable("DEPARTMENT_ID", userregisterDTOS.getBcNumber());
//			//	context.setVariable("employeer", company.getCompanyName());
//
//				// Permanent Address
//				context.setVariable("PERMANENT_ADDR", userregisterDTOS.getPermanentAddr());
//				context.setVariable("PERMANENT_STATE", userregisterDTOS.getPermanentState());
//				context.setVariable("PERMANENT_PIN_CODE", userregisterDTOS.getPermanentPinCode());
//				context.setVariable("PERMANENT_CITY", userregisterDTOS.getPermanentCity());
//
//				// Current Address
//				context.setVariable("CURRENT_ADDR", userregisterDTOS.getCurrentAddr());
//				context.setVariable("CURRENT_STATE", userregisterDTOS.getCurrentState());
//				context.setVariable("CURRENT_PIN_CODE", userregisterDTOS.getCurrentPinCode());
//				context.setVariable("CURRENT_CITY", userregisterDTOS.getCurrentCity());
//
//				// Bank details
//				context.setVariable("ACCOUNT_NUM", userregisterDTOS.getAccountNum());
//				context.setVariable("BANK_NAME", userregisterDTOS.getBname());
//				context.setVariable("BRANCH_NAME", userregisterDTOS.getBranch());
//				context.setVariable("IFSC_CODE", userregisterDTOS.getIfscode());
//
//				// Nominee details
//				context.setVariable("NOMINEE_NAME", userregisterDTOS.getNomineeName());
//				context.setVariable("NOMINEE_RELATION", userregisterDTOS.getRelation());
//			//	context.setVariable("NOMINEE_ADDR", userregisterDTOS.getNomineeAddress());
//				context.setVariable("NOMINEE_AADHAR_NUM", userregisterDTOS.getAadharNum());
//				context.setVariable("NOMINEE_MOBILE_NUM", userregisterDTOS.getMobileNum());
//
//				// Metadata
//				context.setVariable("COMPANY_ID", userregisterDTOS.getCompanyId());
//				context.setVariable("PASSWORD", userregisterDTOS.getPassword());
//				context.setVariable("CREATED_AT", userregisterDTOS.getCreatedAt());
//
//				String imagePath = getClass().getResource("/static/dist/images/company_logo.png").toExternalForm();
//				context.setVariable("logoPath", imagePath);
//
//				String pdfUrl = "/generated_pdfs/" + userregisterDTOS.getEmail() + "_registration.pdf";
//				model.addAttribute("pdfUrl", pdfUrl);
//
//			} catch (Exception e) {
//				e.printStackTrace();
//				model.addAttribute("error", "Error generating PDF.");
//			}
//			
//			
//			byte[] pdfBytes = pdfService.generatePdf("registrationpdfold", context);
//			
//			// Clean up session attributes
//			session.removeAttribute("registrationOtp");
//			session.removeAttribute("otpVerified");
//			session.removeAttribute("otpGeneratedAt");
//			session.removeAttribute("tempUserData");
//
//			return ResponseEntity.ok()
//					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=registration_application.pdf")
//					.contentType(MediaType.APPLICATION_PDF).body(pdfBytes);

		} catch (Exception e) {
			System.err.println("Registration failed: " + e.getMessage());
	        e.printStackTrace();
	        // Return appropriate error response
	       // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return "Loan/LoanApply";
	}
	@PostMapping("/cd_share_deducation")
	public String CdSharededucation(
			 Model model,@RequestParam("txtcddeduct") String txtcddeduct,@RequestParam("txtsharededuct") String txtsharededuct,@RequestParam("Paymentdt") String Paymentdt, HttpSession session ) {
		//User currentUser = getCurrentUser(request,session);
		String userId = session.getAttribute("userId").toString();
		User user = userService.findByUserId(userId);		
		cdShareDeductionService.insertcddeducat(userId, txtcddeduct, txtsharededuct, Paymentdt);
		CDShareDeductions cdShareDeductions = cdShareDeductionService.findLatestRecordByUserId(userId);
		// System.out.println("cdShareDeductions Details " + cdShareDeductions);
			if (cdShareDeductions == null) {
				cdShareDeductions = new CDShareDeductions();
			}
			model.addAttribute("user", user);
			model.addAttribute("cdShareDeductions", cdShareDeductions);
			model.addAttribute("activePage", "CD Share");
		//System.out.println(cdShareDeductionsDTO);
		return "Loan/cd_share";
	}
}
