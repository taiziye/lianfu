package com.tangpo.lianfu.entity;

import java.io.Serializable;

/**
 * Created by 果冻 on 2015/11/9.
 */
public class Employee implements Serializable {
    private String user_id;
    private String rank;
    private String sex;
    private String name;
    private String phone;
    private String bank;
    private String bank_account;
    private String bank_name;
    private String register_time;

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getBank_account() {
        return bank_account;
    }

    public void setBank_account(String bank_account) {
        this.bank_account = bank_account;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
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

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getRegister_time() {
        return register_time;
    }

    public void setRegister_time(String register_time) {
        this.register_time = register_time;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Employee(String bank, String sex, String bank_account, String bank_name, String name, String phone, String rank, String register_time, String user_id) {

        this.bank = bank;
        this.bank_account = bank_account;
        this.bank_name = bank_name;
        this.name = name;
        this.phone = phone;
        this.rank = rank;
        this.register_time = register_time;
        this.user_id = user_id;
        this.sex = sex;

    }

    public Employee() {

    }
}
