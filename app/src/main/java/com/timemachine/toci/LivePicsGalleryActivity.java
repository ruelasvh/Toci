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
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
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
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.TitlePageIndicator;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by Victor Ruelas on 4/5/16.
 */
public class LivePicsGalleryActivity extends AppCompatActivity implements OnConnectionFailedListener {

    private final static String FETCH_CROWDS_FILTER = "BY_ID";
    private String UBER_CLIENT_ID;
    private String LYFT_CLIENT_ID;
    private static final int REQUEST_ACCESS_CAMERA = 2;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener pageChangeListener;
    long mTotalSize = 0;
    private static LiveCrowd mThisLiveCrowd;
    private String mTitle;
    private double mLatitude;
    private double mLongitude;
    private static HashMap<Integer, ArrayList<String>> mPicUrls;
    public static ArrayList<String> mCurrImage;
    private GetCrowds mGetCrowdsTask;
    private final String GET_CROWDS_TASK_RUNNING = "GET_CROWDS_TASK_RUNNING";
    private Network mNetwork;
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

        // Set up preferences resources
        mContext = this;
        mAppPrefs = new AppPrefs(mContext);

        // Instantiate mNetwork helper class
        mNetwork = new Network(this);

        // Get crowd from previous activity
        mThisLiveCrowd = SerializeLiveCrowd.fromJson(getIntent().getExtras().getString("crowd"));
        // Set the actionbar mTitle
        setTitle(mThisLiveCrowd.getTitle());
        mPicUrls = mThisLiveCrowd.getPicUrls();
        try {
            mTitle = URLEncoder.encode(mThisLiveCrowd.getTitle(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            mTitle = "";
        }
        String[] latlng = mThisLiveCrowd.getLatlng().split(",");
        mLatitude = Double.parseDouble(latlng[0]);
        mLongitude = Double.parseDouble(latlng[1]);

        // Get Uber and Lyft Client IDs
        UBER_CLIENT_ID = getString(R.string.uber_client_id);
        LYFT_CLIENT_ID = getString(R.string.lyft_client_id);

        // Create the adapter that will return a fragment for each of the five
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Bind invisible title indicator to the adapter (to be able to get current page and current crowd id)
        TitlePageIndicator titlePageIndicator = (TitlePageIndicator) findViewById(R.id.titles);
        titlePageIndicator.setViewPager(mViewPager);
        pageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Empty
            }

            @Override
            public void onPageSelected(int position) {
                /* Load pictures in pager */
                Integer size = mPicUrls.size();
                Integer currIndex = size - (position + 1);
                mCurrImage = mPicUrls.get(currIndex);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Empty
            }
        };
        titlePageIndicator.setOnPageChangeListener(pageChangeListener);
        // Force onPageSelected when fragment is created
        // Do this in a runnable to make sure the viewPager's views are already instantiated before
        // triggering the onPageSelected call
        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                pageChangeListener.onPageSelected(mViewPager.getCurrentItem());
            }
        });

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
                        "&dropoff[latitude]=" + mLatitude +
                        "&dropoff[longitude]=" + mLongitude +
                        "8&dropoff[nickname]=" + mTitle;
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
                        "&destination[latitude]=" + mLatitude +
                        "&destination[longitude]=" + mLongitude;
                String signupLink = "https://www.lyft.com/signup/SDKSIGNUP?clientId=" + LYFT_CLIENT_ID +
                        "&sdkName=android_direct";
                openDeeplink(packageId, uri, signupLink);
            }
        });
        mRideGmapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String packageId = "com.google.android.apps.maps";
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", mLatitude, mLongitude, mTitle);
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
    }

    /**
     * Inflate the menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_live_pics_gallery, menu);
        if (mAppPrefs.getFavorite_crowds().contains(mThisLiveCrowd.getId())) {
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
                if (mNetwork.isOnline()) {
                    if (mAppPrefs.hasCameraAccess()) {
                        dispatchTakePictureIntent();
                    } else {
                        mAppPrefs.setCameraPermissions();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_offline,
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_favorite_toggle:
                // Save to favorites
                if (mAppPrefs.getFavorite_crowds().contains(mThisLiveCrowd.getId())) {
                    item.setIcon(R.drawable.ic_action_star_off);
                    item.setTitle("Remove From Favorites");
                    saveToFavs(mThisLiveCrowd.getId());
                } else {
                    item.setIcon(R.drawable.ic_action_star_on);
                    item.setTitle("Add To Favorites");
                    saveToFavs(mThisLiveCrowd.getId());
                }
                return true;
            case R.id.action_report_image:
                // Report image and mark it public = FALSE
                if (mNetwork.isOnline()) {
                    try {
                        String imageId = mCurrImage.get(2);
                        String imageUrl = mCurrImage.get(0);
                        new ReportImageTask(this).execute(imageId, imageUrl);
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(getApplicationContext(), "Cannot report this image",
                                Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_offline,
                            Toast.LENGTH_SHORT).show();
                }
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    // Store the file url as it will be null after returning from camera app
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save state of mGetCrowdsTask
        if (isTaskRunning()) {
            outState.putBoolean(GET_CROWDS_TASK_RUNNING, true);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // check if mGetCrowdsTask is running so it can be restarted
        if (savedInstanceState.getBoolean(GET_CROWDS_TASK_RUNNING, false)) {
            refreshCrowd();
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_ACCESS_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(getApplicationContext(), R.string.error_camera_permission,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Receiving activity result method will be called after closing the camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing image
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image. Upload picture to server
                new UploadFileToServer().execute();
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
            int num_pages = mPicUrls.size();
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
        private RelativeLayout mDetailsContainer;
        private TextView mStreetAddressView;
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
            final ArrayList<String> image;
            mDetailsContainer = (RelativeLayout) rootView.findViewById(R.id.details_container);
            mStreetAddressView = (TextView) mDetailsContainer.findViewById(R.id.street_address);
            mTimeStampView = (TextView) mDetailsContainer.findViewById(R.id.timestamp);
            mHelperView = (ImageView) mDetailsContainer.findViewById(R.id.image_container);
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

            /* Set crowd's address */
            mStreetAddressView.setText(mThisLiveCrowd.getAddress());
            /* Load pictures in pager */
            Integer size = mPicUrls.size();
            Integer pageNum = this.getPageNum();
            switch (pageNum) {
                case 1:
                    image = mPicUrls.get( pageNum - 1 );
                    Picasso.with(getActivity()).load( image.get(0) )
                            .into(liveImageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    mTimeStampView.setText(image.get(1));
                                }

                                @Override
                                public void onError() {
                                    // Silent Error
                                }
                            });
                    break;
                case 2:
                    image = mPicUrls.get( pageNum - 1 );
                    Picasso.with(getActivity()).load( image.get(0) )
                            .into(liveImageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    mTimeStampView.setText(image.get(1));
                                }

                                @Override
                                public void onError() {
                                    // Silent Error
                                }
                            });
                    break;
                case 3:
                    image = mPicUrls.get( pageNum - 1 );
                    Picasso.with(getActivity()).load( image.get(0) )
                            .into(liveImageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    mTimeStampView.setText(image.get(1));
                                }

                                @Override
                                public void onError() {
                                    // Silent Error
                                }
                            });
                    break;
                case 4:
                    image = mPicUrls.get( pageNum - 1 );
                    Picasso.with(getActivity()).load( image.get(0) )
                            .into( liveImageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    mTimeStampView.setText(image.get(1));
                                }

                                @Override
                                public void onError() {
                                    // Silent Error
                                }
                            });
                    break;
                case 5:
                    image = mPicUrls.get( pageNum - 1 );
                    Picasso.with(getActivity()).load( image.get(0) )
                            .into( liveImageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    mTimeStampView.setText(image.get(1));
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
    }


    /**
     * Uploading the file to server on a different thread than the main UI tread.
     */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {

        public UploadFileToServer() {
        }

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
                                publishProgress((int) ((num / (float) mTotalSize) * 100));
                            }
                        });

                /**
                 * Shrink image
                 */
                // get original dimensions
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(mCurrentPhotoPath, options);
                int originalWidth = options.outWidth;
                int originalHeight = options.outHeight;
                int newWidth = -1;
                int newHeight = -1;
                float mulFactor;
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
                Bitmap bMap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                Bitmap scaledBmap = Bitmap.createScaledBitmap(bMap, newWidth, newHeight, false);
                File resizedFile = createImageFile();

                try {
                    OutputStream fOut = new BufferedOutputStream(new FileOutputStream(resizedFile));
                    scaledBmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                    bMap.recycle();
                    scaledBmap.recycle();
                } catch (Exception e) {
                    // Handle exception
                }

                entity.addPart("image", new FileBody(resizedFile));
                entity.addPart("id", new StringBody(mThisLiveCrowd.getId()));
                entity.addPart("city", new StringBody(mThisLiveCrowd.getCity()));
                entity.addPart("state", new StringBody(mThisLiveCrowd.getState()));
                entity.addPart("country", new StringBody(mThisLiveCrowd.getCountry()));
                mTotalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = "Photo uploaded!";
                } else {
                    responseString = "Error occurred! Photo not uploaded";
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String resultMessage) {
            super.onPostExecute(resultMessage);
            Toast.makeText(getApplicationContext(), resultMessage,
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Created by Victor Ruelas on 10/24/17.
     */
    private static class ReportImageTask extends AsyncTask<String, Void, String> {

        private WeakReference<LivePicsGalleryActivity> activityReference;

        ReportImageTask(LivePicsGalleryActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(String... params) {
            String paramImageId = params[0];
            String paramImageUrl = params[1];

            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("id", paramImageId));
            nameValuePairs.add(new BasicNameValuePair("image_url", paramImageUrl));

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Config.REPORT_IMAGE_URL);
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpClient.execute(httpPost);
                String responseMessage = EntityUtils.toString(response.getEntity());
                return responseMessage;

            } catch (ClientProtocolException e) {
                return "Failed to report image: ClientProtocolException.";
            } catch (IOException e) {
                return "Failed to report image: IOException";
            }

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // get a reference to the activity if it is still there
            LivePicsGalleryActivity activity = activityReference.get();
            if (activity == null) return;

            activity.refreshCrowd();
            Toast.makeText(activity, R.string.report_image_success_message,
                    Toast.LENGTH_SHORT).show();
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
    static final int REQUEST_TAKE_PHOTO = 9;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.timemachine.toci",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        } else {
            Toast.makeText(getApplicationContext(), "No camera app installed",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Helper Methods for handling pictures taken
     */
    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir + File.separator
                + "IMG_" + timeStamp + ".jpg");

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
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
        if (mNetwork.isOnline()) {
            mGetCrowdsTask = new GetCrowds(this, new GetCrowds.AsyncResponse() {
                @Override
                public void onAsyncTaskFinish(LiveCrowd[] crowds) {
                    mPicUrls = crowds[0].getPicUrls();
                    mSectionsPagerAdapter.notifyDataSetChanged();
                    mViewPager.setAdapter(mSectionsPagerAdapter);
                    // triggering the onPageSelected to reset mCurrImage
                    pageChangeListener.onPageSelected(mViewPager.getCurrentItem());

                }
            });
            mGetCrowdsTask.execute(FETCH_CROWDS_FILTER, mThisLiveCrowd.getId());
        } else {
            Toast.makeText(getApplicationContext(), "No Connection Available",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void cancelRefreshCrowd() {
        if (mGetCrowdsTask != null) {
            mGetCrowdsTask.cancel(true);
        }
    }

    private boolean isTaskRunning() {
        return (mGetCrowdsTask != null) && (mGetCrowdsTask.getStatus() == AsyncTask.Status.RUNNING);
    }
}
