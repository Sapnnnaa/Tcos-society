package com.telco.members.tcos.dto;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Data
@Setter
public class VoucherDTO {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date voucherApprovedDate;
    private String vId;
    private String vSln;
    private String vouchertype;
    private double amount;
}
