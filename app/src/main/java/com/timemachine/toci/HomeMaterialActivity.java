package com.timemachine.toci;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class HomeMaterialActivity extends AppCompatActivity
        implements LoginFragment.OnFragmentInteractionListener, AboutUsFragment.OnFragmentInteractionListener,
        AddNewCrowdFragment.OnFragmentInteractionListener, FavoriteCrowdsFragment.OnFragmentInteractionListener,
        FavoriteCityFragment.OnFragmentInteractionListener,NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;
    private CharSequence mTitle;

    // Helper fields to help store favorite settings
    Context mContext;
    AppPrefs mAppPrefs;
    List<String> mCityFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_material);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.fragment_drawer);
        // Set title of different fragments
        mTitle = getTitle();
        // Set up the drawer.
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
         //populate the navigation drawer
        mNavigationDrawerFragment.setUserData(getResources().getString(R.string.cz_moto));
//        mNavigationDrawerFragment.setUserData(getResources().getString(R.string.cz_moto),
//                BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_pink));

    } // End onCreate method

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        // Used to retrieve user preferences
        mContext = getApplicationContext();
        mAppPrefs = new AppPrefs(mContext);

        // update the main content by replacing fragments
        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = new SearchFragment();
                break;
            case 1:
                fragment = FavoriteCrowdsFragment.newInstance();
                break;
            case 2:
                fragment = AddNewCrowdFragment.newInstance();
                break;
            case 3:
                fragment = new LoginFragment();
                break;
            case 4:
                fragment = new AboutUsFragment();
        }

        // if user preferences exist, load fragments accordingly
//        if (mAppPrefs.getFavorite_cities() != null) {
//
//            mCityFavorites = new ArrayList<>(mAppPrefs.getFavorite_cities());
//            int totalCities = mCityFavorites.size();
//
//            if (position == 0) {
//                fragment = new SearchFragment();
//            }
//            for (int i = 1; i <= totalCities; i++) {
//                if (position == i) {
//                    fragment = FavoriteCityFragment.newInstance(mCityFavorites.get(i-1));
//                }
//            }
//            if (position == (totalCities+1)) {
//                fragment = FavoriteCrowdsFragment.newInstance(mAppPrefs.getFav_crowds());
//            } else if (position == (totalCities+2)) {
//                fragment = AddNewCrowdFragment.newInstance();
//            } else if (position == (totalCities+3)) {
//                fragment = new LoginFragment();
//            } else if (position == (totalCities+4)) {
//                fragment = new AboutUsFragment();
//            }
//        } else { // otherwise load basic list of fragments
//            switch (position) {
//                case 0:
//                    fragment = new SearchFragment();
//                    break;
//                case 1:
//                    fragment = FavoriteCrowdsFragment.newInstance(mAppPrefs.getFav_crowds());
//                    break;
//                case 2:
//                    fragment = AddNewCrowdFragment.newInstance();
//                    break;
//                case 3:
//                    fragment = new LoginFragment();
//                    break;
//                case 4:
//                    fragment = new AboutUsFragment();
//            }
//        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fragment)
                    .addToBackStack(null).commit();
        }
    }


    public void onSectionAttached(String section) {

        switch (section) {
            case "SearchFragment":
                mTitle = getString(R.string.app_name);
                break;
            case "AddNewCrowdFragment":
                mTitle = "Add New Crowd";
                break;
            case "FavoriteCrowdsFragment":
                mTitle = "Favorite Crowds";
                break;
            case "LoginFragment":
                mTitle = "Log In";
                break;
            case "AboutUsFragment":
                mTitle = "About Us";
                break;
            default:
                break;
        }

        mToolbar.setTitle(mTitle);
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else
            super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
//            case R.id.some_menu_item:
//                // Can do something when user presses "some_menu_item"
//                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * Method to communicate with fragments
     * @param uri
     */
    public void onFragmentInteraction(Uri uri) {
        // empty
    }


}
