package com.timemachine.toci;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import android.support.v7.widget.Toolbar;

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
    private final String GET_CROWDS_TASK_RUNNING = "GET_CROWDS_TASK_RUNNING";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_ACCESS_CAMERA = 2;
    private String UBER_CLIENT_ID;
    private String LYFT_CLIENT_ID;
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
    private Network mNetwork;
    private File mCapturedFile;
    private String mCapturedImagePath;
    Context mContext;
    AppPrefs mAppPrefs;

    // Action buttons
    private Boolean isFabMenuOpen = false;
    private FloatingActionButton mNavigateButton;
    private FloatingActionButton mRideUberButton;
    private TextView mRideUberLabel;
    private FloatingActionButton mRideLyftButton;
    private TextView mRideLyftLabel;
    private FloatingActionButton mRideGmapsButton;
    private TextView mRideGmapsLabel;
    private Animation fadeIn, fadeOut;

    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livepics_gallery);

        // Set up preferences resources
        mContext = this;
        mAppPrefs = new AppPrefs(mContext);

        // Instantiate mNetwork helper class
        mNetwork = new Network(this);

        // Get crowd from previous activity
        mThisLiveCrowd = SerializeLiveCrowd.fromJson(getIntent().getExtras().getString("crowd"));
        mToolbar = findViewById(R.id.toolbar_actionbar);
        // Set the actionbar mTitle
        mToolbar.setTitle(mThisLiveCrowd.getTitle());
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

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
        mViewPager = findViewById(R.id.pager);
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
                // Empty
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
        mNavigateButton = findViewById(R.id.fab_ride_crowd);
        mRideUberButton = findViewById(R.id.fab_ride_uber);
        mRideUberLabel = findViewById(R.id.uber_label);
        mRideLyftButton = findViewById(R.id.fab_ride_lyft);
        mRideLyftLabel = findViewById(R.id.lyft_label);
        mRideGmapsButton = findViewById(R.id.fab_ride_gmaps);
        mRideGmapsLabel = findViewById(R.id.gmpas_label);
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
        return super.onCreateOptionsMenu(menu);
    }


    /**
     * Handle menu items selection.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

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
                if (mNetwork.isOnline()) {
                    try {
                        mCurrImage = mPicUrls.get(mViewPager.getCurrentItem());
                        String imageId = mCurrImage.get(2);
                        String imageUrl = mCurrImage.get(0);
                        new ReportImageTask(this).execute(imageId, imageUrl);
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(getApplicationContext(), R.string.report_image_not_available,
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
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            new UploadFileToServer().execute();
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
            final ImageView liveImageView = rootView.findViewById(R.id.livePic);
            final ArrayList<String> image;
            mDetailsContainer = rootView.findViewById(R.id.details_container);
            mStreetAddressView = mDetailsContainer.findViewById(R.id.street_address);
            mTimeStampView = mDetailsContainer.findViewById(R.id.timestamp);
            mHelperView = mDetailsContainer.findViewById(R.id.image_container);
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
                        hideActionBar();
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
                        showActionBar();
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
            return ((AppCompatActivity) getActivity()).getSupportActionBar();
        }

        protected void hideActionBar(){
            final ActionBar ab = getActionBar();
            final Toolbar mToolbar = ((AppCompatActivity) getActivity()).findViewById(R.id.toolbar_actionbar);
            if (ab != null && ab.isShowing()) {
                if(mToolbar != null) {
                    mToolbar.animate().translationY(-112).setDuration(100L)
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    ab.hide();
                                }
                            }).start();
                } else {
                    ab.hide();
                }
            }
        }

        protected void showActionBar(){
            final ActionBar ab = getActionBar();
            final Toolbar mToolbar = ((AppCompatActivity) getActivity()).findViewById(R.id.toolbar_actionbar);
            if (ab != null && !ab.isShowing()) {
                ab.show();
                if(mToolbar != null) {
                    mToolbar.animate().translationY(0).setDuration(100L).start();
                }
            }
        }
    }


    /**
     * Uploading the file to server on a different thread than the main UI tread.
     */
    private class UploadFileToServer extends AsyncTask<Void, Integer, Integer> {

        public UploadFileToServer() {
        }

        @Override
        protected Integer doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private Integer uploadFile() {

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

                Bitmap bMap = BitmapFactory.decodeFile(mCapturedImagePath);
                ExifInterface exif = new ExifInterface(mCapturedImagePath);
                int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int rotationInDegrees = exifToDegrees(rotation);
                Matrix matrix = new Matrix();
                if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}
                Bitmap rotatedBitmap = Bitmap.createBitmap(bMap,0,0, bMap.getWidth(), bMap.getHeight(), matrix, true);

                OutputStream fOut = new BufferedOutputStream(new FileOutputStream(mCapturedFile));
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.flush();
                fOut.close();
                bMap.recycle();
                rotatedBitmap.recycle();

                entity.addPart("image", new FileBody(mCapturedFile));
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
                    return 200;
                } else {
                    return 500;
                }

            } catch (ClientProtocolException e) {
                e.printStackTrace();
                return 500;
            } catch (IOException e) {
                e.printStackTrace();
                return 500;
            } catch (Exception e) {
                e.printStackTrace();
                return 500;
            }
        }

        /**
         * Gets the Amount of Degrees of rotation using the exif integer to determine how much
         * we should rotate the image.
         * @param exifOrientation - the Exif data for Image Orientation
         * @return - how much to rotate in degrees
         */
        private int exifToDegrees(int exifOrientation) {
            if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
            else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
            else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer resultCode) {
            super.onPostExecute(resultCode);
            if (resultCode == 200) {
                Toast.makeText(getApplicationContext(), R.string.upload_image_success,
                        Toast.LENGTH_SHORT).show();
                refreshCrowd();
            } else {
                Toast.makeText(getApplicationContext(), R.string.generic_error,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Created by Victor Ruelas on 10/24/17.
     */
    private static class ReportImageTask extends AsyncTask<String, Void, Integer> {

        private WeakReference<LivePicsGalleryActivity> activityReference;

        ReportImageTask(LivePicsGalleryActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected Integer doInBackground(String... params) {
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
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    return 200;
                } else {
                    return 500;
                }

            } catch (ClientProtocolException e) {
                e.printStackTrace();
                return 500;
            } catch (IOException e) {
                e.printStackTrace();
                return 500;
            }
        }

        @Override
        protected void onPostExecute(Integer responseCode) {
            super.onPostExecute(responseCode);

            // get a reference to the activity if it is still there
            LivePicsGalleryActivity activity = activityReference.get();
            if (activity == null) return;

            if (responseCode == 200) {
                activity.refreshCrowd();
                Toast.makeText(activity, R.string.report_image_success, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, R.string.generic_error, Toast.LENGTH_SHORT).show();
            }

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

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                mCapturedFile = createImageFile();
            } catch (IOException ex) {
                // Handle error quietly
            }
            if (mCapturedFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.timemachine.toci", mCapturedFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } else {
            Toast.makeText(getApplicationContext(), "No camera app installed",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Helper Methods for handling pictures taken
     */

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir + File.separator
                + "IMG_" + timeStamp + ".jpg");

        // Save a file: path for use with ACTION_VIEW intents
        mCapturedImagePath = image.getAbsolutePath();
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
                    pageChangeListener.onPageSelected(mViewPager.getCurrentItem());

                }
            });
            mGetCrowdsTask.execute(FETCH_CROWDS_FILTER, mThisLiveCrowd.getId());
        } else {
            Toast.makeText(getApplicationContext(), R.string.error_offline,
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
