package com.tangpo.lianfu.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 果冻 on 2015/11/9.
 */
public class UserConsumRecord implements Serializable {

    private String id;
    private String fee;
    private String discount;
    private String store;
    private String datetime;
    private String record_status;

    public UserConsumRecord(String datetime, String discount, String fee, String record_status, String id, String store) {
        this.datetime = datetime;
        this.discount = discount;
        this.fee = fee;
        this.record_status = record_status;
        this.id = id;
        this.store = store;
    }

    public UserConsumRecord() {

    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime() {
        this.datetime = (new SimpleDateFormat("yyyy-MM-dd hh:mm")).format(new Date());
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getRecord_status() {
        return record_status;
    }

    public void setRecord_status(String record_status) {
        this.record_status = record_status;
    }

    public String getID() {
        return id;
    }

    public void setID(String shop_name) {
        this.id = shop_name;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }
}
