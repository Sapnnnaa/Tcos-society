package com.telco.members.tcos.dto;

import lombok.Data;

@Data
public class PersonalMonthDTO {

    // April, May, June...
    private String month;

    // Loan month:
    // 1 = new loan month
    // 2 = second month
    // 3 = third month...
    private Integer lnMonth;

    // Filled only when a new loan is taken
    private double loanAmount;

    // Previous outstanding balance when new loan is taken
    private double loanContra;

    // Actual EMI principal paid in that month
    private double principal;

    // Interest deducted from uploaded EMI data
    private double interest;

    // Interest generated/calculated by your system
    private double actualInterest;

    // Running loan balance
    private double balance;
}