package com.telco.members.tcos.model;

import jakarta.persistence.*;
import org.springframework.stereotype.Controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Entity
@Controller
@Table(name="nominiees")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Nominiees {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String userId;
    @Column(name = "nominee_name")
    private String nomineeName;
    private String relation;
    private String addr;
    private String aadharNum;
    private String mobileNum;
}
