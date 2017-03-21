package com.icc.cardiograph.entity;

/**
 * 登录结果实体类
 * Created by yejinxin on 2017/3/17 17:10.
 */
public class LoginEntity {
    private String result;//结果码
    private String key;//密钥

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
