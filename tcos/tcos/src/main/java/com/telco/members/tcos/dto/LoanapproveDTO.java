package com.telco.members.tcos.dto;


import com.telco.members.tcos.model.LoanStatus;
import com.telco.members.tcos.model.LoanType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Data
@Setter
@AllArgsConstructor
public class LoanapproveDTO {
    private String userId;
    private LoanStatus loanStatus;
    private Date loanApproveDate;
    private LoanType loanType;
    private double loanAmount;
}
