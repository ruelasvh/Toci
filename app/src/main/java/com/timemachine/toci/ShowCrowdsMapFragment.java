package com.timemachine.toci;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Victor Ruelas on 9/24/16.
 */
public class ShowCrowdsMapFragment extends Fragment
        implements
        OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String ARG_FETCH_CROWDS_PARAM_FILTER = "fetchCrowdsParamFilter";
    private static final String ARG_FETCH_CROWDS_PARAM = "fetchCrowdsParam";
    private static final String ARG_FETCH_CROWDS_PARAM_LIST = "fetchCrowdsParamList";
    private String FETCH_CROWDS_FILTER;
    private String CROWDS;
    private ArrayList<String> CROWDS_LIST;
    public GoogleMap mGoogleMap; // Object of google map
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private GetCrowds mGetCrowdsTask; // AsyncTask to fetch crowds
    private ProgressDialog mFetchCrowdsProgressDialog; // To show progress of fetching crowds
    private OnFragmentInteractionListener mListener; // Interface to help communicate up to the parent activity
    private Network mNetwork; // Class to check mNetwork status
    Context mContext;
    private Map<Marker, LiveCrowd> mAllMarkersMap = new HashMap<>();

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(int position);
    }

    public ShowCrowdsMapFragment() {
        // Required empty constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param fetchCrowdsParamFilter fetch crowds by city or id.
     * @param fetchCrowdsParam city or ids.
     * @return A new instance of fragment ShowCrowdsListFragment.
     */
    public static ShowCrowdsMapFragment newInstance(String fetchCrowdsParamFilter, String fetchCrowdsParam, ArrayList<String> crowds) {
        ShowCrowdsMapFragment fragment = new ShowCrowdsMapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FETCH_CROWDS_PARAM_FILTER, fetchCrowdsParamFilter);
        args.putString(ARG_FETCH_CROWDS_PARAM, fetchCrowdsParam);
        args.putStringArrayList(ARG_FETCH_CROWDS_PARAM_LIST, crowds);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This fragment has it's own toolbar menu, so display it
        setHasOptionsMenu(true);
        mContext = getContext();

        if (getArguments() != null) {
            FETCH_CROWDS_FILTER = getArguments().getString(ARG_FETCH_CROWDS_PARAM_FILTER);
            CROWDS = getArguments().getString(ARG_FETCH_CROWDS_PARAM);
            CROWDS_LIST = getArguments().getStringArrayList(ARG_FETCH_CROWDS_PARAM_LIST);
        }

        // Instantiate Network helper class
        mNetwork = new Network(mContext);
        mGetCrowdsTask = null;

        // Instantiate Google Location API
        buildGoogleLocationApi();
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (CROWDS_LIST == null) {
            mFetchCrowdsProgressDialog = new ProgressDialog(mContext,
                    R.style.AuthenDialogStyle);
            mFetchCrowdsProgressDialog.setIndeterminate(true);
            mFetchCrowdsProgressDialog.setMessage("Finding awsome LiveCrowds...");
            mFetchCrowdsProgressDialog.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_list:
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Fragment listFragment = ShowCrowdsListFragment.newInstance(FETCH_CROWDS_FILTER, CROWDS);
                fragmentManager.beginTransaction().replace(R.id.container, listFragment)
                        .addToBackStack(null).commit();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_show_crowds_map, menu);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
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
    public void onResume() {
        super.onResume();
        // Resume with updating crowds
//        fetchLiveCrowds();
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
            // Enable Toolbar i.e. Directions and Google Maps buttons
            mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
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
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));

                fetchLiveCrowds();
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

    private void fetchLiveCrowds() {
        if (mNetwork.isOnline()) {
            if (CROWDS_LIST != null) {
                setMarkers(SerializeLiveCrowd.fromJson(CROWDS_LIST));
            } else {
                mGetCrowdsTask = new GetCrowds(getActivity(), new GetCrowds.AsyncResponse() {
                    @Override
                    public void onAsyncTaskFinish(LiveCrowd[] crowds) {
                        setMarkers(crowds);
                        if (mFetchCrowdsProgressDialog != null) {
                            mFetchCrowdsProgressDialog.dismiss();
                        }
                    }
                });
                mGetCrowdsTask.execute(FETCH_CROWDS_FILTER, CROWDS);
            }
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
                                    mListener.onFragmentInteraction(searchFragPosition);
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
                    .title(liveCrowds[i].getTitle()));
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
