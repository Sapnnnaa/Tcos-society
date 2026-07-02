package com.telco.members.tcos.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


@Entity
@Table(name = "loans")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int loanId;
    private String userId;
    private double appliedLoan;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date loanApplyDate;
    private double loanAmount;
    private double intrestRate;
    private int emiPaid;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date endDate;
    private double loanRepaid;
    private String loanReason;
    private double loanBalance;
    private LoanStatus loanStatus;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date loanApproveDate;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date updatedAt;
    private String updatedBy;
    private String verifiedBy;
    private String approvedBy;
    private LoanType loanType;
    private int emiBalance;
    private double interestDiff;
    private ApplicationType applicationType;
    private double instalmentAmount;
    private double emiAmount;
    private String description;
   // private double loandeposit;
}
