package com.example.jkjtest1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class main01 extends AppCompatActivity {

    private static String IP_ADDRESS = "192.168.0.5";
    private static String TAG = "phptest";

    TextView Id, passwordCreate, passwordCheck, Name, nickname, birth, number;
    String id, password, pwch, name, nick, bir, num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main01);

        passwordCreate = (TextView)findViewById(R.id.passwordCreate);
        passwordCheck = (TextView)findViewById(R.id.passwordCheck);
        Id = (TextView)findViewById(R.id.id);
        Name = (TextView)findViewById(R.id.name);
        nickname = (TextView)findViewById(R.id.nickname);
        birth = (TextView)findViewById(R.id.birth);
        number = (TextView)findViewById(R.id.number);

        Button button1 = (Button)findViewById(R.id.button1);


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                id = Id.getText().toString();
                password = passwordCreate.getText().toString();
                pwch = passwordCheck.getText().toString();
                name = Name.getText().toString();
                nick = nickname.getText().toString();
                bir = birth.getText().toString();
                num = number.getText().toString();


                if(id==null || id.equals("")) {
                    Toast.makeText(getApplicationContext(), "아이디를 입력해주세요.", Toast.LENGTH_LONG).show();
                    Id.requestFocus();
                } else if(password==null || password.equals("")) {
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요.", Toast.LENGTH_LONG).show();
                    passwordCreate.requestFocus();
                } else if(name==null || name.equals("")) {
                    Toast.makeText(getApplicationContext(), "이름을 입력해주세요.", Toast.LENGTH_LONG).show();
                    Name.requestFocus();
                } else if(nick==null || nick.equals("")) {
                    Toast.makeText(getApplicationContext(), "닉네임을 입력해주세요.", Toast.LENGTH_LONG).show();
                    nickname.requestFocus();
                } else if(bir==null || bir.equals("")) {
                    Toast.makeText(getApplicationContext(), "생년월일을 입력해주세요.", Toast.LENGTH_LONG).show();
                    birth.requestFocus();
                } else if(!password.equals(pwch)) {
                    Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
                    passwordCheck.requestFocus();
                } else {
                    InsertData task = new InsertData();
                    task.execute("http://" + IP_ADDRESS + "/insert.php", id, password, name, nick, bir, num);

                    Id.setText("");
                    passwordCreate.setText("");
                    Name.setText("");
                    nickname.setText("");
                    birth.setText("");
                    number.setText("");

                    Intent intent = new Intent();
                    finish();
                }
            }
        });


    }


    class InsertData extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(main01.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected String doInBackground(String... params) {

            String id = (String)params[1];
            String password = (String)params[2];
            String name = (String)params[3];
            String nick = (String)params[4];
            String bir = (String)params[5];
            String num = (String)params[6];

            String serverURL = (String)params[0];
            String postParameters = "id=" + id + "&password=" + password + "&name=" + name + "&nick=" + nick + "&bir=" + bir + "&num=" + num;


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



