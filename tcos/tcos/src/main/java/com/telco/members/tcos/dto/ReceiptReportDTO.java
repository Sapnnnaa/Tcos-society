package com.telco.members.tcos.dto;

import lombok.Data;
import java.util.Date;
import com.telco.members.tcos.model.Paymenttype;   // ✔ ADD THIS

@Data
public class ReceiptReportDTO {

    private String receiptId;
    private String memberName;
    private String userId;
    private String paymentBank;
    private String accountNo;
    private String chequeNo;

    private String paymentType;          // raw DB text
    private Paymenttype paymentTypeEnum; // ✔ ENUM field

    private double totalAmount;
    private double paymentReceived;
    private String category;
    private Date createdAt;
    private String GLcode;    
    private String GLname;
}
