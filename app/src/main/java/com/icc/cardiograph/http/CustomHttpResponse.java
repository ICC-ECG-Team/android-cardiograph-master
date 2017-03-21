package com.icc.cardiograph.http;

/**
 * 用来统一处理请求响应的封装类
 * @param <T> - 响应中data部分数据类型
 * Created by yejinxin on 16/7/21.
 */
public class CustomHttpResponse<T> {

    /**
     * 结果状态码
     */
    private String result;

    /**
     * 响应数据
     */
    private T data;

    public T getData() {
        return data;
    }

}
