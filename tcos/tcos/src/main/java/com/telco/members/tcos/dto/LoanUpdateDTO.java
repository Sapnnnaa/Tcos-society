package com.telco.members.tcos.dto;

import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

public class LoanUpdateDTO {

    private String userId;

    private Double personalBalance;

    private Double commodityBalance;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date personalLoanApproveDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date commodityLoanApproveDate;

    // getters and setters


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getPersonalBalance() {
        return personalBalance;
    }

    public void setPersonalBalance(Double personalBalance) {
        this.personalBalance = personalBalance;
    }

    public Double getCommodityBalance() {
        return commodityBalance;
    }

    public void setCommodityBalance(Double commodityBalance) {
        this.commodityBalance = commodityBalance;
    }

    public Date getPersonalLoanApproveDate() {
        return personalLoanApproveDate;
    }

    public void setPersonalLoanApproveDate(Date personalLoanApproveDate) {
        this.personalLoanApproveDate = personalLoanApproveDate;
    }

    public Date getCommodityLoanApproveDate() {
        return commodityLoanApproveDate;
    }

    public void setCommodityLoanApproveDate(Date commodityLoanApproveDate) {
        this.commodityLoanApproveDate = commodityLoanApproveDate;
    }
}