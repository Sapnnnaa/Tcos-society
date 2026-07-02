package com.telco.members.tcos.dto;


import com.telco.members.tcos.model.Paymenttype;
import com.telco.members.tcos.model.paymentStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@Getter
@Setter
public class TableDto {
    private String vId;
    private String gid;
    private String name;
    private String userId;
    private String category;
    private String bankName;
    private String accountNum;
    private double totalAmount;
    private String chequeNo;
    private Paymenttype paymenttype;
    private paymentStatus paymentStatus;
    private String paymentBank;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createdDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date updatedDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date voucherApprovedDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date postingDate;
}
