package com.timemachine.toci;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

public class CityActivity extends AppCompatActivity {
    // helper in debugging
    private final static String TAG = CityActivity.class.getSimpleName();

    // Filter crowds by city
    private final static String CITY_FILTER = "CITY";

    private static String mCity;

    private LiveCrowdListAdapter adapter;
    private ProgressBar spinner;
    private ListView crowdList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private GetCrowds getCrowdsTask;
    private Network network;

    // Helper fields to help store favorite settings
    Context mContext;
    AppPrefs mAppPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        mCity = getIntent().getStringExtra("city");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mCity);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        network = new Network(this);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        // BEGIN_INCLUDE (change_colors)
        // Set the color scheme of the SwipeRefreshLayout by providing 4 color resource ids
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.PrimaryAccentColor, R.color.PrimaryAccentColor,
                R.color.PrimaryAccentColor, R.color.PrimaryAccentColor);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshCrowds();
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
                if (getCrowdsTask.getStatus() == AsyncTask.Status.RUNNING) {
                    return true;
                } else {
                    mSwipeRefreshLayout.setRefreshing(true);
                    refreshCrowds();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        refreshCrowds();

        // Disable swipe down to refresh if crowds are updating
        if (network.isOnline() && getCrowdsTask.getStatus() == AsyncTask.Status.RUNNING) {
            mSwipeRefreshLayout.setEnabled(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        cancelRefreshCrowds();
    }

    private void refreshCrowds() {
        if (network.isOnline()) {
            getCrowdsTask = new GetCrowds(new GetCrowds.AsyncResponse() {
                @Override
                public void onAsyncTaskFinish(LiveCrowd[] crowds) {

                    spinner = (ProgressBar) findViewById(R.id.spinner);
                    spinner.setVisibility(View.VISIBLE);
                    crowdList = (ListView) findViewById(R.id.crowds_listview);

                    adapter = new LiveCrowdListAdapter(CityActivity.this, R.layout.row, crowds);
                    adapter.notifyDataSetChanged();
                    if (!adapter.isEmpty()) spinner.setVisibility(View.GONE);
                    crowdList.setAdapter(adapter);
                    mSwipeRefreshLayout.setEnabled(true);
                    mSwipeRefreshLayout.setRefreshing(false);

                }
            });

            getCrowdsTask.execute(CITY_FILTER, mCity);
        } else {
            Toast.makeText(getApplicationContext(), R.string.error_offline,
                    Toast.LENGTH_SHORT).show();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void cancelRefreshCrowds() {
        getCrowdsTask.cancel(true);
    }
}
