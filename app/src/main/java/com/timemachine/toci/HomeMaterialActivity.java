package com.timemachine.toci;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.lang.reflect.Field;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

/**
 * Created by Victor Ruelas on 4/5/16.
 */
public class HomeMaterialActivity extends AppCompatActivity
        implements
        ShowCrowdsListFragment.OnFragmentInteractionListener,
        ShowCrowdsMapFragment.OnFragmentInteractionListener,
        SearchFragment.OnFragmentInteractionListener {

    private static final String SELECTED_ITEM = "arg_selected_item";
    private static final String FETCH_CROWDS_FILTER = "BY_ID";
    private static final int REQUEST_ACCESS_COARSE_LOCATION = 0;
    private static String CROWDS;
    private BottomNavigationView mBottomNav;
    private int mSelectedItem;
    private Toolbar mToolbar;

    // Helper fields to help store favorite settings
    Context mContext;
    AppPrefs mAppPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_material);

        mContext = this;
        mAppPrefs = new AppPrefs(mContext);

        boolean isLoggedin = mAppPrefs.getSessionStatus();
        if (!isLoggedin) {
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
//            getLocationPermissions();
            mAppPrefs.setLocationPermissions();
        }


        mToolbar = findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);

        // Bottom navigation
        mBottomNav = findViewById(R.id.bottom_navigation);
        disableShiftMode(mBottomNav);
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectFragment(item);
                return true;
            }
        });

        MenuItem selectedItem;
        if (savedInstanceState != null) {
            mSelectedItem = savedInstanceState.getInt(SELECTED_ITEM, 0);
            selectedItem = mBottomNav.getMenu().findItem(mSelectedItem);
        } else {
            selectedItem = mBottomNav.getMenu().getItem(0);
        }
        selectFragment(selectedItem);
    }

    @SuppressLint("RestrictedApi")
    public static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            //Timber.e(e, "Unable to get shift mode field");
        } catch (IllegalAccessException e) {
            //Timber.e(e, "Unable to change value of shift mode");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SELECTED_ITEM, mSelectedItem);
        super.onSaveInstanceState(outState);
    }

    private void selectFragment(MenuItem item) {
        Fragment frag = null;
        // init corresponding fragment
        switch (item.getItemId()) {
            case R.id.menu_search:
                frag = new SearchFragment();
                break;
            case R.id.menu_favorites:
                CROWDS = TextUtils.join(",", mAppPrefs.getFavorite_crowds());
                frag = ShowCrowdsListFragment.newInstance(FETCH_CROWDS_FILTER, CROWDS);
                break;
            case R.id.menu_add_crowd:
                frag = AddNewCrowdFragment.newInstance();
                break;
            case R.id.menu_settings:
                frag = new SettingsFragment();
                break;
        }

        updateToolbarText(item.getTitle());
        updateSelectedBottomNavItem(item.getItemId());

        if (frag != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, frag)
                    .addToBackStack(null).commit();
        }
    }

    private void updateSelectedBottomNavItem(int item) {
        mSelectedItem = item;
        mBottomNav.getMenu().findItem(mSelectedItem).setChecked(true);
    }

    private void updateToolbarText(CharSequence text) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(text);
        }
    }

//    public boolean getLocationPermissions() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            return true;
//        }
//        if (checkSelfPermission(ACCESS_COARSE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            return true;
//        }
//        if (shouldShowRequestPermissionRationale(ACCESS_COARSE_LOCATION)) {
//            Snackbar.make(aView, R.string.location_permission_rationale, Snackbar.LENGTH_INDEFINITE)
//                    .setAction(android.R.string.ok, new View.OnClickListener() {
//                        @Override
//                        @TargetApi(Build.VERSION_CODES.M)
//                        public void onClick(View v) {
//                            requestPermissions(new String[]{ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);
//                        }
//                    });
//        } else {
//            requestPermissions(new String[]{ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);
//        }
//        return false;
//    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_ACCESS_COARSE_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getLocationPermissions();
            }
        }
    }

    /**
     * Method to communicate with fragments
     * @param position
     */
    public void onFragmentInteraction(int position) {
        MenuItem selectedItem = mBottomNav.getMenu().getItem(position);
        selectFragment(selectedItem);
    }
}
