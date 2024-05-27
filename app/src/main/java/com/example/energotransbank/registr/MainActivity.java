package com.example.energotransbank.registr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.energotransbank.General;
import com.example.energotransbank.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
    }

    public void reg(View view) {
        Intent intent = new Intent(MainActivity.this, LogIn_and_SignUp.class);
        startActivity(intent);
    }

    public void vhod(View view) {
        Intent intent = new Intent(MainActivity.this, Pin_code.class);
        intent.putExtra("isLogin", true);
        startActivity(intent);
    }

}