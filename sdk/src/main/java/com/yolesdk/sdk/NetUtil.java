package com.yolesdk.sdk;

import android.util.Log;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetUtil{
    public String TAG = "Yole_NetUtil";

    NetUtil()
    {
        Log.d(TAG, TAG);
    }
    public  String sendPost(String url,JSONObject formBody) {
        url = "https://api.yolesdk.com/"+url;
        //创建OkHttp客户端
        OkHttpClient okHttpClient = new OkHttpClient();

        // 封装请求体
        MediaType mediaType = MediaType.parse("application/json");


        //创建请求对象
        RequestBody requestBody = RequestBody.create(mediaType, formBody.toString());

        Log.d(TAG, "appkey:"+YoleSdkMgr.getsInstance().user.getAppkey());
        Log.d(TAG, "url:"+url);
        Log.d(TAG, "FormBody:"+formBody.toString());
        Log.d(TAG, "RequestBody:"+requestBody.toString());
        Request request = new Request.Builder()
                .url(url)
                .addHeader("appkey",YoleSdkMgr.getsInstance().user.getAppkey())
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build();

        try {
            // 执行这个请求对象
            Response response = okHttpClient.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG, "onResponse2:"+result);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return e.toString();
        }
    }
    public void initAppBySdk(String mobile,String gaid,String userAgent,String imei,String mac,String countryCode,String mcc,String mnc,String cpCode) throws Exception {

        JSONObject formBody = new JSONObject ();
        if(mobile.length() > 0)
            formBody.put("mobile",mobile);
        if(gaid.length() > 0)
            formBody.put("gaid",gaid);
        if(userAgent.length() > 0)
            formBody.put("userAgent",userAgent);
        if(imei.length() > 0)
            formBody.put("imei",imei);
        if(mac.length() > 0)
            formBody.put("mac",mac);
        if(countryCode.length() > 0)
            formBody.put("countryCode",countryCode);
        if(mcc.length() > 0)
            formBody.put("mcc",mcc);
        if(mnc.length() > 0)
            formBody.put("mnc",mnc);
        if(cpCode.length() > 0)
            formBody.put("cpCode",cpCode);

        String res = this.sendPost("api/user/initAppBySdk",formBody);
        Log.d(TAG, "initAppBySdk"+res);
        YoleSdkMgr.getsInstance().user.decodeInitAppBySdk(res);
    }
    public void createDCBInvoiceBySdk(String cpCode,String mobile,String amount,String countryCode,String mcc,String mnc,String orderNumber) throws Exception {

        JSONObject formBody = new JSONObject ();
        if(cpCode.length() > 0)
            formBody.put("cpCode",cpCode);
        if(mobile.length() > 0)
            formBody.put("mobile",mobile);
        if(amount.length() > 0)
            formBody.put("amount",amount);
        if(countryCode.length() > 0)
            formBody.put("countryCode",countryCode);
        if(mcc.length() > 0)
            formBody.put("mcc",mcc);
        if(mnc.length() > 0)
            formBody.put("mnc",mnc);
        if(orderNumber.length() > 0)
            formBody.put("orderNumber",orderNumber);

        String res = this.sendPost("api/RUPayment/createDCBInvoiceBySdk",formBody);
        Log.d(TAG, "createDCBInvoiceBySdk"+res);
        YoleSdkMgr.getsInstance().user.decodePaymentResults(res);
    }
    public static String serializeMetadata(HashMap<String, String> metadata) throws IOException {
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(metadata);
            return baos.toString();
        }
    }
    public String toString(Set<Map.Entry<String,String>> entrySet) {
        Iterator<Map.Entry<String,String>> i = entrySet.iterator();
        if (! i.hasNext())
            return "{}";

        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (;;) {
            Map.Entry<String,String> e = i.next();
            String key = e.getKey();
            String value = e.getValue();
            sb.append(""+key+"");
            sb.append('=');
            sb.append(""+value+"");
            if (! i.hasNext())
                return sb.append('}').toString();
            sb.append(',').append(' ');
        }
    }

}
