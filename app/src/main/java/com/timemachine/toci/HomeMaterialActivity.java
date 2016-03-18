package com.timemachine.toci;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


public class HomeMaterialActivity extends AppCompatActivity
        implements SearchFragment.OnFragmentInteractionListener, MostPopularFragment.OnFragmentInteractionListener,
        LoginFragment.OnFragmentInteractionListener, AboutUsFragment.OnFragmentInteractionListener,
        AddNewCrowdFragment.OnFragmentInteractionListener ,NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;
    private CharSequence mTitle;

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
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        //Toast.makeText(this, "Menu item selected -> " + position, Toast.LENGTH_SHORT).show();
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new SearchFragment();
                break;
            case 1:
                fragment = MountainViewFeaturedFragment.newInstance();
                //fragment = new MVCAFeaturedRecyclerViewFragment();
                break;
            case 2:
                fragment = PaloAltoFeaturedFragment.newInstance();
                break;
            case 3:
                fragment = SanFranciscoFeaturedFragment.newInstance();
                break;
            case 4:
                fragment = SanJoseFeaturedFragment.newInstance();
                break;
            case 5:
                fragment = AddNewCrowdFragment.newInstance();
                break;
            case 6:
                fragment = new LoginFragment();
                break;
            case 7:
                fragment = new AboutUsFragment();
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fragment)
                    .addToBackStack(null).commit();
        }
    }


    public void onSectionAttached(String section) {
        switch (section) {
            default:
                mTitle = getString(R.string.app_name);
                break;
            case "MountainViewFeaturedFragment":
                mTitle = "Mountain View";
                break;
            case "PaloAltoFeaturedFragment":
                mTitle = "Palo Alto";
                break;
            case "SanFranciscoFeaturedFragment":
                mTitle = "San Francisco";
                break;
            case "SanJoseFeaturedFragment":
                mTitle = "San Jose";
                break;
            case "AddNewCrowdFragment":
                mTitle = "Add New Crowd";
                break;
        }
        setTitle();
    }

    private void setTitle() {
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
            case R.id.action_settings:
                // Can do something when user presses "Settings"
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }

    }

    public void onFragmentInteraction(Uri uri) {
        // empty
    }


}
