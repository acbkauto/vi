package com.demo.acbk.vungleintegration;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.vungle.warren.AdConfig;
import com.vungle.warren.Vungle;
import com.vungle.warren.VungleNativeAd;

import java.util.Locale;

import static com.demo.acbk.vungleintegration.VungleAds.placementsList;
import static com.demo.acbk.vungleintegration.VungleAds.vungleLoadAdCallback;
import static com.demo.acbk.vungleintegration.VungleAds.vunglePlayAdCallBack;

public class NavigationDrawerActivity extends Activity implements PlanetAdapter.OnItemClickListener {
    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;

    private VungleNativeAd vungleNativeAd;
    private View nativeAdView;
    private RelativeLayout flexfeed_container;
    private AdConfig adConfig = new AdConfig();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        mTitle=mDrawerTitle=getTitle();
        mPlanetTitles=getResources().getStringArray(R.array.planets_array);
        mDrawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList=(RecyclerView)findViewById(R.id.left_drawer);

        /*
        // Set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // Improve performance by indicating the list if fixed size.
        mDrawerList.setHasFixedSize(true);
         */

        // Set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new PlanetAdapter(mPlanetTitles, this));
        // Enable ActionBar app icon to behave as action to toggle new drawer

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the proper interactions
        // Between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.drawable.baseline,
                R.string.drawer_open,       /* "Open Drawer" description for voiceover */
                R.string.drawer_close       /* "Close Drawer" description for voiceover */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // Creates call to opPrepareOptionsMenu()
            }

            public void onDrawerOpened (View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // Creates call to on PrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate the menu; this adds items to the action bar if it present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.action_websearch:
                // Create intent to perform web search for this planet
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
                // Catch event that there is no activity to handle intent
                if (intent.resolveActivity(getPackageManager()) != null) {

                    // Play ads here for ads played users
                    for (int i=0; i<3; i++) {

                        final int index=i;

                        if (Vungle.isInitialized() && index !=0) {
                            Vungle.loadAd(placementsList.get(index), vungleLoadAdCallback);
                        }

                        if (Vungle.isInitialized() && Vungle.canPlayAd(placementsList.get(index))) {
                            if (index == 2) {
                                // Play Flex Feed ads
                                vungleNativeAd = Vungle.getNativeAd(placementsList.get(2), vunglePlayAdCallBack);
                                nativeAdView = vungleNativeAd.renderNativeView();
                                flexfeed_container.addView(nativeAdView);
                            } else if (index == 1) {
                                Vungle.playAd(placementsList.get(index), null, vunglePlayAdCallBack);
                            } else {
                                adConfig.setBackButtonImmediatelyEnabled(true);
                                adConfig.setAutoRotate(true);
                                adConfig.setMuted(false);
                                Vungle.setIncentivizedFields("Test", "Title", "msgBody", "keepWatching", "RewardedClose");
                                Vungle.playAd(placementsList.get(index), adConfig, vunglePlayAdCallBack);
                            }
                        }
                    }

                    // Web Search intent
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /* The click listener for RecyclerView in the navigation drawer */
    @Override
    public void onClick(View view, int position) {
        selectItem(position);
    }

    private void selectItem(int position) {
        // Update the main content by replacing fragments
        Fragment fragment = PlanetFragment.newInstance(position);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();

        // Update selected item title, then close the drawer
        setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle=title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChaged().
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //Pass any configuration change to the drawer toggle
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Fragment that appear in the "content_frame", shows a planet
     */
    public static class PlanetFragment extends Fragment {
        public static final String ARG_PLANET_NUMBER="planet_number";

        public PlanetFragment() {
            // Empty constructor required for fragment subclasses
        }

        public static Fragment newInstance(int position) {
            Fragment fragment = new PlanetFragment();
            Bundle args = new Bundle();
            args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_planet, container, false);
            int i = getArguments().getInt(ARG_PLANET_NUMBER);
            String planet = getResources().getStringArray(R.array.planets_array)[i];

            int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
            "drawable", getActivity().getPackageName());
            ImageView iv=((ImageView) rootView.findViewById(R.id.image));
            iv.setImageResource(imageId);

            getActivity().setTitle(planet);
            return rootView;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (vungleNativeAd != null) {
            vungleNativeAd.setAdVisibility(false);
        }
    }
}