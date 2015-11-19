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
    private String zsname;
    private String phone;
    private String bank;
    private String bank_account;
    private String bank_name;
    private String register_time;
    private String sex;
    private String id_number;

    @Override
    public String toString() {
        return "Employee{" +
                "bank='" + bank + '\'' +
                ", user_id='" + user_id + '\'' +
                ", rank='" + rank + '\'' +
                ", zsname='" + zsname + '\'' +
                ", phone='" + phone + '\'' +
                ", bank_account='" + bank_account + '\'' +
                ", bank_name='" + bank_name + '\'' +
                ", register_time='" + register_time + '\'' +
                ", sex='" + sex + '\'' +
                ", id_number='" + id_number + '\'' +
                ", upgrade='" + upgrade + '\'' +
                '}';
    }

    private String upgrade;

    public Employee(String bank, String bank_account, String bank_name, String id_number, String phone, String rank, String register_time, String sex, String upgrade, String user_id, String zsname) {
        this.bank = bank;
        this.bank_account = bank_account;
        this.bank_name = bank_name;
        this.id_number = id_number;
        this.phone = phone;
        this.rank = rank;
        this.register_time = register_time;
        this.sex = sex;
        this.upgrade = upgrade;
        this.user_id = user_id;
        this.zsname = zsname;
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

    public static Creator<Employee> getCREATOR() {
        return CREATOR;
    }

    public String getId_number() {
        return id_number;
    }

    public void setId_number(String id_number) {
        this.id_number = id_number;
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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getUpgrade() {
        return upgrade;
    }

    public void setUpgrade(String upgrade) {
        this.upgrade = upgrade;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getZsname() {
        return zsname;
    }

    public void setZsname(String zsname) {
        this.zsname = zsname;
    }

    public Employee() {

    }

    protected Employee(Parcel in) {
        user_id = in.readString();
        rank = in.readString();
        zsname = in.readString();
        phone = in.readString();
        bank = in.readString();
        bank_account = in.readString();
        bank_name = in.readString();
        register_time = in.readString();
        sex = in.readString();
        id_number = in.readString();
        upgrade = in.readString();
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
        dest.writeString(zsname);
        dest.writeString(phone);
        dest.writeString(bank);
        dest.writeString(bank_account);
        dest.writeString(bank_name);
        dest.writeString(register_time);
        dest.writeString(sex);
        dest.writeString(id_number);
        dest.writeString(upgrade);
    }
}
