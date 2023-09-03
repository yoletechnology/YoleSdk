package com.yolesdk.sdk.tool;

import android.util.Log;

import com.yolesdk.sdk.YoleSdkMgr;
import com.yolesdk.sdk.tool.NetUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class NetworkRequest {
    public String TAG = "Yole_NetworkRequest";
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

        String res = NetUtil.sendPost("api/user/initAppBySdk",formBody);
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

        String res = NetUtil.sendPost("api/RUPayment/createDCBInvoiceBySdk",formBody);
        Log.d(TAG, "createDCBInvoiceBySdk"+res);
        YoleSdkMgr.getsInstance().user.decodePaymentResults(res);
    }

    /**获取支付策略*/
    /**
     *
     * @param countryCode   国家码         CH
     */
    public void getPaymentSms(String countryCode) throws Exception {
        JSONObject formBody = new JSONObject ();
        if(countryCode.length() > 0)
            formBody.put("countryCode",countryCode);

        String res = NetUtil.sendPost("api/payment/getPaymentSms",formBody);
        Log.d(TAG, "getPaymentSms"+res);
        YoleSdkMgr.getsInstance().user.getPaymentSms(res);
    }

    public void smsPaymentNotify(String payOrderNum,String paymentStatus) throws Exception {
        JSONObject formBody = new JSONObject ();
        formBody.put("payOrderNum",payOrderNum);
        formBody.put("paymentStatus",paymentStatus);

        String res = NetUtil.sendPost("api/payment/smsPaymentNotify",formBody);
        Log.d(TAG, "smsPaymentNotify"+res);
        YoleSdkMgr.getsInstance().user.smsPaymentNotify(res);
    }
}
