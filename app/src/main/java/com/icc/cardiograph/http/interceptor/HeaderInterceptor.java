package com.icc.cardiograph.http.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 统一添加请求header，cookie不需要在这里加，cookiejar进行统一管理cookie
 */
public class HeaderInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Response response;
        Request request = original.newBuilder()
                .header("User-Agent", "")
                //.header("Accept", "Application/JSON")
                .method(original.method(), original.body())
                .build();
        // try the request
        response = chain.proceed(request);
        return response;
    }

}