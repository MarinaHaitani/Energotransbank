package com.example.energotransbank;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.energotransbank.registr.MainActivity;
import com.example.energotransbank.registr.Pin_code;
import com.example.energotransbank.registr.Registraciya;

import java.util.Timer;
import java.util.TimerTask;

public class Zastavka extends AppCompatActivity {
Timer ttimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_zastavka);

        ttimer = new Timer();
        ttimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(Zastavka.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 5000);
    }
}