package com.icc.cardiograph.http;

import com.icc.cardiograph.constance.Constance;

/**
 * 用来统一处理请求响应的封装类
 * @param <T> - 响应中data部分数据类型
 * Created by liguo on 16/7/21.
 */
public class HttpResponse<T> {

    /**
     * 结果状态码
     */
    private String result;

    /**
     * 出错信息
     */
    private String msg;

    /**
     * 响应数据
     */
    private T data;

    public boolean isSuccess() {
        return (Constance.RESULT_OK.equals(result));
    }

    public boolean isTimeOut() {
        return ("77777".equals(result));
    }

    public String getMsg() {
        return msg;
    }

    public String getResult() {
        return result;
    }

    public T getData() {
        return data;
    }

}
