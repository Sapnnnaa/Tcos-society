package com.telco.members.tcos.dto;

import java.util.List;

import lombok.Data;

@Data
public class PersonalSettlementDTO {

    private double openingBalance;

    private List<PersonalMonthDTO> months;

    private double totalPrincipal;

    // Total Interest Deducted
    private double totalInterest;

    // Total Actual/System Generated Interest
    private double totalActualInterest;

    private double closingBalance;


    // ====================================================
    // LAST LOAN DETAILS - FOR PREVIEW MODAL
    // ====================================================

    // Example: August
    private String lastLoanMonth;

    // Example: 23-08-2024
    private String lastLoanDate;

    // Example: 230000
    private double lastLoanAmount;

    // Example: 130000
    private double lastLoanContra;
}