package com.timemachine.toci;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link SettingsBottomSheetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String ARG_LAYOUT_TITLE = "layoutTitleParam";
    private static final String ARG_LAYOUT_CONTENT = "layoutContentParam";
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SettingsBottomSheetFragment.
     */
    public static SettingsBottomSheetFragment newInstance(Integer layoutTitle, Integer layoutContent) {
        SettingsBottomSheetFragment fragment = new SettingsBottomSheetFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_TITLE, layoutTitle);
        args.putInt(ARG_LAYOUT_CONTENT, layoutContent);
        fragment.setArguments(args);
        return fragment;
    }

    public SettingsBottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };

    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        View contentView = View.inflate(getContext(), R.layout.fragment_settings_bottomsheet, null);
        dialog.setContentView(contentView);

        TextView dialogTitle = dialog.findViewById(R.id.settings_bottom_sheet_title);
        dialogTitle.setText(Html.fromHtml(getString(getArguments().getInt(ARG_LAYOUT_TITLE))));

        TextView dialogContent = dialog.findViewById(R.id.settings_bottom_sheet_content);
        dialogContent.setText(Html.fromHtml(getString(getArguments().getInt(ARG_LAYOUT_CONTENT))));

        CoordinatorLayout.LayoutParams layoutParams =
                (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        final CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

        View parent = (View) contentView.getParent();
        parent.setFitsSystemWindows(true);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(parent);
        contentView.measure(0, 0);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        bottomSheetBehavior.setPeekHeight(screenHeight);

        if (layoutParams.getBehavior() instanceof  BottomSheetBehavior) {
            ((BottomSheetBehavior) layoutParams.getBehavior()).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

        layoutParams.height = screenHeight;
        parent.setLayoutParams(layoutParams);

        // Close bottom sheet on button click
        dialog.findViewById(R.id.close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
