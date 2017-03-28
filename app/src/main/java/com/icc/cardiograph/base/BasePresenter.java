package com.icc.cardiograph.base;

import android.content.Context;

import com.icc.cardiograph.util.rxbus.RxManager;

import rx.subscriptions.CompositeSubscription;

/**
 * T-MVP Presenter基类
 */
public abstract class BasePresenter<M, T> {
    public Context context;
    public M mModel;
    public T mView;
    public RxManager mRxManager = new RxManager();
    protected CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    public void setVM(T v, M m) {
        this.mView = v;
        this.mModel = m;
        this.onStart();

    }

    public abstract void onStart();

    public void onDestroy() {
        mRxManager.clear();
        mCompositeSubscription.unsubscribe();
    }
}
