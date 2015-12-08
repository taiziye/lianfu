package com.tangpo.lianfu.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by 果冻 on 2015/11/15.
 */
public class FindStore implements Parcelable {
    private String id;
    private String lng;
    private String lat;
    private String store;
    private String contact;
    private String tel;
    private String business;
    private String address;
    private String photo;
    private String favorite;
    private String distance;

    public String getFavorite() {
        return favorite;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getBusiness() {
        return business;
    }

    @Override
    public String toString() {
        return "FindStore{" +
                "address='" + address + '\'' +
                ", id='" + id + '\'' +
                ", lng='" + lng + '\'' +
                ", lat='" + lat + '\'' +
                ", store='" + store + '\'' +
                ", contact='" + contact + '\'' +
                ", tel='" + tel + '\'' +
                ", business='" + business + '\'' +
                ", photo='" + photo + '\'' +
                ", favorite='" + favorite + '\'' +
                ", distance='" + distance + '\'' +
                '}';
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public FindStore(Parcel in) {
        id = in.readString();
        lng = in.readString();
        lat = in.readString();
        store = in.readString();
        contact = in.readString();
        tel = in.readString();
        address = in.readString();
        photo = in.readString();
        favorite=in.readString();
        distance=in.readString();
    }

    public static final Creator<FindStore> CREATOR = new Creator<FindStore>() {
        @Override
        public FindStore createFromParcel(Parcel in) {
            FindStore store = new FindStore();
            store.id = in.readString();
            store.lng = in.readString();
            store.lat = in.readString();
            store.store = in.readString();
            store.tel = in.readString();
            store.contact = in.readString();
            store.address = in.readString();
            store.business = in.readString();
            store.photo = in.readString();
            store.favorite=in.readString();
            store.distance=in.readString();
            return store;
        }

        @Override
        public FindStore[] newArray(int size) {
            return new FindStore[size];
        }
    };

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

    public FindStore(String id, String lng, String lat, String store, String contact, String tel, String business, String address, String photo, String favorite, String distance) {
        this.id = id;
        this.lng = lng;
        this.lat = lat;
        this.store = store;
        this.contact = contact;
        this.tel = tel;
        this.business = business;
        this.address = address;
        this.photo = photo;
        this.favorite = favorite;
        this.distance = distance;
    }

    public FindStore() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(lng);
        dest.writeString(lat);
        dest.writeString(store);
        dest.writeString(tel);
        dest.writeString(contact);
        dest.writeString(address);
        dest.writeString(business);
        dest.writeString(photo);
        dest.writeString(favorite);
        dest.writeString(distance);
    }

}
