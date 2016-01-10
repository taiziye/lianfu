package com.tangpo.lianfu.entity;

/**
 * Created by 果冻 on 2016/1/10.
 */
public class Chat {
    private String hxid;
    private String username;
    private String img;
    private String msg;
    private String time;

    public Chat() {
    }

    public Chat(String hxid, String username, String img, String msg, String time) {
        this.hxid = hxid;
        this.username = username;
        this.img = img;
        this.msg = msg;
        this.time = time;
    }

    public String getHxid() {
        return hxid;
    }

    public void setHxid(String hxid) {
        this.hxid = hxid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
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
        return "Chat{" +
                "hxid='" + hxid + '\'' +
                ", username='" + username + '\'' +
                ", img='" + img + '\'' +
                ", msg='" + msg + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
