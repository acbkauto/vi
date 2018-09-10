package com.demo.acbk.vungleintegration;

import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.vungle.warren.AdConfig;
import com.vungle.warren.InitCallback;
import com.vungle.warren.LoadAdCallback;
import com.vungle.warren.PlayAdCallback;

import java.util.Arrays;
import java.util.List;

public class VungleAds {

    // Variables for ads SDK
    static final String LOG_TAG="adsSDK Integration DEMO";
    static final String app_id="5ae0db55e2d43668c97bd65e";
    static final String autocachePlacementReferenceID = "DEFAULT-6595425";
    static final List<String> placementsList =
            Arrays.asList(autocachePlacementReferenceID, "DYNAMIC_TEMPLATE_INTERSTITIAL-6969365", "FLEX_FEED-2416159");



    // PlayAdCallback
    final static PlayAdCallback vunglePlayAdCallBack = new PlayAdCallback() {
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
        }
    };

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
}
