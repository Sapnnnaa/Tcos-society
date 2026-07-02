package com.telco.members.tcos.model;

import java.util.Date;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberReceipt {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int Id;
	private String userId;
	private String name;
	private String bname;
	private String accountNum;
	private String paymentBank;
	private String chequeNo;
	private String accountNumber;
	private String category;
	private double totalAmount;
	private String description;
	private String rId;
	private String preparedBy;
	private String CheckedBy;
	private String passedBy;
	private Paymenttype paymenttype;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date createdDate;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date updatedDate;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date approveDate;
	private int stage;

}
