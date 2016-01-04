package com.tangpo.lianfu.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by 果冻 on 2015/11/9.
 */
public class Employee implements Parcelable {
    private String user_id;
    private String rank;
    private String sex;
    private String username;
    private String name;
    private String phone;
    private String bank;
    private String bank_account;
    private String bank_name;
    private String register_time;
    private String id_number;
    private String upgrade;
    private String isServer;
    private String isstop;

    protected Employee(Parcel in) {
        user_id = in.readString();
        rank = in.readString();
        name = in.readString();
        username=in.readString();
        phone = in.readString();
        bank = in.readString();
        bank_account = in.readString();
        bank_name = in.readString();
        register_time = in.readString();
        sex = in.readString();
        id_number = in.readString();
        upgrade = in.readString();
        isServer = in.readString();
        isstop = in.readString();
    }

    @Override
    public String toString() {
        return "Employee{" +
                "user_id='" + user_id + '\'' +
                ", rank='" + rank + '\'' +
                ", sex='" + sex + '\'' +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", bank='" + bank + '\'' +
                ", bank_account='" + bank_account + '\'' +
                ", bank_name='" + bank_name + '\'' +
                ", register_time='" + register_time + '\'' +
                ", id_number='" + id_number + '\'' +
                ", upgrade='" + upgrade + '\'' +
                ", isServer='" + isServer + '\'' +
                ", isstop='" + isstop + '\'' +
                '}';
    }

    public Employee() {
    }

    public Employee(String user_id, String rank, String sex, String username, String name, String phone, String bank, String bank_account, String bank_name, String register_time, String id_number, String upgrade, String isServer, String isstop) {
        this.user_id = user_id;
        this.rank = rank;
        this.sex = sex;
        this.username = username;
        this.name = name;
        this.phone = phone;
        this.bank = bank;
        this.bank_account = bank_account;
        this.bank_name = bank_name;
        this.register_time = register_time;
        this.id_number = id_number;
        this.upgrade = upgrade;
        this.isServer = isServer;
        this.isstop = isstop;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getRegister_time() {
        return register_time;
    }

    public void setRegister_time(String register_time) {
        this.register_time = register_time;
    }

    public String getId_number() {
        return id_number;
    }

    public void setId_number(String id_number) {
        this.id_number = id_number;
    }

    public String getUpgrade() {
        return upgrade;
    }

    public void setUpgrade(String upgrade) {
        this.upgrade = upgrade;
    }

    public String getIsServer() {
        return isServer;
    }

    public void setIsServer(String isServer) {
        this.isServer = isServer;
    }

    public String getIsstop() {
        return isstop;
    }

    public void setIsstop(String isstop) {
        this.isstop = isstop;
    }

    public static final Creator<Employee> CREATOR = new Creator<Employee>() {
        @Override
        public Employee createFromParcel(Parcel in) {
            return new Employee(in);
        }

        @Override
        public Employee[] newArray(int size) {
            return new Employee[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_id);
        dest.writeString(rank);
        dest.writeString(name);
        dest.writeString(username);
        dest.writeString(phone);
        dest.writeString(bank);
        dest.writeString(bank_account);
        dest.writeString(bank_name);
        dest.writeString(register_time);
        dest.writeString(sex);
        dest.writeString(id_number);
        dest.writeString(upgrade);
        dest.writeString(isServer);
        dest.writeString(isstop);
    }
}
