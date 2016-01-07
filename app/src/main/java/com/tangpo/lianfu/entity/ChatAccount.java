package com.tangpo.lianfu.entity;

/**
 * Created by 果冻 on 2016/1/5.
 */
public class ChatAccount {
    private String user_id;
    private String easemod_id;
    private String uuid;
    private String pwd;
    private String photo;

    public ChatAccount() {
    }

    public ChatAccount(String user_id, String easemod_id, String uuid, String pwd, String photo) {
        this.user_id = user_id;
        this.easemod_id = easemod_id;
        this.uuid = uuid;
        this.pwd = pwd;
        this.photo = photo;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    @Override
    public String toString() {
        return "ChatAccount{" +
                "user_id='" + user_id + '\'' +
                ", easemod_id='" + easemod_id + '\'' +
                ", uuid='" + uuid + '\'' +
                ", pwd='" + pwd + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }
}
