package com.yolesdk.sdk;

public class InitSdkData {
    public enum PayStatus {
        /**可用*/
        AVAILABLE,
        /**不可用*/
        UNAVAILABLE
    }
    /**用户编码*/
    public String userCode = "";
    /**商品名称*/
    public String productName = "";
    /**商品图标*/
    public String productIcon = "";
    /**公司名称*/
    public String companyName = "";
    /**货币符号*/
    public String currencySymbol = "";
    /**支持小数点位数*/
    public int currencyDecimal = 0;
    /**DCB支付状态*/
    public PayStatus dcbSmsPayStatus = PayStatus.AVAILABLE;
}
