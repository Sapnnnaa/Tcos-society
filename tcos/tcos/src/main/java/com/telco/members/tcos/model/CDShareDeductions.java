package com.telco.members.tcos.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

@Entity
@Table(name="cd_share_deductions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CDShareDeductions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String userId;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date paymentDate;

    private double cdDeduction;
    private double cdDeductionTotal;
    private double cdInterest;
    private double cdInterestTotal;

    private double shareDeduction;
    private double shareDeductionTotal;
    private double totalDeduction;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date createdAt;

    private double cdDeductionGenerated;
    private double shareDeductionGenerated;

    // REQUIRED DISPLAY FIELDS
    @Transient
    private String memberName;

    @Transient
    private String ticketNo;

    @Transient
    private String companyName;

}
