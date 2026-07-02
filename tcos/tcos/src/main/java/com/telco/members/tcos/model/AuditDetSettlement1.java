package com.telco.members.tcos.model;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data

public class AuditDetSettlement1 {
	@Id
	  @GeneratedValue(strategy = GenerationType.IDENTITY)
	  private int Id;
	 private int totPLinstcashamt;
     private double totalPLint;
     private double totPLactintcah;
     private int totCLinstcashamt;
     private double  totalCLint;
     private double totCLactintcash;
     private double totalcrdeposit;
     private int AOLoantotalbal;
     private int AVLoantotalbal;
     private double AOLoanintamt;
     private double AVLoanintamt;
     private String varcompany;
     private double suretyamt;
     private double Totliabilityamt;     
     private int lnmonth;  
     private String varcompany1;
     private int lnadv;
     private int lncontra;
     
     
     
}
