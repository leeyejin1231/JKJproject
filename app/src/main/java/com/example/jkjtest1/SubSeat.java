package com.example.jkjtest1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class SubSeat extends AppCompatActivity {

    private static String IP_ADDRESS = "192.168.0.5";
    private static String TAG = "phptest";

    private ArrayList<PersonalData> mArrayList;
    private UsersAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private String mJsonString;
    private Handler handler = new Handler();
    private TimerTask second;

    TextView hanTime, nokTime;

    int hansec = 360;
    int noksec = 480;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_seat);

        testStart();

        Toast.makeText(getApplicationContext(), "원하는 행을 터치하세요.", Toast.LENGTH_LONG).show();


        mRecyclerView = (RecyclerView) findViewById(R.id.listView_main_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));



        mArrayList = new ArrayList<>();

        mAdapter = new UsersAdapter(this, mArrayList);
        mRecyclerView.setAdapter(mAdapter);

        final TextView hangText = (TextView)findViewById(R.id.hangText);


        Button hanButton = (Button) findViewById(R.id.hanButton);
        hanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                mArrayList.clear();
                mAdapter.notifyDataSetChanged();

                hangText.setText("봉화산행");

                GetData task = new GetData();
                task.execute("http://" + IP_ADDRESS + "/getjsonsub.php", "");

            }
        });

        Button nokButton = (Button) findViewById(R.id.nokButton);
        nokButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                mArrayList.clear();
                mAdapter.notifyDataSetChanged();

                hangText.setText("응암행");

                GetData task = new GetData();
                task.execute("http://" + IP_ADDRESS + "/getjsonsub2.php", "");

            }
        });

    }


    public void testStart() {
        hanTime = (TextView)findViewById(R.id.hanTime);
        nokTime = (TextView)findViewById(R.id.nokTime);

        second = new TimerTask() {

            @Override
            public void run() {
                Update();
                hansec--;
                noksec--;

                if (hansec == 0){
                    hansec = 380;
                }

                if (noksec == 0){
                    noksec = 500;
                }

            }
        };

        Timer timer = new Timer();
        timer.schedule(second, 0, 1000);
    }


    protected void Update() {
        Runnable updater = new Runnable() {
            public void run() {
                hanTime.setText(hansec/60 + "분 " + hansec%60 + "초");
                nokTime.setText(noksec/60 + "분 " + noksec%60 + "초");
        }
        };
        handler.post(updater);
    }






    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(SubSeat.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);

            if (result == null) {


            } else {

                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String postParameters = params[1];


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "GetData : Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }


    private void showResult() {

        String TAG_JSON = "webnautes";
        String TAG_ID = "no";
        String TAG_NAME = "number";
        String TAG_COUNTRY = "state";


        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String no = item.getString(TAG_ID);
                String number = item.getString(TAG_NAME);
                String state = item.getString(TAG_COUNTRY);

                PersonalData personalData = new PersonalData();

                personalData.setMember_id(no);
                personalData.setMember_name(number);
                personalData.setMember_country(state);

                mArrayList.add(personalData);
                mAdapter.notifyDataSetChanged();
            }


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }


    public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.CustomViewHolder> {

        private ArrayList<PersonalData> mList = null;
        private Activity context = null;


        public UsersAdapter(Activity context, ArrayList<PersonalData> list) {
            this.context = context;
            this.mList = list;
        }

        class CustomViewHolder extends RecyclerView.ViewHolder {
            protected TextView id;
            protected TextView name;
            protected TextView country;
            protected ImageView seatres;


            public CustomViewHolder(View view) {
                super(view);
                this.id = (TextView) view.findViewById(R.id.textView_list_id);
                this.name = (TextView) view.findViewById(R.id.textView_list_name);
                this.country = (TextView) view.findViewById(R.id.textView_list_country);
                this.seatres = (ImageView) view.findViewById(R.id.seat_state);

            }
        }


        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.seat_item, null);
            CustomViewHolder viewHolder = new CustomViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {

            viewholder.id.setText(mList.get(position).getMember_id());
            viewholder.name.setText(mList.get(position).getMember_name() + " 호차   ");

            if(Integer.parseInt(mList.get(position).getMember_country())==1) {
                viewholder.country.setText(" 사용 중");
                viewholder.seatres.setBackgroundResource(R.drawable.nemo1);


            } else {
                viewholder.country.setText(" 사용 가능");
                viewholder.seatres.setBackgroundResource(R.drawable.nemo2);
            }

        }

        @Override
        public int getItemCount() {
            return (null != mList ? mList.size() : 0);
        }

    }
}


