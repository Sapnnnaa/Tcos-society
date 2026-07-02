package com.telco.members.tcos.dto;

import com.telco.members.tcos.model.AccountSettlement;
import com.telco.members.tcos.model.AccountStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Data
@Setter
public class AccountDTO {
    private String userId;
    private String name;
    private String company;
    private AccountStatus accountStatus;
    private AccountSettlement accountSettlement;
    // Getters and Setters
}
