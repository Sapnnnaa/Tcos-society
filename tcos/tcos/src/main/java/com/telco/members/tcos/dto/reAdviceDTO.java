package com.telco.members.tcos.dto;


import com.telco.members.tcos.model.LoanType;
import com.telco.members.tcos.model.Paymenttype;
import com.telco.members.tcos.model.paymentStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Data
@Setter
public class reAdviceDTO {
    private int AdviceId;
    private String userId;
    private String Name;
    private long phonenum;
    private String companyName;
    private String bondType;
    private String bondId; //unique bond id - cb,pd
    private String accountNum;
    private String bname;
    private String ifscode;
    private String branch;
    private String payableAt;
    private String paymentBank;
    private String gid;
    private String chequeNo;
    private String preparedBy;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date adviceDate;
    private String checkedBy;
    private String passedBy;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date updatedAt;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date postingDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date adviceApprovedDate;
    private paymentStatus paymentStatus;
    private int adviceDownloaded;
    private int adviceApproved;
    private Paymenttype paymenttype;
    private double loanAmount;
    private LoanType loanType;
    private int times;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date adviceDate2;
}
