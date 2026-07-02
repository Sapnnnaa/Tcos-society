package com.telco.members.tcos.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@Getter
@Setter
public class ReportDTO {
    private String rId;
    private String category;
    private String userId;
    private String name;
    private String vouchertype;
    private double Amount;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date approveDate;
}
