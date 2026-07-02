package com.telco.members.tcos.model;

public enum AccountSettlement {

    PENDING,APPLIED,APPROVED,REJECTED;

    public static AccountSettlement getDefault() {
        return PENDING;
    }
}
