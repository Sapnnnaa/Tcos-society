package com.telco.members.tcos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="AccountAssign")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountAssign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String slNo;
    private String accountHead;
}