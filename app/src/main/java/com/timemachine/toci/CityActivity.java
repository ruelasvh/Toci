package com.timemachine.toci;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by Victor Ruelas on 4/5/16.
 */

public class CityActivity extends AppCompatActivity
    implements
        ShowCrowdsListFragment.OnFragmentInteractionListener,
        ShowCrowdsMapFragment.OnFragmentInteractionListener {

    private static final String FETCH_CROWDS_FILTER = "BY_CITY";
    private static String CROWDS;

    public CityActivity() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        CROWDS = getIntent().getStringExtra("city");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolbar.setTitle(CROWDS);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Fragment fragment = ShowCrowdsListFragment.newInstance(FETCH_CROWDS_FILTER, CROWDS);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fragment)
        .addToBackStack(null).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * Method to communicate with fragments
     * @param position
     */
    public void onFragmentInteraction(int position) {
    }
}
