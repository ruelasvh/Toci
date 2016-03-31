package com.timemachine.toci;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


public class SearchFragment extends Fragment {

    /**
     * LogCat
     */
    private static final String TAG = SearchFragment.class.getSimpleName();
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_TITLE = "SearchFragment";


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        // Could add some parameters here that we wish to initialize
        // during the instantiation of this fragment.
        return fragment;
    }

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        final EditText mainEditText = (EditText) rootView.findViewById(R.id.enter_city);
        final ImageView mainButton = (ImageView) rootView.findViewById(R.id.main_btn);
        mainButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //mainEditText.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
                String zip_code = mainEditText.getText().toString();

                switch (zip_code) {
                    case "Maps":
                        StartMaps();
                        break;
                    case "Mountain View":
                        StartMtnViewActivity();
                        break;
                    case "94040":
                        StartMtnViewActivity();
                        break;
                    case "Palo Alto":
                        StartPaloAltoActivity();
                        break;
                    case "94301":
                        StartPaloAltoActivity();
                    case "":
                        break;
                    default:
                        Toast.makeText(getActivity(), "No city found!", Toast.LENGTH_SHORT).show();
                        break;
                }

                return false;

            }
        });

        return rootView;
    }

    /*
    public void StartMtnViewActivity() {
        Intent intent = new Intent(getActivity(), MountainViewCAActivity.class);
        startActivity(intent);
    }
    */

    public void StartMaps() {
        Intent intent = new Intent(getActivity(), MapsActivity.class);
        startActivity(intent);
    }

    public void StartMtnViewActivity() {
        Intent intent = new Intent(getActivity(), MountainViewCATabbedActivity.class);
        startActivity(intent);
    }

    public void StartPaloAltoActivity() {
        Intent intent = new Intent(getActivity(), PaloAltoCATabbedActivity.class);
        startActivity(intent);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((HomeMaterialActivity) context).onSectionAttached(
                ARG_SECTION_TITLE);

    }

    @Override
    public void onDetach() {
        Log.d(TAG, "Detached");
        super.onDetach();
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
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        public void onFragmentInteraction(Uri uri);
//    }
}