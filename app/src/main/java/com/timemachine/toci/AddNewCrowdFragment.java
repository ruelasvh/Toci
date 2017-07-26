package com.timemachine.toci;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Victor Ruelas on 3/16/16.
 * Copyright (c) 2016 CrowdZeeker, LLC. All rights reserved.
 */
public class AddNewCrowdFragment extends Fragment
        implements
        OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // Identifier for debugging
    private static final String TAG = AddNewCrowdFragment.class.getSimpleName();

    // used to set title to fragment when it's attached to the activity
    private static final String SECTION_TITLE = "AddNewCrowdFragment";

    final private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    /**
     * Request code passed to the PlacePicker intent to identify its result when it returns.
     */
    private static final int REQUEST_PLACE_PICKER = 1;

//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    // Helper class for determining network availability
    private Network network;

    // Floating bar action button
    FloatingActionButton fab;

    private GoogleMap mMap;
    private LatLng mLatLng;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Context mContext;
    // AsyncTask to fetch crowds
    private GetCrowds mGetCrowdsTask;
    private Map<Marker, LiveCrowd> mAllMarkersMap = new HashMap<>();


    public AddNewCrowdFragment() {
        // Required empty public constructor
    }

    //    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment AddNewCrowdFragment
//     */
    // TODO: Rename and change types and number of parameters
    public static AddNewCrowdFragment newInstance(/*String param1, String param2*/) {
        AddNewCrowdFragment fragment = new AddNewCrowdFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }

        // Instantiate Network helper class
        mContext = getContext();
        network = new Network(mContext);
        mGetCrowdsTask = null;

        // Instantiate Google Location API
        buildGoogleLocationApi();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_new_crowd, parent, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Floating bar action button
        fab = (FloatingActionButton) view.findViewById(R.id.fab_add_new_crowd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (network.isOnline()) {
                    try {
                        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                        Intent intent = intentBuilder.build(getActivity());
                        // Start the Intent by requesting a result, identified by a request code.
                        startActivityForResult(intent, REQUEST_PLACE_PICKER);

                    } catch (GooglePlayServicesRepairableException e) {
                        GooglePlayServicesUtil
                                .getErrorDialog(e.getConnectionStatusCode(), getActivity(), 0);
                    } catch (GooglePlayServicesNotAvailableException e) {
                        Toast.makeText(getActivity(), "Google Play Services is not available.",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                } else {
                    Toast.makeText(getContext().getApplicationContext(), R.string.error_offline,
                            Toast.LENGTH_SHORT).show();
                }

                // Disable button after first click
                fab.setEnabled(false);
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if ( ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {
            mMap.setMyLocationEnabled(true);
            // Disable Toolbar i.e. Directions and Google Maps buttons
            mMap.getUiSettings().setMapToolbarEnabled(false);
        } else {
            Toast.makeText(getActivity(), "Enable GPS and location services.",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if ( ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
//                Log.d(this.getClass().getSimpleName(),String.valueOf(mLastLocation.getLatitude()));
//                Log.d(this.getClass().getSimpleName(),String.valueOf(mLastLocation.getLongitude()));
                LatLng currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            }
        } else {
            Toast.makeText(getActivity(), "Enable GPS and location services.",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently

        // ...
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently

        // ...
    }

    /* Called after the autocomplete activity has finished to return its result. */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_PLACE_PICKER) {
            if (resultCode == Activity.RESULT_OK) {
                // Get the user's selected place from the Intent.
//                final Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                final Place place = PlacePicker.getPlace(data, getActivity());

//============= After selecting place insert to database immediately ========================//
                new AddCrowdToDatabase(){
                    @Override
                    public void onPostExecute(String result) {
                        Snackbar.make(getActivity().findViewById(R.id.root_add_new_crowd),
                                result, Snackbar.LENGTH_LONG)
                                .setAction("UNDO", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        new RemoveCrowdFromDatabase() {
                                            @Override
                                            public void onPostExecute(String result) {
                                                mMap.clear();
                                            }
                                        }.execute(place.getId(), getCity(place.getAddress()));
                                    }
                        }).show();

                        mGetCrowdsTask = new GetCrowds(getActivity(), new GetCrowds.AsyncResponse() {
                            @Override
                            public void onAsyncTaskFinish(LiveCrowd[] crowds) {
                                setMarker(crowds);
                            }
                        });
                        mGetCrowdsTask.execute("BY_ID", place.getId());
                    }
                }.execute(
                        place.getId(),
                        place.getName().toString(),
                        place.getAddress().toString(),
                        cleanLatLng(place.getLatLng().toString()),
                        getCity(place.getAddress())
                );

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                Log.e(getTag(), "Error: Status = " + status.toString());
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Indicates that the activity closed before a selection was made. For example if
                // the user pressed the back button.
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();

//        Log.d(TAG, "Resumed");
        fab.setEnabled(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Set Fragment's title in parent activity
        ((HomeMaterialActivity) context).onSectionAttached(SECTION_TITLE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public class InsertDatabaseListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

//            Log.d(TAG, "Action detected");

        }
    }

    public void setMarker(LiveCrowd[] liveCrowds) {
        for (int i = 0; i < liveCrowds.length; i++) {
            String[] latLngParts = liveCrowds[i].getLatlng().split(",");
            double lat = Double.parseDouble(latLngParts[0]);
            double lng = Double.parseDouble(latLngParts[1]);

            mLatLng = new LatLng(lat, lng);

            Marker marker = mMap.addMarker(new MarkerOptions().position(mLatLng)
                    .title(liveCrowds[i].getTitle()));
            marker.showInfoWindow();
            mAllMarkersMap.put(marker, liveCrowds[i]);
            mMap.setOnInfoWindowClickListener(this);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        LiveCrowd liveCrowd = mAllMarkersMap.get(marker);
        Intent intent = new Intent(mContext, LivePicsGalleryActivity.class);
        intent.putExtra("crowd", SerializeLiveCrowd.toJson(liveCrowd));
        mContext.startActivity(intent);
    }

    private static String getCity(CharSequence address) {

        String city = "";
        boolean[] separators = new boolean[address.length()];
        int[] separatorIndeces = new int[5];
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
                separatorIndeces[counter] = k;
                counter++;
            }
        }

        city = address.subSequence(separatorIndeces[0]+2, separatorIndeces[1]).toString();

        return city;
    }

    // Get short address for displaying in the details container
    private static String cleanLatLng(String latLng) {

        return latLng.substring(latLng.indexOf("(")+1, latLng.indexOf(")"));
    }

    public void buildGoogleLocationApi() {
        // Create instance of GoogleApiClient
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    // End of Fragment code
}
