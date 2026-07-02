package com.telco.members.tcos.dto;

import com.telco.members.tcos.model.Paymenttype;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@Data
@Getter
@Setter
public class ReceiptDto {
    private String rId;
    private String category;
    private String userId;
    private String name;
    private double totalAmount;
    private Paymenttype paymenttype;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createdDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date updatedDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date approveDate;
}
