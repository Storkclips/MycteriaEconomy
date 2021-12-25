package me.spiderdeluxe.mycteriaeconomy.models.npc;

import lombok.Getter;

public enum NPCFunction {
	SHOP("SHOP"),
	ATM("ATM");

	@Getter
	private final String stringMessage;

	NPCFunction(final String stringMessage) {
		this.stringMessage = stringMessage;
	}

	public static NPCFunction fromName(final String name) {
		switch (name) {
			case "SHOP":
				return SHOP;
			case "ATM":
				return ATM;
		}
		return null;
	}
}
