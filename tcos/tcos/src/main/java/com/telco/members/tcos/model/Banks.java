package com.telco.members.tcos.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="banks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Banks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String userId;
    private String accountNum;
    private String bankName;
    private String branchName;
    private String ifscCode;
    private String payableAt;
}
