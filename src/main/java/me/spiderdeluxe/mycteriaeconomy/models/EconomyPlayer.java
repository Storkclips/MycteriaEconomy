package me.spiderdeluxe.mycteriaeconomy.models;

import me.spiderdeluxe.mycteriaeconomy.MycteriaEconomyPlugin;
import org.bukkit.entity.Player;

public class EconomyPlayer {
    private final Player player;
    private double bankBalance;

    public EconomyPlayer(final Player player) {
        this.player = player;
    }

    public EconomyPlayer(final Player player, final double balance) {
        this.player = player;
        this.bankBalance = balance;
    }

    public static EconomyPlayer fromPlayer(final Player player) {
        return MycteriaEconomyPlugin.getInstance().getEconomyPlayers().get(player);
    }

    public void registerEconomyPlayer() {
        MycteriaEconomyPlugin.getInstance().getEconomyPlayers().put(player, this);
    }

    public void unregisterEconomyPlayer() {
        MycteriaEconomyPlugin.getInstance().getEconomyPlayers().remove(player);
        savePlayerData();
    }

    public void savePlayerData() {
        MycteriaEconomyPlugin.getInstance().getEconomyPlayerManager().saveEconomyPlayer(this);
    }

    public double getBankBalance() {
        return bankBalance;
    }

    public void setBankBalance(final double bankBalance) {
        this.bankBalance = bankBalance;
    }

    public void increaseBankBalance(final double amount) {
        this.bankBalance += amount;
    }

    public void decreaseBankBalance(final double amount) {
        this.bankBalance -= amount;
    }

    public Player getPlayer() {
        return player;
    }
}
