package com.liorbaz.liorapp;

import android.app.ProgressDialog;
import android.content.Intent;
//import android.media.MediaPlayer;
//import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

//    /**

//     * Event handler called when the user presses "Right Arrow"
//    /**
//
//    }
//        Toast.makeText(this, "Left!!!", Toast.LENGTH_LONG).show();
//    public void sendLeft(View view) {
//     */
//     * Event handler called when the user presses "Left Arrow"
public class DisplayVideoActivity extends AppCompatActivity {

    // Declare variables
    private static final String LOG_TAG = "-- -- MyDebug -->";

    private ProgressDialog mDialog;
    private WebView mWebView;
    private Toast mToastToShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get the layout from video_main.xml
        setContentView(R.layout.activity_display_video);

        //Find your VideoView in your video_main.xml layout
        mWebView = (WebView) findViewById(R.id.WebView);

        //Display Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get URL from user (MainActivity)
        Intent intent = getIntent();
        String Url = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
//        Uri videoStreamUri = Uri.parse(Url);
        Log.i(LOG_TAG, "DisplayVideoActivity: - URL string =" + Url);

        /********************************
         * Execute StreamVideo AsyncTask
         ********************************/
        // Create a progressbar
        mDialog = new ProgressDialog(DisplayVideoActivity.this);

        // Set progressbar title
        mDialog.setTitle(getResources().getString(R.string.loading_video_str));

        // Set progressbar message
        mDialog.setMessage(getResources().getString(R.string.buffering_video_str));
        mDialog.setIndeterminate(false);
        mDialog.setCancelable(true);

        // Show progressbar
        mDialog.show();

        /*******************
         * Media Controller
         *******************/
        try {
            // Start the MediaController
//            MediaController mediacontroller = new MediaController(DisplayVideoActivity.this);
//            mediacontroller.setAnchorView(mWebView);
//
//            mWebView.setMediaController(mediacontroller);
//            mWebView.setVideoURI(videoStreamUri);
            Log.e(LOG_TAG, "Before mWebView.loadUrl(Url);");
            mWebView.loadUrl(Url);
            Log.e(LOG_TAG, "After mWebView.loadUrl(Url);");

        } catch (Exception e) {
            mDialog.dismiss();
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            mDialog.dismiss();
        }

//        try {
//            mWebView.requestFocus();
//            mWebView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//
//                // Close the progress bar and play the video
//                public void onPrepared(MediaPlayer mp) {
//                    mDialog.dismiss();
//                    mVideo.start();
//                }
//            });
//        }
//        catch (Exception aE){
//            mDialog.dismiss();
//            Log.d("Video Play Error :", aE.getMessage());
//            System.out.println("Video Play Error :"+aE.toString());
//            finish();
//        }
    }

    //Enable the navigate back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void showToast(String msg, int toastDurationInMilliSeconds) {

        // Set the toast and duration
        mToastToShow = Toast.makeText(this, msg, Toast.LENGTH_LONG);

        // Set the countdown to display the toast
        CountDownTimer toastCountDown;
        toastCountDown = new CountDownTimer(toastDurationInMilliSeconds, 1000 /*Tick duration*/) {
            public void onTick(long millisUntilFinished) {
                mToastToShow.show();
            }

            public void onFinish() {
                mToastToShow.cancel();
            }
        };

        // Show the toast and starts the countdown
        mToastToShow.show();
        toastCountDown.start();
    }

    /**
     * Event handler called when the user presses "Left Arrow"
     */
    public void sendLeft(View view) {
        // Show the toast and starts the countdown
        showToast("Left", 200);
//        Toast.makeText(this, "Left!!!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Event handler called when the user presses "Right Arrow"
     */
    public void sendRight(View view) {
        showToast("Right", 200);
//        Toast.makeText(this, "Right!!!", Toast.LENGTH_SHORT).show();
    }

}