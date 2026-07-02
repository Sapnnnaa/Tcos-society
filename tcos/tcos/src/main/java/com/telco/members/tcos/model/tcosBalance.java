package com.telco.members.tcos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="tcos_balance")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class tcosBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String bankName;
    private String accountNumber;
    private double bankBalance;
}
