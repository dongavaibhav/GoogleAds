package com.example.googleads;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private Button interAds, rewadedAds, nativeads;
    private RewardedAd rewardedAd;
    boolean isLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        List<String> testDeviceIds = Arrays.asList("B2E06E8644C24394D909CFECD54198DC");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);

        bannerAds();
        interstitialAds();
        loadRewardedAd();
        interAds = (Button) findViewById(R.id.interstital);
        rewadedAds = (Button) findViewById(R.id.rewardedAds);
        nativeads = (Button) findViewById(R.id.nativeAds);

        // Load Inter Ads
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });

        interAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }
        });

        rewadedAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRewardedVideo();
            }
        });

        nativeads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UnifiedAdNative.class));
            }
        });
    }

    // Banner Ads
    public void bannerAds() {
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("B2E06E8644C24394D909CFECD54198DC")
                .build();
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClosed() {
                Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
                Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });

        mAdView.loadAd(adRequest);
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    // Interstitial Ads
    public void interstitialAds() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }

    // Rewarded Video Ads
    private void loadRewardedAd() {
        if (rewardedAd == null || !rewardedAd.isLoaded()) {
            rewardedAd = new RewardedAd(this, "ca-app-pub-3940256099942544/5224354917");
            isLoading = true;
            rewardedAd.loadAd(
                    new AdRequest.Builder().build(),
                    new RewardedAdLoadCallback() {
                        @Override
                        public void onRewardedAdLoaded() {
                            // Ad successfully loaded.
                            isLoading = false;
                            Toast.makeText(MainActivity.this, "onRewardedAdLoaded", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onRewardedAdFailedToLoad(int errorCode) {
                            // Ad failed to load.
                            isLoading = false;
                            Toast.makeText(MainActivity.this, "onRewardedAdFailedToLoad", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
        }
    }

    private void showRewardedVideo() {
        if (rewardedAd.isLoaded()) {
            RewardedAdCallback adCallback =
                    new RewardedAdCallback() {
                        @Override
                        public void onRewardedAdOpened() {
                            // Ad opened.
                            Toast.makeText(MainActivity.this, "onRewardedAdOpened", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onRewardedAdClosed() {
                            // Ad closed.
                            Toast.makeText(MainActivity.this, "onRewardedAdClosed", Toast.LENGTH_SHORT).show();
                            // Preload the next video ad.
                            loadRewardedAd();
                        }

                        @Override
                        public void onUserEarnedReward(RewardItem rewardItem) {
                            // User earned reward.
                            Toast.makeText(MainActivity.this, "onUserEarnedReward", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onRewardedAdFailedToShow(int errorCode) {
                            // Ad failed to display
                            Toast.makeText(MainActivity.this, "onRewardedAdFailedToShow", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    };
            rewardedAd.show(this, adCallback);
        }
    }
}
