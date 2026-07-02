package com.telco.members.tcos.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name="transaction_history")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class transactionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;//unique generated
    private String userId;//from member_receipt table
    private String category;//Member,Vendor,other
    private String name;//from member_receipt table
    private leasureType leasuretype;//set to receipt
    private String transactionId;//vid,rid,bid
    private String transactionName;//the vouchertype from receipt table
    private Paymenttype paymenttype;//from member_receipt table
    private double amount;//amount in receipt table
    private double balance;//shareDeductionTotal value
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date transactionDate;//approve_date from member_receipt table
    private int uniqueId; //cheque number/utr
    private String paymentBank; //payment bank
    private String description; //desc of each module
    private int Amount2; //applied loan amount
   
    private String paymenttypevalue; 
}
