package com.icc.cardiograph.http;

/**
 * Description:
 * Author: liguosun
 * Produced: 16/7/21 下午7:47
 */
public class ApiException extends RuntimeException {

    private String resultCode = "";
    private String resultMsg = "";

    public ApiException(String resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    public String getResultCode() {
        return resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

}
