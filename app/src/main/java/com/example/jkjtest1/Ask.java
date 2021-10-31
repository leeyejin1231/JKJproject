package com.example.jkjtest1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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


public class Ask extends AppCompatActivity {           //어뎁터 생성

    private static String IP_ADDRESS = "192.168.0.5";
    private static String TAG = "phptest";

    private ArrayList<AskData> mArrayList;
    private UsersAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private String mJsonString;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask);
        mRecyclerView = (RecyclerView) findViewById(R.id.listView_main_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));



        mArrayList = new ArrayList<>();

        mAdapter = new UsersAdapter(this, mArrayList);
        mRecyclerView.setAdapter(mAdapter);
//여기 네줄 추가
        mArrayList.clear();
        mAdapter.notifyDataSetChanged();

        Ask.GetData task = new Ask.GetData();
        task.execute("http://" + IP_ADDRESS + "/askjsontitle.php", "");



        Button button_all = (Button) findViewById(R.id.button_main_all);
        button_all.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                mArrayList.clear();
                mAdapter.notifyDataSetChanged();

                Ask.GetData task = new Ask.GetData();
                task.execute("http://" + IP_ADDRESS + "/askjsontitle.php", "");

            }
        });

        final Button comWrite= (Button)findViewById(R.id.comWrite);
        comWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Ask_write.class);
                startActivityForResult(intent, 101);

            }
        });

    }


    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Ask.this,
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
        String TAG_ID = "title";
        String TAG_NAME = "nowDate";


        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String title = item.getString(TAG_ID);
                String nowDate = item.getString(TAG_NAME);

                AskData askData = new AskData();

                askData.setMember_id(title);

                if(nowDate.equals(null)){
                    askData.setMember_name("0");

                } else {
                    askData.setMember_name(nowDate);
                }

                mArrayList.add(askData);
                mAdapter.notifyDataSetChanged();



            }


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }


    public class UsersAdapter extends RecyclerView.Adapter<Ask.UsersAdapter.CustomViewHolder> {

        private ArrayList<AskData> mList = null;
        private Activity context = null;





        public UsersAdapter(Activity context, ArrayList<AskData> list) {
            this.context = context;
            this.mList = list;
        }
        //ViewHolder에 View를 끼워넣어줌
        class CustomViewHolder extends RecyclerView.ViewHolder {
            protected Button id;
            protected TextView name;

            //추가
            public final View mView;

            public CustomViewHolder(View view) {
                super(view);
                this.id = (Button) view.findViewById(R.id.title);
                this.name = (TextView) view.findViewById(R.id.date);
                mView = view;
            }
        }


        @Override
        public Ask.UsersAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.com_list, null);
            Ask.UsersAdapter.CustomViewHolder viewHolder = new Ask.UsersAdapter.CustomViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull Ask.UsersAdapter.CustomViewHolder viewholder, int mposition) {

            //추가 final로 설정해줘야함!
            final int position = mposition;
            //추가)값설정 position에
            AskData item = mList.get(position);

            viewholder.id.setText(mList.get(position).getMember_id());
            viewholder.name.setText(mList.get(position).getMember_name());

            //추가)눌리면 해당 뷰의 position을 토스트메시지로 띄워줌
            viewholder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Context context = v.getContext();
//                    Toast.makeText(context, position +"", Toast.LENGTH_LONG).show();


                    //화면 전환 위해 따로 추가
                    Intent intent = new Intent(getApplicationContext(), Ask01.class);
                    intent.putExtra("title",mList.get(position).getMember_id()); /*송신*/
                    startActivityForResult(intent, 101); //101 화면 구분자
                    //startActivity(intent);
                }
            });


        }

        @Override
        public int getItemCount() {
            return (null != mList ? mList.size() : 0);
        }

    }

}