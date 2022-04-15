package me.spiderdeluxe.mycteriaeconomy.models.bank;

import lombok.Getter;

import java.util.Locale;

public enum BankType {
    STATE("State"),
    LOCAL("Local"),
    COMMUNITY("Community");

    @Getter
    private final String stringMessage;

    BankType(final String stringMessage) {
        this.stringMessage = stringMessage;
    }

    public static BankType fromName(final String name) {
        return switch (name.toLowerCase(Locale.ROOT)) {
            case "state" -> STATE;
            case "local" -> LOCAL;
            case "community" -> COMMUNITY;
            default -> null;
        };
    }
}
