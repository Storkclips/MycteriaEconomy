package me.wmorales01.mycteriaeconomy.models;

import me.wmorales01.mycteriaeconomy.MycteriaEconomy;
import org.bukkit.entity.Player;

public class EconomyPlayer {
    private final Player player;
    private double bankBalance;

    public EconomyPlayer(Player player) {
        this.player = player;
    }

    public EconomyPlayer(Player player, double balance) {
        this.player = player;
        this.bankBalance = balance;
    }

    public static EconomyPlayer fromPlayer(Player player) {
        return MycteriaEconomy.getInstance().getEconomyPlayers().get(player);
    }

    public void registerEconomyPlayer() {
        MycteriaEconomy.getInstance().getEconomyPlayers().put(player, this);
    }

    public void unregisterEconomyPlayer() {
        MycteriaEconomy.getInstance().getEconomyPlayers().remove(player);
        savePlayerData();
    }

    public void savePlayerData() {
        MycteriaEconomy.getInstance().getEconomyPlayerManager().saveEconomyPlayer(this);
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
        return player;
    }
}
