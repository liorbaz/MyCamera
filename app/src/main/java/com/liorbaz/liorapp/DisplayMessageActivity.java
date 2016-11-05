package com.liorbaz.liorapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DisplayMessageActivity extends AppCompatActivity {
    public final static String PING_CMD = "ping -c ";
    public final static String NUM_OF_REPLIES = "4 ";

    protected ListView mPingOutputListView;
//    protected LogArrayAdapter mAdapter;
    protected ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        Intent intent = getIntent();
        String ip = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        //Display Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView textView = (TextView) findViewById(R.id.dest_ip);
        textView.setText(ip);
        textView.setTextSize(40);
        textView.setTextColor(Color.RED);

        new PingAsyncTask().execute(ip);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //Use AsyncTask in order to perform ping in a different thread
    private class PingAsyncTask extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... ip) {
            executePing(ip[0]);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mPingOutputListView = (ListView) findViewById(R.id.ping_output_list);
            mAdapter = new ArrayAdapter (DisplayMessageActivity.this, R.layout.ping_result_list_layout);
//            mAdapter = new ArrayAdapter (DisplayMessageActivity.this, R.layout.activity_display_message, R.id.ping_output_list);
//                    new LogArrayAdapter (DisplayMessageActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, new String[]{"LLL"});

            // Assign adapter to ListView
            mPingOutputListView.setAdapter(mAdapter);
        }

        /**
         * The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground()
         */
        @Override

        protected void onProgressUpdate (String...aStrings) {
            mAdapter.add(aStrings[0]);
//            mAdapter.notifyDataSetChanged();
        }

        //This method will be performed by the worker thread (different than the calling UI thread) - triggered by doInBackground
        private void executePing(String ip) {
//            String pingResult = "";

            try {
                String inputLine;

                Runtime r = Runtime.getRuntime();
                Process p = r.exec(PING_CMD + NUM_OF_REPLIES + ip);

                BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                while ((inputLine = in.readLine()) != null) {
                    publishProgress(inputLine);//Method from AsyncTask which is activated on the UI thread (onProgressUpdate)
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }





//    public class LogArrayAdapter extends ArrayAdapter {
//        private final Context context;
//        private final String[] values;
//
//        public LogArrayAdapter (Context context, String[] values) {
//            super(context, R.layout.activity_display_message, values);
//            this.context = context;
//            this.values = values;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ListView listView = (ListView) findViewById(R.id.ping_output_list);
//            return listView;
//        }
//    }
}
