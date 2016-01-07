package com.tangpo.lianfu.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shengshoubo on 2015/11/24.
 */
public class ProfitPay implements Parcelable{
    private String id;
    private String store_id;
    private String store_name;
    private String user_id;
    private String username;
    private String name;
    private String phone;
    private String fee;
    private String agio;
    private String profit;
    private String omode;
    private String pay_status;
    private String pay_date;
    private String pay_account;
    private String desc;

    public ProfitPay(String id, String store_id, String store_name, String user_id, String username, String name, String phone, String fee, String agio, String profit, String omode, String pay_status, String pay_date, String pay_account, String desc) {
        this.id = id;
        this.store_id = store_id;
        this.store_name = store_name;
        this.user_id = user_id;
        this.username = username;
        this.name = name;
        this.phone = phone;
        this.fee = fee;
        this.agio = agio;
        this.profit = profit;
        this.omode = omode;
        this.pay_status = pay_status;
        this.pay_date = pay_date;
        this.pay_account = pay_account;
        this.desc = desc;
    }

    public ProfitPay() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getAgio() {
        return agio;
    }

    public void setAgio(String agio) {
        this.agio = agio;
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }

    public String getOmode() {
        return omode;
    }

    public void setOmode(String omode) {
        this.omode = omode;
    }

    public String getPay_status() {
        return pay_status;
    }

    public void setPay_status(String pay_status) {
        this.pay_status = pay_status;
    }

    public String getPay_date() {
        return pay_date;
    }

    public void setPay_date(String pay_date) {
        this.pay_date = pay_date;
    }

    public String getPay_account() {
        return pay_account;
    }

    public void setPay_account(String pay_account) {
        this.pay_account = pay_account;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    protected ProfitPay(Parcel in) {

        id = in.readString();
        store_id = in.readString();
        store_name = in.readString();
        user_id = in.readString();
        username = in.readString();
        name = in.readString();
        phone = in.readString();
        fee = in.readString();
        agio = in.readString();
        profit = in.readString();
        omode = in.readString();
        pay_status = in.readString();
        pay_date = in.readString();
        pay_account = in.readString();
        desc = in.readString();
    }

    public static final Parcelable.Creator<ProfitPay> CREATOR = new Parcelable.Creator<ProfitPay>() {
        @Override
        public ProfitPay createFromParcel(Parcel in) {
            return new ProfitPay(in);
        }

        @Override
        public ProfitPay[] newArray(int size) {
            return new ProfitPay[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(store_id);
        dest.writeString(store_name);
        dest.writeString(id);
        dest.writeString(store_id);
        dest.writeString(store_name);
        dest.writeString(user_id);
        dest.writeString(username);
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(fee);
        dest.writeString(agio);
        dest.writeString(profit);
        dest.writeString(omode);
        dest.writeString(pay_status);
        dest.writeString(pay_date);
        dest.writeString(pay_account);
        dest.writeString(desc);
    }

    @Override
    public String toString() {
        return "ProfitPay{" +
                "id='" + id + '\'' +
                ", store_id='" + store_id + '\'' +
                ", store_name='" + store_name + '\'' +
                ", user_id='" + user_id + '\'' +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", fee='" + fee + '\'' +
                ", agio='" + agio + '\'' +
                ", profit='" + profit + '\'' +
                ", omode='" + omode + '\'' +
                ", pay_status='" + pay_status + '\'' +
                ", pay_date='" + pay_date + '\'' +
                ", pay_account='" + pay_account + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
