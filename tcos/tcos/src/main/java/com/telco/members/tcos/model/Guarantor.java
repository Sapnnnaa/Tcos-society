package com.telco.members.tcos.model;

import jakarta.persistence.*;
import lombok.*;

import java.security.PrivateKey;
import java.util.Date;


@Table(name = "guarantor")
@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Guarantor {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int guarantorId;
    @Column(name = "userId")
    private String userId;
    @Column(name = "g_userId")
    private String g_userId;
    private String g_name;
    private long phone_num;
    private Date approvedate;
    private int g_value;
    @Column(name = "loanId")
    private int loanId;
}
