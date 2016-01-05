package com.tangpo.lianfu.entity;

/**
 * Created by 果冻 on 2015/11/24.
 */
public class Repay {
    private String id;
    private String username;
    private String name;
    private String consume_date;
    private String fee;
    private String profit;
    private String pay_status;
    private String pay_date;
    private String pay_account;
    private String desc;
    private String bank_account;
    private String bank_name;
    private String bank;
    private String bank_address;

    public Repay() {
    }

    public Repay(String id, String username, String name, String consume_date, String fee, String profit, String pay_status, String pay_date, String pay_account, String desc, String bank_account, String bank_name, String bank, String bank_address) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.consume_date = consume_date;
        this.fee = fee;
        this.profit = profit;
        this.pay_status = pay_status;
        this.pay_date = pay_date;
        this.pay_account = pay_account;
        this.desc = desc;
        this.bank_account = bank_account;
        this.bank_name = bank_name;
        this.bank = bank;
        this.bank_address = bank_address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConsume_date() {
        return consume_date;
    }

    public void setConsume_date(String consume_date) {
        this.consume_date = consume_date;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }

    public String getPay_status() {
        return pay_status;
    }

    public void setPay_status(String pay_status) {
        this.pay_status = pay_status;
    }

    public String getPay_date() {
        return pay_date;
    }

    public void setPay_date(String pay_date) {
        this.pay_date = pay_date;
    }

    public String getPay_account() {
        return pay_account;
    }

    public void setPay_account(String pay_account) {
        this.pay_account = pay_account;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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

    @Override
    public String toString() {
        return "Repay{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", consume_date='" + consume_date + '\'' +
                ", fee='" + fee + '\'' +
                ", profit='" + profit + '\'' +
                ", pay_status='" + pay_status + '\'' +
                ", pay_date='" + pay_date + '\'' +
                ", pay_account='" + pay_account + '\'' +
                ", desc='" + desc + '\'' +
                ", bank_account='" + bank_account + '\'' +
                ", bank_name='" + bank_name + '\'' +
                ", bank='" + bank + '\'' +
                ", bank_address='" + bank_address + '\'' +
                '}';
    }
}
