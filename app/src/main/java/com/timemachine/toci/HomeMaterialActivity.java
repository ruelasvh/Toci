package com.timemachine.toci;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenu;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;

import java.lang.reflect.Field;

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

        // Used to retrieve user preferences
        mContext = getApplicationContext();
        mAppPrefs = new AppPrefs(mContext);
        boolean isLoggedin = mAppPrefs.getSessionStatus();

        // Put asynctask here to check server if logged in
        if (!isLoggedin) {
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);

        // Bottom navigation
        mBottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
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

    public void onSectionAttached(String section) {
        // Used to communicate with other fragments
    }

    /**
     * Method to communicate with fragments
     * @param position
     */
    public void onFragmentInteraction(int position) {
    }
}
