package com.telco.members.tcos.dto;

import com.telco.members.tcos.model.*;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@Getter
@Setter
public class MemberReceiptDTO {

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
		@DateTimeFormat(pattern = "yyyy-MM-dd")
		private Date createdDate;
		@DateTimeFormat(pattern = "yyyy-MM-dd")
		private Date updatedDate;
		@DateTimeFormat(pattern = "yyyy-MM-dd")
		private Date approveDate;
		private List<Receipt> memberVoucher;
		private int stage;

}
