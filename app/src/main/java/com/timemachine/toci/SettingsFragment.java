package com.timemachine.toci;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
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
    private Button mLogoutButton;
    private Button mAboutButton;
    private Button mPrivacyPolicyButton;
    private Button mTermsAndContitionsButton;
    private BottomSheetDialogFragment bottomSheetDialogAboutUs;
    private BottomSheetDialogFragment bottomSheetDialogPrivacyPolicy;
    private BottomSheetDialogFragment bottomSheetDialogTermsAndContitions;

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

        bottomSheetDialogAboutUs = SettingsBottomSheetFragment.newInstance(R.string.about_title, "aboutus");
        bottomSheetDialogPrivacyPolicy = SettingsBottomSheetFragment.newInstance(R.string.pp_title, "privacy");
        bottomSheetDialogTermsAndContitions = SettingsBottomSheetFragment.newInstance(R.string.terms_and_conditions_title, "terms");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentsView = inflater.inflate(R.layout.fragment_settings, container, false);

        mAboutButton = fragmentsView.findViewById(R.id.about);
        mAboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialogAboutUs.show(getActivity().getSupportFragmentManager(),
                bottomSheetDialogAboutUs.getTag());
            }
        });

        mPrivacyPolicyButton = fragmentsView.findViewById(R.id.legalPrivacyPolicy);
        mPrivacyPolicyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialogPrivacyPolicy.show(getActivity().getSupportFragmentManager(),
                bottomSheetDialogPrivacyPolicy.getTag());
            }
        });

        mTermsAndContitionsButton = fragmentsView.findViewById(R.id.legalTermsConditions);
        mTermsAndContitionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialogTermsAndContitions.show(getActivity().getSupportFragmentManager(),
                bottomSheetDialogTermsAndContitions.getTag());
            }
        });

        mLogoutButton = fragmentsView.findViewById(R.id.signout);
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
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
}
