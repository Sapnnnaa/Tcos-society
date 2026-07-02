package com.telco.members.tcos.model;

public enum LoanType{

    PERSONAL,COMMODITY,MEMBERS,EMPLOYEE,VENDOR,OTHER ;

    public static LoanType getDefault() {
        return PERSONAL;
    }
    public static LoanType fromOrdinal(int ordinal) {
        LoanType[] values = LoanType.values();
        return (ordinal >= 0 && ordinal < values.length) ? values[ordinal] : getDefault();
    }
}
