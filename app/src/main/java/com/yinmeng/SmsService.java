package com.yinmeng;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * sakura.phonetransfer
 *
 * @author 赵磊
 * @date 2018/5/22
 * 功能描述：
 */
public class SmsService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
