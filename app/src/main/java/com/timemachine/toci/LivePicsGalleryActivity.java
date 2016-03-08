package com.timemachine.toci;

import java.util.Locale;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public class LivePicsGalleryActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.livepics_gallery);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Create the adapter that will return a fragment for each of the five
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        // Hide actionbar
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (getSupportActionBar().isShowing()) {
                        getSupportActionBar().hide();
                    } else {
                        getSupportActionBar().show();
                    }
                } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    getSupportActionBar().hide();
                }
                return false;
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_live_pics_gallery, menu);
        return true;
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

            case R.id.action_camera:
                Intent intent = new Intent(this, CaptureImageActivity.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed(){
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            Log.i("MainActivity", "popping backstack");
            getSupportFragmentManager().popBackStackImmediate();
            return;
        } else {
            Log.i("MainActivity", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
                case 3:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 4:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }


    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        public int getPageNum() {
            return getArguments().getInt(ARG_SECTION_NUMBER, 0);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_livepiclayout, container, false);
            ImageView image = (ImageView) rootView.findViewById(R.id.livePic);
            //image.setImageDrawable(getResources().getDrawable(R.drawable.stephensgreenlive2));
            switch (this.getPageNum()) {
                case 1:
                    Picasso.with(getActivity()).load("http://www.mollysmtview.com/images/gal-9.jpg").into(image);
                    break;
                case 2:
                    Picasso.with(getActivity()).load("http://www.mollysmtview.com/images/img_1680.jpg").into(image);
                    break;
                case 3:
                    Picasso.with(getActivity()).load("http://www.mollysmtview.com/images/img_1694.jpg").into(image);
                    break;
                case 4:
                    Picasso.with(getActivity()).load("http://www.mollysmtview.com/images/img_1684.jpg").into(image);
                    break;
                case 5:
                    Picasso.with(getActivity()).load("http://www.mollysmtview.com/images/img_1727.jpg").into(image);
                    break;

            }
            //Toast.makeText(getActivity(), "Page " + this.getPageNum(), Toast.LENGTH_SHORT).show();

            return rootView;
        }
    }

}
