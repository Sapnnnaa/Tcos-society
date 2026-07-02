package com.telco.members.tcos.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Data
public class InterestOnLoan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int loanId;
    private LoanType loanType;
    private String userId;
    private double loanBalance;
    private double instalment;
    @NumberFormat(pattern = "#,##0.00")
    private double interest;
    private double share;
    private double cd;
    private double extraCharges;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date generatedFor;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createdAt;
    private String name;
    private int companyId;

}