package com.telco.members.tcos.model;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Data


public class PaymentAccountdet {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
	private String user_id;
    private String account_num;
    private String bank_name;
    private String branch_name;
    private String ifsc_code;
    private String payable_at;
    private double amount;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date transaction_date;
    private String leasuretype;
    private String transactionid;
    private String transactionname;
    private String category;
	
}
