package com.tangpo.lianfu.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 果冻 on 2015/11/9.
 */
public class Member implements Serializable {

    private String user_id;
    private String username;
    private String sex;
    private String name;
    private String phone;
    private String bank;
    private String bank_account;
    private String bank_name;
    private String register_time;
    private String id_number;

    @Override
    public String toString() {
        return "Member{" +
                "bank='" + bank + '\'' +
                ", user_id='" + user_id + '\'' +
                ", username='" + username + '\'' +
                ", sex='" + sex + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", bank_account='" + bank_account + '\'' +
                ", bank_name='" + bank_name + '\'' +
                ", register_time='" + register_time + '\'' +
                ", id_number='" + id_number + '\'' +
                '}';
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

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

    public String getRegister_time() {
        return register_time;
    }

    public void setRegister_time(String register_time) {
        this.register_time = (new SimpleDateFormat("yyyy-MM-dd hh:mm")).format(new Date());
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId_number() {
        return id_number;
    }

    public void setId_number(String id_number) {
        this.id_number = id_number;
    }

    public Member(String bank, String bank_account, String bank_name, String name, String phone, String register_time, String user_id, String username, String sex) {

        this.bank = bank;
        this.bank_account = bank_account;
        this.bank_name = bank_name;
        this.name = name;
        this.phone = phone;
        this.register_time = register_time;
        this.user_id = user_id;
        this.username = username;
        this.sex = sex;
    }

    public Member() {

    }

    /*@Override
    public String toString() {
        return "Member{" +
                "bank:'" + bank + '\'' +
                ", user_id:'" + user_id + '\'' +
                ", username:'" + username + '\'' +
                ", name:'" + name + '\'' +
                ", phone:'" + phone + '\'' +
                ", bank_account:'" + bank_account + '\'' +
                ", bank_name:'" + bank_name + '\'' +
                ", register_time:'" + register_time + '\'' +
                ", sex:'" + sex + '\'' +
                '}';
    }*/
}
