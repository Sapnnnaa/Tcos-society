package com.telco.members.tcos.controller;

import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;


import com.telco.members.tcos.dto.DocDataDto;
import com.telco.members.tcos.model.CDShareDeductions;
import com.telco.members.tcos.model.DueRecovery;
import com.telco.members.tcos.model.EmiPaymentType;
import com.telco.members.tcos.model.EmiPaymentsDetails;
import com.telco.members.tcos.model.LoanType;
import com.telco.members.tcos.model.RecoveryData;
import com.telco.members.tcos.service.CDShareDeductionService;
import com.telco.members.tcos.service.DueRecoveryService;
import com.telco.members.tcos.service.EmiPaymentDetailsService;
import com.telco.members.tcos.service.LoanService;
import com.telco.members.tcos.service.RecoveryDataService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/members")
public class ReportsUploadController {

    @Autowired
    private RecoveryDataService recoveryDataService;
    @Autowired
    private CDShareDeductionService cdShareDeductionService;
    @Autowired
    private EmiPaymentDetailsService emiPaymentDetailsService;
    @Autowired
    private LoanService loanService;
    @Autowired
    private DueRecoveryService dueRecoveryService;

    @Value("${ghostscript.path}")
    private String ghostscriptPath;

    private static final Logger logger = LoggerFactory.getLogger(ReportsUploadController.class);

    // Using session-based storage to avoid thread safety issues while maintaining separate save functionality
    private final Map<String, UploadSession> sessionStorage = new HashMap<>();

    @GetMapping("/uploadData")
    public String uploadPage() {
        return "reportUpload/reports_upload_page";
    }

    @PostMapping("/tempupload")
    public ResponseEntity<?> getMethodName(@RequestParam("yearmonth") String dateFor,
                                           @RequestParam("file") MultipartFile file,
                                           @RequestParam("companyName") String company,
                                           HttpServletRequest request) {

        List<DocDataDto> reportsList = new ArrayList<>();
        int month = Integer.parseInt(dateFor.substring(5, 7));
        int year = Integer.parseInt(dateFor.substring(0, 4));
        System.out.println(month + "  " + year);

        try {
            String fileName = file.getOriginalFilename();

            if (fileName != null && (fileName.endsWith(".xlsx") || fileName.endsWith(".xls"))) {
                reportsList = processExcelFile(file);
            } else if (fileName != null && fileName.endsWith(".pdf")) {
                reportsList = processPDFFile(file, company);
            } else {
                throw new IllegalArgumentException("Unsupported file type. Only Excel or PDF allowed.");
            }

            // Store in session-based storage for later saving
            String sessionId = request.getSession().getId();
            UploadSession uploadSession = new UploadSession();
            uploadSession.setReportsList(reportsList);
            uploadSession.setMonth(month);
            uploadSession.setYear(year);
            uploadSession.setCompany(company);
            uploadSession.setUploadTime(new Date());

            sessionStorage.put(sessionId, uploadSession);

            // Return data for display without saving
            return ResponseEntity.ok(reportsList);

        } catch (Exception e) {
            System.out.println("error in reports upload controller: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    private List<DocDataDto> processPDFFile(MultipartFile file, String company) throws Exception {

        List<DocDataDto> list = new ArrayList<>();


        File tempPdf = File.createTempFile("upload-", ".pdf");
        file.transferTo(tempPdf);


        int totalPages;

        try (PDDocument doc = Loader.loadPDF(tempPdf)) {
            totalPages = doc.getNumberOfPages();
        }

        System.out.println("TOTAL PAGES : " + totalPages);


        for (int i = 1; i <= totalPages; i++) {

            File outputTxt = File.createTempFile("gs-page-" + i, ".txt");

            // GHOSTSCRIPT
            ProcessBuilder pb = new ProcessBuilder(
                    ghostscriptPath,
                    "-q",
                    "-dFirstPage=" + i,
                    "-dLastPage=" + i,
                    "-sDEVICE=txtwrite",
                    "-o",
                    outputTxt.getAbsolutePath(),
                    tempPdf.getAbsolutePath()
            );

            pb.redirectErrorStream(true);

            Process process = pb.start();

            int exitCode = process.waitFor();

            if (exitCode != 0) {

                System.out.println("Ghostscript failed on page " + i);
                continue;
            }


            String text = java.nio.file.Files.readString(outputTxt.toPath());

            // Validate company only on first page
            if (i == 1) {
                validateCompanyFromPdf(text, company);
            }

            System.out.println("\n================ PAGE " + i + " ================\n");
            System.out.println(text);

            String[] lines = text.split("\\r?\\n");

            boolean contLine = false;

            String currentUserId = null;


            for (String line : lines) {

                line = line.trim();

                if (line.isEmpty()
                        || line.contains("Srl")
                        || line.contains("TOTAL")
                        || line.contains("Page")
                        || line.contains("Company")
                        || line.contains("CONSOLIDATED")) {

                    continue;
                }

                String[] parts = line.split("\\s+");

                // SECOND  LINE

                if (contLine) {

                    contLine = false;

                    if (parts.length == 1) {

                        contLine = true;
                    }

                    else if (parts.length >= 2 && currentUserId != null) {

                        double prin = parseAmount(parts[0]);
                        double interest = parseAmount(parts[1]);

                        String principalCode = "";
                        String interestCode = "";

                        if (company.equals("TML")
                                || company.equals("TMLBSL")
                                || company.equals("TTL")) {

                            // Commodity Loan
                            principalCode = "6010";
                            interestCode = "6015";

                        } else if (company.equals("TCN")) {

                            // Commodity Loan
                            principalCode = "4260";
                            interestCode = "4270";
                        }

                        if (prin > 0) {

                            list.add(createDto(currentUserId,
                                    principalCode,
                                    prin));
                        }

                        if (interest > 0) {

                            list.add(createDto(currentUserId,
                                    interestCode,
                                    interest));
                        }

                    }

                    continue;
                }

                // FIRST MAIN ROW

                if (parts[0].matches("\\d+")) {

                    String userId = null;

                    for (int j = 0; j < parts.length; j++) {

                        // Find MEMB-NO like E0100227516
                        if (parts[j].startsWith("E")) {

                            // Next column should be PERS NO
                            if (j + 1 < parts.length &&
                                    parts[j + 1].matches("\\d{3,6}")) {

                                userId = parts[j + 1];
                            }
                            break;
                        }
                    }

                    if (userId == null) {
                        continue;
                    }

                    currentUserId = userId;

                    // Extract all decimal amounts
                    List<Double> amounts = new ArrayList<>();

                    for (String p : parts) {

                        String cleaned = p.replace(",", "");

                        if (cleaned.matches("\\d+\\.\\d+")) {
                            amounts.add(parseAmount(p));
                        }
                    }

                    double share = amounts.size() > 0 ? amounts.get(0) : 0;
                    double cd = amounts.size() > 1 ? amounts.get(1) : 0;
                    double prin = amounts.size() > 2 ? amounts.get(2) : 0;
                    double interest = amounts.size() > 3 ? amounts.get(3) : 0;

                    String shareCode = "";
                    String cdCode = "";
                    String personalPrincipalCode = "";
                    String personalInterestCode = "";

                    if (company.equals("TML")
                            || company.equals("TMLBSL")
                            || company.equals("TTL")) {

                        shareCode = "6030";
                        cdCode = "6035";

                        // Personal Loan
                        personalPrincipalCode = "6020";
                        personalInterestCode = "6025";

                    } else if (company.equals("TCN")) {

                        shareCode = "";
                        cdCode = "4220";

                        // Personal Loan
                        personalPrincipalCode = "4240";
                        personalInterestCode = "4250";
                    }

                    if (share > 0) {
                        list.add(createDto(userId, shareCode, share));
                    }

                    if (cd > 0) {
                        list.add(createDto(userId, cdCode, cd));
                    }

                    if (prin > 0) {
                        list.add(createDto(userId, personalPrincipalCode, prin));
                    }

                    if (interest > 0) {
                        list.add(createDto(userId, personalInterestCode, interest));
                    }

                    contLine = true;
                }
            }

            outputTxt.delete();
        }

        tempPdf.delete();

        return list;
    }

    private double parseAmount(String val) {
        try {
            return Double.parseDouble(val.replace(",", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    private DocDataDto createDto(String userId, String wageType, double amount) {
        DocDataDto dto = new DocDataDto();
        dto.setUserId(userId);
        dto.setWageType(wageType);
        dto.setAmount(amount);
        return dto;
    }

    private void validateCompanyFromPdf(String text, String selectedCompany) {

        text = text.toUpperCase();

        String detectedCompany = null;

        if (text.contains("COMPANY : 0100 - TATA MOTORS LTD")) {
            detectedCompany = "TML";
        }
        else if (text.contains("COMPANY : 0300 - TATA MOTORS GS LTD")) {
            detectedCompany = "TMLBSL";
        }
        else if (text.contains("COMPANY : TTL - TATA TECHNOLOGIES LTD")) {
            detectedCompany = "TTL";
        }
        else if (text.contains("COMPANY : TCN - TELCON LTD")) {
            detectedCompany = "TCN";
        }

        if (detectedCompany == null) {
            throw new RuntimeException("Unable to identify company from uploaded PDF.");
        }

        if (!detectedCompany.equals(selectedCompany)) {
            throw new RuntimeException(
                    "Wrong PDF uploaded. Selected Company = "
                            + selectedCompany
                            + ", PDF belongs to "
                            + detectedCompany
            );
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
                    Cell userIdCell = row.getCell(0); // Column C
                    Cell wageTypeCell = row.getCell(6); // Column D
                    Cell amountCell = row.getCell(7); // Column F

                    // Skip if any cell is missing
                    if (userIdCell == null || wageTypeCell == null || amountCell == null) {
                        continue;
                    }

                    DocDataDto dto = new DocDataDto();

                    // Process User ID
                    if (userIdCell.getCellType() == CellType.STRING) {
                        String userIdStr = userIdCell.getStringCellValue().trim();
                        if (userIdStr.isEmpty()) {
                            continue;
                        }
                        dto.setUserId(userIdStr);
                    } else if (userIdCell.getCellType() == CellType.NUMERIC) {
                        dto.setUserId(String.valueOf((long) userIdCell.getNumericCellValue()));
                    } else {
                        continue; // Skip if userId is neither string nor numeric
                    }

                    // Process Wage Type
                    if (wageTypeCell.getCellType() == CellType.STRING) {
                        dto.setWageType(wageTypeCell.getStringCellValue().trim());
                    } else if (wageTypeCell.getCellType() == CellType.NUMERIC) {
                        dto.setWageType(String.valueOf((int) wageTypeCell.getNumericCellValue()));
                    } else {
                        dto.setWageType(""); // or continue to skip this row
                    }

                    // Process Amount
                    if (amountCell.getCellType() == CellType.STRING) {
                        String amountStr = amountCell.getStringCellValue().trim();
                        if (amountStr.isEmpty()) {
                            continue;
                        }
                        dto.setAmount(Double.parseDouble(amountStr));
                    } else if (amountCell.getCellType() == CellType.NUMERIC) {
                        dto.setAmount(amountCell.getNumericCellValue());
                    } else {
                        continue; // Skip if amount is neither string nor numeric
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

    private ResponseEntity<?> processAndSaveData(List<DocDataDto> allRecords, int month, int year, String company) {
        try {
            // Create payment date (1st of the given month/year)
            Date paymentDate = Date.from(LocalDate.of(year, month, 1).atStartOfDay(ZoneOffset.UTC).toInstant());
            Date currentDate = new Date();

            Map<String, CDShareDeductions> cdShareMap = new HashMap<>();
            Map<String, EmiPaymentsDetails> emiPaymentsMap = new HashMap<>();
            List<RecoveryData> recoveryDataList = new ArrayList<>();

            if (company.equals("TMLBSL") || company.equals("TML") || company.equals("TTL")) {
                // Process all records
                for (DocDataDto dto : allRecords) {
                    String userId = dto.getUserId();
                    double amount = dto.getAmount();
                    String wageType = dto.getWageType();

                    if (wageType.equals("6035") || wageType.equals("6030")) {
                        // Process CD/Share Deductions
                        CDShareDeductions deduction = cdShareMap.computeIfAbsent(userId,
                                id -> createNewCDShareDeduction(id, paymentDate, currentDate));

                        if (wageType.equals("6035")) {
                            deduction.setCdDeduction(deduction.getCdDeduction() + amount);
                            deduction.setTotalDeduction(deduction.getTotalDeduction() + amount);
                        } else if (wageType.equals("6030")) {
                            deduction.setShareDeduction(deduction.getShareDeduction() + amount);
                            deduction.setTotalDeduction(deduction.getTotalDeduction() + amount);
                        }
                    } else {
                        // Process EMI Payments - FIXED: Removed 6030 from here since it's handled above
                        LoanType loanType = (wageType.equals("6010") || wageType.equals("6015") || wageType.equals("6351")) ? LoanType.COMMODITY
                                : LoanType.PERSONAL;

                        String compositeKey = userId + "|" + loanType.name();

                        EmiPaymentsDetails emiPayment = emiPaymentsMap.computeIfAbsent(compositeKey,
                                key -> createNewEmiPayment(userId, paymentDate, currentDate, loanType));

                        // Process different wage types - FIXED: Removed 6030 from here
                        // Process different wage types
                        if (wageType.equals("6010") || wageType.equals("6020")) {

                            // Commodity Principal (6010)
                            // Personal Principal (6020)

                            emiPayment.setEmiAmount(emiPayment.getEmiAmount() + amount);
                            emiPayment.setPrincipalRemaing(emiPayment.getPrincipalRemaing() + amount);

                        } else if (wageType.equals("6025") || wageType.equals("6015")) {

                            // Personal Interest (6025)
                            // Commodity Interest (6015)

                            emiPayment.setUploadedInterest(emiPayment.getUploadedInterest() + amount);

                        } else if (wageType.equals("6350") || wageType.equals("6351")) {

                            emiPayment.setPrincipalRemaing(emiPayment.getPrincipalRemaing() + amount);
                        }
                    }

                    // Save recovery data
                    RecoveryData data = new RecoveryData();
                    data.setUserId(userId);
                    data.setAmount(amount);
                    data.setWageType(wageType);
                    data.setForDate(paymentDate);
                    recoveryDataList.add(data);
                }
            } else if(company.equals("TCN")) {
                for (DocDataDto dto : allRecords) {
                    String userId = dto.getUserId();
                    double amount = dto.getAmount();
                    String wageType = dto.getWageType();

                    // Fixed empty string check
                    if (wageType.equals("4220") || wageType.isEmpty()) {
                        // Process CD/Share Deductions
                        CDShareDeductions deduction = cdShareMap.computeIfAbsent(userId,
                                id -> createNewCDShareDeduction(id, paymentDate, currentDate));

                        if (wageType.equals("4220")) {
                            deduction.setCdDeduction(deduction.getCdDeduction() + amount);
                            deduction.setTotalDeduction(deduction.getTotalDeduction() + amount);
                        } else if (wageType.isEmpty()) {
                            deduction.setShareDeduction(deduction.getShareDeduction() + amount);
                            deduction.setTotalDeduction(deduction.getTotalDeduction() + amount);
                        }
                    } else {
                        // Process EMI Payments
                        LoanType loanType = (wageType.equals("4260") || wageType.equals("4270") || wageType.equals("426B")) ? LoanType.COMMODITY
                                : LoanType.PERSONAL;

                        String compositeKey = userId + "|" + loanType.name();

                        EmiPaymentsDetails emiPayment = emiPaymentsMap.computeIfAbsent(compositeKey,
                                key -> createNewEmiPayment(userId, paymentDate, currentDate, loanType));

                        // Process different wage types
                        if (wageType.equals("4240") || wageType.equals("4260")) {
                            emiPayment.setEmiAmount(emiPayment.getEmiAmount() + amount);
                            emiPayment.setPrincipalRemaing(emiPayment.getPrincipalRemaing() + amount);
                        } else if (wageType.equals("4250") || wageType.equals("4270")) {
                            emiPayment.setUploadedInterest(emiPayment.getUploadedInterest() + amount);
                        } else if (wageType.equals("424B") || wageType.equals("426B")) {
                            emiPayment.setPrincipalRemaing(emiPayment.getPrincipalRemaing() + amount);
                        }
                    }

                    // Save recovery data
                    RecoveryData data = new RecoveryData();
                    data.setUserId(userId);
                    data.setAmount(amount);
                    data.setWageType(wageType);
                    data.setForDate(paymentDate);
                    recoveryDataList.add(data);
                }
            }

            // Save all data
            cdShareDeductionService.saveAll(new ArrayList<>(cdShareMap.values()));
            emiPaymentDetailsService.saveAll(new ArrayList<>(emiPaymentsMap.values()));
            recoveryDataService.saveAll(recoveryDataList);

            int cid=101;
            if(company.equals("TML"))
                cid=101;
            else if(company.equals("TMLBSL"))
                cid=102;
            else if(company.equals("TTL"))
                cid=103;
            else if(company.equals("TCN"))
                cid=104;

            recoveryDataService.recoveryDueCheck(currentDate, cid);

            // Return the processed records for display
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("data", allRecords);
            response.put("message", "Data processed and saved successfully");

            loanService.loanEnd();// calling for loan_end function to mark loans status as needed

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to save data: " + e.getMessage()));
        }
    }

    // Now uses session-based data for saving
    @GetMapping("/saveReport")
    public ResponseEntity<?> saveReportData(HttpServletRequest request) {
        try {
            String sessionId = request.getSession().getId();
            UploadSession uploadSession = sessionStorage.get(sessionId);

            if (uploadSession == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "No upload session found. Please upload a file first."));
            }

            // Process and save the data
            ResponseEntity<?> result = processAndSaveData(
                    uploadSession.getReportsList(),
                    uploadSession.getMonth(),
                    uploadSession.getYear(),
                    uploadSession.getCompany()
            );

            // Clear session data after successful save
            if (result.getStatusCode() == HttpStatus.OK) {
                sessionStorage.remove(sessionId);
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to save data: " + e.getMessage()));
        }
    }

    private EmiPaymentsDetails createNewEmiPayment(String userId, Date paymentDate, Date createdAt, LoanType loanType) {
        EmiPaymentsDetails emiPayment = new EmiPaymentsDetails();
        emiPayment.setUserId(userId);
        emiPayment.setPaymentDate(paymentDate);
        emiPayment.setCreatedAt(createdAt);
        emiPayment.setLoanType(loanType);
        emiPayment.setEmiPaymentType(EmiPaymentType.Normal_deduction);

        emiPayment.setEmiAmount(0.0);
        emiPayment.setPrincipalRemaing(0.0);
        emiPayment.setUploadedInterest(0.0);

        return emiPayment;
    }

    private static CDShareDeductions createNewCDShareDeduction(String userId, Date paymentDate, Date createdAt) {
        CDShareDeductions deduction = new CDShareDeductions();
        deduction.setUserId(userId);
        deduction.setPaymentDate(paymentDate);
        deduction.setCreatedAt(createdAt);
        // Initialize all amounts to 0
        deduction.setCdDeduction(0.0);
        deduction.setCdDeductionTotal(0.0);
        deduction.setShareDeduction(0.0);
        deduction.setShareDeductionTotal(0.0);
        deduction.setTotalDeduction(0.0);
        deduction.setCdDeductionGenerated(0.0);
        deduction.setShareDeductionGenerated(0.0);
        return deduction;
    }



    // Inner class for session-based storage to avoid thread safety issues
    private static class UploadSession {
        private List<DocDataDto> reportsList;
        private int month;
        private int year;
        private String company;
        private Date uploadTime;

        // Getters and Setters
        public List<DocDataDto> getReportsList() { return reportsList; }
        public void setReportsList(List<DocDataDto> reportsList) { this.reportsList = reportsList; }
        public int getMonth() { return month; }
        public void setMonth(int month) { this.month = month; }
        public int getYear() { return year; }
        public void setYear(int year) { this.year = year; }
        public String getCompany() { return company; }
        public void setCompany(String company) { this.company = company; }
        public Date getUploadTime() { return uploadTime; }
        public void setUploadTime(Date uploadTime) { this.uploadTime = uploadTime; }
    }


    @GetMapping("/recoveryDue")
    public String showDifferencePage(Model model) {

        List<DueRecovery> list=dueRecoveryService.findAll();
        model.addAttribute("dueRecovery",list);

        return "reportUpload/due_recovery";
    }

    @GetMapping("/recoveryDue/download")
    public void downloadExcel(HttpServletResponse response) {
        try {
            List<DueRecovery> list = dueRecoveryService.findAll();
            // Create workbook and sheet
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Due Recoveries");

            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);

            // Create data style
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);

            // Create amount style (for currency formatting)
            CellStyle amountStyle = workbook.createCellStyle();
            amountStyle.cloneStyleFrom(dataStyle);
            DataFormat format = workbook.createDataFormat();
            amountStyle.setDataFormat(format.getFormat("₹#,##0.00"));

            // Create date style
            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.cloneStyleFrom(dataStyle);
            dateStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("dd-MM-yyyy"));

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Ticket Number", "Wage Type", "Amount (₹)", "For Date"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Fill data rows
            int rowNum = 1;
            for (DueRecovery recovery : list) {
                Row row = sheet.createRow(rowNum++);

                // Ticket Number
                Cell ticketCell = row.createCell(0);
                ticketCell.setCellValue(recovery.getUserId());
                ticketCell.setCellStyle(dataStyle);

                // Wage Type
                Cell wageTypeCell = row.createCell(1);
                wageTypeCell.setCellValue(recovery.getWageType());
                wageTypeCell.setCellStyle(dataStyle);

                // Amount
                Cell amountCell = row.createCell(2);
                amountCell.setCellValue(recovery.getAmount());
                amountCell.setCellStyle(amountStyle);

                // For Date
                Cell dateCell = row.createCell(3);
                if (recovery.getForDate() != null) {
                    dateCell.setCellValue(recovery.getForDate());
                } else {
                    dateCell.setCellValue("");
                }
                dateCell.setCellStyle(dateStyle);
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Add summary row if needed
            if (!list.isEmpty()) {
                Row summaryRow = sheet.createRow(rowNum + 1);

                // Total label
                Cell totalLabelCell = summaryRow.createCell(1);
                totalLabelCell.setCellValue("TOTAL:");
                CellStyle boldStyle = workbook.createCellStyle();
                Font boldFont = workbook.createFont();
                boldFont.setBold(true);
                boldStyle.setFont(boldFont);
                totalLabelCell.setCellStyle(boldStyle);

                // Total amount
                Cell totalAmountCell = summaryRow.createCell(2);
                double totalAmount = list.stream().mapToDouble(r -> r.getAmount()).sum(); // Removed .doubleValue()
                totalAmountCell.setCellValue(totalAmount);
                CellStyle totalAmountStyle = workbook.createCellStyle();
                totalAmountStyle.cloneStyleFrom(amountStyle);
                totalAmountStyle.setFont(boldFont);
                totalAmountCell.setCellStyle(totalAmountStyle);
            }

            // Generate filename with current date
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
            String filename = "Due_Recoveries_" + now.format(formatter) + ".xlsx";

            // Set response headers
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");

            // Write workbook to response
            workbook.write(response.getOutputStream());
            workbook.close();

        } catch (Exception e) {
            logger.error("Error generating Excel file", e);
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Error generating Excel file: " + e.getMessage());
            } catch (IOException ioException) {
                logger.error("Error sending error response", ioException);
            }
        }
    }



}