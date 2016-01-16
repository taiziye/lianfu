package com.tangpo.lianfu.entity;

/**
 * Created by 果冻 on 2015/11/9.
 */
public class MemRecord {

    private String id;
    private String fee;
    private String discount;
    private String store_id;

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    private String store;
    private String datetime;
    private String record_status;

    @Override
    public String toString() {
        return "MemRecord{" +
                "id='" + id + '\'' +
                ", fee='" + fee + '\'' +
                ", discount='" + discount + '\'' +
                ", discount='" + store_id + '\'' +
                ", store='" + store + '\'' +
                ", datetime='" + datetime + '\'' +
                ", record_status='" + record_status + '\'' +
                '}';
    }

    public MemRecord(String id, String fee, String discount, String store_id,String store, String datetime, String record_status) {
        this.id = id;
        this.fee = fee;
        this.discount = discount;
        this.store_id=store_id;
        this.store = store;
        this.datetime = datetime;
        this.record_status = record_status;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getRecord_status() {
        return record_status;
    }

    public void setRecord_status(String record_status) {
        this.record_status = record_status;
    }

    public MemRecord() {

    }
}
