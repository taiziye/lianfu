package com.tangpo.lianfu.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 果冻 on 2015/11/17.
 */
public class Profit implements Parcelable {
    private String id;
    private String username;
    private String consume_date;
    private String fee;
    private String discount;
    private String profit;
    private String pay_status;
    private String pay_way;
    private String pay_date;
    private String pay_account;
    private String desc;

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public Profit(String consume_date, String discount, String desc, String fee, String id, String pay_account, String pay_date, String pay_status, String pay_way, String profit, String username) {
        this.consume_date = consume_date;
        this.desc = desc;
        this.fee = fee;
        this.discount = discount;
        this.id = id;
        this.pay_account = pay_account;
        this.pay_date = pay_date;
        this.pay_status = pay_status;
        this.pay_way = pay_way;
        this.profit = profit;
        this.username = username;
    }

    protected Profit(Parcel in) {
        id = in.readString();
        username = in.readString();
        consume_date = in.readString();
        fee = in.readString();
        discount = in.readString();
        profit = in.readString();
        pay_status = in.readString();
        pay_way = in.readString();
        pay_date = in.readString();
        pay_account = in.readString();
        desc = in.readString();
    }

    public static final Creator<Profit> CREATOR = new Creator<Profit>() {
        @Override
        public Profit createFromParcel(Parcel in) {
            return new Profit(in);
        }

        @Override
        public Profit[] newArray(int size) {
            return new Profit[size];
        }
    };

    public String getConsume_date() {

        return consume_date;
    }

    public void setConsume_date(String consume_date) {
        this.consume_date = consume_date;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPay_account() {
        return pay_account;
    }

    public void setPay_account(String pay_account) {
        this.pay_account = pay_account;
    }

    public String getPay_date() {
        return pay_date;
    }

    public void setPay_date(String pay_date) {
        this.pay_date = pay_date;
    }

    public String getPay_status() {
        return pay_status;
    }

    public void setPay_status(String pay_status) {
        this.pay_status = pay_status;
    }

    public String getPay_way() {
        return pay_way;
    }

    public void setPay_way(String pay_way) {
        this.pay_way = pay_way;
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Profit() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(username);
        dest.writeString(consume_date);
        dest.writeString(fee);
        dest.writeString(discount);
        dest.writeString(profit);
        dest.writeString(pay_status);
        dest.writeString(pay_way);
        dest.writeString(pay_date);
        dest.writeString(pay_account);
        dest.writeString(desc);
    }

    @Override
    public String toString() {
        return "Profit{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", consume_date='" + consume_date + '\'' +
                ", fee='" + fee + '\'' +
                ", discount='" + discount + '\'' +
                ", profit='" + profit + '\'' +
                ", pay_status='" + pay_status + '\'' +
                ", pay_way='" + pay_way + '\'' +
                ", pay_date='" + pay_date + '\'' +
                ", pay_account='" + pay_account + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
