package com.icc.cardiograph.mvp.presenter;

import android.app.Activity;
import android.util.Log;

import com.icc.cardiograph.BuildConfig;
import com.icc.cardiograph.R;
import com.icc.cardiograph.api.UserApiImpl;
import com.icc.cardiograph.constance.Constance;
import com.icc.cardiograph.entity.LoginEntity;
import com.icc.cardiograph.mvp.contract.LoginContract;
import com.icc.cardiograph.util.IMEUtil;
import com.icc.cardiograph.util.Util;
import com.icc.cardiograph.util.rxjava.RxSchedulers;

import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created by TMVPHelper on 2016/10/20
 */
public class LoginPresenter extends LoginContract.Presenter{

    @Override
    public void login(Activity context, String username, String password) {
//        mModel.login(context, username, password, new LoginContract.OnLoginListener() {
//            @Override
//            public void loginSuccess(String key) {
//                mView.showMsg("登录成功");
//                mView.loginSuccess(key);
//            }
//
//            @Override
//            public void loginFail() {
//                mView.showMsg("登录失败");
//            }
//        });

//        mCompositeSubscription.add(mModel
//                .login(context, username, password)
//                .compose(RxSchedulers.io_main())
//                .subscribe(
//                        // onNext
//                        loginEntity -> {
//                            if(loginEntity.getResult().equals(Constance.RESULT_OK)) {
//                                mView.showMsg("登录成功");
//                                mView.loginSuccess(loginEntity.getKey());
//                            }else {
//                                mView.showMsg("登录失败");
//                            }
//                        },
//                        // onError
//                        throwable -> mView.showMsg("登录失败"),
//                        // onCompleted
//                        () ->{
//                        }
//                ));

        Subscriber loginSubscriber = new Subscriber<LoginEntity>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mView.showMsg("登录失败");
            }

            @Override
            public void onNext(LoginEntity loginEntity) {
                Log.d("yjx",loginEntity.getResult());
                if(loginEntity.getResult().equals(Constance.RESULT_OK)) {
                    mView.showMsg("登录成功");
                    mView.loginSuccess(loginEntity.getKey());
                }else {
                    mView.showMsg("登录失败");
                }
            }
        };
//
//        mCompositeSubscription.add(mModel
//                .login(context, username, password)
//                .subscribe(loginSubscriber));

        if (isInputValid(context, username, password)) {
            IMEUtil.hideIme(context);
            mCompositeSubscription.add(UserApiImpl.getInstance()
                    .login(BuildConfig.URL_SERVER, "auth", username, password)
                    .subscribe(loginSubscriber));
        }

    }

    private boolean isInputValid(Activity context, String phone, String password) {// 输入检查
        if (phone.length() == 0) {// 手机号码是否为11为
            Util.toastCenter(context ,"请输入用户名");
            return false;
        } else if (password.length() < 6 || password.length() > 20) {// 密码长度6到20位
            Util.toastCenter(context, R.string.PW_illegal_content1);
            return false;
        }
        return true;
    }
}