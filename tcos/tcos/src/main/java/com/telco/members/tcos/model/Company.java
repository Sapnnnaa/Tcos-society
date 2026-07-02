package com.telco.members.tcos.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "company")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Company {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int companyId;
    private String companyName;
}
