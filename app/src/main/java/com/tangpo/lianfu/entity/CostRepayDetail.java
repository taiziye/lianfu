package com.tangpo.lianfu.entity;

/**
 * Created by 果冻 on 2016/1/5.
 */
public class CostRepayDetail {
    private String id;
    private String name;
    private String phone;
    private String cost;
    private String backcost;
    private String gains;
    private String consume_date;

    public CostRepayDetail() {
    }

    public CostRepayDetail(String id, String name, String phone, String cost, String backcost, String gains, String consume_date) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.cost = cost;
        this.backcost = backcost;
        this.gains = gains;
        this.consume_date = consume_date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getBackcost() {
        return backcost;
    }

    public void setBackcost(String backcost) {
        this.backcost = backcost;
    }

    public String getGains() {
        return gains;
    }

    public void setGains(String gains) {
        this.gains = gains;
    }

    public String getConsume_date() {
        return consume_date;
    }

    public void setConsume_date(String consume_date) {
        this.consume_date = consume_date;
    }

    @Override
    public String toString() {
        return "CostRepayDetail{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", cost='" + cost + '\'' +
                ", backcost='" + backcost + '\'' +
                ", gains='" + gains + '\'' +
                ", consume_date='" + consume_date + '\'' +
                '}';
    }
}
