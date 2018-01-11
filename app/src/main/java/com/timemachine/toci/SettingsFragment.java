package com.timemachine.toci;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SECTION_TITLE = "SettingsFragment";

    // Helper fields to help store favorite settings
    Context mContext;
    AppPrefs mAppPrefs;
    private Button mLogoutLink;
    private Button mAboutLink;
    private BottomSheetDialogFragment bottomSheetDialogFragment;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SettingsFragment.
     */
    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Used to save user preferences
        mContext = getActivity().getApplicationContext();
        mAppPrefs = new AppPrefs(mContext);
        // About us bottom sheet fragment
        bottomSheetDialogFragment = new AboutUsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentsView = inflater.inflate(R.layout.fragment_settings, container, false);

        mAboutLink = (Button) fragmentsView.findViewById(R.id.about);
        mAboutLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialogFragment.show(getActivity().getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
            }
        });

        mLogoutLink = (Button) fragmentsView.findViewById(R.id.signout);
        mLogoutLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set session in app prefs to false
                mAppPrefs.setSessionStatus(false);
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return fragmentsView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Set Fragments's title in parent activity
        ((HomeMaterialActivity) context).onSectionAttached(SECTION_TITLE);
    }
}
