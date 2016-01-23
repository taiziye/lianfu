package com.tangpo.lianfu.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 果冻 on 2015/11/11.
 */
public class EmployeeConsumeRecord implements Serializable {

    private String id;
    private String user_id;
    private String store_id;
    private String username;
    private String phone;

    private String name;

    private String store;
    private String consume_date;
    private String gains;
    private String fee;
    private String IsPass;
    private String discount;
    private String pay_status;
    private String pay_way;
    private String pay_date;
    private String onlines;
    private String desc;
    private String ticket;
    private String ticketpic;
    public void setConsume_date(String consume_date) {
        this.consume_date = consume_date;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTicketpic() {
        return ticketpic;
    }

    public void setTicketpic(String ticketpic) {
        this.ticketpic = ticketpic;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getGains() {
        return gains;
    }

    public void setGains(String gains) {
        this.gains = gains;
    }

    public String getIsPass() {
        return IsPass;
    }

    public void setIsPass(String isPass) {
        IsPass = isPass;
    }

    public String getOnlines() {
        return onlines;
    }

    public void setOnlines(String onlines) {
        this.onlines = onlines;
    }

    public String getConsume_date() {
        return consume_date;
    }

    public void setConsume_date() {
        this.consume_date = (new SimpleDateFormat("yyyy/MM/dd hh:mm")).format(new Date());
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


    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
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
        this.id = "";
        this.user_id = "";
        this.store_id = "";
        this.username = "";
        this.name = "";
        this.phone="";
        this.store = "";
        this.consume_date = "";
        this.gains = "";
        this.fee = "";
        this.IsPass = "";
        this.discount = "";
        this.pay_status = "";
        this.pay_way = "";
        this.pay_date = "";
        this.onlines = "";
        this.desc = "";
    }

    @Override
    public String toString() {
        return "EmployeeConsumeRecord{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", consume_date='" + consume_date + '\'' +
                ", fee='" + fee + '\'' +
                ", IsPass='" + IsPass + '\'' +
                ", discount='" + discount + '\'' +
                ", pay_status='" + pay_status + '\'' +
                ", pay_way='" + pay_way + '\'' +
                ", pay_date='" + pay_date + '\'' +
                ", onlines='" + onlines + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }

    public EmployeeConsumeRecord(String id, String user_id, String store_id, String username, String name,String phone,String store, String consume_date, String gains, String fee, String isPass, String discount, String pay_status, String pay_way, String pay_date, String onlines, String desc) {
        this.id = id;
        this.user_id = user_id;
        this.store_id = store_id;
        this.username = username;
        this.name = name;
        this.phone=phone;
        this.store = store;
        this.consume_date = consume_date;
        this.gains = gains;
        this.fee = fee;
        this.IsPass = isPass;
        this.discount = discount;
        this.pay_status = pay_status;
        this.pay_way = pay_way;
        this.pay_date = pay_date;
        this.onlines = onlines;
        this.desc = desc;
    }
}
