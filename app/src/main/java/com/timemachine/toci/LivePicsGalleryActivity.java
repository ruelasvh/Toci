package com.timemachine.toci;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.places.Places;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

/**
 * Created by Victor Ruelas on 4/5/16.
 */
public class LivePicsGalleryActivity extends AppCompatActivity implements OnConnectionFailedListener {

    private final static String FETCH_CROWDS_FILTER = "BY_ID";
    private String UBER_CLIENT_ID;
    private String LYFT_CLIENT_ID;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    /**
     * Layout which holds details about crowds
     */
    private RelativeLayout mDetailsLayout;

    /**
     * The api client used in connecting to google places api
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * File url to store image/video
     */
    private Uri fileUri;
    long totalSize = 0;

    /**
     * Variable to hold crowd passed from pevious activity
     */
    private static LiveCrowd thisLiveCrowd;
    private String title;
    private double latitude;
    private double longitude;

    /**
     * String array to hold picture urls passed from previous activity
     */
    private static HashMap<Integer, ArrayList<String>> picUrls;

    /**
     * AsyncTask for retrieving latest pictures
     */
    private GetCrowds getCrowdsTask;
    private final String GET_CROWDS_TASK_RUNNING = "getCrowdsTaskRunning";

    /**
     * Helper class to discover network availability
     */
    private Network network;

    /**
     * Helper fields to help store favorite settings
     */
    Context mContext;
    AppPrefs mAppPrefs;

    // Action buttons
    private Boolean isFabMenuOpen = false;
    private FloatingActionButton mNavigateButton;
    private FloatingActionButton mCallButton;
    private FloatingActionButton mRideUberButton;
    private TextView mRideUberLabel;
    private FloatingActionButton mRideLyftButton;
    private TextView mRideLyftLabel;
    private FloatingActionButton mRideGmapsButton;
    private TextView mRideGmapsLabel;
    private Animation fadeIn, fadeOut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.livepics_gallery);

        // Get crowd from previous activity
        thisLiveCrowd = SerializeLiveCrowd.fromJson(getIntent().getExtras().getString("crowd"));
        picUrls = thisLiveCrowd.getPicUrls();
        try {
            title = URLEncoder.encode(thisLiveCrowd.getTitle(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            title = "";
        }
        String[] latlng = thisLiveCrowd.getLatlng().split(",");
        latitude = Double.parseDouble(latlng[0]);
        longitude = Double.parseDouble(latlng[1]);

        // Get Uber and Lyft Client IDs
        UBER_CLIENT_ID = getString(R.string.uber_client_id);

        // Create the adapter that will return a fragment for each of the five
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fab_menu_open);
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fab_menu_close);
        // Set up the navigate and call buttons
        mNavigateButton = (FloatingActionButton) findViewById(R.id.fab_ride_crowd);
        mCallButton = (FloatingActionButton) findViewById(R.id.fab_call_crowd);
        mRideUberButton = (FloatingActionButton) findViewById(R.id.fab_ride_uber);
        mRideUberLabel = (TextView) findViewById(R.id.uber_label);
        mRideLyftButton = (FloatingActionButton) findViewById(R.id.fab_ride_lyft);
        mRideLyftLabel = (TextView) findViewById(R.id.lyft_label);
        mRideGmapsButton = (FloatingActionButton) findViewById(R.id.fab_ride_gmaps);
        mRideGmapsLabel = (TextView) findViewById(R.id.gmpas_label);
        mNavigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toggle Fab Menu
                toggleFabMenu();
            }
        });
        mRideUberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String packageId = "com.ubercab";
                String uri = "uber://?client_id=" + UBER_CLIENT_ID +
                        "&action=setPickup&pickup=my_location" +
                        "&dropoff[latitude]=" + latitude +
                        "&dropoff[longitude]=" + longitude +
                        "8&dropoff[nickname]=" + title;
                String signupLink = "https://uber.com/sign-up?client_id=" + UBER_CLIENT_ID;
                openDeeplink(packageId, uri, signupLink);
            }
        });
        mRideLyftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String packageId = "me.lyft.android";
                String uri = "lyft://ridetype?id=lyft" +
                        "&partner=" + LYFT_CLIENT_ID +
                        "&destination[latitude]=" + latitude +
                        "&destination[longitude]=" + longitude;
                String signupLink = "https://www.lyft.com/signup/SDKSIGNUP?clientId=" + LYFT_CLIENT_ID +
                        "&sdkName=android_direct";
                openDeeplink(packageId, uri, signupLink);
            }
        });
        mRideGmapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String packageId = "com.google.android.apps.maps";
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", latitude, longitude, title);
                String signupLink = "https://www.google.com/url?sa=t&rct=j&q=&esrc=s&source=web&cd=7&cad=rja&uact=8&ved=0ahUKEwj08urXpevTAhVF7CYKHZbSDbEQFghHMAY&url=https%3A%2F%2Fplay.google.com%2Fstore%2Fapps%2Fdetails%3Fid%3Dcom.google.android.apps.maps%26hl%3Den&usg=AFQjCNGh3toBm79BSodS6P_e6E3tOHtb9Q&sig2=qWa7rcnV2Y6drANuHz17mQ";
                openDeeplink(packageId, uri, signupLink);
            }
        });
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:016509966132"));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "No Phone Number Available",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Set up preferences resources
        mContext = getApplicationContext();
        mAppPrefs = new AppPrefs(mContext);
        // Set the actionbar title
        setTitle(thisLiveCrowd.getTitle());
        // Instantiate network helper class
        network = new Network(this);
    }

    /**
     * Inflate the menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_live_pics_gallery, menu);
        if (mAppPrefs.getFavorite_crowds().contains(thisLiveCrowd.getId())) {
            menu.findItem(R.id.action_favorite_toggle).setIcon(R.drawable.ic_action_star_on);
        } else {
            menu.findItem(R.id.action_favorite_toggle).setIcon(R.drawable.ic_action_star_off);
        }
        return true;
    }


    /**
     * Handle menu items selection.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // This returns to previous fragment in previous activity.
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_camera:
                // Bring up the camera
                if (network.isOnline()) {
                    captureImage();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_offline,
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_favorite_toggle:
                // Save to favorites
                if (mAppPrefs.getFavorite_crowds().contains(thisLiveCrowd.getId())) {
                    item.setIcon(R.drawable.ic_action_star_off);
                    item.setTitle("Remove From Favorites");
                    saveToFavs(thisLiveCrowd.getId());
                } else {
                    item.setIcon(R.drawable.ic_action_star_on);
                    item.setTitle("Add To Favorites");
                    saveToFavs(thisLiveCrowd.getId());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    // Store the file url as it will be null after returning from camera app
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation changes
        outState.putParcelable("file_uri", fileUri);

        // save state of getCrowdsTask
        if (isTaskRunning()) {
            outState.putBoolean(GET_CROWDS_TASK_RUNNING, true);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");

        // check if getCrowdsTask is running so it can be restarted
        if (savedInstanceState.getBoolean(GET_CROWDS_TASK_RUNNING, false)) {
            refreshCrowd();
        }
    }

    /**
     * Receiving activity result method will be called after closing the camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image. Upload picture to server
                new UploadFileToServer().execute();
                Toast.makeText(getApplicationContext(), "Photo successfully uploaded",
                        Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled image capture
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(), "Internal Error: Couldn't capture image",
                        Toast.LENGTH_SHORT).show();
            }
        }
        // Refresh crowd with new pictures
        refreshCrowd();
    }

    // Method to connect to the google places api
    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    // Method to disconnect from the google places api
    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    // Method to handle connection errors. Now error handling is silent.
    @Override
    public void onConnectionFailed(ConnectionResult result) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        cancelRefreshCrowd();
    }

    public void openDeeplink(String packageId, String uri, String signupLink) {
        try {
            PackageManager pm = getApplicationContext().getPackageManager();
            pm.getPackageInfo(packageId, PackageManager.GET_ACTIVITIES);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (packageId.equals("me.lyft.android")) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            if (packageId.equals("com.google.android.apps.maps")) {
                intent.setPackage(packageId);
            }
            intent.setData(Uri.parse(uri));
            startActivity(intent);
        } catch (PackageManager.NameNotFoundException e) {
            // No app. Open mobile website for registering.
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(signupLink));
            startActivity(i);
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

            // Show total pages.
            int num_pages = picUrls.size();
            return num_pages;
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
        private RelativeLayout mDetailsContainerv2;
        private TextView mTimeStampView;
        private ImageView mHelperView;
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
            // Empty constructor
        }

        public int getPageNum() {
            return getArguments().getInt(ARG_SECTION_NUMBER, 0);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_livepiclayout, container, false);
            final ImageView liveImageView = (ImageView) rootView.findViewById(R.id.livePic);
            mDetailsContainerv2 = (RelativeLayout) rootView.findViewById(R.id.details_container);
            mTimeStampView = (TextView) mDetailsContainerv2.findViewById(R.id.timestamp);
            mHelperView = (ImageView) mDetailsContainerv2.findViewById(R.id.image_container);
            mHelperView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Empty on purpose
                }
            });

            /* Set up behavior to toggle toolbar and details container */
            liveImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getActionBar().isShowing()) {
                        // Hide the actionbar/toolbar
                        getActionBar().hide();
                        ((LivePicsGalleryActivity) getActivity()).mNavigateButton.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                            @Override
                            public void onHidden(FloatingActionButton fab) {
                                super.onHidden(fab);
                                ((LivePicsGalleryActivity) getActivity()).mNavigateButton.setImageResource(R.drawable.ic_directions_car_white);
                            }
                        });
                        ((LivePicsGalleryActivity) getActivity()).closeFabMenu();
                    } else {
                        // Show the actionbar/toolbar
                        getActionBar().show();
                        ((LivePicsGalleryActivity) getActivity()).mNavigateButton.show();
                    }
                }
            });

            /* Load pictures in pager */
            switch (this.getPageNum()) {
                case 1:
                    Picasso.with(getActivity()).load( picUrls.get( picUrls.size()-1 ).get(0) )
                            .into(liveImageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    mTimeStampView.setText(picUrls.get( picUrls.size()-1 ).get(1));
                                }

                                @Override
                                public void onError() {
                                    // Silent Error
                                }
                            });
                    break;
                case 2:
                    Picasso.with(getActivity()).load( picUrls.get( picUrls.size()-2 ).get(0) )
                            .into(liveImageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    mTimeStampView.setText(picUrls.get( picUrls.size()-2 ).get(1));
                                }

                                @Override
                                public void onError() {
                                    // Silent Error
                                }
                            });
                    break;
                case 3:
                    Picasso.with(getActivity()).load( picUrls.get( picUrls.size()-3 ).get(0) )
                            .into(liveImageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    mTimeStampView.setText(picUrls.get( picUrls.size()-3 ).get(1));
                                }

                                @Override
                                public void onError() {
                                    // Silent Error
                                }
                            });
                    break;
                case 4:
                    Picasso.with(getActivity()).load( picUrls.get( picUrls.size()-4 ).get(0) )
                            .into( liveImageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    mTimeStampView.setText(picUrls.get( picUrls.size()-4 ).get(1));
                                }

                                @Override
                                public void onError() {
                                    // Silent Error
                                }
                            });
                    break;
                case 5:
                    Picasso.with(getActivity()).load( picUrls.get( picUrls.size()-5 ).get(0) )
                            .into( liveImageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    mTimeStampView.setText(picUrls.get( picUrls.size()-5 ).get(1));
                                }

                                @Override
                                public void onError() {
                                    // Silent Error
                                }
                            });
                    break;

            }

            return rootView;

        }

        /**
         * Helper methods to hide actionbar/toolbar, get the details container from activity,
         * and animate the details container
         */
        private android.support.v7.app.ActionBar getActionBar() {
            return ((LivePicsGalleryActivity) getActivity()).getSupportActionBar();
        }

        private RelativeLayout getDetailsContainer() {
            return ((LivePicsGalleryActivity) getActivity()).mDetailsLayout;
        }

        // To animate details container from bottom to top
        public void slideToBottom(View view){
            view.setAlpha(1.0f);
            view.animate()
                    .translationY(view.getHeight())
                    .alpha(0.0f);
        }

        // To animate details container slide out from bottom to top
        public void slideToTop(View view){
            view.setVisibility(View.VISIBLE);
            view.setAlpha(0.0f);
            view.animate()
                    .translationY(0)
                    .alpha(1.0f);
        }

    }


    /**
     * Uploading the file to server on a different thread than the main UI tread.
     */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {

            String responseString;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Config.FILE_UPLOAD_URL);

            try {

                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(fileUri.getPath());

                /**
                 * Shrink image
                 */
                // get absolute path of file to shrink
                String newPath = sourceFile.getAbsolutePath();
                // get original dimensions
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(newPath, options);
                int originalWidth = options.outWidth;
                int originalHeight = options.outHeight;
                int newWidth = -1;
                int newHeight = -1;
                float mulFactor = -1.0F;
                if (originalHeight > originalWidth) {
                    newHeight = 1024;
                    mulFactor = (float) originalWidth / (float) originalHeight;
                    newWidth = (int) (newHeight*mulFactor);
                } else if (originalWidth > originalHeight) {
                    newWidth = 1024;
                    mulFactor = (float) originalHeight / (float)  originalWidth;
                    newHeight = (int) (newWidth*mulFactor);
                } else if (originalHeight == originalWidth) {
                    newHeight = 1024;
                    newWidth = 1024;
                }
                /**
                 * End of shrink image block
                 */

                // Decode the file
                Bitmap bMap = BitmapFactory.decodeFile(newPath);
                Bitmap out = Bitmap.createScaledBitmap(bMap, newWidth, newHeight, false);
                File resizedFile= getOutputMediaFile();

                OutputStream fOut = null;
                try {
                    fOut = new BufferedOutputStream(new FileOutputStream(resizedFile));
                    out.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                    bMap.recycle();
                    out.recycle();
                } catch (Exception e) {
                    // Handle exception
                }

                entity.addPart("image", new FileBody(resizedFile));
                entity.addPart("id", new StringBody(thisLiveCrowd.getId()));
                entity.addPart("city", new StringBody(thisLiveCrowd.getCity()));
                entity.addPart("state", new StringBody(thisLiveCrowd.getState()));
                entity.addPart("country", new StringBody(thisLiveCrowd.getCountry()));
                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

    }

    // Helper method to show and hide FAB Menu
    public void toggleFabMenu() {
        if (isFabMenuOpen) {
            mRideUberButton.setClickable(false);
            mRideLyftButton.setClickable(false);
            mRideGmapsButton.setClickable(false);
            mRideUberButton.hide();
            mRideUberLabel.startAnimation(fadeOut);
            mRideUberLabel.setVisibility(View.INVISIBLE);
            mRideLyftButton.hide();
            mRideLyftLabel.startAnimation(fadeOut);
            mRideLyftLabel.setVisibility(View.INVISIBLE);
            mRideGmapsButton.hide();
            mRideGmapsLabel.startAnimation(fadeOut);
            mRideGmapsLabel.setVisibility(View.INVISIBLE);
            mNavigateButton.setImageResource(R.drawable.ic_directions_car_white);
            isFabMenuOpen = false;
        } else {
            mRideUberButton.setClickable(true);
            mRideLyftButton.setClickable(true);
            mRideGmapsButton.setClickable(true);
            mRideUberButton.show();
            mRideUberLabel.startAnimation(fadeIn);
            mRideUberLabel.setVisibility(View.VISIBLE);
            mRideLyftButton.show();
            mRideLyftLabel.startAnimation(fadeIn);
            mRideLyftLabel.setVisibility(View.VISIBLE);
            mRideGmapsButton.show();
            mRideGmapsLabel.startAnimation(fadeIn);
            mRideGmapsLabel.setVisibility(View.VISIBLE);
            mNavigateButton.setImageResource(R.drawable.ic_close_white);
            isFabMenuOpen = true;
        }
    }

    public void closeFabMenu() {
        if (isFabMenuOpen) {
            mRideUberButton.setClickable(false);
            mRideLyftButton.setClickable(false);
            mRideGmapsButton.setClickable(false);
            mRideUberButton.hide();
            mRideUberLabel.startAnimation(fadeOut);
            mRideUberLabel.setVisibility(View.INVISIBLE);
            mRideLyftButton.hide();
            mRideLyftLabel.startAnimation(fadeOut);
            mRideLyftLabel.setVisibility(View.INVISIBLE);
            mRideGmapsButton.hide();
            mRideGmapsLabel.startAnimation(fadeOut);
            mRideGmapsLabel.setVisibility(View.INVISIBLE);
            isFabMenuOpen = false;
        }
    }

    /**
     * Launching camera app for capturing image
     */

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /**
     * Helper Methods for handling pictures taken
     */

    // Creating file uri to store image/video
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    // returning image/video
    private static File getOutputMediaFile(int type) {
        // External sdcard location
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), Config.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it doesn't exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }
        return mediaFile;
    }

    /**
     * Helper methods to format information about a place nicely.
     */
    public static Spanned formatPlaceDetails(Resources res, CharSequence name,
                                             CharSequence address) {
        return Html.fromHtml(res.getString(R.string.place_details_exist, name, address));
    }

    // Get short address for displaying in the details container
    private static String getShortAddress(CharSequence address) {

        String short_address = "";
        boolean[] separators = new boolean[address.length()];
        int[] separatorIndices = new int[5];
        int counter = 0;

        for(int j = 0; j < address.length(); j++)
            separators[j] = false;

        for(int i = 0; i < address.length(); i++) {
            if(address.charAt(i) == ',') {
                separators[i] = true;
            }
        }

        for(int k = 0; k < separators.length; k++) {
            if(separators[k]) {
                separatorIndices[counter] = k;
                counter++;
            }
        }
        short_address = address.subSequence(0, separatorIndices[2]).toString();

        return short_address;
    }

    /**
     * Methods which builds google places api.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this /* AppCompatActivity */,
                        this /* OnConnectionFailedListener */ )
                .build();

    }

    // returning image/video
    private static File getOutputMediaFile() {
        // External sdcard location
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), Config.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it doesn't exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    // Helper method for saving individual crowd to shared preferences
    public void saveToFavs(String crowdId) {
        final Set<String> favCrowds = mAppPrefs.getFavorite_crowds();

        if (favCrowds.contains(crowdId)) {
            mAppPrefs.removeFavorite_crowd(crowdId);
            Toast.makeText(this, "Removed From Favorites", Toast.LENGTH_SHORT).show();
        } else {
            mAppPrefs.setFavorite_crowd(crowdId);
            Toast.makeText(this, "Added To Favorites", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshCrowd() {
        if (network.isOnline()) {
            getCrowdsTask = new GetCrowds(this, new GetCrowds.AsyncResponse() {
                @Override
                public void onAsyncTaskFinish(LiveCrowd[] crowds) {
                    picUrls = crowds[0].getPicUrls();
                    mSectionsPagerAdapter.notifyDataSetChanged();
                    mViewPager.setAdapter(mSectionsPagerAdapter);
                }
            });
            getCrowdsTask.execute(FETCH_CROWDS_FILTER, thisLiveCrowd.getId());
        } else {
            Toast.makeText(getApplicationContext(), "No Connection Available",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void cancelRefreshCrowd() {
        if (getCrowdsTask != null) {
            getCrowdsTask.cancel(true);
        }
    }

    private boolean isTaskRunning() {
        return (getCrowdsTask != null) && (getCrowdsTask.getStatus() == AsyncTask.Status.RUNNING);
    }

}
