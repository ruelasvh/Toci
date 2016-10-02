package com.timemachine.toci;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Victor Ruelas on 3/16/16.
 * Copyright (c) 2016 CrowdZeeker, LLC. All rights reserved.
 */
public class AddNewCrowdFragment extends Fragment implements OnMapReadyCallback {

    // used to set title to fragment when it's attached to the activity
    private static final String ARG_SECTION_TITLE = "AddNewCrowdFragment";

    final private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Card searchCard;

    private Button btnUpload;

    private TextView mPlaceDetailsText;
    private TextView mPlaceAttribution;

    private GoogleMap mMap;
    private LatLng mLatLng = new LatLng(37.3894, 122.0819);

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
                .findFragmentById(R.id.web);
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
    }

    public void setLocation(LatLng latLng) {
        mLatLng = latLng;
        // Add a marker in mLatLng and move the camera
        mMap.addMarker(new MarkerOptions().position(mLatLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));
    }



    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {

        // Floating bar action button
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
        public void onClick(View view) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(getActivity());
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });

        mPlaceDetailsText = (TextView) view.findViewById(R.id.place_details);
        mPlaceAttribution = (TextView) view.findViewById(R.id.place_attribution);
        btnUpload = (Button) view.findViewById(R.id.btnUpload);
        btnUpload.setVisibility(View.GONE);
    }

    /* Called after the autocomplete activity has finished to return its result. */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // Get the user's selected place from the Intent.
                final Place place = PlaceAutocomplete.getPlace(getActivity(), data);

                // Format the place's details and display them in a TextView.
                mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(), place.getId(),
                        place.getAddress(), place.getPhoneNumber(), place.getWebsiteUri()));

                // Update the map
                setLocation(place.getLatLng());

                // Display attribution if required.
                CharSequence attributions = place.getAttributions();
                if (!TextUtils.isEmpty(attributions)) {
                    mPlaceAttribution.setText(Html.fromHtml(attributions.toString()));
                } else {
                    mPlaceAttribution.setText("");
                }

                // Upload placeId and name to crowdzeeker database.
                btnUpload.setVisibility(View.VISIBLE);
                btnUpload.setText("Add This Crowd");
                btnUpload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new InsertToDatabase(){
                            @Override
                            public void onPostExecute(String result) {
//                                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
                                Snackbar.make(getActivity().findViewById(android.R.id.content), result,
                                        Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            }
                        }.execute(place.getId(),
                                place.getName().toString(), getCity(place.getAddress()));
                        // Changed button's text to "Added" and cancel onclicklistener
                        btnUpload.setOnClickListener(null);
                        btnUpload.setText("Added");
                    }
                });


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                Log.e(getTag(), "Error: Status = " + status.toString());
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Indicates that the activity closed before a selection was made. For example if
                // the user pressed the back button.
            }
        }

    }

    /*
 * Helper methods to format information about a place nicely.
 */
    public static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                             CharSequence address, CharSequence phoneNumber,
                                             Uri websiteUri) {
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
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

        ((HomeMaterialActivity) context).onSectionAttached(
                ARG_SECTION_TITLE);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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

}
