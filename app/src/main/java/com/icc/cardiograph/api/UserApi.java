package com.icc.cardiograph.api;

import com.icc.cardiograph.entity.LoginEntity;
import com.icc.cardiograph.http.CustomHttpResponse;
import com.icc.cardiograph.http.HttpResponse;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by yejinxin on 2017/3/17 17:19.
 */

public interface UserApi {

    /**
     * 用户登录
     */
    @POST
    @FormUrlEncoded
    Observable<LoginEntity> login(@Url String url,
                                                      @Field("action") String action,
                                                      @Field("username") String username,
                                                      @Field("password") String password);
}
