package com.icc.cardiograph.base;

import android.app.Activity;
import android.app.Application;

import com.icc.cardiograph.util.JumpUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * 全局应用程序上下文
 * @author yejinxin
 */
public class ApplicationEx extends Application {
	
	private static ApplicationEx instance;
	private List<Activity> activities = new LinkedList<Activity>();

	public static ApplicationEx getInstance() {
        return instance;
    }
    
    /**
     * 添加activity
     * @param activity
     */
    public void addActivity(Activity activity) {
		activities.add(activity);
	}
    
    /**
     * 移除activity
     * @param activity
     */
    public void removeActivity(Activity activity) {
    	if (activities.contains(activity)) {
    		activities.remove(activity);
		}
	}

    /**
     * 退出应用，关闭堆栈中Activity
     */
    public void exit() {
		for (Activity activity : activities) {
			activity.finish();
		}
		System.exit(0);
	}
    	
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
		JumpUtil.init(instance);
    }
    
    /**
     * 当系统内存过低时的事件
     */
	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	/**
	 * 程序退出时的事件
	 */
	@Override
	public void onTerminate() {
		super.onTerminate();
	}
	
	public Activity getCurActivity(){
		return activities.get(activities.size()-1);
	}
}
