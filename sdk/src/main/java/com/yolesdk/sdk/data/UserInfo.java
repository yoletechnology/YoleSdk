package com.yolesdk.sdk.data;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.yolesdk.sdk.tool.PhoneInfo;
import com.yolesdk.sdk.YoleSdkMgr;
import com.yolesdk.sdk.callback.CallBackFunction;

import org.json.JSONException;
import org.json.JSONObject;

public class UserInfo {
    private static Context act;
    private String TAG = "Yole_UserInfo";
    private static PhoneInfo info =  null;
    private static PayInfo payInfo =  null;
    private static String appkey = "";
    private static String cpCode = "";
    private static String phoneNumber = "";//手机号
    private static String payOrderNum = "";//支付的订单号
    private static String amount = "";
    private static CallBackFunction backFunc = null;
    private static boolean isDebugger = false;
    public static InitSdkData initSdkData = null;

    public UserInfo(Context var1, String _appkey, String _cpCode,boolean debugger)
    {
        act = var1;
        appkey = _appkey;
        cpCode = _cpCode;
        isDebugger = debugger;
        info = new PhoneInfo(act);
        Log.d(TAG, "NetUtil init:appkey="+appkey+"appkey="+cpCode);
    }

    public static String getCpCode()
    {
        if(isDebugger == true)
        {
            return YoleSdkDefaultValue.Demo_CpCode;
        }
        return cpCode;
    }
    public static String getAppkey()
    {
        if(isDebugger == true)
        {
            return YoleSdkDefaultValue.Demo_Appkey;
        }
        return appkey;
    }
    public static String getMcc()
    {
        if(isDebugger == true)
        {
            return info.mcc_network == "" ? info.mcc_sim : info.mcc_network;
        }
        return info.mcc_network;
    }
    public static String getMnc()
    {
        if(isDebugger == true)
        {
            return info.mnc_network == "" ? info.mnc_sim : info.mnc_network;
        }
        return info.mnc_network;
    }
    public static String getCountryCode()
    {
        if(isDebugger == true)
        {
            return YoleSdkDefaultValue.Demo_CountryCode;
        }
        return info.countryCode;
    }
    public static String getImei()
    {
        return info.imei;
    }
    public static String getMac()
    {
        return info.mac;
    }
    public static String getPackageName()
    {
        return info.packageName;
    }
    public static String getAppName()
    {
        return info.appName;
    }
    public static Drawable getIcon()
    {
        return info.icon;
    }
    public static String getVersionName()
    {
        return info.VersionName;
    }
    public static String getPhoneModel()
    {
        return info.phoneModel.replace(" ","-");
    }
    public static String getGaid()
    {
        return info.gaid;
    }
    public static String getPhoneNumber()
    {
        if(isDebugger == true && phoneNumber.length() <= 0)
        {
                return YoleSdkDefaultValue.Demo_PhoneNumber;
        }
        return phoneNumber;
    }
    public static void setPhoneNumber(String phone)
    {
        phoneNumber = phone;
    }
    public static String getPayOrderNum()
    {
        return payOrderNum;
    }
    public static void setPayOrderNum(String orderNum)
    {
        payOrderNum = orderNum;
    }
    public static String getAmount()
    {
        return amount;
    }
    public static void setAmount(String value)
    {
        amount = value;
    }
    public static void setPayCallBack(CallBackFunction value)
    {
        backFunc = value;
    }
    public static CallBackFunction getPayCallBack()
    {
        return backFunc;
    }
    public static void decodePaymentResults(String res)
    {
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
    public static void decodeInitAppBySdk(String res)
    {

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
            }

        } catch (JSONException e) {
            e.printStackTrace();
            YoleSdkMgr.getsInstance().initBack.fail(e.toString());
        }
    }

    public static PayInfo getPayInfo()
    {
        return payInfo;
    }
    public static void setPayInfo(PayInfo payment)
    {
        payInfo = payment;
    }
}
