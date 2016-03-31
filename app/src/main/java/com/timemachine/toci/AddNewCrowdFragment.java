package com.timemachine.toci;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
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

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.view.CardViewNative;

/**
 * Created by Victor Ruelas on 3/16/16.
 * Copyright (c) 2016 CrowdZeeker, LLC. All rights reserved.
 */
public class AddNewCrowdFragment extends Fragment {

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
        return inflater.inflate(R.layout.fragment_add_new_crowd, parent, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        searchCard = new Card(getContext());
        searchCard.setTitle("Search");
        searchCard.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
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
        CustomThumbCard thumb = new CustomThumbCard(getContext());
        thumb.setDrawableResource(R.drawable.places_ic_search);
        searchCard.addCardThumbnail(thumb);
        CardViewNative cardView = (CardViewNative) view.findViewById(R.id.search);
        cardView.setCard(searchCard);

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
                mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(), /*place.getId(),*/
                        place.getAddress(), place.getPhoneNumber(), place.getWebsiteUri()));

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
                                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
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

    /*
     * Helper methods to format information about a place nicely.
     */
    public static Spanned formatPlaceDetails(Resources res, CharSequence name, /*String id,*/
                                              CharSequence address, CharSequence phoneNumber,
                                              Uri websiteUri) {
        return Html.fromHtml(res.getString(R.string.place_details_add_new, name, /*id,*/ address, phoneNumber,
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

    /**
     * Custom card search button thumbnail.
     */

    public class CustomThumbCard extends CardThumbnail {

        public CustomThumbCard(Context context) {
            super(context);
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View viewImage) {
            if (viewImage!=null){
                //viewImage.getLayoutParams().width=250;
                //viewImage.getLayoutParams().height=250;

                DisplayMetrics metrics=parent.getResources().getDisplayMetrics();
                viewImage.getLayoutParams().width= (int)(30*metrics.density);
                viewImage.getLayoutParams().height = (int)(30*metrics.density);
            }
        }
    }

}
