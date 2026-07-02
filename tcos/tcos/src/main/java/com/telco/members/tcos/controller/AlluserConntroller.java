package com.telco.members.tcos.controller;

import com.telco.members.tcos.dto.*;
import com.telco.members.tcos.model.*;
import com.telco.members.tcos.repository.AccessCtrlRepo;
import com.telco.members.tcos.repository.loanRepository;
import com.telco.members.tcos.service.*;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.thymeleaf.context.Context;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Controller
//@SessionAttributes({ "yearMonth", "interestReportDTO" })
@RequestMapping("/members")
public class AlluserConntroller {
    @Autowired
    private UserService userService;
    @Autowired
    private AccessCtrlRepo accessCtrlRepo;
    @Autowired
    private LoanService loanService;
    @Autowired
    private EmiPaymentDetailsService emiPaymentDetailsService;
    @Autowired
    private PdfService pdfService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private GuarantorService guarantorService;
    @Autowired
    private LoanHistoryService loanHistoryService;
    @Autowired
    private CDShareDeductionService cdShareDeductionService;
    @Autowired
    private AsyncMethod AsyncMethod;
    @Autowired
    private ReportDataService reportDataService;
    
    @Autowired
    private AuditDetService auditDetService;

    @Autowired
    private loanRepository loanRepository;
 

    private String companyName = "company";
    Map<String, InterestReportDTO> reportMap = new HashMap<>();
    List<ReportData> finalReportData = new ArrayList<>();

    int id1 = 101; //tm - tms for some places - Tata Motors
    int id4 = 104; //th - tcn for tata Hitachi
    int id2 = 102; //tmlbsl -- hvtl for some section
    int id3 = 103; //ttl - tt for some place - tata technology

    private List<InterestReportDTO> interestReportDTO;
    private int year;
    private int month;
    private int company;

    @GetMapping("/tm/member_details")
    public String m_details(Model model) {
        List<UserregisterDTO> userss = userService.getAllUserspecific(id1);
        model.addAttribute("users", userss);
        return "reports/employeedetails";
    }

    // changes for interest here -- by nishant
    @GetMapping("/tm/report")
    public String l_details(Model model) {

        interestReportDTO = new ArrayList<>();
//		companyName = companyService.findbyCompanyId(id1).getCompanyName();
        companyName = "TML";

        model.addAttribute("company", companyName);
        return "reports/report";
    }

    @GetMapping("/tms/interest")
    private CompletableFuture<ResponseEntity<List<InterestOnLoan>>> intForTmsAsync(
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate reportDate) {

        int month = reportDate.getMonthValue();
        int year = reportDate.getYear();
        int day = reportDate.getDayOfMonth();
        this.year = year;
        this.month = month;
        this.company = id1;

        return AsyncMethod.getOrGenerateInterestAsync(month, id1, year, day)
            .thenApply(ResponseEntity::ok)
            .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @GetMapping("/tmlbsl/member_details")
    public String m1_details(Model model) {
        List<UserregisterDTO> userss = userService.getAllUserspecific(id2);
        model.addAttribute("users", userss);
        return "reports/employeedetails";
    }

    @GetMapping("/hvtl/report")
    public String l1_details(Model model) {

        companyName = "TMLBSL";

        model.addAttribute("company", companyName);

        return "reports/report";
    }

    @GetMapping("/hvtl/interest")
    public CompletableFuture<ResponseEntity<List<InterestOnLoan>>> intForThAsync(
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate reportDate) {

        int month = reportDate.getMonthValue();
        int year = reportDate.getYear();
        int day = reportDate.getDayOfMonth();

        return AsyncMethod.getOrGenerateInterestAsync(month, id2, year, day)
            .thenApply(ResponseEntity::ok)
            .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @GetMapping("/tcn/member_details")
    public String m2_details(Model model) {
        List<UserregisterDTO> userss = userService.getAllUserspecific(id4);
        model.addAttribute("users", userss);
        return "reports/employeedetails";
    }

    @GetMapping("/th/report")
    public String l2_details(Model model) {

//		companyName = companyService.findbyCompanyId(id2).getCompanyName();
        companyName = "TCN";
        model.addAttribute("company", companyName);
        return "reports/report";
    }

    @GetMapping("/th/interest")
    public CompletableFuture<ResponseEntity<List<InterestOnLoan>>> intForTtAsync(
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate reportDate) {

        int month = reportDate.getMonthValue();
        int year = reportDate.getYear();
        int day = reportDate.getDayOfMonth();

        return AsyncMethod.getOrGenerateInterestAsync(month, id4, year, day)
            .thenApply(ResponseEntity::ok)
            .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @GetMapping("/ttl/member_details")
    public String m3_details(Model model) {
        List<UserregisterDTO> userss = userService.getAllUserspecific(id3);
        model.addAttribute("users", userss);
        return "reports/employeedetails";
    }

    @GetMapping("/tt/report")
    public String l3_details(Model model) {

//		companyName = companyService.findbyCompanyId(id3).getCompanyName();
        companyName = "TTL";

        model.addAttribute("company", companyName);

        return "reports/report";
    }

    @GetMapping("/tt/interest")
    public CompletableFuture<ResponseEntity<List<InterestOnLoan>>> intForTbAsync(
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate reportDate) {
        
        int month = reportDate.getMonthValue();
        int year = reportDate.getYear();
        int day = reportDate.getDayOfMonth();
        
        return AsyncMethod.getOrGenerateInterestAsync(month, id3, year, day)
            .thenApply(ResponseEntity::ok)
            .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @GetMapping("/generateinterestpdf")
    public ResponseEntity<byte[]> generateInterestReportPdf() {
    	
        Context context = new Context();
        context.setVariable("interestReports", interestReportDTO);
        context.setVariable("reportTitle", "All Data Report");
        context.setVariable("printDate", new Date());
        context.setVariable("company", companyName);

        byte[] pdfBytes = pdfService.generatePdf("reports/interest_report", context); // your existing method!

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=interest_report.pdf")
                .contentType(MediaType.APPLICATION_PDF).body(pdfBytes);
    }
    @GetMapping("/generateauditpdf/{p_userId}/{p_audityear}")
    public ResponseEntity<byte[]> generateAuditReportPdf(@PathVariable String p_userId,@PathVariable String p_audityear) {
    	// System.out.println("Member Voucher details in this "+p_userId);
    	// System.out.println("Member Voucher details in this "+p_audityear);
    	AuditDetSettlement auditsummary = auditDetService.getAuditsummarys(p_userId,p_audityear);
    	AuditDetSettlement1 auditsummary1 = auditDetService.getAuditsSummarydata(p_userId,p_audityear);
    	
    	System.out.println("Member Voucher details in this "+auditsummary);
    	System.out.println("Member Voucher details in this "+auditsummary1);
        Context context = new Context();
        context.setVariable("auditsummary", auditsummary);
        context.setVariable("auditsummary1", auditsummary1);
        context.setVariable("reportTitle", "All Data Report");
        context.setVariable("printDate", new Date());
        context.setVariable("Name", companyName);
        context.setVariable("companyName", companyName);
        context.setVariable("ticketNo", p_userId);
        
        byte[] pdfBytes = pdfService.generatePdf("reports/audit_settlement", context); // your existing method!

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=interest_report.pdf")
                .contentType(MediaType.APPLICATION_PDF).body(pdfBytes);
    }
    
    @GetMapping("/genauditsettlementpdf/{p_userId}/{p_audityear}")
    public ResponseEntity<byte[]> generateAuditSettlementPdf(@PathVariable String p_userId,@PathVariable String p_audityear) {
    	// System.out.println("Member Voucher details in this "+p_userId);
    	// System.out.println("Member Voucher details in this "+p_audityear);
    	AuditDetSettlement auditsummary = auditDetService.getAuditsummarys(p_userId,p_audityear);
    	AuditDetSettlement1 auditsummary1 = auditDetService.getAuditsSummarydata(p_userId,p_audityear);
    	
    	System.out.println("Member Voucher details in this "+auditsummary);
    	System.out.println("Member Voucher details in this "+auditsummary1);
        Context context = new Context();
        context.setVariable("auditsummary", auditsummary);
        context.setVariable("auditsummary1", auditsummary1);
        context.setVariable("reportTitle", "All Data Report");
        context.setVariable("printDate", new Date());
        context.setVariable("Name", companyName);
        context.setVariable("companyName", companyName);
        context.setVariable("ticketNo", p_userId);
        context.setVariable("financalyr", p_audityear);
        
        byte[] pdfBytes = pdfService.generatePdf("reports/auditdet_print", context); // your existing method!

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=interest_report1.pdf")
                .contentType(MediaType.APPLICATION_PDF).body(pdfBytes);
    }

    @GetMapping("/reports/compare")
    public String uploadDoc(Model model) {

        model.addAttribute("company", companyName);

        return "reports/doc_upload";
    }

//	@GetMapping("/hval/member_details")
//	public String m5_details(Model model) {
//		List<UserregisterDTO> userss = userService.getAllUserspecific(id5);
//		model.addAttribute("users", userss);
//		return "reports/employeedetails";
//	}

//	@GetMapping("/hval/report")
//	public String l5_details(Model model) {
//
//		companyName = companyService.findbyCompanyId(id5).getCompanyName();
//
//		model.addAttribute("company", companyName);
//
//		return "reports/report";
//	}
//
//	@GetMapping("/hval/interest")
//	public ResponseEntity<List<InterestOnLoan>> intForAh(
//			@RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate reportDate) {
//
//		int month = reportDate.getMonthValue();
//		int year = reportDate.getYear();
//
//		List<InterestOnLoan> loanList = AsyncMethod.getOrGenerateInterest(month, id1, year);
//
//		return ResponseEntity.ok(loanList);
//
//	}

    @PostMapping("/reports/uploadExcel")
    @ResponseBody
    public ResponseEntity<?> handleExcelUpload(@RequestParam("file") MultipartFile file) {
        try {
            // 1. Validate input file
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("Uploaded file cannot be empty");
            }
            int code=101;
            if(companyName.equals("TML")) {
                code=101;
            } else if(companyName.equals("TTL")) {
                code=103;
            } else if(companyName.equals("TMLBSL")) {
                code=102;
            } else {
                code=104;
            }

            // 2. Get existing reports
            List<ReportData> existingReports = reportDataService.getByMonth(this.month, code, this.year);
            Map<String, ReportData> reportMap = new HashMap<>();
            for (ReportData report : existingReports) {
                if (report.getUserId() != null) {
                    reportMap.put(report.getUserId(), report);
                }
            }

            // 3. Process Excel file
            List<DocDataDto> uploadedData;
            try {
                uploadedData = processExcelFile(file);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Error processing Excel file: " + e.getMessage());
            }

            // 4. Process each record
            for (DocDataDto d : uploadedData) {
                String uid = d.getUserId();
                if (uid == null || uid.trim().isEmpty()) {
                    continue;
                }

                ReportData rdata = reportMap.get(uid);
                if (rdata == null) {
                    rdata = new ReportData();
                    rdata.setUserId(uid);
                    reportMap.put(uid, rdata);
                }

                String wageType = d.getWageType();
//	            if (wageType == null) {
//	                continue;
//	            }

                double amount = d.getAmount();

                // TML/TTL/TMLBSL processing
                if (companyName.equals("TML") || companyName.equals("TTL") || companyName.equals("TMLBSL")) {
                    switch (wageType) {
                        case "6350": rdata.setNormalBalanceUpload(amount); break;
                        case "6020": rdata.setNoramlRecoveryUpload(amount); break;
                        case "6025": rdata.setNormalInterestUpload(amount); break;
                        case "6351": rdata.setCommBalanceUpload(amount); break;
                        case "6010": rdata.setCommRecoveryUpload(amount); break;
                        case "6015": rdata.setCommInterestUpload(amount); break;
                        case "6035": rdata.setCdUpload(amount); break;
                        case "6030": rdata.setShareUpload(amount); break;
                    }
                }
                // TCN processing
                else if (companyName.equals("TCN")) {
                    switch (wageType) {
                        case "424B": rdata.setNormalBalanceUpload(amount); break;
                        case "4240": rdata.setNoramlRecoveryUpload(amount); break;
                        case "4250": rdata.setNormalInterestUpload(amount); break;
                        case "426B": rdata.setCommBalanceUpload(amount); break;
                        case "4260": rdata.setCommRecoveryUpload(amount); break;
                        case "4270": rdata.setCommInterestUpload(amount); break;
                        case "4220": rdata.setCdUpload(amount); break;
                        case "": rdata.setShareUpload(amount); break;
                    }
                }
            }

            // 5. Save and return results
            List<ReportData> resultList = new ArrayList<>(reportMap.values());
//	        reportDataService.saveAll(resultList);
            finalReportData = resultList;

            return ResponseEntity.ok(resultList);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    private List<DocDataDto> processExcelFile(MultipartFile file) throws Exception {
        List<DocDataDto> extractedData = new ArrayList<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                // Skip header row
                if (row.getRowNum() == 0)
                    continue;

                try {
                    // Get required cells
                    Cell userIdCell = row.getCell(2); // Column C
                    Cell wageTypeCell = row.getCell(3); // Column D
                    Cell amountCell = row.getCell(5); // Column F

                    // Skip if any cell is missing
                    if (userIdCell == null || wageTypeCell == null || amountCell == null) {
                        continue;
                    }

                    DocDataDto dto = new DocDataDto();

                    // Process User ID
                    if (userIdCell.getCellType() == CellType.STRING) {
                        dto.setUserId(userIdCell.getStringCellValue().trim());
                    } else {
                        dto.setUserId(String.valueOf((long) userIdCell.getNumericCellValue()));
                    }

                    // Skip if userId is blank
                    if (dto.getUserId().isEmpty()) {
                        continue;
                    }

                    // Process Wage Type
                    if (wageTypeCell.getCellType() == CellType.STRING) {
                        dto.setWageType(wageTypeCell.getStringCellValue().trim());
                    } else if (wageTypeCell.getCellType() == CellType.NUMERIC) {
                        dto.setWageType(String.valueOf((int) wageTypeCell.getNumericCellValue()));
                    } else {
                        dto.setWageType(""); // or null if preferred
                    }

                    // Process Amount
                    if (amountCell.getCellType() == CellType.STRING) {
                        dto.setAmount(Double.parseDouble(amountCell.getStringCellValue().trim()));
                    } else {
                        dto.setAmount(amountCell.getNumericCellValue());
                    }

                    extractedData.add(dto);

                } catch (Exception e) {
                    System.err.println("Skipped row " + (row.getRowNum() + 1) + ": " + e.getMessage());
                    continue;
                }
            }
        }

        return extractedData;
    }

    @GetMapping("/reports/savedata")
    public ResponseEntity<?> savingAllData(@ModelAttribute("yearMonth") YearMonth yearMonth,
                                           @ModelAttribute("interestReportDTO") List<InterestReportDTO> dataList) {

        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        Date reportDate = Date.from(firstDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());

        if (dataList == null || dataList.isEmpty()) {
            return ResponseEntity.badRequest().body("No data available to save");
        }

        for (InterestReportDTO data : dataList) {
            EmiPaymentsDetails emi = new EmiPaymentsDetails();
            Loan normalLoan, commLoan;
            normalLoan = loanService.findLatestLoan(data.getUserId(), LoanType.PERSONAL, LoanStatus.APPROVED);
            commLoan = loanService.findLatestLoan(data.getUserId(), LoanType.COMMODITY, LoanStatus.APPROVED);

            // part for saving emis start here
            emi.setPaymentDate(reportDate);
            emi.setUserId(data.getUserId());
            emi.setEmiPaymentType(EmiPaymentType.Normal_deduction);
            emi.setCreatedAt(new Date());

            if (normalLoan != null) {
                emi.setLoanId(normalLoan.getLoanId());
                emi.setEmiAmount(data.getLoanInstalmentNormalUpload());// uploaded
                emi.setGeneratedEmiAmount(data.getLoanInstalmentNormal());// calculated
                emi.setIntrestRate(normalLoan.getIntrestRate());
                emi.setGeneratedInterest(data.getLoanInterestNormal());// generated
                emi.setUploadedInterest(data.getLoanInterestNormalUpload());// uploaded
                emi.setPrincipalRemaing(normalLoan.getLoanBalance() - emi.getEmiAmount());// principal remaining
                emi.setTotalPaid(normalLoan.getLoanRepaid() + emi.getEmiAmount());// total paid
                normalLoan.setLoanBalance(emi.getPrincipalRemaing());
                normalLoan.setLoanRepaid(emi.getTotalPaid());
                emiPaymentDetailsService.save(emi); // for saving normal loan emi
                loanService.save(normalLoan);
            }

            if (commLoan != null) {
                emi.setLoanId(commLoan.getLoanId());
                emi.setEmiAmount(data.getLoanInstalmentCommUpload());// uploaded
                emi.setGeneratedEmiAmount(data.getLoanInstalmentComm());// calculated
                emi.setIntrestRate(commLoan.getIntrestRate());
                emi.setGeneratedInterest(data.getLoanInterestComm());// generated
                emi.setUploadedInterest(data.getLoanInterestCommUpload());// uploaded
                emi.setPrincipalRemaing(commLoan.getLoanBalance() - emi.getEmiAmount());// principal remaining
                emi.setTotalPaid(commLoan.getLoanRepaid() + emi.getEmiAmount());// total paid
                commLoan.setLoanBalance(emi.getPrincipalRemaing());
                commLoan.setLoanRepaid(emi.getTotalPaid());
                emiPaymentDetailsService.save(emi); // for saving comm loan emi
                loanService.save(commLoan);
            }
            // saving emi ends here

            // part for cd and share deductions
            CDShareDeductions latestRecord;
            try {
                latestRecord = cdShareDeductionService.getLatestRecord(data.getUserId());
                if (latestRecord == null) {
                    latestRecord = new CDShareDeductions();
                }
            } catch (Exception e) {
                // TODO: handle exception
                latestRecord = new CDShareDeductions();
            }
            CDShareDeductions cdShare = new CDShareDeductions();
            cdShare.setUserId(data.getUserId());
            cdShare.setPaymentDate(reportDate);
            cdShare.setCreatedAt(new Date());
            // for cd
            cdShare.setCdDeduction(data.getCdUpload());
            cdShare.setCdDeductionGenerated(userService.getUserByUserId(data.getUserId()).getCdCut());
            cdShare.setCdDeductionTotal(cdShare.getCdDeduction() + latestRecord.getCdDeductionTotal());
            // for share
            cdShare.setShareDeduction(data.getShareUpload());
            cdShare.setShareDeductionGenerated(data.getShare());
            cdShare.setShareDeductionGenerated(userService.getUserByUserId(data.getUserId()).getShareCut());
            cdShare.setShareDeductionTotal(cdShare.getShareDeduction() + latestRecord.getShareDeductionTotal());

            cdShareDeductionService.save(cdShare); // saving for cd and share deduction data

        }

        return ResponseEntity.ok(Map.of("message", "Data saved successfully", "recordsProcessed", dataList.size()));
    }

    @GetMapping("/reports/loans")
    public String showAllUserLoanDetails() {

        return "reports/loan_details";
    }
    @GetMapping("/reports/auditdetsettlement")
    public String showAllUserAuditDetails() {

        return "closure/auditdet_settlements";
    }
    private String userIdsave = "";

    @GetMapping("/reports/{userId}")
    public ResponseEntity<ReportingDTO> findAllDetails(@PathVariable("userId") String userId) {
        ReportingDTO data = new ReportingDTO();
        userIdsave = userId;
        User user = userService.findByUserId(userIdsave);
        //List<loanHistory> loans = loanHistoryService.getAllLoans(user.getUserId());
        List<Loan> loans = new ArrayList<>();

        Loan personalLoan =
                loanRepository.findTop1ByUserIdAndLoanTypeOrderByLoanApproveDateDesc(
                        user.getUserId(),
                        LoanType.PERSONAL);

        if (personalLoan != null) {
            loans.add(personalLoan);
        }

        Loan commodityLoan =
                loanRepository.findTop1ByUserIdAndLoanTypeOrderByLoanApproveDateDesc(
                        user.getUserId(),
                        LoanType.COMMODITY);

        if (commodityLoan != null) {
            loans.add(commodityLoan);
        }


//        List<EmiPaymentsDetails> emis = emiPaymentDetailsService.allEmis(user.getUserId());
        CDShareDeductions emis = cdShareDeductionService.getLatestRecord(userIdsave);
        int Surety = guarantorService.getGuarantorCount(userIdsave);
        data.setSurety(Surety);
        data.setUser(user);
        data.setLoans(loans);
        data.setEmis(emis);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/auditdetsettlement/{p_userId}/{p_audityear}")
    public ResponseEntity<AuditSettlementDTO> findauditDetsettlement( @PathVariable("p_userId") String p_userId,@PathVariable("p_audityear") String p_audityear) {
    	AuditSettlementDTO data = new AuditSettlementDTO();
    	
     //   userIdsave = userId;
      //  User user = userService.findByUserId(userIdsave);
    	
        System.out.println(p_audityear);
    	
       List<AuditDetSettlement> auditdetsettlement = auditDetService.getAuditDetsdata(p_userId,p_audityear);
    	List<AuditDetSettlement1> auditdetsettlement1 = auditDetService.getAuditDetsdata1(p_userId,p_audityear);
       
    	System.out.println(p_userId);
    	System.out.println(p_audityear);
      
     //    System.out.println(auditdetsettlement);
       
//        List<EmiPaymentsDetails> emis = emiPaymentDetailsService.allEmis(user.getUserId());
      //  CDShareDeductions emis = cdShareDeductionService.getLatestRecord(userIdsave);
        //int Surety = guarantorService.getGuarantorCount(userIdsave);
       // data.setSurety(Surety);
       // data.setUser(user);
       // data.setLoans(loans);
       // data.setEmis(emis);
        data.setAuditdetsettlement(auditdetsettlement);
        data.setAuditdetsettlement1(auditdetsettlement1);
       //data.setAuditdetsettlement_norm(auditdetsettlement1);
        System.out.println(data);
        return ResponseEntity.ok(data);
    }
    
    
    // New endpoint to serve financial page
    @GetMapping("/reports/financial-details")
    public String financialDetailsPage() {
        System.out.println(userIdsave);
        return "reports/financial-details"; // Name of your HTML template
    }

    @GetMapping("/reports/downloadExcel")
    public void downloadExcel(HttpServletResponse response) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Comparison Report");

        // Create a bold centered style
        CellStyle headerStyle = workbook.createCellStyle();
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        headerStyle.setFont(boldFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // ROW 0
        Row row0 = sheet.createRow(0);
        row0.setHeightInPoints(20);
        row0.createCell(0).setCellValue("Ticket Number");
        row0.createCell(1).setCellValue("Name");
        sheet.addMergedRegion(new CellRangeAddress(0, 2, 0, 0)); // rowspan=3
        sheet.addMergedRegion(new CellRangeAddress(0, 2, 1, 1)); // rowspan=3

        row0.createCell(2).setCellValue("Normal Loan");
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 2, 10));
        row0.createCell(11).setCellValue("Commodity Loan");
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 11, 19));
        row0.createCell(20).setCellValue("Share");
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 20, 22));
        row0.createCell(23).setCellValue("CD");
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 23, 25));

        for (int i = 0; i <= 25; i++) {
            if (row0.getCell(i) != null) {
                row0.getCell(i).setCellStyle(headerStyle);
            }
        }

        // ROW 1
        Row row1 = sheet.createRow(1);
        row1.setHeightInPoints(20);
        row1.createCell(2).setCellValue("Balance");
        row1.createCell(5).setCellValue("Recovery");
        row1.createCell(8).setCellValue("Interest");

        row1.createCell(11).setCellValue("Balance");
        row1.createCell(14).setCellValue("Recovery");
        row1.createCell(17).setCellValue("Interest");

        sheet.addMergedRegion(new CellRangeAddress(1, 1, 2, 4));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 5, 7));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 8, 10));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 11, 13));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 14, 16));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 17, 19));

        for (int i = 2; i <= 19; i += 3) {
            row1.getCell(i).setCellStyle(headerStyle);
        }

        // ROW 2 - Sub-Headers
        Row row2 = sheet.createRow(2);
        row2.setHeightInPoints(20);
        String[] subHeaders = { "Upload", "Generated", "Difference", "Upload", "Generated", "Difference", "Upload",
                "Generated", "Difference", "Upload", "Generated", "Difference", "Upload", "Generated", "Difference",
                "Upload", "Generated", "Difference", "Upload", "Generated", "Difference", "Upload", "Generated",
                "Difference" };

        for (int i = 0; i < subHeaders.length; i++) {
            Cell cell = row2.createCell(i + 2); // starts from column 2 (index 2)
            cell.setCellValue(subHeaders[i]);
            cell.setCellStyle(headerStyle);
        }

        // ROWS - Data
        int rowIndex = 3;
        for (ReportData data : finalReportData) {
            Row row = sheet.createRow(rowIndex++);
            int col = 0;

            row.createCell(col++).setCellValue(data.getUserId()); // Ticket Number
            if (data.getName() != null)
                row.createCell(col++).setCellValue(data.getName()); // Name
            else
                row.createCell(col++).setCellValue(userService.findByUserId(data.getUserId()).getName());

            // Normal Loan - Balance
            row.createCell(col++)
                    .setCellValue(data.getNormalBalanceUpload() != 0 ? data.getNormalBalanceUpload() : 0.0);
            row.createCell(col++)
                    .setCellValue(data.getNormalBalanceGenerated() != 0 ? data.getNormalBalanceGenerated() : 0.0);
            row.createCell(col++)
                    .setCellValue(Math.round(((data.getNormalBalanceUpload() != 0 ? data.getNormalBalanceUpload() : 0.0)
                            - (data.getNormalBalanceGenerated() != 0 ? data.getNormalBalanceGenerated() : 0.0)) * 100.0)
                            / 100.0);

            // Normal Loan - Recovery
            row.createCell(col++)
                    .setCellValue(data.getNoramlRecoveryUpload() != 0 ? data.getNoramlRecoveryUpload() : 0.0);
            row.createCell(col++)
                    .setCellValue(data.getNoramlRecoveryGenerated() != 0 ? data.getNoramlRecoveryGenerated() : 0.0);
            row.createCell(col++).setCellValue(
                    Math.round(((data.getNoramlRecoveryUpload() != 0 ? data.getNoramlRecoveryUpload() : 0.0)
                            - (data.getNoramlRecoveryGenerated() != 0 ? data.getNoramlRecoveryGenerated() : 0.0))
                            * 100.0) / 100.0);

            // Normal Loan - Interest
            row.createCell(col++)
                    .setCellValue(data.getNormalInterestUpload() != 0 ? data.getNormalInterestUpload() : 0.0);
            row.createCell(col++)
                    .setCellValue(data.getNormalInterestGenerated() != 0 ? data.getNormalInterestGenerated() : 0.0);
            row.createCell(col++).setCellValue(
                    Math.round(((data.getNormalInterestUpload() != 0 ? data.getNormalInterestUpload() : 0.0)
                            - (data.getNormalInterestGenerated() != 0 ? data.getNormalInterestGenerated() : 0.0))
                            * 100.0) / 100.0);

            // Commodity Loan - Balance
            row.createCell(col++).setCellValue(data.getCommBalanceUpload() != 0 ? data.getCommBalanceUpload() : 0.0);
            row.createCell(col++)
                    .setCellValue(data.getCommBalanceGenerated() != 0 ? data.getCommBalanceGenerated() : 0.0);
            row.createCell(col++)
                    .setCellValue(Math.round(((data.getCommBalanceUpload() != 0 ? data.getCommBalanceUpload() : 0.0)
                            - (data.getCommBalanceGenerated() != 0 ? data.getCommBalanceGenerated() : 0.0)) * 100.0)
                            / 100.0);

            // Commodity Loan - Recovery
            row.createCell(col++).setCellValue(data.getCommRecoveryUpload() != 0 ? data.getCommRecoveryUpload() : 0.0);
            row.createCell(col++)
                    .setCellValue(data.getCommRecoveryGenerated() != 0 ? data.getCommRecoveryGenerated() : 0.0);
            row.createCell(col++)
                    .setCellValue(Math.round(((data.getCommRecoveryUpload() != 0 ? data.getCommRecoveryUpload() : 0.0)
                            - (data.getCommRecoveryGenerated() != 0 ? data.getCommRecoveryGenerated() : 0.0)) * 100.0)
                            / 100.0);

            // Commodity Loan - Interest
            row.createCell(col++).setCellValue(data.getCommInterestUpload() != 0 ? data.getCommInterestUpload() : 0.0);
            row.createCell(col++)
                    .setCellValue(data.getCommInterestGenerated() != 0 ? data.getCommInterestGenerated() : 0.0);
            row.createCell(col++)
                    .setCellValue(Math.round(((data.getCommInterestUpload() != 0 ? data.getCommInterestUpload() : 0.0)
                            - (data.getCommInterestGenerated() != 0 ? data.getCommInterestGenerated() : 0.0)) * 100.0)
                            / 100.0);

            // Share
            row.createCell(col++).setCellValue(data.getShareUpload() != 0 ? data.getShareUpload() : 0.0);
            row.createCell(col++).setCellValue(data.getShareGenerated() != 0 ? data.getShareGenerated() : 0.0);
            row.createCell(col++)
                    .setCellValue(Math
                            .round(((data.getShareUpload() != 0 ? data.getShareUpload() : 0.0)
                                    - (data.getShareGenerated() != 0 ? data.getShareGenerated() : 0.0)) * 100.0)
                            / 100.0);

            // CD
            row.createCell(col++).setCellValue(data.getCdUpload() != 0 ? data.getCdUpload() : 0.0);
            row.createCell(col++).setCellValue(data.getCdGenerated() != 0 ? data.getCdGenerated() : 0.0);
            row.createCell(col++).setCellValue(Math.round(((data.getCdUpload() != 0 ? data.getCdUpload() : 0.0)
                    - (data.getCdGenerated() != 0 ? data.getCdGenerated() : 0.0)) * 100.0) / 100.0);
        }

        // Adjust column widths
        for (int i = 0; i <= 25; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write to browser
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=comparison_report.xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @GetMapping("/userValidation")
    public String userAccess(){
        return "dashboard/user_access";
    }

    @GetMapping("/userValidation/security/{adminKey}")
    public ResponseEntity<accessDTO> checkValidation(@PathVariable int adminKey){
        accessDTO data = new accessDTO();
        Integer accessId = accessCtrlRepo.getAccessId(accessDec.Access_Control);
        boolean isValid = (accessId != null && adminKey == accessId);
        if(isValid){
            data.setAccess(accessCtrlRepo.findAll());
            return ResponseEntity.ok(data);
        }
        return ResponseEntity.ok(data);
    }

    @GetMapping("/userValidation/security/updateKey")
    public ResponseEntity<String> updateKey(@RequestParam int accessId, @RequestParam int newKey) {
        Optional<AccessControl> access = accessCtrlRepo.findById(accessId);
        if (access.isPresent()) {
            AccessControl ctrl = access.get();
            ctrl.setAccessId(newKey);
            accessCtrlRepo.save(ctrl);
            return ResponseEntity.ok("Updated");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found");
    }

}