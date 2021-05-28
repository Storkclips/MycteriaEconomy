package me.wmorales01.mycteriaeconomy.models;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;

public class EconomyPlayer {
	private double bankBalance;
	private UUID uuid;

	public EconomyPlayer(Player player) {
		MycteriaEconomy plugin = MycteriaEconomy.getPlugin(MycteriaEconomy.class);
		this.uuid = player.getUniqueId();
		plugin.addEconomyPlayers(this);
	}
	
	public EconomyPlayer(UUID uuid) {
		MycteriaEconomy plugin = MycteriaEconomy.getPlugin(MycteriaEconomy.class);
		this.uuid = uuid;
		plugin.addEconomyPlayers(this);
	}

	public EconomyPlayer(UUID uuid, double balance) {
		this.uuid = uuid;
		this.bankBalance = balance;
	}

	public double getBankBalance() {
		return bankBalance;
	}

	public void setBankBalance(double bankBalance) {
		this.bankBalance = bankBalance;
	}

	public void addBankBalance(double amount) {
		this.bankBalance += amount;
	}

	public void removeBankBalance(double amount) {
		this.bankBalance -= amount;
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}

	public static EconomyPlayer fromPlayer(Player player) {
		MycteriaEconomy plugin = MycteriaEconomy.getPlugin(MycteriaEconomy.class);

		EconomyPlayer result = null;
		for (EconomyPlayer ecoPlayer : plugin.getEconomyPlayers()) {
			if (!ecoPlayer.getPlayer().equals(player))
				continue;

			result = ecoPlayer;
		}

		return result;
	}
	
	public static EconomyPlayer fromUUID(UUID uuid) {
		MycteriaEconomy plugin = MycteriaEconomy.getPlugin(MycteriaEconomy.class);

		EconomyPlayer result = null;
		for (EconomyPlayer ecoPlayer : plugin.getEconomyPlayers()) {
			if (!ecoPlayer.getPlayer().getUniqueId().equals(uuid))
				continue;

			result = ecoPlayer;
		}

		return result;
	}

	public UUID getUuid() {
		return uuid;
	}
}
