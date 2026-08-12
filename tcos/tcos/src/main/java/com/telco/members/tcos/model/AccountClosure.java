package com.telco.members.tcos.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "account_closure")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountClosure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String userId;

    private Double deposit;

    private Double dueAmount;

    private Double payout;

    private Integer surety;

    private Double others;

    private String preparedBy;

    @Enumerated(EnumType.STRING)
    private Paymenttype settlement;

    @Enumerated(EnumType.STRING)
    private ApplicationType closureType;

    @Enumerated(EnumType.STRING)
    private AccountSettlement accountSettlement;
}
