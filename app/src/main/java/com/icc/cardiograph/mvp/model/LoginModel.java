package com.icc.cardiograph.mvp.model;

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

import rx.Observable;
import rx.Subscriber;

/**
 * Created by TMVPHelper on 2016/10/20
 */
public class LoginModel implements LoginContract.Model {

    private LoginContract.OnLoginListener onLoginListener;

    @Override
    public void login(Activity context, String username, String password, LoginContract.OnLoginListener onLoginListener) {
        this.onLoginListener = onLoginListener;
        if (isInputValid(context, username, password)) {
            IMEUtil.hideIme(context);
            startLogin(username, password);
//			Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			intent.putExtra("userName", phone);
//			startActivity(intent);
//			recordUserName();
//			finish();
        }
    }

    @Override
    public Observable<LoginEntity> login(Activity context, String username, String password) {
        if (isInputValid(context, username, password)) {
            IMEUtil.hideIme(context);
            return UserApiImpl.getInstance().login(BuildConfig.URL_SERVER, "auth", username, password);
        }
        return null;
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

    /**
     * 登录操作
     *
     */
    private void startLogin(String phone, String password) {
//        List<NameValuePair> nvpList = new ArrayList<NameValuePair>();
//        nvpList.add(new BasicNameValuePair("action", "auth"));
//        nvpList.add(new BasicNameValuePair("username", phone));
//        nvpList.add(new BasicNameValuePair("password", password));
//        new RequestTask(BuildConfig.URL_SERVER, Constance.POST, nvpList, new RequestTask.ResponseCallBack() {
//            @Override
//            public void onExecuteResult(String response) {
//                try {
//                    Log.d("yjx",response);
//                    JSONObject jsonObject = new JSONObject(response);
//                    String result = jsonObject.getString("result");
//                    if(result.equals(Constance.RESULT_OK)){
//                        String key = jsonObject.getString("key");
//                        onLoginListener.loginSuccess(key);
//                    }else{
//                        onLoginListener.loginFail();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).execute();
        final Subscriber<LoginEntity> loginSubscriber = new Subscriber<LoginEntity>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Log.e("icc", "onError", e);
                onLoginListener.loginFail();
            }

            @Override
            public void onNext(LoginEntity loginEntity) {
                try {
                    Log.d("yjx",loginEntity.getResult());
                    String result = loginEntity.getResult();
                    if(result.equals(Constance.RESULT_OK)){
                        String key = loginEntity.getKey();
                        onLoginListener.loginSuccess(key);
                    }else{
                        onLoginListener.loginFail();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        UserApiImpl.getInstance().login(BuildConfig.URL_SERVER, "auth", phone, password, loginSubscriber);
    }
}