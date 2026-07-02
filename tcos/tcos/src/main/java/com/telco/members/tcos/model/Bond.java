package com.telco.members.tcos.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bond")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bond {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "userId")
    private String userId;
    @Column(name = "paymenttype")
    private Paymenttype bondtype;
    @Column(name = "Amount")
    private int amount;
}
