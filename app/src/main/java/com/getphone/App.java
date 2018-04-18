package com.getphone;

import android.app.Application;

import org.xutils.x;

/**
 * com.getphone
 *
 * @author 赵磊
 * @date 2018/4/16
 * 功能描述：
 */
public class App extends Application {
    // 在application的onCreate中初始化
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.


    }
}
