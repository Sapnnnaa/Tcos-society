package com.telco.members.tcos.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Table(name = "surety")
@Entity
@Data
@Getter
@Setter
public class Surety {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sId;
    @Column(name = "userId")
    private String userId;
    @Column(name = "g_userId")
    private String g_userId;
    private String g_name;
    private long phone_num;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date approvedate;
    @Transient
    private String memberName;
}
