package me.spiderdeluxe.mycteriaeconomy.models.npc;

import lombok.Getter;

public enum NPCFunction {
	SHOP("SHOP"),
	TELLER("TELLER"),
	ATM("ATM");

	@Getter
	private final String stringMessage;

	NPCFunction(final String stringMessage) {
		this.stringMessage = stringMessage;
	}

	public static NPCFunction fromName(final String name) {
		return switch (name) {
			case "TELLER" -> TELLER;
			case "SHOP" -> SHOP;
			case "ATM" -> ATM;
			default -> null;
		};
	}
}
