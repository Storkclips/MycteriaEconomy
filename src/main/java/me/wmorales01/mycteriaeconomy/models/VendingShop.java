package me.wmorales01.mycteriaeconomy.models;

public interface VendingShop {

    double getProfit();

    void setProfit(double profit);

    void increaseProfit(double amount);

    void decreaseProfit(double amount);
}