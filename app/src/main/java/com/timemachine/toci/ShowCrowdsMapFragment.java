package com.timemachine.toci;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
 * A simple {@link Fragment} subclass.
 */
public class ShowCrowdsMapFragment extends Fragment
        implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    /*object of google map*/
    public GoogleMap mGoogleMap;
    // AsyncTask to fetch crowds
    private GetCrowds mGetCrowdsTask;
    // Interface to help communicate up to the parent activity
    private OnFragmentSelectedListener mListener;
    // Class to check mNetwork status
    private Network mNetwork;
    // Helper fields to help store favorite settings
    Context mContext;
    AppPrefs mAppPrefs;
    private Map<Marker, LiveCrowd> mAllMarkersMap = new HashMap<>();

    public interface OnFragmentSelectedListener {
        void onNavDrawerItemSelected(int position);
    }

    public ShowCrowdsMapFragment() {
        // Required empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This fragment has it's own toolbar menu, so display it
        setHasOptionsMenu(true);
        // Instantiate Network helper class
        mNetwork = new Network(getContext());
        mGetCrowdsTask = null;
        // Set up preferences
        mContext = getContext();
        mAppPrefs = new AppPrefs(mContext);
        // Set up interface between NavDrawer and this Fragment
        mListener = (OnFragmentSelectedListener) mContext;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_crowds_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.crowds_map);
        mapFragment.getMapAsync(this);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (OnFragmentSelectedListener) mContext;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentSelectedListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Resume with updating crowds
        fetchLiveCrowds();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Cancel fetching crowds
        cancelAsyncGetCrowdsTask();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        if ( ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {
            mGoogleMap.setMyLocationEnabled(true);

            // Get current location
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), false));
            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

            fetchLiveCrowds();

            // Move map to current location
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));

            // Enagle Toolbar i.e. Directions and Google Maps buttons
            mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        } else {
            Toast.makeText(getActivity(), "Enable GPS and location services.",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void fetchLiveCrowds() {
        // Clear map and all markers
        if (mGoogleMap != null) {
            mGoogleMap.clear();
            mAllMarkersMap.clear();
        }

        String crowdIds = TextUtils.join(",", mAppPrefs.getFavorite_crowds());

        if (mNetwork.isOnline()) {
            mGetCrowdsTask = new GetCrowds(getActivity(), new GetCrowds.AsyncResponse() {
                @Override
                public void onAsyncTaskFinish(LiveCrowd[] crowds) {
                    setMarkers(crowds);
                }
            });
            mGetCrowdsTask.execute("BY_ID", crowdIds);
        } else {
            Toast.makeText(getContext().getApplicationContext(), R.string.error_offline,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void setMarkers(LiveCrowd[] liveCrowds) {
        LatLng latLng;

        if (liveCrowds.length == 0) {
            mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    Snackbar.make(((Activity) mContext)
                            .findViewById(R.id.show_crowds_map_container),
                            "No LiveCrowds saved.", Snackbar.LENGTH_LONG)
                            .setAction("FIND SOME", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Launch SearchFragment and select it in NavDrawer
                                    int searchFragPosition = 0;
                                    mListener.onNavDrawerItemSelected(searchFragPosition);
                                }
                            })
                            .show();
                }
            });
            return;
        }

        for (int i = 0; i < liveCrowds.length; i++) {
            String[] latLngParts = liveCrowds[i].getLatlng().split(",");
            double lat = Double.parseDouble(latLngParts[0]);
            double lng = Double.parseDouble(latLngParts[1]);

            latLng = new LatLng(lat, lng);

            Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(latLng)
                    .title(liveCrowds[i].getTitle()).snippet("LiveCrowd"));
            mAllMarkersMap.put(marker, liveCrowds[i]);
            mGoogleMap.setOnInfoWindowClickListener(this);
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        LiveCrowd liveCrowd = mAllMarkersMap.get(marker);
        Intent intent = new Intent(mContext, LivePicsGalleryActivity.class);
        intent.putExtra("crowd", SerializeLiveCrowd.toJson(liveCrowd));
        mContext.startActivity(intent);
    }

    private void cancelAsyncGetCrowdsTask() {
        if (mGetCrowdsTask != null) {
            mGetCrowdsTask.cancel(true);
        }
    }

}
