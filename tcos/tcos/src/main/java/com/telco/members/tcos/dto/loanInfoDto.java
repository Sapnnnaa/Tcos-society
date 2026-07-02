package com.telco.members.tcos.dto;

import com.telco.members.tcos.model.LoanType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Data
@Setter
public class loanInfoDto{

    private int loanId;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date loanDate;
    private LoanType loantype;
    private double loanBalance;
    private double loanInterest;

}
