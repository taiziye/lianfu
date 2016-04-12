package com.tangpo.lianfu.utils;

import com.tangpo.lianfu.entity.ChatAccount;

import java.util.List;
import java.util.Map;

/**
 * Created by 果冻 on 2016/4/11.
 */
public class UserDao {
    public static final String TABLE_NAME = "friends";

    public static final String TABLE_ID = "table_id";
    public static final String ID = "id";
    public static final String USERNAME = "username";
    public static final String NAME = "name";
    public static final String PHONE = "phone";
    public static final String EASEMOD_ID = "easemod_id";
    public static final String UUID = "uuid";
    public static final String PHOTO = "photo";

    public UserDao() {
    }

    public void saveContactList(List<ChatAccount> users) {
        DBManager.getInstance().saveContactList(users);
    }

    public Map<String, ChatAccount> getContactList() {
        return DBManager.getInstance().getContactList();
    }

    public void deleteContact(String username) {
        DBManager.getInstance().deleteContact(username);
    }

    public void saveContact(ChatAccount user) {
        DBManager.getInstance().saveContact(user);
    }
}
