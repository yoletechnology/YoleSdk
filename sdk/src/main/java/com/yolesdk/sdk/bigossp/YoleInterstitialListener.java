package com.yolesdk.sdk.bigossp;

import androidx.annotation.NonNull;

import sg.bigo.ads.api.AdError;

public interface YoleInterstitialListener {
    void onAdError(int code,String message);

    void onAdImpression();

    void onAdClicked();

    void onAdOpened();

    void onAdClosed();
}
