package com.telco.members.tcos.dto;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Data
@Setter
public class printDto {
    private int Id;
    private String vId;
    private String name;
    private String userId;
    private String category;
    private String accountNum;
    private String bankName;
    private String branchName;
    private String ifscCode;
    private String paymentBank;
    private String payableAt;
    private String chequeNo;
    private double Amount;
}
