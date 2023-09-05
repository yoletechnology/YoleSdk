package com.yolesdk.sdk.data.init;

public class YoleInitBigosspConfig {

    private String bannerAdId="";
    private String interstitialAdId="";
    private String rewardAdId="";
    public YoleInitBigosspConfig() {
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

    public YoleInitBigosspConfig setBannerAdId(String val) {
        this.bannerAdId = val;
        return this;
    }
    public YoleInitBigosspConfig setInterstitialAdId(String val) {
        this.interstitialAdId = val;
        return this;
    }
    public YoleInitBigosspConfig setRewardAdId(String val) {
        this.rewardAdId = val;
        return this;
    }
}
