package com.telco.members.tcos.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.telco.members.tcos.dto.CDMonthDTO;
import com.telco.members.tcos.dto.CDSettlementDTO;
import com.telco.members.tcos.dto.CommodityMonthDTO;
import com.telco.members.tcos.dto.CommoditySettlementDTO;
import com.telco.members.tcos.dto.PersonalMonthDTO;
import com.telco.members.tcos.dto.PersonalSettlementDTO;
import com.telco.members.tcos.dto.SettlementDTO;
import com.telco.members.tcos.dto.SettlementMonthDTO;
import com.telco.members.tcos.dto.ShareMonthDTO;
import com.telco.members.tcos.dto.ShareSettlementDTO;
import com.telco.members.tcos.model.CDShareDeductions;
import com.telco.members.tcos.model.EmiPaymentsDetails;
import com.telco.members.tcos.model.Loan;
import com.telco.members.tcos.model.LoanType;
import com.telco.members.tcos.model.User;
import com.telco.members.tcos.repository.CompanyRepository;
import com.telco.members.tcos.repository.EmiPaymentsDetailsRepository;
import com.telco.members.tcos.repository.loanRepository;
import com.telco.members.tcos.repository.usersRepository;

@Service
public class SettlementService {

    @Autowired
    private CDShareDeductionService cdShareDeductionService;

    @Autowired
    private EmiPaymentsDetailsRepository emiRepository;

    @Autowired
    private loanRepository loanRepository;

    @Autowired
    private usersRepository usersRepository;

    @Autowired
    private CompanyRepository companyRepository;


    public SettlementDTO getSettlement(
            String userId,
            String financialYear) {

        SettlementDTO settlementDTO =
                new SettlementDTO();


        //====================================================
        // USER INFORMATION
        //====================================================

        User user =
                usersRepository.findByUserId(userId);

        if (user != null) {

            settlementDTO.setMemberId(
                    user.getUserId());

            settlementDTO.setMemberName(
                    user.getName());

            settlementDTO.setCompany(
                    companyRepository
                            .getCompanyBycompanyId(
                                    user.getCompanyId()));
        }


        //====================================================
        // FINANCIAL YEAR
        //====================================================

        String[] year =
                financialYear.split("-");

        LocalDate fromDate =
                LocalDate.of(
                        Integer.parseInt(year[0]),
                        Month.APRIL,
                        1);

        LocalDate toDate =
                LocalDate.of(
                        Integer.parseInt(year[1]),
                        Month.MARCH,
                        31);

        Date from =
                java.sql.Date.valueOf(fromDate);

        Date to =
                java.sql.Date.valueOf(toDate);


        //====================================================
// SHARE + CD DATA
//====================================================

        List<CDShareDeductions> deductions =
                cdShareDeductionService
                        .getSettlementData(
                                userId,
                                fromDate,
                                toDate);


        if (deductions != null && !deductions.isEmpty()) {

            //================================================
            // SORT BY PAYMENT DATE
            //================================================

            deductions.sort((a, b) -> {

                Date d1 = a.getPaymentDate();
                Date d2 = b.getPaymentDate();

                if (d1 == null && d2 == null) {
                    return 0;
                }

                if (d1 == null) {
                    return 1;
                }

                if (d2 == null) {
                    return -1;
                }

                return d1.compareTo(d2);
            });


            //================================================
            // SHARE DTO
            //================================================

            ShareSettlementDTO shareDTO =
                    new ShareSettlementDTO();

            List<ShareMonthDTO> shareMonths =
                    new ArrayList<>();


            //================================================
            // CD DTO
            //================================================

            CDSettlementDTO cdDTO =
                    new CDSettlementDTO();

            List<CDMonthDTO> cdMonths =
                    new ArrayList<>();


            //================================================
            // OPENING BALANCE
            //================================================

            CDShareDeductions first =
                    deductions.get(0);


            double openingShare =
                    first.getShareDeductionTotal()
                            - first.getShareDeduction();


            double openingCD =
                    first.getCdDeductionTotal()
                            - first.getCdDeduction();


            shareDTO.setOpeningBalance(
                    openingShare);

            cdDTO.setOpeningBalance(
                    openingCD);


            //================================================
            // RUNNING BALANCES
            //================================================

            double shareRunningBalance =
                    openingShare;

            double cdRunningBalance =
                    openingCD;


            //================================================
            // APRIL TO MARCH
            //================================================

            LocalDate currentDate =
                    fromDate;


            while (!currentDate.isAfter(toDate)) {

                //================================================
                // MONTH NAME
                //================================================

                String monthName =
                        currentDate.getMonth()
                                .toString()
                                .substring(0, 1)
                                +
                                currentDate.getMonth()
                                        .toString()
                                        .substring(1)
                                        .toLowerCase();


                //================================================
                // DEFAULT MONTH VALUES
                //================================================

                double shareDeduction = 0;

                double cdDeduction = 0;


                //================================================
                // FIND DEDUCTION FOR THIS MONTH
                //================================================

                for (CDShareDeductions row : deductions) {

                    Date paymentDate =
                            row.getPaymentDate();

                    if (paymentDate == null) {
                        continue;
                    }


                    LocalDate paymentDateLocal =
                            new java.sql.Date(
                                    paymentDate.getTime())
                                    .toLocalDate();


                    boolean sameMonth =
                            paymentDateLocal.getYear()
                                    == currentDate.getYear()
                                    &&
                                    paymentDateLocal.getMonthValue()
                                            == currentDate.getMonthValue();


                    if (sameMonth) {

                        // SHARE
                        shareDeduction +=
                                row.getShareDeduction();


                        // CD
                        cdDeduction +=
                                row.getCdDeduction();
                    }
                }


                //================================================
                // UPDATE SHARE BALANCE
                //================================================

                shareRunningBalance +=
                        shareDeduction;


                //================================================
                // UPDATE CD BALANCE
                //================================================

                cdRunningBalance +=
                        cdDeduction;


                //================================================
                // SHARE MONTH
                //================================================

                ShareMonthDTO shareMonth =
                        new ShareMonthDTO();

                shareMonth.setMonth(
                        monthName);

                shareMonth.setDeduction(
                        shareDeduction);

                shareMonths.add(
                        shareMonth);


                //================================================
                // CD MONTH
                //================================================

                CDMonthDTO cdMonth =
                        new CDMonthDTO();

                cdMonth.setMonth(
                        monthName);

                cdMonth.setDeposit(
                        cdDeduction);

                cdMonth.setRunningBalance(
                        cdRunningBalance);

                cdMonths.add(
                        cdMonth);


                //================================================
                // NEXT MONTH
                //================================================

                currentDate =
                        currentDate.plusMonths(1);
            }


            //================================================
            // SHARE
            //================================================

            shareDTO.setMonths(
                    shareMonths);

            shareDTO.setClosingBalance(
                    shareRunningBalance);


            //================================================
            // CD
            //================================================

            cdDTO.setMonths(
                    cdMonths);

            cdDTO.setClosingBalance(
                    cdRunningBalance);


            //================================================
            // CD INTEREST
            //================================================

            CDShareDeductions last =
                    deductions.get(
                            deductions.size() - 1);

            cdDTO.setInterest(
                    last.getCdInterestTotal());


            //================================================
            // SET DTO
            //================================================

            settlementDTO.setShare(
                    shareDTO);

            settlementDTO.setCd(
                    cdDTO);
        }


        //====================================================
// PERSONAL LOAN SETTLEMENT
//====================================================

        List<Loan> personalLoans =
                loanRepository.getLoansTillDate(
                        userId,
                        LoanType.PERSONAL,
                        to);


        if (personalLoans != null
                && !personalLoans.isEmpty()) {


            //================================================
            // EMI PAYMENTS
            //================================================

            List<EmiPaymentsDetails> personalEmis =
                    emiRepository.getSettlementEmis(
                            userId,
                            LoanType.PERSONAL,
                            from,
                            to);


            //================================================
            // SORT LOANS
            // OLDEST -> NEWEST
            //================================================

            personalLoans.sort((a, b) -> {

                Date d1 =
                        a.getLoanApproveDate();

                Date d2 =
                        b.getLoanApproveDate();

                if (d1 == null && d2 == null) {
                    return 0;
                }

                if (d1 == null) {
                    return 1;
                }

                if (d2 == null) {
                    return -1;
                }

                return d1.compareTo(d2);
            });


            //================================================
            // SORT EMI
            //================================================

            if (personalEmis != null) {

                personalEmis.sort((a, b) -> {

                    Date d1 =
                            a.getPaymentDate();

                    Date d2 =
                            b.getPaymentDate();

                    if (d1 == null && d2 == null) {
                        return 0;
                    }

                    if (d1 == null) {
                        return 1;
                    }

                    if (d2 == null) {
                        return -1;
                    }

                    return d1.compareTo(d2);
                });
            }


            //================================================
            // PERSONAL DTO
            //================================================

            PersonalSettlementDTO personalDTO =
                    new PersonalSettlementDTO();

            List<PersonalMonthDTO> personalMonths =
                    new ArrayList<>();


            //================================================
            // LAST LOAN DETAILS
            // THESE ARE ONLY FOR PREVIEW MODAL
            //================================================

            String lastPersonalLoanMonth = null;

            String lastPersonalLoanDate = null;

            double lastPersonalLoanAmount = 0;

            double lastPersonalLoanContra = 0;


            //================================================
            // FIND OPENING LOAN
            //================================================

            double openingBalance = 0;

            Loan openingLoan = null;


            for (Loan loan :
                    personalLoans) {

                Date approveDate =
                        loan.getLoanApproveDate();

                if (approveDate == null) {
                    continue;
                }


                // Loan approved on or before
                // financial year start

                if (!approveDate.after(from)) {

                    if (openingLoan == null) {

                        openingLoan = loan;

                    } else {

                        Date oldDate =
                                openingLoan
                                        .getLoanApproveDate();

                        if (approveDate.after(oldDate)) {

                            openingLoan = loan;
                        }
                    }
                }
            }


            //================================================
            // OPENING BALANCE
            //================================================

            if (openingLoan != null) {

                openingBalance =
                        openingLoan.getLoanBalance();
            }


            personalDTO.setOpeningBalance(
                    openingBalance);


            //================================================
            // RUNNING BALANCE
            //================================================

            double runningBalance =
                    openingBalance;


            //================================================
            // APRIL TO MARCH
            //================================================

            LocalDate currentDate =
                    fromDate;


            while (!currentDate.isAfter(toDate)) {


                //================================================
                // MONTH NAME
                //================================================

                String monthName =
                        currentDate.getMonth()
                                .toString()
                                .substring(0, 1)
                                +
                                currentDate.getMonth()
                                        .toString()
                                        .substring(1)
                                        .toLowerCase();


                //================================================
                // CREATE MONTH DTO
                //================================================

                PersonalMonthDTO monthDTO =
                        new PersonalMonthDTO();

                monthDTO.setMonth(
                        monthName);


                //================================================
                // DEFAULT VALUES
                //================================================

                monthDTO.setLnMonth(null);

                monthDTO.setLoanAmount(0);

                monthDTO.setLoanContra(0);

                monthDTO.setPrincipal(0);

                monthDTO.setInterest(0);

                monthDTO.setActualInterest(0);

                monthDTO.setBalance(
                        runningBalance);


                //================================================
                // CHECK NEW PERSONAL LOAN
                //================================================

                for (Loan loan :
                        personalLoans) {

                    Date approveDate =
                            loan.getLoanApproveDate();

                    if (approveDate == null) {
                        continue;
                    }


                    LocalDate loanDate =
                            new java.sql.Date(
                                    approveDate.getTime())
                                    .toLocalDate();


                    //================================================
                    // CHECK SAME MONTH
                    //================================================

                    boolean sameMonth =
                            loanDate.getYear()
                                    == currentDate.getYear()
                                    &&
                                    loanDate.getMonthValue()
                                            == currentDate.getMonthValue();


                    //================================================
                    // CHECK FINANCIAL YEAR
                    //================================================

                    boolean duringFY =
                            !loanDate.isBefore(
                                    fromDate)
                                    &&
                                    !loanDate.isAfter(
                                            toDate);


                    if (sameMonth && duringFY) {


                        //================================================
                        // OLD OUTSTANDING BALANCE
                        //
                        // THIS IS YOUR LOAN CONTRA
                        //================================================

                        double loanContra =
                                runningBalance;


                        //================================================
                        // NEW LOAN AMOUNT
                        //================================================

                        double loanAmount =
                                loan.getLoanAmount();


                        //================================================
                        // SAVE LAST LOAN DETAILS
                        // FOR PREVIEW MODAL
                        //
                        // Because loans are sorted
                        // OLD -> NEW, every new loan
                        // overwrites these values.
                        //
                        // Therefore at the end they contain
                        // the LAST loan.
                        //================================================

                        lastPersonalLoanMonth =
                                monthName;


                        lastPersonalLoanAmount =
                                loanAmount;


                        lastPersonalLoanContra =
                                loanContra;


                        lastPersonalLoanDate =
                                new SimpleDateFormat(
                                        "dd-MM-yyyy")
                                        .format(
                                                approveDate);


                        //================================================
                        // NEW LOAN BECOMES OUTSTANDING
                        //================================================

                        runningBalance =
                                loanAmount;


                        //================================================
                        // EXISTING UI LOGIC
                        //
                        // KEEP 1
                        // Your normal monthly table
                        // will continue showing 1
                        // when loan is taken.
                        //================================================

                        monthDTO.setLnMonth(
                                1);


                        //================================================
                        // LOAN AMOUNT
                        //================================================

                        monthDTO.setLoanAmount(
                                loanAmount);


                        //================================================
                        // LOAN CONTRA
                        //================================================

                        monthDTO.setLoanContra(
                                loanContra);


                        //================================================
                        // BALANCE AFTER NEW LOAN
                        //================================================

                        monthDTO.setBalance(
                                runningBalance);
                    }
                }


                //================================================
                // EMI PAYMENTS
                //================================================

                if (personalEmis != null) {

                    for (EmiPaymentsDetails emi :
                            personalEmis) {

                        Date paymentDate =
                                emi.getPaymentDate();

                        if (paymentDate == null) {
                            continue;
                        }


                        LocalDate emiDate =
                                new java.sql.Date(
                                        paymentDate.getTime())
                                        .toLocalDate();


                        //================================================
                        // CHECK SAME MONTH
                        //================================================

                        boolean sameMonth =
                                emiDate.getYear()
                                        == currentDate.getYear()
                                        &&
                                        emiDate.getMonthValue()
                                                == currentDate.getMonthValue();


                        if (!sameMonth) {
                            continue;
                        }


                        //================================================
                        // PRINCIPAL
                        //================================================

                        double principal =
                                emi.getEmiAmount();


                        //================================================
                        // UPLOADED INTEREST
                        //================================================

                        double uploadedInterest =
                                emi.getUploadedInterest();


                        //================================================
                        // ACTUAL / GENERATED INTEREST
                        //================================================

                        double actualInterest =
                                emi.getGeneratedInterest();


                        //================================================
                        // REDUCE OUTSTANDING BY PRINCIPAL
                        //================================================

                        runningBalance =
                                runningBalance
                                        - principal;


                        //================================================
                        // PRINCIPAL
                        //================================================

                        monthDTO.setPrincipal(
                                monthDTO.getPrincipal()
                                        + principal);


                        //================================================
                        // UPLOADED INTEREST
                        //================================================

                        monthDTO.setInterest(
                                monthDTO.getInterest()
                                        + uploadedInterest);


                        //================================================
                        // ACTUAL INTEREST
                        //================================================

                        monthDTO.setActualInterest(
                                monthDTO.getActualInterest()
                                        + actualInterest);


                        //================================================
                        // FINAL MONTH BALANCE
                        //================================================

                        monthDTO.setBalance(
                                runningBalance);
                    }
                }


                //================================================
                // ADD MONTH
                //================================================

                personalMonths.add(
                        monthDTO);


                //================================================
                // NEXT MONTH
                //================================================

                currentDate =
                        currentDate.plusMonths(1);
            }


            //================================================
            // PERSONAL TOTALS
            //================================================

            double totalPrincipal = 0;

            double totalInterest = 0;

            double totalActualInterest = 0;


            for (PersonalMonthDTO row :
                    personalMonths) {

                totalPrincipal +=
                        row.getPrincipal();

                totalInterest +=
                        row.getInterest();

                totalActualInterest +=
                        row.getActualInterest();
            }


            //================================================
            // SET PERSONAL MONTH DATA
            //================================================

            personalDTO.setMonths(
                    personalMonths);


            //================================================
            // SET PERSONAL TOTALS
            //================================================

            personalDTO.setTotalPrincipal(
                    totalPrincipal);

            personalDTO.setTotalInterest(
                    totalInterest);

            personalDTO.setTotalActualInterest(
                    totalActualInterest);


            //================================================
            // FINAL CLOSING BALANCE
            //================================================

            personalDTO.setClosingBalance(
                    runningBalance);


            //================================================
            // SET LAST LOAN DETAILS
            // FOR PREVIEW MODAL
            //================================================

            personalDTO.setLastLoanMonth(
                    lastPersonalLoanMonth);

            personalDTO.setLastLoanDate(
                    lastPersonalLoanDate);

            personalDTO.setLastLoanAmount(
                    lastPersonalLoanAmount);

            personalDTO.setLastLoanContra(
                    lastPersonalLoanContra);


            //================================================
            // SET PERSONAL DTO
            //================================================

            settlementDTO.setPersonal(
                    personalDTO);
        }



        //====================================================
// COMMODITY LOAN SETTLEMENT
//====================================================

        List<Loan> commodityLoans =
                loanRepository.getLoansTillDate(
                        userId,
                        LoanType.COMMODITY,
                        to);


        if (commodityLoans != null
                && !commodityLoans.isEmpty()) {


            //================================================
            // EMI PAYMENTS IN FINANCIAL YEAR
            //================================================

            List<EmiPaymentsDetails> commodityEmis =
                    emiRepository.getSettlementEmis(
                            userId,
                            LoanType.COMMODITY,
                            from,
                            to);


            //================================================
            // SORT LOANS
            // OLDEST -> NEWEST
            //================================================

            commodityLoans.sort((a, b) -> {

                Date d1 =
                        a.getLoanApproveDate();

                Date d2 =
                        b.getLoanApproveDate();

                if (d1 == null && d2 == null) {
                    return 0;
                }

                if (d1 == null) {
                    return 1;
                }

                if (d2 == null) {
                    return -1;
                }

                return d1.compareTo(d2);
            });


            //================================================
            // SORT EMI PAYMENTS
            // OLDEST -> NEWEST
            //================================================

            if (commodityEmis != null) {

                commodityEmis.sort((a, b) -> {

                    Date d1 =
                            a.getPaymentDate();

                    Date d2 =
                            b.getPaymentDate();

                    if (d1 == null && d2 == null) {
                        return 0;
                    }

                    if (d1 == null) {
                        return 1;
                    }

                    if (d2 == null) {
                        return -1;
                    }

                    return d1.compareTo(d2);
                });
            }


            //================================================
            // COMMODITY DTO
            //================================================

            CommoditySettlementDTO commodityDTO =
                    new CommoditySettlementDTO();

            List<CommodityMonthDTO> commodityMonths =
                    new ArrayList<>();


            //================================================
            // LAST LOAN DETAILS
            // FOR PREVIEW MODAL
            //================================================

            String lastCommodityLoanMonth = null;

            String lastCommodityLoanDate = null;

            double lastCommodityLoanAmount = 0;

            double lastCommodityLoanContra = 0;


            //================================================
            // FIND OPENING LOAN
            //================================================

            double openingBalance = 0;

            Loan openingLoan = null;


            for (Loan loan :
                    commodityLoans) {

                Date approveDate =
                        loan.getLoanApproveDate();

                if (approveDate == null) {
                    continue;
                }


                //================================================
                // LOAN APPROVED ON OR BEFORE
                // FINANCIAL YEAR START
                //================================================

                if (!approveDate.after(from)) {

                    if (openingLoan == null) {

                        openingLoan = loan;

                    } else {

                        Date oldDate =
                                openingLoan
                                        .getLoanApproveDate();

                        if (approveDate.after(oldDate)) {

                            openingLoan = loan;
                        }
                    }
                }
            }


            //================================================
            // OPENING BALANCE
            //================================================

            if (openingLoan != null) {

                openingBalance =
                        openingLoan.getLoanBalance();
            }


            commodityDTO.setOpeningBalance(
                    openingBalance);


            //================================================
            // RUNNING BALANCE
            //================================================

            double runningBalance =
                    openingBalance;


            //================================================
            // APRIL TO MARCH
            //================================================

            LocalDate currentDate =
                    fromDate;


            while (!currentDate.isAfter(toDate)) {


                //================================================
                // MONTH NAME
                //================================================

                String monthName =
                        currentDate.getMonth()
                                .toString()
                                .substring(0, 1)
                                +
                                currentDate.getMonth()
                                        .toString()
                                        .substring(1)
                                        .toLowerCase();


                //================================================
                // CREATE MONTH DTO
                //================================================

                CommodityMonthDTO monthDTO =
                        new CommodityMonthDTO();

                monthDTO.setMonth(
                        monthName);


                //================================================
                // DEFAULT VALUES
                //================================================

                monthDTO.setLnMonth(null);

                monthDTO.setLoanAmount(0);

                monthDTO.setLoanContra(0);

                monthDTO.setPrincipal(0);

                monthDTO.setInterest(0);

                monthDTO.setActualInterest(0);

                monthDTO.setBalance(
                        runningBalance);


                //================================================
                // CHECK NEW COMMODITY LOAN
                //================================================

                for (Loan loan :
                        commodityLoans) {

                    Date approveDate =
                            loan.getLoanApproveDate();

                    if (approveDate == null) {
                        continue;
                    }


                    //================================================
                    // CONVERT LOAN DATE
                    //================================================

                    LocalDate loanDate =
                            new java.sql.Date(
                                    approveDate.getTime())
                                    .toLocalDate();


                    //================================================
                    // CHECK SAME MONTH
                    //================================================

                    boolean sameMonth =
                            loanDate.getYear()
                                    == currentDate.getYear()
                                    &&
                                    loanDate.getMonthValue()
                                            == currentDate.getMonthValue();


                    //================================================
                    // CHECK FINANCIAL YEAR
                    //================================================

                    boolean duringFY =
                            !loanDate.isBefore(
                                    fromDate)
                                    &&
                                    !loanDate.isAfter(
                                            toDate);


                    if (sameMonth && duringFY) {


                        //================================================
                        // OLD OUTSTANDING BALANCE
                        //
                        // THIS IS THE LOAN CONTRA
                        //
                        // Example:
                        //
                        // Current outstanding = ₹4,70,000
                        // New loan            = ₹5,00,000
                        //
                        // Loan Contra = ₹4,70,000
                        //================================================

                        double loanContra =
                                runningBalance;


                        //================================================
                        // NEW LOAN AMOUNT
                        //================================================

                        double loanAmount =
                                loan.getLoanAmount();


                        //================================================
                        // SAVE LAST LOAN DETAILS
                        // FOR PREVIEW MODAL
                        //
                        // Loans are sorted OLD -> NEW.
                        // Therefore each new loan overwrites
                        // these values.
                        //
                        // At the end, these contain the
                        // NEWEST/LAST COMMODITY LOAN.
                        //================================================

                        lastCommodityLoanMonth =
                                monthName;


                        lastCommodityLoanDate =
                                new SimpleDateFormat(
                                        "dd-MM-yyyy")
                                        .format(
                                                approveDate);


                        lastCommodityLoanAmount =
                                loanAmount;


                        lastCommodityLoanContra =
                                loanContra;


                        //================================================
                        // NEW LOAN BECOMES OUTSTANDING
                        //================================================

                        runningBalance =
                                loanAmount;


                        //================================================
                        // EXISTING MONTHLY UI LOGIC
                        //
                        // KEEP 1
                        //
                        // Your normal table will show:
                        //
                        // Ln Month = 1
                        //
                        // when a new commodity loan is taken.
                        //================================================

                        monthDTO.setLnMonth(
                                1);


                        //================================================
                        // LOAN AMOUNT
                        //================================================

                        monthDTO.setLoanAmount(
                                loanAmount);


                        //================================================
                        // LOAN CONTRA
                        //================================================

                        monthDTO.setLoanContra(
                                loanContra);


                        //================================================
                        // BALANCE AFTER NEW LOAN
                        //================================================

                        monthDTO.setBalance(
                                runningBalance);
                    }
                }


                //================================================
                // EMI PAYMENTS FOR CURRENT MONTH
                //================================================

                if (commodityEmis != null) {

                    for (EmiPaymentsDetails emi :
                            commodityEmis) {

                        Date paymentDate =
                                emi.getPaymentDate();

                        if (paymentDate == null) {
                            continue;
                        }


                        //================================================
                        // CONVERT EMI DATE
                        //================================================

                        LocalDate emiDate =
                                new java.sql.Date(
                                        paymentDate.getTime())
                                        .toLocalDate();


                        //================================================
                        // CHECK SAME MONTH
                        //================================================

                        boolean sameMonth =
                                emiDate.getYear()
                                        == currentDate.getYear()
                                        &&
                                        emiDate.getMonthValue()
                                                == currentDate.getMonthValue();


                        if (!sameMonth) {
                            continue;
                        }


                        //================================================
                        // PRINCIPAL
                        //================================================

                        double principal =
                                emi.getEmiAmount();


                        //================================================
                        // UPLOADED / DEDUCTED INTEREST
                        //================================================

                        double uploadedInterest =
                                emi.getUploadedInterest();


                        //================================================
                        // ACTUAL / GENERATED INTEREST
                        //================================================

                        double actualInterest =
                                emi.getGeneratedInterest();


                        //================================================
                        // REDUCE OUTSTANDING
                        // ONLY PRINCIPAL REDUCES LOAN BALANCE
                        //================================================

                        runningBalance =
                                runningBalance
                                        - principal;


                        //================================================
                        // ADD PRINCIPAL
                        //================================================

                        monthDTO.setPrincipal(
                                monthDTO.getPrincipal()
                                        + principal);


                        //================================================
                        // ADD UPLOADED INTEREST
                        //================================================

                        monthDTO.setInterest(
                                monthDTO.getInterest()
                                        + uploadedInterest);


                        //================================================
                        // ADD ACTUAL INTEREST
                        //================================================

                        monthDTO.setActualInterest(
                                monthDTO.getActualInterest()
                                        + actualInterest);


                        //================================================
                        // FINAL BALANCE FOR THIS MONTH
                        //================================================

                        monthDTO.setBalance(
                                runningBalance);
                    }
                }


                //================================================
                // ADD MONTH
                //================================================

                commodityMonths.add(
                        monthDTO);


                //================================================
                // NEXT MONTH
                //================================================

                currentDate =
                        currentDate.plusMonths(1);
            }


            //================================================
            // TOTALS
            //================================================

            double totalCommodityPrincipal = 0;

            double totalCommodityInterest = 0;

            double totalCommodityActualInterest = 0;


            for (CommodityMonthDTO row :
                    commodityMonths) {

                totalCommodityPrincipal +=
                        row.getPrincipal();

                totalCommodityInterest +=
                        row.getInterest();

                totalCommodityActualInterest +=
                        row.getActualInterest();
            }


            //================================================
            // SET COMMODITY MONTH DATA
            //================================================

            commodityDTO.setMonths(
                    commodityMonths);


            //================================================
            // SET TOTAL PRINCIPAL
            //================================================

            commodityDTO.setTotalPrincipal(
                    totalCommodityPrincipal);


            //================================================
            // SET TOTAL UPLOADED INTEREST
            //================================================

            commodityDTO.setTotalInterest(
                    totalCommodityInterest);


            //================================================
            // SET TOTAL ACTUAL INTEREST
            //================================================

            commodityDTO.setTotalActualInterest(
                    totalCommodityActualInterest);


            //================================================
            // FINAL CLOSING BALANCE
            //================================================

            commodityDTO.setClosingBalance(
                    runningBalance);


            //================================================
            // SET LAST LOAN DETAILS
            // FOR PREVIEW MODAL
            //================================================

            commodityDTO.setLastLoanMonth(
                    lastCommodityLoanMonth);


            commodityDTO.setLastLoanDate(
                    lastCommodityLoanDate);


            commodityDTO.setLastLoanAmount(
                    lastCommodityLoanAmount);


            commodityDTO.setLastLoanContra(
                    lastCommodityLoanContra);


            //================================================
            // SET COMMODITY DTO
            //================================================

            settlementDTO.setCommodity(
                    commodityDTO);
        }

        //====================================================
        // MONTH MAP
        //====================================================

        Map<String, SettlementMonthDTO> monthMap =
                new LinkedHashMap<>();


        //====================================================
        // SHARE
        //====================================================

        if (settlementDTO.getShare() != null) {

            for (ShareMonthDTO s :
                    settlementDTO.getShare()
                            .getMonths()) {

                SettlementMonthDTO row =
                        monthMap.computeIfAbsent(
                                s.getMonth(),
                                k -> {

                                    SettlementMonthDTO dto =
                                            new SettlementMonthDTO();

                                    dto.setMonth(k);

                                    return dto;
                                });


                row.setShare(
                        s.getDeduction());
            }
        }


        //====================================================
        // CD
        //====================================================

        if (settlementDTO.getCd() != null) {

            for (CDMonthDTO c :
                    settlementDTO.getCd()
                            .getMonths()) {

                SettlementMonthDTO row =
                        monthMap.computeIfAbsent(
                                c.getMonth(),
                                k -> {

                                    SettlementMonthDTO dto =
                                            new SettlementMonthDTO();

                                    dto.setMonth(k);

                                    return dto;
                                });


                row.setCd(
                        c.getDeposit());

                row.setCdBalance(
                        c.getRunningBalance());
            }
        }


        //====================================================
        // PERSONAL LOAN
        //====================================================

        if (settlementDTO.getPersonal() != null) {

            for (PersonalMonthDTO p :
                    settlementDTO.getPersonal()
                            .getMonths()) {


                SettlementMonthDTO row =
                        monthMap.computeIfAbsent(
                                p.getMonth(),
                                k -> {

                                    SettlementMonthDTO dto =
                                            new SettlementMonthDTO();

                                    dto.setMonth(k);

                                    return dto;
                                });


                // LN MONTH
                row.setPersonalLnMonth(
                        p.getLnMonth());


                // LOAN AMOUNT
                row.setPersonalLoanAmount(
                        p.getLoanAmount());


                // LOAN CONTRA
                row.setPersonalLoanContra(
                        p.getLoanContra());


                // PRINCIPAL
                row.setPersonalPrincipal(
                        p.getPrincipal());


                // UPLOADED INTEREST
                row.setPersonalInterest(
                        p.getInterest());


                // ACTUAL INTEREST
                row.setPersonalActualInterest(
                        p.getActualInterest());


                // BALANCE
                row.setPersonalBalance(
                        p.getBalance());
            }
        }


        //====================================================
        // COMMODITY LOAN
        //====================================================

        if (settlementDTO.getCommodity() != null) {

            for (CommodityMonthDTO c :
                    settlementDTO.getCommodity()
                            .getMonths()) {

                SettlementMonthDTO row =
                        monthMap.computeIfAbsent(
                                c.getMonth(),
                                k -> {

                                    SettlementMonthDTO dto =
                                            new SettlementMonthDTO();

                                    dto.setMonth(k);

                                    return dto;
                                });


                //================================================
                // LN MONTH
                //================================================

                row.setCommodityLnMonth(
                        c.getLnMonth());


                //================================================
                // LOAN AMOUNT
                //================================================

                row.setCommodityLoanAmount(
                        c.getLoanAmount());


                //================================================
                // LOAN CONTRA
                //================================================

                row.setCommodityLoanContra(
                        c.getLoanContra());


                //================================================
                // PRINCIPAL
                //================================================

                row.setCommodityPrincipal(
                        c.getPrincipal());


                //================================================
                // UPLOADED INTEREST
                //================================================

                row.setCommodityInterest(
                        c.getInterest());


                //================================================
                // ACTUAL INTEREST
                //================================================

                row.setCommodityActualInterest(
                        c.getActualInterest());


                //================================================
                // BALANCE
                //================================================

                row.setCommodityBalance(
                        c.getBalance());
            }
        }


        //====================================================
        // MONTH DATA
        //====================================================

        settlementDTO.setMonthData(
                new ArrayList<>(
                        monthMap.values()));


        //====================================================
        // TOTALS
        //====================================================

        double totalShare = 0;

        double totalCd = 0;

        double totalPersonalPrincipal = 0;

        double totalPersonalInterest = 0;

        double totalCommodityPrincipal = 0;

        double totalCommodityInterest = 0;


        for (SettlementMonthDTO row :
                settlementDTO.getMonthData()) {

            totalShare +=
                    row.getShare();

            totalCd +=
                    row.getCd();

            totalPersonalPrincipal +=
                    row.getPersonalPrincipal();

            totalPersonalInterest +=
                    row.getPersonalInterest();

            totalCommodityPrincipal +=
                    row.getCommodityPrincipal();

            totalCommodityInterest +=
                    row.getCommodityInterest();
        }


        settlementDTO.setTotalShare(
                totalShare);

        settlementDTO.setTotalCd(
                totalCd);

        settlementDTO.setTotalPersonalPrincipal(
                totalPersonalPrincipal);

        settlementDTO.setTotalPersonalInterest(
                totalPersonalInterest);

        settlementDTO.setTotalCommodityPrincipal(
                totalCommodityPrincipal);

        settlementDTO.setTotalCommodityInterest(
                totalCommodityInterest);


        //====================================================
        // CREDIT SIDE
        //====================================================

        double totalDeposit = 0;


        if (settlementDTO.getShare() != null) {

            totalDeposit +=
                    settlementDTO
                            .getShare()
                            .getClosingBalance();
        }


        if (settlementDTO.getCd() != null) {

            totalDeposit +=
                    settlementDTO
                            .getCd()
                            .getClosingBalance();

            totalDeposit +=
                    settlementDTO
                            .getCd()
                            .getInterest();
        }


        settlementDTO.setTotalDeposit(
                totalDeposit);


        //====================================================
        // DEBIT / LIABILITY SIDE
        //====================================================

        double totalLiability = 0;


        if (settlementDTO.getPersonal() != null) {

            totalLiability +=
                    settlementDTO
                            .getPersonal()
                            .getClosingBalance();

            totalLiability +=
                    settlementDTO
                            .getTotalPersonalInterest();
        }


        if (settlementDTO.getCommodity() != null) {

            totalLiability +=
                    settlementDTO
                            .getCommodity()
                            .getClosingBalance();

            totalLiability +=
                    settlementDTO
                            .getTotalCommodityInterest();
        }


        settlementDTO.setTotalLiability(
                totalLiability);


        //====================================================
        // RECEIVABLE / PAYABLE
        //====================================================

        settlementDTO.setAmountReceivable(
                totalDeposit
                        - totalLiability);


        //====================================================
        // RETURN
        //====================================================

        return settlementDTO;
    }
}