package com.telco.members.tcos.model;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="loan_voucher")
public class LoanVoucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;
    private String userId;
    private double amount;
    private LoanType loanType;
    private double loanBalance;
    private String bondId;//unique bond id
    private String BSln;//account head serial number
    private String bondType;//account head
    private int adviceId;//loanId
    private String gid;//Group ID and unique download id
    private String chequeNo;
    private Paymenttype paymentType;
    private String paymentBank;
    private String accountNumber;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date adviceDate;// Also clearing Date id rejected
    private int adviceDownloaded;
    private String preparedBy;
    private String checkedBy;
    private String passedBy;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date updatedAt;
    private int adviceApproved;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date adviceApprovedDate;//clearing Date
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date postingDate;
    private paymentStatus paymentStatus;
    @Transient
    private String memberName;
}
