package me.spiderdeluxe.mycteriaeconomy.models.account;

import lombok.Getter;

import java.util.Locale;

public enum AccountType {
    PERSONAL("Personal"),
    BUSINESS("Business");

    @Getter
    private final String stringMessage;

    AccountType(final String stringMessage) {
        this.stringMessage = stringMessage;
    }

    public static AccountType fromName(final String name) {
        return switch (name.toLowerCase(Locale.ROOT)) {
            case "personal" -> PERSONAL;
            case "business" -> BUSINESS;
            default -> null;
        };
    }
}
