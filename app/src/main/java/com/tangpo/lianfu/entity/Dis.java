package com.tangpo.lianfu.entity;

import java.io.Serializable;

/**
 * Created by 果冻 on 2016/1/3.
 * 可选折扣类
 */
public class Dis implements Serializable {
    private String agio;  //折扣
    private String typename;  //折扣类型

    public String getAgio() {
        return agio;
    }

    public void setAgio(String agio) {
        this.agio = agio;
    }

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public Dis(String agio, String typename) {

        this.agio = agio;
        this.typename = typename;
    }

    public Dis() {

    }
}
