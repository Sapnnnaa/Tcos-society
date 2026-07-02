package com.telco.members.tcos.dto;

import com.telco.members.tcos.model.*;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Data
@Setter
public class loanDTO {
    private int id;
    private String userId;
    private String Name;
    private long phonenum;
    private double appliedLoan;
    private int companyId;
    private String companyName;
    private double loanAmount;
    private double interestRate;
    private Date endDate;
    private double loanRepaid;
    private String loanReason;
    private double loanBalance;
    private LoanStatus loanStatus;
    private String loanStatusType;          // raw DB text
    private LoanStatus loanStatusTypeEnum; // ✔ ENUM field

    private Date loanApproveDate;
    private Date createdAt;
    private LoanType loanType;
    private String accountNum;
    private String bname;
    private String branch;
    private String ifscode;
    private Date UpdatedAt;
    private Double Deductions;
    private String checkedBy;
    private String verifiedBy;
    private String approvedBy;
    private Paymenttype paymenttype;
    private ApplicationType applicationType;
    private String payableAt;
    private double shareDeduction;
    private double cdDeduction;
    private Date adviceDate;
    private String gid;
    private int adviceDownloaded;
    private int adviceApproved;
    private Date adviceApprovedDate;
    private String paymentBank;
    private String accountNumbers;
    private String chequeNo;
    private String bondId;//unique bond id
    private String BSln;//account head serial number
    private String bondType;//account head
    private double loandeposit;
   
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date loanApplyDate;
    
    private String loantypevalue;     // raw DB text
    private LoanType loantypevalueEnum; // ✔ ENUM field
}

