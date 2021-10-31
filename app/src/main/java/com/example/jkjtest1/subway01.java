package com.example.jkjtest1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class subway01 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subway01);

        Button menubutton = (Button)findViewById(R.id.menubutton);
        menubutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(getApplicationContext(), Menu.class);
                startActivityForResult(intent, 101); //101 화면 구분자

            }
        });

        Button menubu = (Button)findViewById(R.id.button);
        menubu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(getApplicationContext(), SubSeat.class);
                startActivityForResult(intent, 101); //101 화면 구분자

            }
        });
    }
}
