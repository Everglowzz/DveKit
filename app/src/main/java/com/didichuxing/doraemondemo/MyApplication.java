package com.didichuxing.doraemondemo;

import android.app.Application;
import android.os.StrictMode;

import com.didichuxing.doraemonkit.DoraemonKit;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by zhangweida on 2018/6/22.
 */

public class MyApplication extends Application {
    private static final String TAG = "App";

    @Override
    public void onCreate() {
        super.onCreate();
        DoraemonKit.install(this);
//        DoraemonKit.setWebDoorCallback(new WebDoorManager.WebDoorCallback() {
//            @Override
//            public void overrideUrlLoading(Context context, String url) {
//                Intent intent = new Intent(App.this, WebViewActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.putExtra(WebViewActivity.KEY_URL, url);
//                startActivity(intent);
//            }
//        });
        StrictMode.enableDefaults();
        APP = this;
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }

    public static  MyApplication APP;
    public static MyApplication getApplication() {
        return APP;
    }

}