package com.icc.cardiograph.api;

import com.icc.cardiograph.entity.LoginEntity;
import com.icc.cardiograph.http.CustomResponseHandler;
import com.icc.cardiograph.http.ResponseHandler;
import com.icc.cardiograph.util.RetrofitUtils;

import rx.Subscriber;

/**
 * Created by yejinxin on 2017/3/17 17:19.
 */

public class UserApiImpl {

    private UserApi userApi;

    public UserApiImpl() {
        userApi = RetrofitUtils.getInstance().createRetrofit().create(UserApi.class);
    }

    public static UserApiImpl getInstance() {
        return new UserApiImpl();
    }

    public void login(String url, String action, String username, String password, Subscriber<LoginEntity> subscriber){
        userApi.login(url, action, username, password)
//                .map(new CustomResponseHandler<LoginEntity>())
                .compose(RetrofitUtils.getInstance().<LoginEntity>applySchedulers())
                .subscribe(subscriber);
    }
}
