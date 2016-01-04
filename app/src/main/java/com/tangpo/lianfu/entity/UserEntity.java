package com.tangpo.lianfu.entity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by shengshoubo on 2015/11/8.
 */
public class UserEntity implements Serializable {
    private String user_type;
    private String user_id;
    private String store_id;
    private String storename;
    private String name;
    private String phone;
    private String id_number;
    private String sex;
    private String birth;
    private String qq;
    private String email;
    private String address;
    private String bank_account;
    private String bank_name;
    private String bank;
    private String bank_address;
    private String rank;
    private String photo;
    private String session_id;
    private String username;
    private String ulevel;
    private String money;
    private String bindwx;
    private String bindwb;
    private String bindqq;

    public UserEntity(String user_type, String user_id, String store_id, String storename, String name, String phone, String id_number, String sex, String birth, String qq, String email, String address, String bank_account, String bank_name, String bank, String bank_address, String rank, String photo, String session_id, String username, String ulevel, String money) {
        this.user_type = user_type;
        this.user_id = user_id;
        this.store_id = store_id;
        this.storename = storename;
        this.name = name;
        this.phone = phone;
        this.id_number = id_number;
        this.sex = sex;
        this.birth = birth;
        this.qq = qq;
        this.email = email;
        this.address = address;
        this.bank_account = bank_account;
        this.bank_name = bank_name;
        this.bank = bank;
        this.bank_address = bank_address;
        this.rank = rank;
        this.photo = photo;
        this.session_id = session_id;
        this.username = username;
        this.ulevel = ulevel;
        this.money = money;
        this.bindwx = "0";
        this.bindwb = "0";
        this.bindqq = "0";
    }

    public UserEntity() {

    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getStorename() {
        return storename;
    }

    public void setStorename(String storename) {
        this.storename = storename;
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

    public String getId_number() {
        return id_number;
    }

    public void setId_number(String id_number) {
        this.id_number = id_number;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getBank_address() {
        return bank_address;
    }

    public void setBank_address(String bank_address) {
        this.bank_address = bank_address;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUlevel() {
        return ulevel;
    }

    public void setUlevel(String ulevel) {
        this.ulevel = ulevel;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getBindwx() {
        return bindwx;
    }

    public void setBindwx(String bindwx) {
        this.bindwx = bindwx;
    }

    public String getBindwb() {
        return bindwb;
    }

    public void setBindwb(String bindwb) {
        this.bindwb = bindwb;
    }

    public String getBindqq() {
        return bindqq;
    }

    public void setBindqq(String bindqq) {
        this.bindqq = bindqq;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "user_type='" + user_type + '\'' +
                ", user_id='" + user_id + '\'' +
                ", store_id='" + store_id + '\'' +
                ", storename='" + storename + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", id_number='" + id_number + '\'' +
                ", sex='" + sex + '\'' +
                ", birth='" + birth + '\'' +
                ", qq='" + qq + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", bank_account='" + bank_account + '\'' +
                ", bank_name='" + bank_name + '\'' +
                ", bank='" + bank + '\'' +
                ", bank_address='" + bank_address + '\'' +
                ", rank='" + rank + '\'' +
                ", photo='" + photo + '\'' +
                ", session_id='" + session_id + '\'' +
                ", username='" + username + '\'' +
                ", ulevel='" + ulevel + '\'' +
                ", money='" + money + '\'' +
                ", bindwx='" + bindwx + '\'' +
                ", bindwb='" + bindwb + '\'' +
                ", bindqq='" + bindqq + '\'' +
                '}';
    }

    public String toJSONString(){
        JSONObject json=new JSONObject();
        try {
            json.put("user_type",this.user_type);
            json.put("user_id",this.user_id);
            json.put("store_id", this.store_id);
            json.put("name", this.name);
            json.put("phone", this.phone);
            json.put("id_number", this.id_number);
            json.put("sex", this.sex);
            json.put("birth", this.birth);
            json.put("qq", this.qq);
            json.put("email", this.email);
            json.put("address", this.address);
            json.put("bank_account", this.bank_account);
            json.put("bank_name", this.bank_name);
            json.put("bank", this.bank);
            json.put("bank_address", this.bank_address);
            json.put("rank", this.rank);
            json.put("photo", this.photo);
            json.put("session_id", this.session_id);
            json.put("username", this.username);
            json.put("ulevel", this.ulevel);
            json.put("money", this.money);
            json.put("bindwx", this.bindwx);
            json.put("bindwb", this.bindwb);
            json.put("bindqq", this.bindqq);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

}
