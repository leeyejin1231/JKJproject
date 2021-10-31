package com.example.jkjtest1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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


public class Ask01 extends AppCompatActivity {
    TextView countH, comTitle, comContents, comDate;
    Button heartButton;


    int likeCount = 0;
    boolean thumbsUpState = false;

    private static String IP_ADDRESS = "192.168.0.5";
    private static String TAG = "phptest";
    private String mJsonString;
    private ArrayList<AskData> mArrayList;
    String thisTitle, thisContents, thisDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask01);

        comTitle = (TextView)findViewById(R.id.comTitle);
        comContents = (TextView)findViewById(R.id.comContent);
        comDate = (TextView)findViewById(R.id.Date);

        Intent intent = getIntent(); /*community에서 데이터 수신*/

        thisTitle = intent.getExtras().getString("title"); /*String형*/

        comTitle.setText(thisTitle);

        Ask01.GetData task = new Ask01.GetData();
        task.execute("http://" + IP_ADDRESS + "/askjson.php", "");


    }

    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Ask01.this,
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


            String serverURL = "http://192.168.0.5/askjson.php";
            String postParameters = "title=" + thisTitle;


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
        String TAG_NAME = "contents";
        String TAG_DATE = "nowDate";


        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String contents = item.getString(TAG_NAME);
                String date = item.getString(TAG_DATE);

                AskData comData = new AskData();

//                comData.setMember_name(contents);
//                comData.setLikeCount(likeCount);

                comContents.setText(contents);
                thisContents = contents;
                comDate.setText(date);


//                mArrayList.add(comData);
            }


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }



}

