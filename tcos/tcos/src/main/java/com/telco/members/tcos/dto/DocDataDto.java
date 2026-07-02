package com.telco.members.tcos.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class DocDataDto  {

    private String userId;
    private String wageType;
    private double amount;

}