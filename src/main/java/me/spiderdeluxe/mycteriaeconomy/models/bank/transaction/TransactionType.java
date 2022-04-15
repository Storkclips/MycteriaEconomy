package me.spiderdeluxe.mycteriaeconomy.models.bank.transaction;

import lombok.Getter;

public enum TransactionType {
    //ATM
    WITHDRAW("Withdraw"),
    DEPOSIT("Deposit"),
    //Private expenses
    PURCHASE("Purchase"),
    PAYMENT("Payment"),
    //Administrative expenses
    COMMISSION("Commission"),
    LOAN("Loan"),
    BILL("Bill");

    @Getter
    private final String stringMessage;

    TransactionType(final String stringMessage) {
        this.stringMessage = stringMessage;
    }

    public static TransactionType fromName(final String name) {
        return (valueOf(name));
    }


    public static String getAmountSign(final TransactionType type) {
        return switch (type) {
            case DEPOSIT -> "+";
            case WITHDRAW, BILL, LOAN, PAYMENT, PURCHASE, COMMISSION -> "-";
        };
    }

}
