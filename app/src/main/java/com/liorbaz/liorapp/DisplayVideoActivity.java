package com.liorbaz.liorapp;

//import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
//import android.media.MediaPlayer;
//import android.net.Uri;
import android.os.Bundle;
//import android.os.Message;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
//import android.view.LayoutInflater;
import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
import android.webkit.WebView;
//import android.widget.MediaController;
//import android.widget.VideoView;

//public class DisplayVideoActivity extends FragmentActivity {
//
//    // Declare variables
//    private static final String LOG_TAG = "-- -- MyDebug -->";
//
//    private ProgressDialog mDialog;
//    private VideoView mVideo;
//    static private String Url;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        Log.i(LOG_TAG, "FragmentActivity:onCreate()");
//
//        //Get the layout from video_main.xml
//        setContentView(R.layout.activity_display_video);
//
//        //Find your VideoView in your video_main.xml layout
////        mVideo = (VideoView) findViewById(R.id.VideoView);
//
//        //Display Back button
////        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        //Get URL from user (MainActivity)
//        Intent intent = getIntent();
//        Url = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
//    }
//
//    static public class DisplayVideoWebFragment extends Fragment {
//
//        private View mRoot;
//        private WebView mWebView;
//
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            Log.i(LOG_TAG, "Fragment:onCreate()");
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup
//                container, @Nullable Bundle savedInstanceState) {
//
//            Log.i(LOG_TAG, "Fragment:onCreateView()");
//            mRoot = inflater.inflate(R.layout.activity_display_video, container, false);
//            mWebView = (WebView) mRoot.findViewById(R.id.WebView);
//            mWebView.loadUrl(Url);
//            Log.i(LOG_TAG, "URL="+Url);
//            return mRoot;
//        }
//
//        @Override
//        public void onDestroy() {
//            super.onDestroy();
//
//        }
//
//        @Override
//        public void onResume() {
//            super.onResume();
//        }
//
//        /**
//         * handles incoming messages from Hotspot service
//         *
//         * @param msg
//         */
//
//        private void handleIncomingMessageFromService(Message msg) {
//        }
//    }
//
//
//}


public class DisplayVideoActivity extends AppCompatActivity {

    // Declare variables
    private static final String LOG_TAG = "-- -- MyDebug -->";

    private ProgressDialog mDialog;
    private WebView mWebView;

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
        Log.i(LOG_TAG, "DisplayVideoActivity: - URL string ="+Url);

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
        }
        finally {
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
}
