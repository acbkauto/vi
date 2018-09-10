package com.demo.acbk.vungleintegration;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.vungle.warren.InitCallback;
import com.vungle.warren.Vungle;

import static com.demo.acbk.vungleintegration.VungleAds.LOG_TAG;
import static com.demo.acbk.vungleintegration.VungleAds.app_id;

/**
 * Launcher activity offering access to the Vungle Integration DEMO
 */
public class MainActivity extends Activity implements AdapterView.OnItemClickListener{
    private Sample[] mSamples;
    private GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Prepare list of samples in dashboard view.
        mSamples = new Sample[]{
                new Sample(R.string.navigationdraweractivity_title, R.string.navigationdraweractivity_description, NavigationDrawerActivity.class)
        };

        // Prepare the GridView
        mGridView = (GridView) findViewById(android.R.id.list);
        mGridView.setAdapter(new SampleAdapter());
        mGridView.setOnItemClickListener(this);

        // Initialize Vungle SDK
        initSDK();
    }

    @Override
    public void onItemClick(AdapterView<?> container, View view, int position, long id) {
        startActivity(mSamples[position].intent);
    }

    private class SampleAdapter extends BaseAdapter {
        @Override
        public int getCount() { return mSamples.length; }

        @Override
        public Object getItem(int position) { return mSamples[position]; }

        @Override
        public long getItemId(int position) { return mSamples[position].hashCode(); }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            if (convertView==null) {
                convertView = getLayoutInflater().inflate(R.layout.dashboard, container, false);
            }

            ((TextView) convertView.findViewById(android.R.id.text1)).setText(mSamples[position].titleResId);
            ((TextView) convertView.findViewById(android.R.id.text2)).setText(mSamples[position].descriptionResId);
            return convertView;
        }
    }

    private class Sample {
        int titleResId;
        int descriptionResId;
        Intent intent;

        private Sample(int titleResId, int descriptionResId, Intent intent) {
            this.intent=intent;
            this.titleResId=titleResId;
            this.descriptionResId=descriptionResId;
        }

        private Sample(int titleResId, int descriptionResId, Class <? extends Activity> activityClass) {
            this(titleResId, descriptionResId, new Intent(MainActivity.this, activityClass));
        }
    }

    // Initialize Vungle SDK
    public void initSDK() {
        Vungle.init(app_id, getApplicationContext(), new InitCallback() {
            @Override
            public void onSuccess() {
                Log.d(LOG_TAG, "InitCallback - Success");
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d(LOG_TAG, "InitCallback - onError: "+throwable.getLocalizedMessage());
            }

            @Override
            public void onAutoCacheAdAvailable(final String placementReferenceID) {
                Log.d(LOG_TAG, "InitCallBack - onAutoCacheAdAvailable" + "\n\tPlacement Reference ID = " + placementReferenceID);
            }
        });
    }
}
