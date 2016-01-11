package com.tangpo.lianfu.entity;

/**
 * Created by 果冻 on 2015/11/11.
 */
public class Store {
    private String id;
    private String lng;
    private String lat;
    private String store;
    private String contact;
    private String tel;
    private String address;
    private String business;
    private String banner;
    private String photo;
    private String qq;
    private String email;

    public Store() {
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

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public Store(String address, String banner, String business, String contact, String id, String lat, String lng, String photo, String store, String tel) {
        this.address = address;
        this.banner = banner;
        this.business = business;
        this.contact = contact;
        this.id = id;
        this.lat = lat;

        this.lng = lng;
        this.photo = photo;
        this.store = store;
        this.tel = tel;
    }
}
