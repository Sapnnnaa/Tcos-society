package com.telco.members.tcos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@Entity
@Table(name="bank_balance")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class bankBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String transactionId;//vid,rid,bid
    private String bankName;//tcosBank
    private String category;//member,other,etc...
    private leasureType leasuretype;//bond,voucher,receipt
    private String userId;
    private String name;
    private Paymenttype paymenttype;
    private double amount;
    private double balance;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date transactionDate;
}
