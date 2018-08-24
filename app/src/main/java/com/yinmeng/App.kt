package com.yinmeng

import android.app.Application

import org.xutils.x


class App : Application() {
    // 在application的onCreate中初始化
    override fun onCreate() {
        super.onCreate()
        x.Ext.init(this)
        // x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.
    }
}
