package com.tangpo.lianfu.entity;

import java.io.Serializable;

/**
 * Created by 果冻 on 2015/11/9.
 */
public class Discount implements Serializable {

    private String id;
    private String desc;
    private String discount;
    private String display_id;
    private String status;
    private String examiner;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getDisplay_id() {
        return display_id;
    }

    public void setDisplay_id(String display_id) {
        this.display_id = display_id;
    }

    public String getExaminer() {
        return examiner;
    }

    public void setExaminer(String examiner) {
        this.examiner = examiner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Discount(String desc, String discount, String display_id, String examiner, String id, String status) {

        this.desc = desc;
        this.discount = discount;
        this.display_id = display_id;
        this.examiner = examiner;
        this.id = id;
        this.status = status;
    }

    public Discount() {
    }

    @Override
    public String toString() {
        return "Discount{" +
                "desc='" + desc + '\'' +
                ", id='" + id + '\'' +
                ", discount='" + discount + '\'' +
                ", display_id='" + display_id + '\'' +
                ", status='" + status + '\'' +
                ", examiner='" + examiner + '\'' +
                '}';
    }
}
