package com.telco.members.tcos.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Banktranstally {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String trans_id;
    private String transname1;
    private String transaction_name;
    private double amount;
    private String transaction_date;
    private String leasuretype;
    private String paymenttype;
    private String description;
}
