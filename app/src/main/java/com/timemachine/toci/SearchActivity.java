package com.timemachine.toci;

import android.app.Activity;
import android.content.Intent;
//import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
//import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class SearchActivity extends Activity {

    TextView mainTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search);

        mainTextView = (TextView) findViewById(R.id.main_TextView);
        final EditText mainEditText = (EditText) findViewById(R.id.enter_city);
        ImageView mainButton = (ImageView) findViewById(R.id.main_btn);
        mainButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                String zip_code = mainEditText.getText().toString();

                switch (zip_code) {
                    case "94040":
                        StartMtnViewActivity();
                        break;
                    case "":
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "No city found!", Toast.LENGTH_SHORT).show();
                        break;
                }

                return false;
            }
        });

    }

    public void StartMtnViewActivity() {
        Intent intent = new Intent(this, MountainViewCAActivity.class);
        startActivity(intent);
    }

}
