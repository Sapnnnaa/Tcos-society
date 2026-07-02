package com.telco.members.tcos.model;

    public enum LoanStatus {

        PENDING,CHECKED,VERIFIED,APPROVED,REJECTED,PAID,FORWARD;
        public static LoanStatus getDefault() {
            return PENDING;
        }
        public static LoanStatus fromOrdinal(int ordinal) {
            LoanStatus[] values = LoanStatus.values();
            return (ordinal >= 0 && ordinal < values.length) ? values[ordinal] : getDefault();
        }
    }
