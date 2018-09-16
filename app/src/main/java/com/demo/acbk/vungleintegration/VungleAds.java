package com.demo.acbk.vungleintegration;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.vungle.warren.AdConfig;
import com.vungle.warren.InitCallback;
import com.vungle.warren.LoadAdCallback;
import com.vungle.warren.PlayAdCallback;
import com.vungle.warren.Vungle;
import com.vungle.warren.VungleNativeAd;
import com.vungle.warren.error.VungleException;

import java.util.Arrays;
import java.util.List;

public class VungleAds {

    // Variables for ads SDK
    static final String LOG_TAG="adsSDK Integration DEMO";
    static final String app_id="5ae0db55e2d43668c97bd65e";
    static final String autocachePlacementReferenceID = "DEFAULT-6595425";
    static final List<String> placementsList =
            Arrays.asList(autocachePlacementReferenceID, "DYNAMIC_TEMPLATE_INTERSTITIAL-6969365", "FLEX_FEED-2416159");
    static final String FREE_TIMES="Free Play Times";

    Context context;

    // Variables for playing ads
    private VungleNativeAd vungleNativeAd;
    private View nativeAdView;
    private AdConfig adConfig = new AdConfig();

    // As DEMO, set Free Play Times = 5;
    private int freeTimes;

    // Constructor
    public VungleAds(Context context) {
        this.context = context;
        freeTimes=5;
    }

    // Init adSDK
    protected void initSDK() {
        Vungle.init(app_id, this.context, new InitCallback() {
            @Override
            public void onSuccess() {
                Log.d(LOG_TAG, "InitialCallBack - Success");
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d(LOG_TAG, "InitialCallBack - onError" + throwable.getLocalizedMessage());
            }

            @Override
            public void onAutoCacheAdAvailable(final String placementReferenceID) {
                Log.d(LOG_TAG, "InitialCallBack - onAutoCacheAdAvailable" +
                        "\n\tPlacement Reference ID = " + placementReferenceID);
            }
        });
    }

    // PlayAdCallback
    final PlayAdCallback vunglePlayAdCallBack = new PlayAdCallback() {
        @Override
        public void onAdStart(final String placementReferenceID) {
            Log.d(LOG_TAG, "PlayAdCallback - onAdStart" + "\n\tPlacement Reference ID =" + placementReferenceID);
        }

        @Override
        public void onAdEnd(final String placementReferenceID, final boolean complete, final boolean isCTAClicked) {
            Log.d(LOG_TAG, "PlayAdCallback - onAdEnd" +
                    "\n\tPlacement Reference ID = "+ placementReferenceID +
                    "\n\tView Completed = " + complete + "" +
                    "\n\tDownload Clicked = " + isCTAClicked);
        }

        @Override
        public void onError(final String placementReferenceID, Throwable throwable) {
            Log.d(LOG_TAG, "PlayAdCallback - onError" +
                    "\n\tPlacement Reference ID = " + placementReferenceID +
                    "\n\tError = " + throwable.getLocalizedMessage());
            checkInitStatus(throwable);
        }
    };

    // Private method to redo the initSDK, when previous initialization fails
    private void checkInitStatus(Throwable throwable) {
        try {
            VungleException ex = (VungleException) throwable;
            Log.d(LOG_TAG, ex.getLocalizedMessage());
            if (ex.getExceptionCode() == VungleException.VUNGLE_NOT_INTIALIZED) {
                initSDK();
            }
        } catch(ClassCastException cex) {
            Log.d(LOG_TAG, cex.getMessage());
        }
    }

    // LoadAdCallback
    final static LoadAdCallback vungleLoadAdCallback = new LoadAdCallback() {
        @Override
        public void onAdLoad(final String placementReferenceID) {
            Log.d(LOG_TAG, "LoadAdCallback - onAdLoad" +
            "\n\tPlacement Reference ID = " + placementReferenceID);
        }

        @Override
        public void onError(final String placementReferenceID, Throwable throwable) {
            Log.d(LOG_TAG, "LoadAdCallback - onError" +
            "\n\tPlacement Reference ID = " + placementReferenceID +
            "\n\tError = " + throwable.getLocalizedMessage());
        }
    };

    // Public method to play ads on air
    public void adOnAir(RelativeLayout relativeLayout) {
        for (int i=0; i<3; i++) {

            if (Vungle.isInitialized() && i!=0) {
                Vungle.loadAd(placementsList.get(i), vungleLoadAdCallback);
            }

            if (Vungle.isInitialized() && Vungle.canPlayAd(placementsList.get(i))) {
                if (i==2) {
                    // Play Flex-feed ads
                    vungleNativeAd=Vungle.getNativeAd(placementsList.get(2), vunglePlayAdCallBack);
                    nativeAdView=vungleNativeAd.renderNativeView();
                    relativeLayout.addView(nativeAdView);
                    vungleNativeAd.finishDisplayingAd();
                    relativeLayout.removeView(nativeAdView);
                    vungleNativeAd=null;
                } else if (i==1) {
                    Vungle.playAd(placementsList.get(i), null, vunglePlayAdCallBack);
                } else {
                    adConfig.setBackButtonImmediatelyEnabled(true);
                    adConfig.setAutoRotate(true);
                    adConfig.setMuted(false);
                    Vungle.setIncentivizedFields("Test", "Title", "msgBody", "keepWatching", "RewardedClose");
                    Vungle.playAd(placementsList.get(i), adConfig, vunglePlayAdCallBack);
                }
            }
        }
    }

    // Free Play Counter
    public boolean freePlay() {
        if (freeTimes == 1) {
            freeTimes=5;
            Log.d(FREE_TIMES, "Free Times now had been reset to " + freeTimes);
            return false;
        } else {
            freeTimes--;
            Log.d(FREE_TIMES, "Free Times - 1, till next time ad show has " + freeTimes +" time(s)");
        }
        return true;
    }
}
