/*package com.example.lb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class Onboard extends AppCompatActivity {
private ViewPager screenPager;
TabLayout tabIndicator;
ViewPagerAdapter viewPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_onboard);


        tabIndicator = findViewById(R.id.tab_indicator);
        List<ScreenItem> list = new ArrayList<>();
        list.add(new ScreenItem("Анализы","Экспресс сбор и получение проб", "Пропустить", R.drawable.board1, R.drawable.plus));
        list.add(new ScreenItem("Уведомления","Вы быстро узнаете о результатах", "Пропустить", R.drawable.board2, R.drawable.plus));
        list.add(new ScreenItem("Мониторинг","Наши врачи всегда наблюдают \n за вашими покказателями здоровья", "Завершить", R.drawable.board3, R.drawable.plus));

        screenPager = findViewById(R.id.screenViewPager);
        viewPagerAdapter = new ViewPagerAdapter(this, list);
        screenPager.setAdapter(viewPagerAdapter);

        // setup tablayout with viewpager

        tabIndicator.setupWithViewPager(screenPager);
        tabIndicator.setSelectedTabIndicator(null);



    }
    public void plus(View view){
            Intent plus = new Intent(Onboard.this, LogIn_and_SignUp.class);
            plus.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(plus);
        }
    public void skip(View view){
        Intent plus = new Intent(Onboard.this, LogIn_and_SignUp.class);
        plus.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(plus);
    }

*/