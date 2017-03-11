package com.timemachine.toci;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
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

    private final static String TAG = SignupFragment.class.getSimpleName();

    // The fragment initialization parameters.
    private static final String ARG_PARAM1 = "email";

    // UI references.
    private ImageButton mLoginLink;
    private EditText mUsernameView;
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mPasswordConfirmView;
    private ImageButton mRegisterSubmit;

    // Store UI references values.
    private String mEmail;

    private OnAccountCreatedListener mListener;

    public SignupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param email Parameter 1.
     * @return A new instance of fragment SignupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupFragment newInstance(String email) {
        SignupFragment fragment = new SignupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, email);
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

        // Set up back navigation to login screen.
        mLoginLink = (ImageButton) rootView.findViewById(R.id.link_login);
        mLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // Set up the registration form.
        mUsernameView = (EditText) rootView.findViewById(R.id.input_username);
        mEmailView = (EditText) rootView.findViewById(R.id.input_email);
        // Set email if entered one in the login screen
        mEmailView.setText(mEmail);
        mPasswordView = (EditText) rootView.findViewById(R.id.input_password);
        mPasswordConfirmView = (EditText) rootView.findViewById(R.id.input_password_confirm);
        mRegisterSubmit = (ImageButton) rootView.findViewById(R.id.registration_form_submit);
        mRegisterSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = mUsernameView.getText().toString();
                String email = mEmailView.getText().toString();
                String password = mPasswordView.getText().toString();
                onRegistrationButtonPressed(username, email, password);
            }
        });

        return rootView;
    }


    // Get data from activity and do something with it
    public void myFragmentDataFromActivity(int regisAttemptResponse) {
        // Set errors in the UI references according to server response
        mUsernameView.setError(null);
        mEmailView.setError(null);

        boolean cancel = false;
        View focusView = null;

        switch (regisAttemptResponse) {
            case R.string.error_unavaiable_username:
                mUsernameView.setError(getString(R.string.error_unavaiable_username));
                focusView = mUsernameView;
                cancel = true;
                break;
            case R.string.error_account_exists:
                mUsernameView.setError(getString(R.string.error_account_exists));
                mEmailView.setError(getString(R.string.error_account_exists));
                // Pass email back to LoginActivity on successful registration
                handleEmailToActivity(mEmailView.getText().toString());
                focusView = mUsernameView;
                cancel = true;
                break;
            case R.string.error_registered_email:
                mEmailView.setError(getString(R.string.error_registered_email));
                focusView = mEmailView;
                cancel = true;
                break;
            case R.string.successful_regis:
                showRegistrationSuccessDialog(getString(R.string.successful_regis));
                // Pass email back to LoginActivity on successful registration
                handleEmailToActivity(mEmailView.getText().toString());
                getActivity().getSupportFragmentManager().popBackStack();
                break;
            default:
                mUsernameView.setError(getString(R.string.error_regis));
                mEmailView.setError(getString(R.string.error_regis));
                focusView = mUsernameView;
                cancel = true;
                // Flash registration attempt response
                Toast.makeText(getActivity(), regisAttemptResponse,
                        Toast.LENGTH_LONG).show();
                Log.i(TAG, "Error in registration request: " + getString(regisAttemptResponse));
                break;
        }

        if (cancel) {
            // There was an error in registration attempt; focus the respective
            // form field with an error.
            focusView.requestFocus();
        }
    }

    // Show AlertDialog on successful registration
    public void showRegistrationSuccessDialog(String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(),
                R.style.AuthenDialogStyle);
        dialogBuilder.setMessage(message);
        dialogBuilder.setCancelable(true);

        dialogBuilder.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }
        );

        AlertDialog registrationSuccessDialog = dialogBuilder.create();
        registrationSuccessDialog.show();
    }

    // Pass email back to LoginActivity
    public void handleEmailToActivity(String email) {
        if (mListener != null) {
            mListener.onAccountCreated(email);
        }
    }

    // Pass username, email, password to activity to attempt registration
    public void onRegistrationButtonPressed(String username, String email, String password) {
        // Reset errors.
        mUsernameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mPasswordConfirmView.setError(null);

        // Store values at the time of registration attempt.
        String passwordConfirm = mPasswordConfirmView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for valid email
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!LoginActivity.isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!LoginActivity.isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for valid username
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!LoginActivity.isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        // Confirm password.
        if (!TextUtils.isEmpty(password) && !password.equals(passwordConfirm)) {
            mPasswordConfirmView.setError(getString(R.string.error_passwords_match));
            focusView = mPasswordConfirmView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        }
        else if (mListener != null) {
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
        // Pass login form info to LoginActivity for processing
        void onAttemptRegistration(String username, String email, String password);
    }
}
