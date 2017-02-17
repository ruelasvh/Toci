package com.timemachine.toci;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignupFragment.OnAccountCreatedListener} interface
 * to handle interaction events.
 * Use the {@link SignupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "email";

    // TODO: Rename and change types of parameters
    private String mEmail;
    private ImageButton mLoginLink;
    private EditText mEmailView;

    private OnAccountCreatedListener mListener;

    public SignupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment SignupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupFragment newInstance(String param1) {
        SignupFragment fragment = new SignupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEmail = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_signup, container, false);

        // Set up email
        mEmailView = (EditText) rootView.findViewById(R.id.input_email);
        mEmailView.setText(mEmail);
        mLoginLink = (ImageButton) rootView.findViewById(R.id.link_login);
        mLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        onLoginLinkPressed(mEmail);

        onRegistrationButtonPressed("fakenews", "fakenews@gmail.com", "fakepassword");

        return rootView;
    }

    public void myFragmentDataFromActivity(String data) {

        Toast.makeText(getActivity(), data,
                Toast.LENGTH_LONG).show();

    }

    // Pass email back to LoginActivity
    public void onLoginLinkPressed(String email) {
        if (mListener != null) {
            mListener.onAccountCreated(email);
        }
    }

    // Pass username, email, password to activity to attempt registration
    public void onRegistrationButtonPressed(String username, String email, String password) {
        if (mListener != null) {
            mListener.onAttemptRegistration(username, email, password);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAccountCreatedListener) {
            mListener = (OnAccountCreatedListener) context;
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnAccountCreatedListener {
        // Pass email back to LoginActivity
        void onAccountCreated(String email);

        void onAttemptRegistration(String username, String email, String password);
    }
}
