package com.timemachine.toci;

import android.app.Dialog;
import android.os.AsyncTask;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link SettingsBottomSheetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String ARG_TITLE = "titleParam";
    private static final String ARG_CONTENT = "contentParam";
    private TextView dialogContent;
    private TextView dialogTitle;
    private SetSectionInView fetchLegal = null;
    private ProgressBar spinner;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SettingsBottomSheetFragment.
     */
    public static SettingsBottomSheetFragment newInstance(Integer title, String content) {
        SettingsBottomSheetFragment fragment = new SettingsBottomSheetFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TITLE, title);
        args.putString(ARG_CONTENT, content);
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
        View contentView = View.inflate(getContext(), R.layout.fragment_settings_bottomsheet, null);
        dialog.setContentView(contentView);

        spinner = dialog.findViewById(R.id.settings_bottom_sheet_spinner);
        spinner.setVisibility(View.VISIBLE);

        dialogTitle = dialog.findViewById(R.id.settings_bottom_sheet_title);
        dialogTitle.setText(Html.fromHtml(getString(getArguments().getInt(ARG_TITLE))));

        dialogContent = dialog.findViewById(R.id.settings_bottom_sheet_content);
        String sectionQuery = getArguments().getString(ARG_CONTENT);
        String queryUrl = buildSectionUrl(Config.GET_LEGAL, sectionQuery);
        fetchLegal = new SetSectionInView(dialogContent);
        fetchLegal.execute(queryUrl);

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

    public class SetSectionInView extends AsyncTask<String, Void, String> {
        private final TextView mView;

        public SetSectionInView(TextView view) {
            mView = view;
        }

        @Override
        protected void onPreExecute() {
            // deal with spinner here
        }

        @Override
        protected String doInBackground(String... urls) {
            final int count = urls.length;
            String htmlSection = "";

            for (int i = 0; i < count; i++) {
                try {
                    URI queryUri = new URI(urls[i]);
                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet();
                    request.setURI(queryUri);
                    HttpResponse httpResponse = client.execute(request);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    JSONArray response = new JSONArray(EntityUtils.toString(httpEntity));
                    String htmlString = response.getJSONObject(0).getString("html");
                    htmlSection += htmlString;
                    if (isCancelled()) break;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return htmlSection;
        }

        @Override
        protected void onPostExecute(final String content) {
            mView.setText(Html.fromHtml(content));
            spinner.setVisibility(View.GONE);
        }
    }

    private String buildSectionUrl(String root, String api) {
        String url = root;

        try {
            url += "?section=";
            url += URLEncoder.encode(api, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return url;
    }
}
