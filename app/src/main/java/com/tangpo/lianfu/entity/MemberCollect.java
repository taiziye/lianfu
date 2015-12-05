package com.tangpo.lianfu.entity;

import java.io.Serializable;

/**
 * Created by 果冻 on 2015/11/15.
 */
public class MemberCollect implements Serializable {
    private String id;
    private String lng;
    private String lat;
    private String store;
    private String contact;
    private String tel;
    private String address;
    private String business;
    private String photo;

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public MemberCollect(String address, String business, String contact, String id, String lat, String lng, String photo, String store, String tel) {
        this.address = address;
        this.business = business;
        this.contact = contact;
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.photo = photo;
        this.store = store;
        this.tel = tel;
    }

    public MemberCollect() {

    }
}
