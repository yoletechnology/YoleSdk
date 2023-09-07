package com.yolesdk.sdk;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yolesdk.sdk.dcb.PaymentView;
import com.yolesdk.sdk.callback.CallBackFunction;

public class YoleSdkMgr extends YoleSdkBase{

    private String TAG = "Yole_YoleSdkMgr";
    private static  YoleSdkMgr _instance = null;
    public String ruPayOrderNum = "";

    public static YoleSdkMgr getsInstance() {
        if(YoleSdkMgr._instance == null)
        {
            YoleSdkMgr._instance = new YoleSdkMgr();
        }
        return YoleSdkMgr._instance;
    }
    private YoleSdkMgr() {
        Log.e(TAG,"YoleSdkMgr");
    }

    /** bcd支付*/
    public void bcdStartPay(Activity act, String amount, String orderNumber, CallBackFunction backFunction)
    {
        if(user.getConfig().isDcb() == false)
        {
            if(isDebugger)
                Toast.makeText(act, "sdk初始化时，未接入Dcb模块", Toast.LENGTH_SHORT).show();
            backFunction.onCallBack(false,"sdk初始化时，未接入Dcb模块","");
            return;
        }

        user.setAmount(amount);
        user.setPayOrderNum(orderNumber);
        user.setPayCallBack(new CallBackFunction(){
            @Override
            public void onCallBack(boolean data,String info,String billingNumber) {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LoadingDialog.getInstance(act).hideDialog();
                        if(data == true){
                        }else{
                        }
                        if(isDebugger)
                            Toast.makeText(act, act.getString(R.string.results)+":"+info, Toast.LENGTH_SHORT).show();
                        backFunction.onCallBack(data,info,billingNumber);
                    }
                });
            }
        });

        if(this.getBCDFeasibility(act) == false)
        {
            YoleSdkMgr.getsInstance().user.getPayCallBack().onCallBack(false,act.getString(R.string.parameter_error),"");
            return;
        }


        Intent i =new Intent(act, PaymentView.class);
        act.startActivity(i);
    }
    public void createDCBInvoiceBySdk() {

        LoadingDialog.getInstance(context).show();//显示

        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    request.createDCBInvoiceBySdk(
                            user.getCpCode(),
                            user.getPhoneNumber(),
                            user.getAmount(),
                            user.getCountryCode(),
                            user.getMcc(),
                            user.getMnc(),
                            user.getPayOrderNum());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    /*****************************************************************/
    /************************SMS 支付*********************************/
    /*****************************************************************/


    /**sms模块的权限注册*/
    public void  smsRequest(Activity var1) {

        sms.smsRequest(var1);
    }
    /**sms支付开始 设置回调，显示loading界面***/
    public void  smsStartPay(Activity var1,String _payOrderNum,CallBackFunction callBack) {
        ruPayOrderNum = _payOrderNum;
        LoadingDialog.getInstance(var1).show();//显示
        user.setPayCallBack(new CallBackFunction(){
            @Override
            public void onCallBack(boolean data, String info, String billingNumber) {
                LoadingDialog.getInstance(var1).hide();//显示
                callBack.onCallBack(data,info,billingNumber);
                smsPaymentNotify(data);
            }
        });
        this.paySdkStartPay();
    }
    /**sms支付开始 发送短信功能***/
    private void paySdkStartPay()
    {
        sms.sendSMSS(user.getSmsCode(),user.getSmsNumber());
    }
    /**sms支付完成 同步服务器结果***/
    public void smsPaymentNotify(boolean  paymentStatus)
    {
        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    request.smsPaymentNotify(ruPayOrderNum,paymentStatus == true ? "SUCCESSFUL" : "FAILED");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


}
