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
@Table(name="member_voucher")
public class MemberVoucher {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int Id;
	private String vId;
	private String gid; // group genenrated
	private String name;
	private String userId;
	private String category;
	private String accountNum;
	private String bankName;
	private String branchName;
	private String ifscCode;
	private String payableAt;
	private double totalAmount;
	private Paymenttype paymenttype;
	private String paymentBank;
	private String paymentAccountNum;
	private String chequeNo;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date createdDate;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date updatedDate;
	private String preparedBy;
	private String checkedBy;
	private String passedBy;
	private int voucherApproved; //approved
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date postingDate;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date voucherApprovedDate;//clearing Date
	private paymentStatus paymentStatus;
	private int voucherDownloaded;
	private String description;
	private int generatedTimes;
	private int regenerated;
}