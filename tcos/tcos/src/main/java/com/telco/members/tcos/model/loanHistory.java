package com.telco.members.tcos.model;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name="loan_history")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class loanHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;
    private int loanId;
    private String userId;
    private LoanType loantype;
    private double loanAmount;
    private double loanBalance;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date loanDate;
}
