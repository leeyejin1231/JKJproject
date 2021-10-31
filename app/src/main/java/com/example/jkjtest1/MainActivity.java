    package com.example.jkjtest1;

    import android.app.ProgressDialog;
    import android.content.Intent;
    import android.os.AsyncTask;
    import android.os.Handler;
    import android.support.v7.app.AppCompatActivity;
    import android.os.Bundle;
    import android.text.method.ScrollingMovementMethod;
    import android.util.Log;
    import android.view.View;
    import android.widget.Button;
    import android.widget.ListAdapter;
    import android.widget.SimpleAdapter;
    import android.widget.TextView;
    import android.widget.Toast;

    import org.json.JSONArray;
    import org.json.JSONException;
    import org.json.JSONObject;
    import org.w3c.dom.Text;

    import java.io.BufferedReader;
    import java.io.IOException;
    import java.io.InputStream;
    import java.io.InputStreamReader;
    import java.io.OutputStream;
    import java.net.HttpURLConnection;
    import java.net.MalformedURLException;
    import java.net.URL;
    import java.util.HashMap;

    public class MainActivity extends AppCompatActivity {

        private static String IP_ADDRESS = "192.168.0.5";
        private static String TAG = "phptest";

        TextView id, pw;
        String ID, PW, password;

        private String mJsonString;

        private static final String TAG_JSON="webnautes";
        private static final String TAG_PASSWORD = "password";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            id = (TextView)findViewById(R.id.id);
            pw = (TextView)findViewById(R.id.pw);

            id.setMovementMethod(new ScrollingMovementMethod());

            Button loginButton = (Button)findViewById(R.id.button1);
            Button button2 = (Button)findViewById(R.id.button2);

            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =  new Intent(getApplicationContext(), main01.class);
                    startActivityForResult(intent, 101); //101 화면 구분자
                }
            });

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ID = id.getText().toString();
                    PW = pw.getText().toString();

                    if(ID.equals("")){
                                    Toast.makeText(getApplicationContext(), "아이디를 입력해주세요.", Toast.LENGTH_LONG).show();
                                    id.requestFocus();
                                } else if(PW.equals("")){
                                    Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요.", Toast.LENGTH_LONG).show();
                                    pw.requestFocus();
                                } else {
                                    GetData task = new GetData();
                                    task.execute(ID);

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            if (PW.equals(password)) {
                                    Intent intent = new Intent(getApplicationContext(), subway01.class);
                                    startActivityForResult(intent, 101); //101 화면 구분자

                                } else {
                                    Toast.makeText(getApplicationContext(), "비밀번호 또는 아이디가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
                                    id.requestFocus();
                                }
                            }
                        }, 1000);


                    }
                }
            });

        }
        private class GetData extends AsyncTask<String, Void, String>{

            ProgressDialog progressDialog;
            String errorString = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = ProgressDialog.show(MainActivity.this,
                        "Please Wait", null, true, true);
            }


            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                progressDialog.dismiss();
               // Toast.makeText(getApplicationContext(), ""+result, Toast.LENGTH_LONG).show();
                Log.d(TAG, "response - " + result);

                if (result == null){
                    Toast.makeText(getApplicationContext(), ""+errorString, Toast.LENGTH_LONG).show();

                }
                else {

                    mJsonString = result;
                    showResult();
                }
            }


            @Override
            protected String doInBackground(String... params) {

                String ID = (String)params[0];

                String serverURL = "http://192.168.0.5/query.php";
                String postParameters = "id=" + ID;


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
                    if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                        inputStream = httpURLConnection.getInputStream();
                    }
                    else{
                        inputStream = httpURLConnection.getErrorStream();
                    }


                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    StringBuilder sb = new StringBuilder();
                    String line;

                    while((line = bufferedReader.readLine()) != null){
                        sb.append(line);
                    }


                    bufferedReader.close();


                    return sb.toString().trim();


                } catch (Exception e) {

                    Log.d(TAG, "InsertData: Error ", e);
                    errorString = e.toString();

                    return null;
                }

            }
        }


        private void showResult(){
            try {
                JSONObject jsonObject = new JSONObject(mJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for(int i=0;i<jsonArray.length();i++){

                    JSONObject item = jsonArray.getJSONObject(i);

                    password = item.getString(TAG_PASSWORD);


                    HashMap<String,String> hashMap = new HashMap<>();

                    hashMap.put(TAG_PASSWORD, password);



                }


            } catch (JSONException e) {

                Log.d(TAG, "showResult : ", e);
            }

        }

    }
