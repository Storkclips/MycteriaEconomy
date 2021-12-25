package me.spiderdeluxe.mycteriaeconomy.models.shop;

import lombok.Getter;

import java.util.Locale;

public enum ShopType {
    VENDING("&bVENDING"),
    TRADING("&aTRADING");

    @Getter
    private final String stringMessage;

    ShopType(final String stringMessage) {
        this.stringMessage = stringMessage;
    }

    public static ShopType fromName(final String name) {
        switch (name.toLowerCase(Locale.ROOT)) {
            case "vending":
                return VENDING;
            case "trading":
                return TRADING;
        }
        return null;
    }
}
