package com.telco.members.tcos.dto;

import lombok.Data;

@Data
public class SettlementMonthDTO {

    private String month;

    // Share
    private double share;

    // CD
    private double cd;
    private double cdBalance;

    // Personal Loan
    private Integer personalLnMonth;

    private double personalLoanAmount;

    private double personalLoanContra;

    private double personalPrincipal;

    private double personalInterest;

    private double personalActualInterest;

    private double personalBalance;

    // Commodity Loan
    private Integer commodityLnMonth;

    private double commodityLoanAmount;

    private double commodityLoanContra;

    private double commodityPrincipal;

    private double commodityInterest;

    private double commodityActualInterest;

    private double commodityBalance;

}