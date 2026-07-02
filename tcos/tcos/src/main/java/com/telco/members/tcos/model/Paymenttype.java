package com.telco.members.tcos.model;

public enum Paymenttype {
        NONE,
        ADVICE,
        NEFT,
        CASH,
        CHEQUE,
        ONLINE,
        RECEIPTS,
        PAYMENT,
        NA,
        Recovery,
        Receipt,
        Guarantor;
        public static com.telco.members.tcos.model.Paymenttype getDefault() {
            return NONE;
        }
}
