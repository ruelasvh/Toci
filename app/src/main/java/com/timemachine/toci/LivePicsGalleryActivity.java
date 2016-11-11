package com.timemachine.toci;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.places.Places;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;


public class LivePicsGalleryActivity extends AppCompatActivity implements OnConnectionFailedListener {

    /**
     * Logcat tag
     */
    private static final String TAG = LivePicsGalleryActivity.class.getSimpleName();

    /**
     * Camera activity request codes
     */
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
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    /**
     * Layout which holds details about crowds
     */
    RelativeLayout mDetailsLayout;

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
     * Variable to hold crowd passed from CityActivity
     */
    private static LiveCrowd thisLiveCrowd;

    /**
     * String array to hold picture urls passed from previous activity
     */
    private static HashMap<Integer, ArrayList<String>> picUrls;

    /**
     * Helper fields to help store favorite settings
     */
    Context mContext;
    AppPrefs mAppPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.livepics_gallery);

        // Get crowd from CityActivity->LiveCrowdListAdapter
        thisLiveCrowd = SerializeLiveCrowd.fromJson(getIntent().getExtras().getString("crowd"));
        picUrls = thisLiveCrowd.getPicUrls();

        // Create the adapter that will return a fragment for each of the five
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Set views for the crowds details
        mDetailsLayout = (RelativeLayout) findViewById(R.id.details_container);
        final ImageView mNavigate = (ImageView) findViewById(R.id.navigate);


        // ImageView which takes up space so that onclicklistener from livepic is not triggered
        ImageView detailsView = (ImageView) findViewById(R.id.image_container);
        detailsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Empty on purpose
            }
        });

        // Set up preferences resources
        mContext = getApplicationContext();
        mAppPrefs = new AppPrefs(mContext);

        // Set the actionbar title
        setTitle(thisLiveCrowd.getTitle());

//        Log.d(TAG, thisLiveCrowd.getPicUrls().toString());
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
                captureImage();
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
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
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
//                Toast.makeText(getApplicationContext(), "User cancelled image capture",
//                        Toast.LENGTH_SHORT).show();
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
//            int num_pages = SerializeLiveCrowd.fromJson(getIntent().getExtras().getString("crowd")).getPicUrls().size();
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
            final ImageView liveImageView = (ImageView) rootView.findViewById(R.id.livePic);
            final TextView timeStampView = (TextView) getDetailsContainer().findViewById(R.id.timestamp);

            /* Set up behavior to toggle toolbar and details container */
            liveImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getActionBar().isShowing()) {
                        // Hide the actionbar/toolbar
                        getActionBar().hide();
                        // Animation fade out of the details container
                        slideToBottom(getDetailsContainer());
                    } else {
                        // Show the actionbar/toolbar
                        getActionBar().show();
                        // Animation fade in of the details container
                        slideToTop(getDetailsContainer());
                    }
                }
            });

            /* Load pictures in pager */
            switch (this.getPageNum()) {
                case 1:
                    Picasso.with(getActivity()).load( picUrls.get( picUrls.size()-1 ).get(0) )
                            .into( liveImageView );
//                    timeStampView.setText( picUrls.get( picUrls.size()-1 ).get(1));
//                    timeStampView.setText("First image");
                    ((TextView)getDetailsContainer().findViewById(R.id.timestamp)).setText("First image");
                    break;
                case 2:
                    Picasso.with(getActivity()).load( picUrls.get( picUrls.size()-2 ).get(0) )
                            .into(liveImageView);
//                    timeStampView.setText( picUrls.get( picUrls.size()-2 ).get(1) );
//                    timeStampView.setText("Second image");
                    ((TextView)getDetailsContainer().findViewById(R.id.timestamp)).setText("Second image");
                    break;
                case 3:
                    Picasso.with(getActivity()).load( picUrls.get( picUrls.size()-3 ).get(0) )
                            .into(liveImageView);
//                    timeStampView.setText( picUrls.get( picUrls.size()-3 ).get(1) );
//                    timeStampView.setText("Third image");
                    ((TextView)getDetailsContainer().findViewById(R.id.timestamp)).setText("Third image");
                    break;
                case 4:
                    Picasso.with(getActivity()).load( picUrls.get( picUrls.size()-4 ).get(0) )
                            .into( liveImageView );
//                    timeStampView.setText( picUrls.get( picUrls.size()-4 ).get(1) );
//                    timeStampView.setText("Fourth image");
                    ((TextView)getDetailsContainer().findViewById(R.id.timestamp)).setText("Fourth image");
                    break;
                case 5:
                    Picasso.with(getActivity()).load( picUrls.get( picUrls.size()-5 ).get(0) )
                            .into( liveImageView );
//                    timeStampView.setText( picUrls.get( picUrls.size()-5 ).get(1) );
//                    timeStampView.setText("Fifth image");
                    ((TextView)getDetailsContainer().findViewById(R.id.timestamp)).setText("Fifth image");
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
    } // End of PlaceHolderFragment


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
                    // TODO: Handle exception
                }

                entity.addPart("image", new FileBody(resizedFile));
                entity.addPart("id",
                        new StringBody(thisLiveCrowd.getId()));
                entity.addPart("city",
                        new StringBody(thisLiveCrowd.getCity()));
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
            Log.e(TAG, "Response from server: " + result);

            super.onPostExecute(result);
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
                Log.d(TAG, "Failed to create " + Config.IMAGE_DIRECTORY_NAME + " directory");
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
                Log.d(TAG, "Failed to create " + Config.IMAGE_DIRECTORY_NAME + " directory");
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

        // Debug
        if (mAppPrefs.getFavorite_crowds() != null) {
            for (String element : mAppPrefs.getFavorite_crowds()) {
                Log.i(TAG, element);
            }
        }

    }

    private void refreshCrowd() {
        new GetCrowds(new GetCrowds.AsyncResponse() {
            @Override
            public void onAsyncTaskFinish(LiveCrowd[] crowds) {
//                Log.d(TAG + "onAsyncFinish", crowds[0].getId());
                picUrls = crowds[0].getPicUrls();
                mSectionsPagerAdapter.notifyDataSetChanged();
                mViewPager.setAdapter(mSectionsPagerAdapter);
            }
        }).execute("favorites", thisLiveCrowd.getId());
    }

}
