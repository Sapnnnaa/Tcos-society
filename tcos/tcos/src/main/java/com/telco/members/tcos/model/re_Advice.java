package com.telco.members.tcos.model;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="re_Advice")
public class re_Advice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;
    private String userId;
    private double amount;
    private int adviceId;
    private Paymenttype paymentType;
    private String chequeNo;
    private String paymentBank;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date adviceDate;
    private Date adviceDate2;
    private int adviceTimes;
}
