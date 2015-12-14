package com.timemachine.toci;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


public class HomeMaterialActivity extends ActionBarActivity
        implements SearchFragment.OnFragmentInteractionListener, MostPopularFragment.OnFragmentInteractionListener,
        LoginFragment.OnFragmentInteractionListener, AboutUsFragment.OnFragmentInteractionListener, NavigationDrawerCallbacks {

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
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
        // populate the navigation drawer
        mNavigationDrawerFragment.setUserData(getResources().getString(R.string.app_name),
                getResources().getString(R.string.cz_moto),
                BitmapFactory.decodeResource(getResources(), R.drawable.cz_logo));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        //Toast.makeText(this, "Menu item selected -> " + position, Toast.LENGTH_SHORT).show();
        Fragment fragment;
        switch (position) {
            default:
                fragment = new SearchFragment();
                break;
            case 0:
                fragment = new SearchFragment();
                break;
            case 1:
                fragment = new MostPopularFragment();
                break;
            case 2:
                fragment = new MVCAFeaturedFragment();
                //fragment = new LoginFragment();;
                break;
            case 3:
                fragment = new AboutUsFragment();
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment).commit();
        }
    }


    public void onSectionAttached(int number) {
        switch (number) {
            default:
            case 0:
                mTitle = getString(R.string.drawer_section_1);
                break;
            case 1:
                mTitle = getString(R.string.drawer_section_2);
                break;
            case 2:
                mTitle = getString(R.string.drawer_section_3);
                break;
            case 3:
                mTitle = getString(R.string.drawer_section_4);
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
            getMenuInflater().inflate(R.menu.home_material, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onFragmentInteraction(Uri uri) {
        // empty
    }


}
