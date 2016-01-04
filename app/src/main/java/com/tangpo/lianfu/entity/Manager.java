package com.tangpo.lianfu.entity;

import java.io.Serializable;

/**
 * Created by 果冻 on 2015/11/11.
 */
public class Manager implements Serializable {

    private String store_id;
    private String store_name;
    private String income;
    private String mem_num;
    private String profit;
    private String need_pay;
    private String admin_num;
    private String staff_num;
    private String payback;
    private String costback;

    public Manager(String store_id, String store_name, String income, String mem_num, String profit, String need_pay, String admin_num, String staff_num, String payback, String costback) {
        this.store_id = store_id;
        this.store_name = store_name;
        this.income = income;
        this.mem_num = mem_num;
        this.profit = profit;
        this.need_pay = need_pay;
        this.admin_num = admin_num;
        this.staff_num = staff_num;
        this.payback = payback;
        this.costback = costback;
    }

    public Manager() {

    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getMem_num() {
        return mem_num;
    }

    public void setMem_num(String mem_num) {
        this.mem_num = mem_num;
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }

    public String getNeed_pay() {
        return need_pay;
    }

    public void setNeed_pay(String need_pay) {
        this.need_pay = need_pay;
    }

    public String getAdmin_num() {
        return admin_num;
    }

    public void setAdmin_num(String admin_num) {
        this.admin_num = admin_num;
    }

    public String getStaff_num() {
        return staff_num;
    }

    public void setStaff_num(String staff_num) {
        this.staff_num = staff_num;
    }

    public String getPayback() {
        return payback;
    }

    public void setPayback(String payback) {
        this.payback = payback;
    }

    public String getCostback() {
        return costback;
    }

    public void setCostback(String costback) {
        this.costback = costback;
    }

    @Override
    public String toString() {
        return "Manager{" +
                "store_id='" + store_id + '\'' +
                ", store_name='" + store_name + '\'' +
                ", income='" + income + '\'' +
                ", mem_num='" + mem_num + '\'' +
                ", profit='" + profit + '\'' +
                ", need_pay='" + need_pay + '\'' +
                ", admin_num='" + admin_num + '\'' +
                ", staff_num='" + staff_num + '\'' +
                ", payback='" + payback + '\'' +
                ", costback='" + costback + '\'' +
                '}';
    }
}
