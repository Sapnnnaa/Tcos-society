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
@Table(name="cashier")
public class Cashier {
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;
    private String uuid; //unique generated for each bond
    private String userId;
    private String name;
    private String paymentBank;
    private Paymenttype paymenttype;
    private double amount;
    private leasureType leasureType; //bond, receipt , voucher
    private LoanType loanType; //
    private String uid; //bondId, vId,rId
    private String gid; //group_id or advice or voucher generated in group
    private LoanStatus cashStatus; //it has pending or checked feature
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date updatedDate;
}
