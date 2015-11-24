package com.tangpo.lianfu.entity;

/**
 * Created by 果冻 on 2015/11/24.
 */
public class Repay {
    private String id;
    private String username;
    private String consume_date;
    private String fee;
    private String profit;
    private String pay_status;
    private String pay_date;

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

    public Repay(String id, String username, String consume_date, String fee, String profit, String pay_status, String pay_date, String pay_account, String desc) {

        this.id = id;
        this.username = username;
        this.consume_date = consume_date;
        this.fee = fee;
        this.profit = profit;
        this.pay_status = pay_status;
        this.pay_date = pay_date;
        this.pay_account = pay_account;
        this.desc = desc;
    }

    public Repay() {

    }

    private String pay_account;
    private String desc;
}
