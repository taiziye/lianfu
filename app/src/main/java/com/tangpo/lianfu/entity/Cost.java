package com.tangpo.lianfu.entity;

/**
 * Created by 果冻 on 2016/1/5.
 */
public class Cost {
    private String cost_id;
    private String cost;
    private String backdate;
    private String bank_account;
    private String bank_name;
    private String bank;
    private String bank_address;
    private String backstate;
    private String backinfo;

    public Cost() {
    }

    public Cost(String cost_id, String cost, String backdate, String bank_account, String bank_name, String bank, String bank_address, String backstate, String backinfo) {
        this.cost_id = cost_id;
        this.cost = cost;
        this.backdate = backdate;
        this.bank_account = bank_account;
        this.bank_name = bank_name;
        this.bank = bank;
        this.bank_address = bank_address;
        this.backstate = backstate;
        this.backinfo = backinfo;
    }

    public String getCost_id() {
        return cost_id;
    }

    public void setCost_id(String cost_id) {
        this.cost_id = cost_id;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getBackdate() {
        return backdate;
    }

    public void setBackdate(String backdate) {
        this.backdate = backdate;
    }

    public String getBank_account() {
        return bank_account;
    }

    public void setBank_account(String bank_account) {
        this.bank_account = bank_account;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getBank_address() {
        return bank_address;
    }

    public void setBank_address(String bank_address) {
        this.bank_address = bank_address;
    }

    public String getBackstate() {
        return backstate;
    }

    public void setBackstate(String backstate) {
        this.backstate = backstate;
    }

    public String getBackinfo() {
        return backinfo;
    }

    public void setBackinfo(String backinfo) {
        this.backinfo = backinfo;
    }

    @Override
    public String toString() {
        return "Cost{" +
                "cost_id='" + cost_id + '\'' +
                ", cost='" + cost + '\'' +
                ", backdate='" + backdate + '\'' +
                ", bank_account='" + bank_account + '\'' +
                ", bank_name='" + bank_name + '\'' +
                ", bank='" + bank + '\'' +
                ", bank_address='" + bank_address + '\'' +
                ", backstate='" + backstate + '\'' +
                ", backinfo='" + backinfo + '\'' +
                '}';
    }
}
