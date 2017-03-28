package com.icc.cardiograph.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import com.icc.cardiograph.util.JumpUtil;
import com.icc.cardiograph.util.ToastUtil;
import com.icc.cardiograph.util.rxjava.RxjavaUtil;
import com.icc.cardiograph.util.rxjava.bean.UITask;

import butterknife.ButterKnife;

/**
 * Activity基类
 */
public abstract class BaseActivity<T extends BasePresenter, E extends BaseModel> extends Activity implements JumpUtil.JumpInterface {
    public T mPresenter;
    public E mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    protected void init() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(getLayoutResID());
        mPresenter = TUtil.getT(this, 0);
        mModel = TUtil.getT(this, 1);
        if (this instanceof BaseView) mPresenter.setVM(this, mModel);
        ButterKnife.bind(this);
        initView();
    }

    /**
     * 获得Layout文件id
     *
     * @return
     */
    protected abstract int getLayoutResID();


    protected abstract void initView();


    /**
     * 统一toast
     *
     * @return
     */
    public void msgToast(final String msg) {
        RxjavaUtil.doInUIThread(new UITask<String>(msg) {
            @Override
            public void doInUIThread() {
                ToastUtil.show(msg);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) mPresenter.onDestroy();
    }
}
