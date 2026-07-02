package com.telco.members.tcos.dto;

import java.util.List;

import com.telco.members.tcos.model.CDShareDeductions;
import com.telco.members.tcos.model.EmiPaymentsDetails;
import com.telco.members.tcos.model.User;
import com.telco.members.tcos.model.loanHistory;
import com.telco.members.tcos.model.Loan;

import lombok.Data;

@Data
public class ReportingDTO {
    private int surety;
    private User user;
    //private List<loanHistory> loans;
    private List<Loan> loans;
    private List<loanInfoDto> loans1;
    private List<LoanapproveDTO> loans2;
    private List<LoanapproveDTO> loanNotActive;
    private CDShareDeductions emis;
}