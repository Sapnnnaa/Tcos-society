package com.telco.members.tcos.dto;

import com.telco.members.tcos.model.*;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@Getter
@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class closureDTO {
    private String userId;
    private ApplicationType closureType;
    private double deposit;
    private double dueAmount;
    private double currentDue;
    private double Payout;
    private double currentCL;
    private double others;//other charges
    private int surety;
    private double payable;
    private String details;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date applyDate;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date updatedDate;
    private String preparedBy;
    private String approvedBy;
    private AccountSettlement accountSettlement;
    private Paymenttype settlement;
}
