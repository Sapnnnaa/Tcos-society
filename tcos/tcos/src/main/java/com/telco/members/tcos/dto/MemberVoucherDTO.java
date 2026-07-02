package com.telco.members.tcos.dto;

import com.telco.members.tcos.model.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@Getter
@Setter
public class MemberVoucherDTO {
	private int Id;
	private String vId;
	private String gid;
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
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createdDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date updatedDate;
	private String preparedBy;
	private String checkedBy;
	private String passedBy;
	private int voucherApproved;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date voucherApprovedDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date postingDate;
	private int voucherDownloaded;
	private paymentStatus paymentStatus;
	private String description;
	private List<Voucher> memberVoucher;
	private int generatedTimes;
	private int regenerated;
}
