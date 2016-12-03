package com.liorbaz.liorapp;

import android.app.ProgressDialog;
//import android.media.MediaPlayer;
//import android.net.Uri;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import static com.liorbaz.liorapp.DisplayVideoActivity.LOG_TAG;


public class DisplayVideoActivity extends AppCompatActivity {

    public final static String SERVO_CMD = "com.liorbaz.liorapp.SERVO_CMD";
    final static byte SERVO_CMD_NOP = '0';
    final static byte SERVO_CMD_STOP = '1';
    final static byte SERVO_CMD_LEFT = '2';
    final static byte SERVO_CMD_RIGHT = '3';
    final static byte SERVO_CMD_RESET = '4';

    // Declare variables
    protected static final String LOG_TAG = "-- -- MyDebug -->";
    private ProgressDialog mDialog;
    private WebView mWebView;
    private Toast mToastToShow;
    private boolean mVideoArrowsEnable;
    private String mUrl;

    /**
     * Servo Server socket information
     */
    protected DatagramSocket mClientSocket;
    protected int mServoServerPort;
    protected InetAddress mServoServerIp;

    protected Thread mServoSendCmdThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get Control Method from user (MainActivity)
        Bundle b = getIntent().getExtras();
        if (b != null) {
            mVideoArrowsEnable = b.getBoolean(MainActivity.CONTROL_METHOD);
        }

        /*************************************
         * Handle Servo Motor server details *
         *************************************/
        mServoServerPort = Integer.parseInt(getResources().getString(R.string.servo_server_port));
        String ipStr = getResources().getString(R.string.servo_server_ip);
        try {
            mServoServerIp = InetAddress.getByName(ipStr);
        } catch (UnknownHostException aE) {
            Log.i(LOG_TAG, "DisplayVideoActivity: mServoServerIp = InetAddress.getByName(" + ipStr + ")");
            aE.printStackTrace();
        }

        /***********************************************
         * Initiate connection with Servo Motor Server *
         ***********************************************/
        new ServoAsyncTask().execute();

        /*************************************
         * Handle Servo Motor server details *
         *************************************/
        if (mVideoArrowsEnable) {
            //Get layout with control sensors
            setContentView(R.layout.activity_display_video_arrows);
            ImageButton btnRight = (ImageButton) findViewById(R.id.RightBtn);
            ImageButton btnLeft = (ImageButton) findViewById(R.id.LeftBtn);

            //Define OnTocuh listener
            View.OnTouchListener listener = new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        if (mServoSendCmdThread == null) {
                            Log.d(LOG_TAG, "Entering onTouch-> ACTION_DOWN");
                            handleBtnTouch(v);
                        }
                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (mServoSendCmdThread != null) {
                            Log.d(LOG_TAG, "Entering onTouch-> ACTION_UP");
                            handleBtnRelease(v);
                        }
                    }
                    return false;
                }
            };

            //Add listener to buttons
            btnLeft.setOnTouchListener(listener);
            btnRight.setOnTouchListener(listener);
        } else {
            //Get layout with no arrows (video only)
            setContentView(R.layout.activity_display_video_sensors);
            SensorManager sMgr = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        }

        //Find your VideoView in your video_main.xml layout
        mWebView = (WebView) findViewById(R.id.WebView);

        //Display Back button
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Get URL from user (MainActivity)
        if (b != null) {
            mUrl = b.getString(MainActivity.EXTRA_MESSAGE);
        }
//        Uri videoStreamUri = Uri.parse(Url);
        Log.i(LOG_TAG, "DisplayVideoActivity: - URL string =" + mUrl);

        /********************************
         * Execute StreamVideo AsyncTask
         ********************************/
        // Create a progressbar
//        mDialog = new ProgressDialog(DisplayVideoActivity.this);

        // Set progressbar title
        mDialog.setTitle(

                getResources()

                        .

                                getString(R.string.loading_video_str)

        );

        // Set progressbar message
        mDialog.setMessage(

                getResources()

                        .

                                getString(R.string.buffering_video_str)

        );
        mDialog.setIndeterminate(false);
        mDialog.setCancelable(true);

        // Show progressbar
        mDialog.show();

        /*******************
         * Media Controller
         *******************/
        try

        {
            // Start the MediaController
//            MediaController mediacontroller = new MediaController(DisplayVideoActivity.this);
//            mediacontroller.setAnchorView(mWebView);
//
//            mWebView.setMediaController(mediacontroller);
//            mWebView.setVideoURI(videoStreamUri);
            Log.e(LOG_TAG, "Before mWebView.loadUrl(Url);");
            mWebView.loadUrl(mUrl);
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

    public void handleBtnTouch(View v) {
        byte servoCmd = SERVO_CMD_NOP;

        switch (v.getId()) {
            case R.id.LeftBtn:
                Log.d(LOG_TAG, "handleBtnTouch: case R.id.LeftBtn");
                ImageButton btnLeft = (ImageButton) findViewById(R.id.LeftBtn);
                btnLeft.setPressed(true);
                servoCmd = SERVO_CMD_LEFT;
                showToast("Left", 200);
                break;

            case R.id.RightBtn:
                Log.d(LOG_TAG, "handleBtnTouch: case R.id.RightBtn");
                ImageButton btnRight = (ImageButton) findViewById(R.id.RightBtn);
                btnRight.setPressed(true);
                servoCmd = SERVO_CMD_RIGHT;
                showToast("Right", 200);
                break;

            default:
                break;
        }

        /****************************************************************
         * Start a separate thread for sending commands to servo server *
         ****************************************************************/
        if (servoCmd != SERVO_CMD_NOP) {
            Runnable r = new ServoSendCmdThread(servoCmd);

            mServoSendCmdThread = new Thread(r);
            mServoSendCmdThread.start();
        }
    }

    public void handleBtnRelease(View v) {
        switch (v.getId()) {
            case R.id.LeftBtn:
                Log.d(LOG_TAG, "handleBtnRelease: case R.id.LeftBtn");
                ImageButton btnLeft = (ImageButton) findViewById(R.id.LeftBtn);
                btnLeft.setPressed(false);
                break;

            case R.id.RightBtn:
                Log.d(LOG_TAG, "handleBtnRelease: case R.id.RightBtn");
                ImageButton btnRight = (ImageButton) findViewById(R.id.RightBtn);
                btnRight.setPressed(false);
                break;

            default:
                break;
        }

        /*******************************
         * Destroy mServoSendCmdThread *
         *******************************/
        if (mServoSendCmdThread != null) {
            mServoSendCmdThread.interrupt();
            mServoSendCmdThread = null;
        }
    }

    //Use AsyncTask in order to perform ping in a different thread
    protected void showToast(String msg, int toastDurationInMilliSeconds) {

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


    private class ServoAsyncTask extends AsyncTask<String, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(LOG_TAG, "ServoAsyncTask: onPreExecute()");
            mDialog = new ProgressDialog(DisplayVideoActivity.this);

            // Set progressbar title
            mDialog.setTitle(getResources().getString(R.string.loading_video_str));

            // Set progressbar message
            mDialog.setMessage(getResources().getString(R.string.buffering_video_str));
            mDialog.setIndeterminate(false);
            mDialog.setCancelable(true);

            // Show progressbar
            mDialog.show();
        }


        @Override
        protected Void doInBackground(String... ip) {
            Log.i(LOG_TAG, "ServoAsyncTask: doInBackground()");
            //Initiate UDP socket to server
            try {
                mClientSocket = new DatagramSocket();
                mClientSocket.connect(mServoServerIp, mServoServerPort);
            } catch (SocketException e) {
                Log.e(LOG_TAG, "Failed to create UDP socket" + e.getMessage());
                mClientSocket.close();
            } finally {
                mDialog.dismiss();
            }
            return null;
        }

        /**
         * The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground()
         */
        @Override
        protected void onProgressUpdate(String... aStrings) {
            mDialog.setProgress(10/*TODO: replace with a parameter*/);
        }
    }


    /**
     * A separate thread which will send a servo command as long as long
     * as the user command is still valid (e.g., button is still pressed)
     */
    private class ServoSendCmdThread implements Runnable {
        private final Byte mCmdServoCmd;

        private ServoSendCmdThread(Byte cmdServoCmd) {
            mCmdServoCmd = cmdServoCmd;
        }

        @Override
        public void run() {
            while(!Thread.interrupted()) {
                try {
                    switch (mCmdServoCmd) {
                        case SERVO_CMD_LEFT: {
                            Log.d(LOG_TAG, "Thread.run: case SERVO_CMD_LEFT");
                            sendLeft();
                            break;
                        }

                        case SERVO_CMD_RIGHT: {
                            Log.d(LOG_TAG, "Thread.run: case SERVO_CMD_RIGHT");
                            sendRight();
                            break;
                        }

                        default: {
                            Log.d(LOG_TAG, "Thread.run: Unsupported SERVO_CMD (" + mCmdServoCmd.toString() + ")");
                            break;
                        }
                    }
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    break;
                }
            }
            Log.d(LOG_TAG, "Thread.run: exiting thread (" + mCmdServoCmd.toString() + ")");
        }

        /**
         * Called when the user presses "Left Arrow"
         */
        private void sendLeft() {
            // Show the toast and starts the countdown
            Log.d(LOG_TAG, "Thread: Entering sendLeft");

            if (mClientSocket.isConnected()) {
                byte sendData[] = {SERVO_CMD_LEFT};

                try {
                    DatagramPacket cmdPacket = new DatagramPacket(sendData, sendData.length, mServoServerIp, mServoServerPort);
                    mClientSocket.send(cmdPacket);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Failed to send LEFT command to servo" + e.getMessage());
                }
            }
        }

        /**
         * Called when the user presses "Right Arrow"
         */
        private void sendRight() {
            Log.d(LOG_TAG, "Thread: Entering sendRight");

            if (mClientSocket.isConnected()) {
                byte sendData[] = {SERVO_CMD_RIGHT};

                try {
                    DatagramPacket cmdPacket = new DatagramPacket(sendData, sendData.length, mServoServerIp, mServoServerPort);
                    mClientSocket.send(cmdPacket);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Failed to send RIGHT command to servo" + e.getMessage());
                }
            }
        }
    }
}