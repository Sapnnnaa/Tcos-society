package com.telco.members.tcos.dto;

import java.util.List;
import lombok.Data;

@Data
public class CommoditySettlementDTO {

    private double openingBalance;

    private double closingBalance;

    private List<CommodityMonthDTO> months;

    // Total Principal
    private double totalPrincipal;

    // Total Uploaded/Deducted Interest
    private double totalInterest;

    // Total Actual/System Generated Interest
    private double totalActualInterest;


    // ====================================================
    // LAST LOAN DETAILS - FOR PREVIEW MODAL ONLY
    // ====================================================

    private String lastLoanMonth;

    private String lastLoanDate;

    private double lastLoanAmount;

    private double lastLoanContra;
}