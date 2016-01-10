package com.tangpo.lianfu.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 果冻 on 2016/1/5.
 */
public class ChatAccount implements Parcelable {
    public static final String ID = "_id";
    public static final String USERNAME = "username";
    public static final String NAME = "name";
    public static final String EASEMOD_ID = "easemod_id";
    public static final String PHOTO = "photo";
    public static final String MSG = "msg";
    public static final String TIME = "time";

    private String user_id;
    private String username;
    private String name;
    private String phone;
    private String easemod_id;
    private String uuid;
    private String pwd;
    private String photo;
    private String msg;  //最后的聊天内容
    private String time;  //最后的聊天时间
    private int unread;

    public ChatAccount() {
    }

    public ChatAccount(String user_id, String username, String name, String phone, String easemod_id, String uuid, String pwd, String photo, String msg, String time) {
        this.user_id = user_id;
        this.username = username;
        this.name = name;
        this.phone = phone;
        this.easemod_id = easemod_id;
        this.uuid = uuid;
        this.pwd = pwd;
        this.photo = photo;
        this.msg = msg;
        this.time = time;
    }

    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
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

    public String getEasemod_id() {
        return easemod_id;
    }

    public void setEasemod_id(String easemod_id) {
        this.easemod_id = easemod_id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "ChatAccount{" +
                "user_id='" + user_id + '\'' +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", easemod_id='" + easemod_id + '\'' +
                ", uuid='" + uuid + '\'' +
                ", pwd='" + pwd + '\'' +
                ", photo='" + photo + '\'' +
                ", msg='" + msg + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_id);
        dest.writeString(username);
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(easemod_id);
        dest.writeString(uuid);
        dest.writeString(pwd);
        dest.writeString(photo);
        dest.writeString(msg);
        dest.writeString(time);
    }

    public static final Parcelable.Creator<ChatAccount> CREATOR = new Creator<ChatAccount>() {
        @Override
        public ChatAccount createFromParcel(Parcel source) {
            ChatAccount account = new ChatAccount();
            account.setUser_id(source.readString());
            account.setUsername(source.readString());
            account.setName(source.readString());
            account.setPhone(source.readString());
            account.setEasemod_id(source.readString());
            account.setUuid(source.readString());
            account.setPwd(source.readString());
            account.setPhoto(source.readString());
            account.setMsg(source.readString());
            account.setTime(source.readString());
            return account;
        }

        @Override
        public ChatAccount[] newArray(int size) {
            return new ChatAccount[size];
        }
    };
}
