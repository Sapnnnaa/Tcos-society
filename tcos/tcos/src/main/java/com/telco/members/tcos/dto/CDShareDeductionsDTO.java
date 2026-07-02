package com.telco.members.tcos.dto;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.telco.members.tcos.dto.CDShareDeductionsDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter

public class CDShareDeductionsDTO {
	 private String userId;

	    @Temporal(TemporalType.DATE)
	    @DateTimeFormat(pattern = "dd-MM-yyyy")
	    private Date paymentDate;

	    private double cdDeduction;
	    private double cdDeductionTotal;
	    private double cdInterest;
	    private double cdInterestTotal;

	    private double shareDeduction;
	    private double shareDeductionTotal;
	    private double totalDeduction;

	    @Temporal(TemporalType.DATE)
	    @DateTimeFormat(pattern = "dd-MM-yyyy")
	    private Date createdAt;

	    private double cdDeductionGenerated;
	    private double shareDeductionGenerated;

	    // REQUIRED DISPLAY FIELDS
	    @Transient
	    private String memberName;

	    @Transient
	    private String ticketNo;

	    @Transient
	    private String companyName;
}
