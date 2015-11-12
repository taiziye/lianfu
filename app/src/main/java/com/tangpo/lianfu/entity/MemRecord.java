package com.tangpo.lianfu.entity;

/**
 * Created by 果冻 on 2015/11/9.
 */
public class MemRecord {

    private String shop_name;
    private boolean compute;
    private double money;

    public void setCompute(boolean compute) {
        this.compute = compute;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public boolean isCompute() {

        return compute;
    }

    public String getShop_name() {
        return shop_name;
    }

    public MemRecord(boolean compute, double money, String shop_name) {

        this.compute = compute;
        this.money = money;
        this.shop_name = shop_name;
    }

    public MemRecord() {

    }
}
