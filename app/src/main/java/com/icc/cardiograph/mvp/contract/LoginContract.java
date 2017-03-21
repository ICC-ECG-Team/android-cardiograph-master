package com.icc.cardiograph.mvp.contract;

import android.app.Activity;

import com.icc.cardiograph.base.BaseModel;
import com.icc.cardiograph.base.BasePresenter;
import com.icc.cardiograph.base.BaseView;

/**
 * 登录契约类
 * Created by z2wenfa on 2016/9/1.
 */
public interface LoginContract {

    interface View extends BaseView {
        void loginSuccess(String key);

        void showMsg(String msg);
    }

    interface Model extends BaseModel {
        void login(Activity context, String username, String password, OnLoginListener onLoginListenner);
    }

    abstract class Presenter extends BasePresenter<Model, View> {

        @Override
        public void onStart() {

        }

        public abstract void login(Activity context, String username, String password);
    }

    interface OnLoginListener {
        void loginSuccess(String key);

        void loginFail();
    }
}