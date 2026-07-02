package com.telco.members.tcos.model;

public enum AccountStatus {
    PENDING,
    ACTIVE,
    CLOSED;

    public static AccountStatus getDefault() {
        return PENDING;
    }
}