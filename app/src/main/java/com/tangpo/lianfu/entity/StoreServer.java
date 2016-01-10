package com.tangpo.lianfu.entity;

import java.io.Serializable;

/**
 * Created by 果冻 on 2016/1/5.
 */
public class StoreServer implements Serializable {
    private String user_id;
    private String username;
    private String name;
    private String phone;
    private String photo;

    public StoreServer() {
    }

    public StoreServer(String user_id, String username, String name, String phone, String photo) {
        this.user_id = user_id;
        this.username = username;
        this.name = name;
        this.phone = phone;
        this.photo = photo;
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "StoreServer{" +
                "user_id='" + user_id + '\'' +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }

    /*@Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_id);
        dest.writeString(username);
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(photo);
    }

    public static final Parcelable.Creator<StoreServer> CREATOR = new Creator<StoreServer>() {
        @Override
        public StoreServer createFromParcel(Parcel source) {
            StoreServer server = new StoreServer();
            server.setUser_id(source.readString());
            server.setUsername(source.readString());
            server.setName(source.readString());
            server.setPhone(source.readString());
            server.setPhoto(source.readString());
            return server;
        }

        @Override
        public StoreServer[] newArray(int size) {
            return new StoreServer[size];
        }
    };*/
}
