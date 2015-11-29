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
    private String isPass;
    private String discount;
    private String pay_status;
    private String pay_way;
    private String pay_date;
    private String onLines;
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

    public String getIsPass() {
        return isPass;
    }

    public void setIsPass(String isPass) {
        this.isPass = isPass;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getOnLines() {
        return onLines;
    }

    public void setOnLines(String onLines) {
        this.onLines = onLines;
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


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public EmployeeConsumeRecord() {

    }

    @Override
    public String toString() {
        return "EmployeeConsumeRecord{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", consume_date='" + consume_date + '\'' +
                ", fee='" + fee + '\'' +
                ", isPass='" + isPass + '\'' +
                ", discount='" + discount + '\'' +
                ", pay_status='" + pay_status + '\'' +
                ", pay_way='" + pay_way + '\'' +
                ", pay_date='" + pay_date + '\'' +
                ", onLines='" + onLines + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }

    public EmployeeConsumeRecord(String id, String username, String consume_date, String fee, String isPass, String discount, String pay_status, String pay_way, String pay_date, String onLines, String desc) {
        this.id = id;
        this.username = username;
        this.consume_date = consume_date;
        this.fee = fee;
        this.isPass = isPass;
        this.discount = discount;
        this.pay_status = pay_status;
        this.pay_way = pay_way;
        this.pay_date = pay_date;
        this.onLines = onLines;
        this.desc = desc;
    }
}
