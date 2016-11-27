package com.liorbaz.liorapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

/**
 * TODO:
 * 1. Increase webcam resolution and see how it affects the WebView layout
 * 2. Progress bar: (A) UDP isConnected bug, (B) Apply for webcam loading as well.
 * 3. Radio button on main page for Video activity: Arrows םר אןךא
 * */

public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.liorbaz.liorapp.MESSAGE";
    public final static String CONTROL_METHOD = "com.liorbaz.liorapp.CONTROL_METHOD";
    private final static int NUM_OF_IP_OCTETS = 4;
    private final static int OCTET_IP_MAX_VAL = 255;
    private final static int OCTET_IP_MIN_VAL = 0;
    private final static String OCTET_IP_DELIMITER = "\\.";

    private String mValidIpString  = "";
    private String mVideoUrlString = "";

    private boolean mVideoArrowsEnable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Called when the user clicks the Ping button
     */
    public void pingDst(View view) {
        // Get & validate ip address from text field
        Intent intent = new Intent(this, DisplayMessageActivity.class);

        //Get ip address from text field
        EditText editText = (EditText) findViewById(R.id.edit_message_ping);
        mValidIpString = editText.getText().toString();

        //Validate ip address from text field
        if (handleAndValidateIp(editText)) {
            intent.putExtra(EXTRA_MESSAGE, mValidIpString);
            startActivity(intent);
        }
    }


    private boolean handleAndValidateIp(EditText editText) {
        if (mValidIpString.isEmpty()) {
            //Get the default IP
            mValidIpString = editText.getHint().toString();
        } else if (!performIpValidityCheck(mValidIpString)) {
            //Failed validity check
            Toast.makeText(this, "Invalid IP!", Toast.LENGTH_LONG).show();

            //Reset field text
            setContentView(R.layout.activity_main);
            return false;
        }
        return true;
    }

    private boolean performIpValidityCheck(String message) {
        String[] ipArr = message.split(OCTET_IP_DELIMITER);

        if (ipArr.length != NUM_OF_IP_OCTETS) {
            return false;
        }
        for (String s : ipArr) {
            int octetInt;
            try {
                octetInt = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                return false;
            }

            if ((octetInt > OCTET_IP_MAX_VAL) ||
                    (octetInt < OCTET_IP_MIN_VAL)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Called when the user clicks the Video button
     */
    public void videoDst(View view) {
        Intent intent = new Intent(this, DisplayVideoActivity.class);

        //Get URL from text field
        EditText editText = (EditText) findViewById(R.id.edit_message_video);
        mVideoUrlString = editText.getText().toString();

        //Validate URL from text field
        if (mVideoUrlString.isEmpty()) {
            //Get the default URL
            mVideoUrlString = editText.getHint().toString();
        }

        //Send Parameters (URL & Servo Control Method) to Activity
        Bundle b = new Bundle();
        b.putString(EXTRA_MESSAGE, mVideoUrlString);
        b.putBoolean(CONTROL_METHOD, mVideoArrowsEnable);
        intent.putExtras(b);

        startActivity(intent);
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_btn_arrows:
                if (checked)
                    mVideoArrowsEnable = true;
                    break;
            case R.id.radio_btn_sensors:
                if (checked)
                    mVideoArrowsEnable = false;
                    break;
        }
    }
}