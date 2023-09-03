package com.yolesdk.sdk.data;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.yolesdk.sdk.tool.PhoneInfo;
import com.yolesdk.sdk.YoleSdkMgr;
import com.yolesdk.sdk.callback.CallBackFunction;

import org.json.JSONException;
import org.json.JSONObject;

public class UserInfo extends UserInfoBase{
    private static String TAG = "Yole_UserInfo";

    public UserInfo(Context var1, YoleInitConfig _config)
    {
        act = var1;
        config = _config;
        info = new PhoneInfo(act);
        Log.d(TAG, "NetUtil init:appkey="+config.getAppKey()+"cpCode="+config.getCpCode());
    }
    public  String getSmsNumber(){return super.getSmsNumber();}
    public  String getSmsCode(){return super.getSmsCode();}
    public  String getCpCode(){return super.getCpCode();}
    public  String getAppkey(){return super.getAppkey();}
    public  String getMcc(){return super.getMcc();}
    public  String getMnc(){return super.getMnc();}
    public  String getCountryCode(){return super.getCountryCode();}
    public  String getImei()
    {
        return super.getImei();
    }
    public  String getMac()
    {
        return super.getMac();
    }
    public  String getPackageName()
    {
        return super.getPackageName();
    }
    public  String getAppName()
    {
        return super.getAppName();
    }
    public  Drawable getIcon()
    {
        return super.getIcon();
    }
    public  String getVersionName()
    {
        return super.getVersionName();
    }
    public  String getPhoneModel()
    {
        return super.getPhoneModel();
    }
    public  String getGaid()
    {
        return super.getGaid();
    }
    public  String getPhoneNumber(){return super.getPhoneNumber();}
    public  String getPayOrderNum()
    {
        return super.getPayOrderNum();
    }
    public  String getAmount()
    {
        return super.getAmount();
    }
    public  CallBackFunction getPayCallBack()
    {
        return super.getPayCallBack();
    }

    public  void decodePaymentResults(String res)
    {
        if(res.length() <= 0)
        {
            Log.e(TAG, "decodePaymentResults:"+res);
            if(backFunc != null)
                backFunc.onCallBack(false,"","");
            return;
        }
        if(backFunc != null)
        {
            try {
                JSONObject jsonObject = new JSONObject(res);
                String status = jsonObject.getString("status");
                String errorCode = jsonObject.getString("errorCode");
                String content = jsonObject.getString("content");
                String message = jsonObject.getString("message");

                if(status.indexOf("SUCCESS") ==  -1)
                {
                    backFunc.onCallBack(false,"errorCode:"+errorCode+";message:"+message,"");
                }
                else
                {
                    JSONObject contentJsonObject = new JSONObject(content);
                    String content1 = contentJsonObject.getString("content");
                    JSONObject content1JsonObject = new JSONObject(content1);
                    String billingNumber = content1JsonObject.getString("billingNumber");

                    backFunc.onCallBack(true,status,billingNumber);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                backFunc.onCallBack(false,e.toString(),"");
            }
        }
    }
    public  void decodeInitAppBySdk(String res)
    {
        if(res.length() <= 0)
        {
            Log.e(TAG, "decodeInitAppBySdk:"+res);
            YoleSdkMgr.getsInstance().initBack.fail("");
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(res);
            String status = jsonObject.getString("status");
            String errorCode = jsonObject.getString("errorCode");
            String content = jsonObject.getString("content");
            String message = jsonObject.getString("message");

            JSONObject contentJsonObject = new JSONObject(content);

            if(status.indexOf("SUCCESS") ==  -1)
            {
                YoleSdkMgr.getsInstance().initBack.fail(contentJsonObject.toString());
            }
            else
            {

                String userCode = contentJsonObject.getString("userCode");
                String productName = contentJsonObject.getString("productName");
                String productIcon = contentJsonObject.getString("productIcon");
                String companyName = contentJsonObject.getString("companyName");
                String currencySymbol = contentJsonObject.getString("currencySymbol");
//                int currencyDecimal = contentJsonObject.getInt("currencyDecimal");
                String dcbSmsPayStatus = contentJsonObject.getString("dcbSmsPayStatus");

                initSdkData = new InitSdkData();
                initSdkData.userCode = userCode;
                initSdkData.productName = productName;
                initSdkData.productIcon = productIcon;
                initSdkData.companyName = companyName;
                initSdkData.currencySymbol = currencySymbol;
//                initSdkData.currencyDecimal = currencyDecimal;
                if(dcbSmsPayStatus.indexOf("UNAVAILABLE") != -1){
                    initSdkData.dcbSmsPayStatus = InitSdkData.PayStatus.UNAVAILABLE;
                }else{
                    initSdkData.dcbSmsPayStatus = InitSdkData.PayStatus.AVAILABLE;
                }
                YoleSdkMgr.getsInstance().initBack.success(initSdkData);
                YoleSdkMgr.getsInstance().getPaymentSms();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            YoleSdkMgr.getsInstance().initBack.fail(e.toString());
        }
    }
    public  void getPaymentSms(String res)
    {
        if(res.length() <= 0)
        {
            Log.e(TAG, "getPaymentSms:"+res);
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(res);
            String status = jsonObject.getString("status");

            if(status.indexOf("SUCCESS") ==  -1)
            {
                Log.d(TAG, "getUserCode error:"+status);
            }
            else
            {
                String content = jsonObject.getString("content");
                Log.d(TAG, "content:"+content);
                JSONObject jsonObject1 = new JSONObject(content);

                String id = jsonObject1.getString("id");
                String paymentId = jsonObject1.getString("paymentId");
                String mnc = jsonObject1.getString("mnc");
                String smsNumber = jsonObject1.getString("smsNumber");
                String smsCode = jsonObject1.getString("smsCode");
                String smsPrice = jsonObject1.getString("smsPrice");
                String status1 = jsonObject1.getString("status");
                smsNumber = smsNumber;
                smsCode = smsCode;
                Log.d(TAG, "smsNumber:"+smsNumber +";smsCode:"+smsCode);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public  void smsPaymentNotify(String res)
    {
        if(res.length() <= 0)
        {
            Log.e(TAG, "smsPaymentNotify:"+res);
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(res);
            String status = jsonObject.getString("status");
            if(status.indexOf("SUCCESS") ==  -1)
            {
                Log.d(TAG, "getUserCode error:"+status);
            }
            else
            {
                String content = jsonObject.getString("content");
                Log.d(TAG, "content:"+content);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}
