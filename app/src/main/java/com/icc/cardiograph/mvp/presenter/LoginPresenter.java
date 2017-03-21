package com.icc.cardiograph.mvp.presenter;

import android.app.Activity;

import com.icc.cardiograph.mvp.contract.LoginContract;

/**
* Created by TMVPHelper on 2016/10/20
*/
public class LoginPresenter extends LoginContract.Presenter{

    @Override
    public void login(Activity context, String username, String password) {
        mModel.login(context, username, password, new LoginContract.OnLoginListener() {
            @Override
            public void loginSuccess(String key) {
                mView.showMsg("登陆成功");
                mView.loginSuccess(key);
            }

            @Override
            public void loginFail() {
                mView.showMsg("登陆失败");
            }
        });
    }
}