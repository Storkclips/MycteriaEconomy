package me.wmorales01.mycteriaeconomy.models;

public interface TradingShop {

    double getBalance();

    void setBalance(double balance);

    void increaseBalance(double amount);

    void decreaseBalance(double amount);
}
