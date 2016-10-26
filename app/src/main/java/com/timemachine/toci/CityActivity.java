package com.timemachine.toci;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.List;

public class CityActivity extends AppCompatActivity {
    // helper in debugging
    private final static String TAG = CityActivity.class.getSimpleName();

    private static String mCity;

    private LiveCrowdRowAdapterv2 adapter;
    private ProgressBar spinner;
    private ListView crowdList;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    // Helper fields to help store favorite settings
    Context mContext;
    AppPrefs mAppPrefs;
    List<String> mCityFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        mCity = getIntent().getStringExtra("city");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mCity);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        // BEGIN_INCLUDE (change_colors)
        // Set the color scheme of the SwipeRefreshLayout by providing 4 color resource ids
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.PrimaryAccentColor, R.color.PrimaryAccentColor,
                R.color.PrimaryAccentColor, R.color.PrimaryAccentColor);

        displayCrowds();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                displayCrowds();
            }
        });
    }

    /**
     * Inflate the menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }

    /**
     * Handle menu items selection.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        mContext = getApplicationContext();
        mAppPrefs = new AppPrefs(mContext);

        switch (item.getItemId()) {
            // This returns to previous fragment in previous activity.
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_refresh:
                mSwipeRefreshLayout.setRefreshing(true);
                displayCrowds();
                return true;
//            case R.id.action_favorite:
//                mAppPrefs.setFavorite_city(mCity);
//                if (mAppPrefs.getFavorite_cities() != null) {
//                    for (String city : mAppPrefs.getFavorite_cities()) {
//                        Log.i(TAG, city);
//                    }
//                }
//                Snackbar.make(findViewById(android.R.id.content), "Pinned To Home Menu", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void displayCrowds() {

        new GetCrowdsv2(new GetCrowdsv2.AsyncResponse() {
            @Override
            public void onAsyncTaskFinish(LiveCrowdRow[] crowds) {

                spinner = (ProgressBar) findViewById(R.id.spinner);
                spinner.setVisibility(View.VISIBLE);
                crowdList = (ListView) findViewById(R.id.crowds_listview);

                adapter = new LiveCrowdRowAdapterv2(CityActivity.this, R.layout.row, crowds);
                adapter.notifyDataSetChanged();
                if (!adapter.isEmpty()) spinner.setVisibility(View.GONE);
                crowdList.setAdapter(adapter);
                mSwipeRefreshLayout.setRefreshing(false);

            }
        }).execute(mCity);
    }

}
