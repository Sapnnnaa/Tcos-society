package com.telco.members.tcos.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Data
public class ReportData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String userId;
    private String name;

    // normal loan
    // balance
    private double normalBalanceUpload;
    private double normalBalanceGenerated;
    // recovery
    private double noramlRecoveryUpload;
    private double noramlRecoveryGenerated;
    // interest
    private double normalInterestUpload;
    private double normalInterestGenerated;

    // commodity loan
    // balance
    private double commBalanceUpload;
    private double commBalanceGenerated;
    // recovery
    private double commRecoveryUpload;
    private double commRecoveryGenerated;
    // interest
    private double commInterestUpload;
    private double commInterestGenerated;

    //share
    private double shareUpload;
    private double shareGenerated;
    //cd
    private double cdUpload;
    private double cdGenerated;
    //createdFor
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createdFor;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createdAt;

    private int companyId;

}