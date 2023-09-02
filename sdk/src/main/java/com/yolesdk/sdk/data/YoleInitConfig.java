package com.yolesdk.sdk.data;

import java.util.HashMap;
import java.util.Map;

public class YoleInitConfig {
    private String appId;
    private String appKey;
    private String cpCode;
    private boolean debug;
    private String userAgent;
    private String mobile;
    private String bannerAdId;
    private String interstitialAdId;
    private String rewardAdId;

    private final Map<String, String> extra = new HashMap();

     public YoleInitConfig() {
        this.extra.clear();
    }
    public String getAppId() {
        return this.appId;
    }
    public String getAppKey() {
        return this.appKey;
    }
    public String getCpCode() {
        return this.cpCode;
    }
    public boolean isDebug() {
        return this.debug;
    }
    public String getUserAgent() {
        return this.userAgent;
    }
    public String getMobile() {
        return this.mobile;
    }
    public String getBannerAdId() {
        return this.bannerAdId;
    }
    public String getInterstitialAdId() {
        return this.interstitialAdId;
    }
    public String getRewardAdId() {
        return this.rewardAdId;
    }

    public  YoleInitConfig setAppId(String var1) {
        this.appId = var1;
        return this;
    }
    public  YoleInitConfig setAppKey(String var1) {
        this.appKey = var1;
        return this;
    }
    public  YoleInitConfig setCpCode(String var1) {
        this.cpCode = var1;
        return this;
    }
    public  YoleInitConfig setDebug(boolean var1) {
        this.debug = var1;
        return this;
    }
    public  YoleInitConfig setUserAgent(String var1) {
        this.userAgent = var1;
        return this;
    }
    public  YoleInitConfig setMobile(String var1) {
        this.mobile = var1;
        return this;
    }
    public  YoleInitConfig setBannerAdId(String var1) {
        this.bannerAdId = var1;
        return this;
    }
    public  YoleInitConfig setInterstitialAdId(String var1) {
        this.interstitialAdId = var1;
        return this;
    }
    public  YoleInitConfig setRewardAdId(String var1) {
        this.rewardAdId = var1;
        return this;
    }

}

