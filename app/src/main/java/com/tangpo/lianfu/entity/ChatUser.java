package com.tangpo.lianfu.entity;

/**
 * Created by 果冻 on 2016/1/18.
 */
public class ChatUser {
    public static final String ID = "id";
    public static final String EASEMOD_ID = "easemod_id";
    public static final String USERNAME = "username";

    private String easemod_id;
    private String username;

    public ChatUser() {
    }

    public ChatUser(String easemod_id, String username) {
        this.easemod_id = easemod_id;
        this.username = username;
    }

    public String getEasemod_id() {
        return easemod_id;
    }

    public void setEasemod_id(String easemod_id) {
        this.easemod_id = easemod_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "ChatUser{" +
                "easemod_id='" + easemod_id + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
