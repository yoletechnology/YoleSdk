package com.yolesdk.sdk.bigossp;

import android.util.Log;

import androidx.annotation.NonNull;

import sg.bigo.ads.api.AdError;
import sg.bigo.ads.api.AdInteractionListener;
import sg.bigo.ads.api.AdLoadListener;
import sg.bigo.ads.api.BannerAd;
import sg.bigo.ads.api.BannerAdLoader;
import sg.bigo.ads.api.InterstitialAd;
import sg.bigo.ads.api.InterstitialAdLoader;
import sg.bigo.ads.api.InterstitialAdRequest;
import sg.bigo.ads.api.RewardAdInteractionListener;
import sg.bigo.ads.api.RewardVideoAd;
import sg.bigo.ads.api.RewardVideoAdLoader;
import sg.bigo.ads.api.RewardVideoAdRequest;

public class YoleRewardVideoAd {
    private String TAG = "Yole_YoleRewardVideoAd";
    RewardVideoAd mRewardVideoAd = null;
    RewardVideoAdRequest rewardVideoAdAdRequest = null;
    String defaultSlotId = "";
    YoleRewardVideoListener listener = null;
    public YoleRewardVideoAd(String _defaultSlotId)
    {
        defaultSlotId = _defaultSlotId;
        if(defaultSlotId.length() > 0)
        {
            rewardVideoAdAdRequest = new RewardVideoAdRequest.Builder()
                    .withSlotId(defaultSlotId)
                    .build();

            this.loadAd();
        }
    }
    public void showAd(String slotId,YoleRewardVideoListener _listener)
    {
        Log.i(TAG,"showAd");
        listener = _listener;
        if(rewardVideoAdAdRequest == null || slotId.length() >0)
        {
             rewardVideoAdAdRequest = new RewardVideoAdRequest.Builder()
                    .withSlotId(slotId)
                    .build();
        }

        if(mRewardVideoAd == null ||  mRewardVideoAd != null && mRewardVideoAd.isExpired() == true) {
            if( mRewardVideoAd != null && mRewardVideoAd.isExpired() == true)
            {
                this.destroyAd();
            }

            this.loadAd();
        }
        else
        {

            this.showAd();
        }

    }
    public void loadAd()
    {
        RewardVideoAdLoader rewardVideoAdAdLoader = new RewardVideoAdLoader.Builder().
                withAdLoadListener(new AdLoadListener<RewardVideoAd>() {
                    @Override
                    public void onError(@NonNull AdError error) {
                        // There's something wrong during ad loading
                        Log.i(TAG, "onAdLoaded onError");
                        if(listener != null)
                        {
                            listener.onAdError(error.getCode(),error.getMessage());
                        }
                    }
                    @Override
                    public void onAdLoaded(@NonNull RewardVideoAd ad) {
                        Log.i(TAG, "onAdLoaded success");
                        mRewardVideoAd = ad;
                        if(listener != null)
                        {
                            showAd();
                        }


                    }
                }).build();

        rewardVideoAdAdLoader.loadAd(rewardVideoAdAdRequest);
    }
    public void showAd()
    {
        mRewardVideoAd.setAdInteractionListener(new RewardAdInteractionListener() {
            @Override
            public void onAdError(@NonNull AdError error) {
                // There's something wrong when using this ad.
                if(listener != null)
                    listener.onAdError(error.getCode(),error.getMessage());
            }

            @Override
            public void onAdImpression() {
                // When the ad appears on the screen.
                if(listener != null)
                    listener.onAdImpression();
            }

            @Override
            public void onAdClicked() {
                // When the user clicks on the ad.
                if(listener != null)
                    listener.onAdClicked();
            }

            @Override
            public void onAdOpened() {
                // When the fullsceen ad covers the screen.
                if(listener != null)
                    listener.onAdOpened();
            }

            @Override
            public void onAdClosed() {
                // When the fullsceen ad closes.
                if(listener != null)
                {
                    listener.onAdClosed();
                }
                destroyAll();
            }

            @Override
            public void onAdRewarded() {
                // It's time to offer some reward to the user.
                if(listener != null)
                 listener.onAdRewarded();
            }
        });
        mRewardVideoAd.show();
    }
    public void destroyAd()
    {
        if(mRewardVideoAd != null)
            mRewardVideoAd.destroy();
        mRewardVideoAd = null;
    }
    public void destroyAll()
    {
        destroyAd();
        listener = null;
        loadAd();
    }


}
