package com.tangpo.lianfu.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 果冻 on 2015/11/11.
 */
public class EmployeeConsumeRecord implements Serializable {

    private String id;
    private String username;
    private String consume_date;
    private String fee;
    private String profit;
    private String pay_status;
    private String pay_way;
    private String pay_date;
    private String pay_account;
    private String desc;

    public String getConsume_date() {
        return consume_date;
    }

    public void setConsume_date(String consume_date) {
        this.consume_date = (new SimpleDateFormat("yyyy-MM-dd hh:mm")).format(new Date());
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPay_account() {
        return pay_account;
    }

    public void setPay_account(String pay_account) {
        this.pay_account = pay_account;
    }

    public String getPay_date() {
        return pay_date;
    }

    public void setPay_date(String pay_date) {
        this.pay_date = pay_date;
    }

    public String getPay_status() {
        return pay_status;
    }

    public void setPay_status(String pay_status) {
        this.pay_status = pay_status;
    }

    public String getPay_way() {
        return pay_way;
    }

    public void setPay_way(String pay_way) {
        this.pay_way = pay_way;
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public EmployeeConsumeRecord(String consume_date, String desc, String fee, String id, String pay_account, String pay_date, String pay_status, String pay_way, String profit, String username) {

        this.consume_date = consume_date;
        this.desc = desc;
        this.fee = fee;
        this.id = id;
        this.pay_account = pay_account;
        this.pay_date = pay_date;
        this.pay_status = pay_status;
        this.pay_way = pay_way;
        this.profit = profit;
        this.username = username;
    }

    public EmployeeConsumeRecord() {

    }
}
