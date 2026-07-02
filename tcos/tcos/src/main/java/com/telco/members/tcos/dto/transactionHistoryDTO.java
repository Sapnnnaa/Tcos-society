package com.telco.members.tcos.dto;


import com.telco.members.tcos.model.*;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;



@Getter
@Data
@Setter

public class transactionHistoryDTO {
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
    private Date transactionDate;//approve_date from member_receipt table
    private int uniqueId; //cheque number/utr
    private String paymentBank; //payment bank
    private String description; //desc of each module
    private int Amount2; //applied loan amount
    private String paymentstatusvalue;
    private String paymenttypevalue; 
}
