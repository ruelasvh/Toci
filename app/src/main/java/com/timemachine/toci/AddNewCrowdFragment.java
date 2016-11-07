package com.timemachine.toci;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
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

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * Created by Victor Ruelas on 3/16/16.
 * Copyright (c) 2016 CrowdZeeker, LLC. All rights reserved.
 */
public class AddNewCrowdFragment extends Fragment implements OnMapReadyCallback {

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

    private OnFragmentInteractionListener mListener;

    private GoogleMap mMap;
    private LatLng mLatLng;

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_new_crowd, parent, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if ( ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {
            mMap.setMyLocationEnabled(true);
        }

        // Get current location
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), false));
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

        // Move map to current location
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));

        // Disable Toolbar i.e. Directions and Google Maps buttons
        mMap.getUiSettings().setMapToolbarEnabled(false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {

        // Floating bar action button
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
        public void onClick(View view) {
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
            }
        });
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
                new InsertToDatabase(){
                    @Override
                    public void onPostExecute(String result) {
                        Snackbar.make(getActivity().findViewById(R.id.root_add_new_crowd),
                                result, Snackbar.LENGTH_LONG).setAction("UNDO", new InsertDatabaseListener()).show();
                    }
                }.execute(place.getId(),
                        place.getName().toString(), getCity(place.getAddress()));

                setLocation(place.getLatLng(), place.getName().toString());


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                Log.e(getTag(), "Error: Status = " + status.toString());
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Indicates that the activity closed before a selection was made. For example if
                // the user pressed the back button.
            }
        }

    }

    public void setLocation(LatLng latLng, String title) {
        mLatLng = latLng;
        // Add a marker in mLatLng and move the camera
        mMap.addMarker(new MarkerOptions().position(mLatLng).title(title).snippet("Added To Your Crowds"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Set Fragment's title in parent activity
        ((HomeMaterialActivity) context).onSectionAttached(SECTION_TITLE);

        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class InsertDatabaseListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

//            Log.d(TAG, "Action detected");

        }
    }

}
