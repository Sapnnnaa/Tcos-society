package com.telco.members.tcos.dto;

import com.telco.members.tcos.model.Paymenttype;
import com.telco.members.tcos.model.leasureType;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Data
@Setter
public class BankDTO {

    private String transactionId;//vid,rid,bid
    private String bankName;//tcosBank
    private String category;//member,other,etc...
    private leasureType leasuretype;//bond,voucher,receipt
    private String userId;
    private Paymenttype paymenttype;
    private double amount;
    private double balance;
    private String name;//from member_receipt table
    private String transactionName;//the vouchertype from receipt table
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date transactionDate;//approve_date from member_receipt table

}