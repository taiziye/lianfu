package com.tangpo.lianfu.entity;

/**
 * Created by shengshoubo on 2015/12/16.
 */
public class StoreInfo {
    private String store_id;
    private String store;
    private String contact;
    private String linkman;
    private String phone;
    private String tel;
    private String lng;
    private String lat;
    private String qq;
    private String email;
    private String address;
    private String singuser;
    private String trade;
    private String sheng;
    private String shi;
    private String xian;
    private String shengcode;
    private String shicode;

    public String getShengcode() {
        return shengcode;
    }

    public void setShengcode(String shengcode) {
        this.shengcode = shengcode;
    }

    public String getShicode() {
        return shicode;
    }

    public void setShicode(String shicode) {
        this.shicode = shicode;
    }

    public String getXiancode() {
        return xiancode;
    }

    public void setXiancode(String xiancode) {
        this.xiancode = xiancode;
    }

    private String xiancode;
    private String business;
    private String dpsign;
    private String banner;
    private String photo;

    public StoreInfo() {
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getLinkman() {
        return linkman;
    }

    public void setLinkman(String linkman) {
        this.linkman = linkman;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
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

    public String getSinguser() {
        return singuser;
    }

    public void setSinguser(String singuser) {
        this.singuser = singuser;
    }

    public String getTrade() {
        return trade;
    }

    public void setTrade(String trade) {
        this.trade = trade;
    }

    public String getSheng() {
        return sheng;
    }

    public void setSheng(String sheng) {
        this.sheng = sheng;
    }

    public String getShi() {
        return shi;
    }

    public void setShi(String shi) {
        this.shi = shi;
    }

    public String getXian() {
        return xian;
    }

    public void setXian(String xian) {
        this.xian = xian;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getDpsign() {
        return dpsign;
    }

    public void setDpsign(String dpsign) {
        this.dpsign = dpsign;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public StoreInfo(String store_id, String store, String contact, String linkman, String phone, String tel, String lng, String lat, String qq, String email, String address, String singuser, String trade, String sheng, String shi, String xian,String shengcode,String shicode,String xiancode, String business, String dpsign, String banner, String photo) {
        this.store_id = store_id;
        this.store = store;
        this.contact = contact;
        this.linkman = linkman;
        this.phone = phone;
        this.tel = tel;
        this.lng = lng;
        this.lat = lat;
        this.qq = qq;
        this.email = email;
        this.address = address;
        this.singuser = singuser;
        this.trade = trade;
        this.sheng = sheng;
        this.shi = shi;
        this.xian = xian;
        this.shengcode=shengcode;
        this.shicode=shicode;
        this.xiancode=xiancode;
        this.business = business;
        this.dpsign = dpsign;
        this.banner = banner;
        this.photo = photo;
    }
}
