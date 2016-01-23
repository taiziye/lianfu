package com.tangpo.lianfu.entity;

/**
 * Created by 果冻 on 2016/1/23.
 */
public class HXUser {
    private String user_id;
    private String username;
    private String name;
    private String phone;
    private String easemod_id;
    private String uuid;
    private String pwd;
    private String photo;

    public HXUser() {
    }

    public HXUser(String user_id, String username, String name, String phone, String easemod_id, String uuid, String pwd, String photo) {
        this.user_id = user_id;
        this.username = username;
        this.name = name;
        this.phone = phone;
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
}
