package com.example.jkjtest1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class Ask_write extends AppCompatActivity {

    private static String IP_ADDRESS = "192.168.0.5";
    private static String TAG = "phptest";

    TextView title, contents;
    String Title, Contents, nowDate;
    Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_write);

        Button button2 = (Button) findViewById(R.id.button2);
        Button writeButton = (Button)findViewById(R.id.writeButton);

        title = (TextView)findViewById(R.id.title);
        contents = (TextView)findViewById(R.id.contents);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                finish();
            }
        });

        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Title = title.getText().toString();
                Contents = contents.getText().toString();

                date = new Date();
                nowDate = date.toString();

                InsertData task = new InsertData();
                task.execute("http://" + IP_ADDRESS + "/askboard.php", Title, Contents, nowDate);

                title.setText("");
                contents.setText("");

                Intent intetn = new Intent();
                finish();

            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //requestCode는 101, resultCode는 메뉴.xml의 Result_OK
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101){
            String name = data.getStringExtra("name"); //메뉴.xml의 이름 가져옴
            Toast.makeText(getApplicationContext(), "메뉴화면으로부터 응답 : "+name, Toast.LENGTH_LONG).show();
        }
    }


    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Ask_write.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected String doInBackground(String... params) {

            String Title = (String)params[1];
            String Contents = (String)params[2];
            String nowDate = (String)params[3];

            String serverURL = (String)params[0];
            String postParameters = "title=" + Title + "&contents=" + Contents + "&nowDate=" + nowDate;

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }
}
