package me.spiderdeluxe.mycteriaeconomy.models.shop;

import java.util.Locale;

public enum ShopOwnerType {
	STATE,
	PLAYER;



	public static ShopOwnerType fromName(final String name) {
		switch (name.toLowerCase(Locale.ROOT)) {
			case "state":
				return STATE;
			case "player":
				return PLAYER;
		}
		return null;
	}


}
