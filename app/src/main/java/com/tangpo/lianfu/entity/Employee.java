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

    protected Employee(Parcel in) {
        user_id = in.readString();
        rank = in.readString();
        zsname = in.readString();
        phone = in.readString();
        bank = in.readString();
        bank_account = in.readString();
        bank_name = in.readString();
        register_time = in.readString();
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

    public String getZsname() {
        return zsname;
    }

    public void setZsname(String zsname) {
        this.zsname = zsname;
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

    public Employee(String user_id, String rank, String zsname, String phone, String bank, String bank_account, String bank_name, String register_time) {
        this.user_id = user_id;
        this.rank = rank;
        this.zsname = zsname;
        this.phone = phone;
        this.bank = bank;
        this.bank_account = bank_account;
        this.bank_name = bank_name;
        this.register_time = register_time;
    }

    public Employee() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bank_name);
        dest.writeString(phone);
        dest.writeString(rank);
        dest.writeString(register_time);
        dest.writeString(zsname);
        dest.writeString(bank);
        dest.writeString(user_id);
        dest.writeString(bank_account);
    }
}
