package com.telco.members.tcos.dto;

import lombok.Data;

@Data
public class CommodityMonthDTO {

    private String month;

    // New loan information
    private Integer lnMonth;

    private double loanAmount;

    private double loanContra;

    // EMI information
    private double principal;

    private double interest;

    private double actualInterest;

    // Closing balance
    private double balance;
}