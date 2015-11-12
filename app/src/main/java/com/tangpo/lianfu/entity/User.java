package com.tangpo.lianfu.entity;

import java.io.Serializable;

/**
 * Created by 果冻 on 2015/11/11.
 */
public class User implements Serializable {

    private String store_id;
    private String store_name;
    private String income;
    private String mem_num;
    private String profit;
    private String need_pay;
    private String admin_num;
    private String staff_num;
    private String payback;

    public User() {
    }

    public User(String admin_num, String income, String mem_num, String need_pay, String payback, String profit, String staff_num, String store_id, String store_name) {
        this.admin_num = admin_num;
        this.income = income;
        this.mem_num = mem_num;
        this.need_pay = need_pay;
        this.payback = payback;
        this.profit = profit;
        this.staff_num = staff_num;
        this.store_id = store_id;
        this.store_name = store_name;
    }

    public void setAdmin_num(String admin_num) {
        this.admin_num = admin_num;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public void setMem_num(String mem_num) {
        this.mem_num = mem_num;
    }

    public void setNeed_pay(String need_pay) {
        this.need_pay = need_pay;
    }

    public void setPayback(String payback) {
        this.payback = payback;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }

    public void setStaff_num(String staff_num) {
        this.staff_num = staff_num;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getAdmin_num() {

        return admin_num;
    }

    public String getIncome() {
        return income;
    }

    public String getMem_num() {
        return mem_num;
    }

    public String getNeed_pay() {
        return need_pay;
    }

    public String getPayback() {
        return payback;
    }

    public String getProfit() {
        return profit;
    }

    public String getStaff_num() {
        return staff_num;
    }

    public String getStore_id() {
        return store_id;
    }

    public String getStore_name() {
        return store_name;
    }
}
