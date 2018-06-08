package com.timemachine.toci;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.READ_CONTACTS;

/**
 * Created by Victor Ruelas on 4/5/16.
 */
/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements
        SignupFragment.OnAccountCreatedListener {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private UserRegistrationTask mRegisTask = null;

    // User settings.
    Context mContext;
    AppPrefs mAppPrefs;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Network network;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Used to retrieve user preferences
        mContext = this;
        mAppPrefs = new AppPrefs(mContext);
        if (!mAppPrefs.hasLocationPermissions()) {
            mAppPrefs.setLocationPermissions();
        }

        // For checking if app is connected
        network = new Network(this);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        Button mSignupLink = (Button) findViewById(R.id.link_signup);
        mSignupLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment signupFragment = SignupFragment.newInstance(mEmailView.getText().toString());
                if (signupFragment != null) {
                    // Reset errors.
                    mEmailView.setError(null);
                    mPasswordView.setError(null);
                    mPasswordView.clearComposingText();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.auth_container, signupFragment, "signupFragment")
                            .addToBackStack(null).commit();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Hide keyboard on startup
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (!network.isOnline()) {
            Toast.makeText(getApplicationContext(), R.string.error_offline,
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Attempts to register the account specified by the register form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegistration(String username, String email, String password) {
        if (!network.isOnline()) {
            Toast.makeText(getApplicationContext(), R.string.error_offline,
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (mRegisTask != null) {
            return;
        }

        mRegisTask = new UserRegistrationTask(username, email, password);
        mRegisTask.execute((Void) null);
    }

    public static boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    public static boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 7;
    }

    public static boolean isUsernameValid(String username) {
        return username.length() > 7;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Method to communicate with fragments
     * @param email
     */
    public void onAccountCreated(String email) {
        // Set email passed from SignupFragment
        mEmailView.setText(email);
    }

    /**
     * Method to communicate with fragments
     * @param username
     * @param email
     * @param password
     */
    public void onAttemptRegistration(String username, String email, String password) {
        attemptRegistration(username, email, password);
    }

    /**
     * Represents an asynchronous registration task used to authenticate
     * the user.
     */
    public class UserRegistrationTask extends AsyncTask<Void, Void, String> {

        private final String mUsername;
        private final String mEmail;
        private final String mPassword;
        private ProgressDialog regisProgressDialog;

        UserRegistrationTask(String username, String email, String password) {
            mUsername = username;
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected void onPreExecute() {
            regisProgressDialog = new ProgressDialog(LoginActivity.this,
                    R.style.AuthenDialogStyle);
            regisProgressDialog.setIndeterminate(true);
            regisProgressDialog.setMessage(getString(R.string.regis_progress_message));
            regisProgressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> valuePairs = new ArrayList<>();
            valuePairs.add(new BasicNameValuePair("username", mUsername));
            valuePairs.add(new BasicNameValuePair("email", mEmail));
            valuePairs.add(new BasicNameValuePair("password", mPassword));

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Config.USER_REGISTRATION_URL);
                httpPost.setEntity(new UrlEncodedFormEntity(valuePairs));

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                String response = EntityUtils.toString(httpEntity);

                return response;

            } catch (IOException e) {
                e.printStackTrace();
            }

            return "Error in account registration";
        }

        @Override
        protected void onPostExecute(final String response) {
            mRegisTask = null;
            regisProgressDialog.dismiss();

            final List<String> responseStream = Arrays.asList(response.split(" "));
            SignupFragment signupFragment = (SignupFragment) getSupportFragmentManager().findFragmentByTag("signupFragment");

            final int finalResponse;

            if (responseStream.contains("'username_2'\"")) {
                finalResponse = R.string.error_unavaiable_username;
            }
            else if (responseStream.contains("'username'\"")) {
                finalResponse = R.string.error_account_exists;
            }
            else if (responseStream.contains("'email'\"")) {
                finalResponse = R.string.error_registered_email;
            }
            else if (responseStream.contains("\"Success:")) {
                finalResponse = R.string.successful_regis;
            }
            else {
                finalResponse = R.string.error_regis;
            }

            signupFragment.myFragmentDataFromActivity(finalResponse);
        }

        @Override
        protected void onCancelled() {
            // Set the instance of this class to null, disable progress bar
        }
    }

    /**
     * Represents an asynchronous login task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // Attempt authentication against a network service.

            Boolean response;

            try {
                String link = Config.USER_AUTHENTICATION_URL + "?" +
                        "email=" + URLEncoder.encode(mEmail, "UTF-8") + "&" +
                        "password=" + URLEncoder.encode(mPassword, "UTF-8");
                URI url = new URI(link);
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(url);
                HttpResponse httpResponse = client.execute(request);
                HttpEntity httpEntity = httpResponse.getEntity();
                response = Boolean.valueOf(EntityUtils.toString(httpEntity));
                mAppPrefs.setSessionStatus(response);

                return response;

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
                Intent intent = new Intent(getBaseContext(), HomeMaterialActivity.class);
                startActivity(intent);
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

