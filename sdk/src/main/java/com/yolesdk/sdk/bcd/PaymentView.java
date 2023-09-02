
package com.yolesdk.sdk.bcd;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yolesdk.sdk.YoleSdkMgr;
import com.yolesdk.sdk.tool.Tool;
import com.yolesdk.sdk.R;

public class PaymentView extends Activity {
    public String TAG = "Yole_PaymentView";
    public Activity m_activity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_activity = this;
        //背景透明处理
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setDimAmount(0f);

        this.setContentView(R.layout.payment_view);
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(android.R.color.white));

        String appName = YoleSdkMgr.getsInstance().user.initSdkData.productName;
//        Drawable icon = YouleSdkMgr.getsInstance().user.getIcon();
        String icon = YoleSdkMgr.getsInstance().user.initSdkData.productIcon;
        String cpCode = YoleSdkMgr.getsInstance().user.getCpCode();
        String amount = YoleSdkMgr.getsInstance().user.getAmount();
        String currencySymbol = YoleSdkMgr.getsInstance().user.initSdkData.currencySymbol;
        String orderNumber =  YoleSdkMgr.getsInstance().user.getPayOrderNum();

        ((TextView) findViewById(R.id.name)).setText(appName);
        ((TextView) findViewById(R.id.pric)).setText(currencySymbol+" "+amount);
        ((TextView) findViewById(R.id.der)).setText(m_activity.getString(R.string.order)+"："+orderNumber);
        ((TextView) findViewById(R.id.phonetable)).setText(m_activity.getString(R.string.valid_phone_number)+"");
        ((TextView) findViewById(R.id.buy)).setText(m_activity.getString(R.string.payment)+"");


        //获取网络图片的URL
        Glide.with(this).load(icon).into(((ImageView) findViewById(R.id.iv_ing)));

        EditText phoneEdit = findViewById(R.id.phone);
        phoneEdit.setText(YoleSdkMgr.getsInstance().user.getPhoneNumber());
        phoneEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                YoleSdkMgr.getsInstance().user.setPhoneNumber(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });




        Button back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_activity.finish();
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        YoleSdkMgr.getsInstance().user.getPayCallBack().onCallBack(false,m_activity.getString(R.string.cancel)+"","");
                    }
                }).start();

            }
        });

        Button buy = findViewById(R.id.buy);
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phonenumber = YoleSdkMgr.getsInstance().user.getPhoneNumber();
                if (!Tool.isMatchered(Tool.PHONE, phonenumber)){
                    Toast.makeText(m_activity,m_activity.getString(R.string.format_error)+"",Toast.LENGTH_LONG).show();
                }
                else
                {
                    m_activity.finish();
                    YoleSdkMgr.getsInstance().createDCBInvoiceBySdk();

                }

            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}