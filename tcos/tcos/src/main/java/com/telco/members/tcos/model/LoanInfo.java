package com.telco.members.tcos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.NumberFormat;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Automatically generated primary key
    private int id;
    @NumberFormat(pattern = "#,##0.00")
    private double shareAmount;
    @NumberFormat(pattern = "#,##0.00")
    private double cdMinimum;
    @NumberFormat(pattern = "#,##0.00")
    private double interestOnNormal;
    @NumberFormat(pattern = "#,##0.00")
    private double interestOnCommodity;
    @NumberFormat(pattern = "#,##0.00")
    private double interestOnCD;
    @NumberFormat(pattern = "#,##0.00")
    private double maxCommodityLoan;
}
