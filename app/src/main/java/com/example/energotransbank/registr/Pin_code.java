package com.example.energotransbank.registr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.energotransbank.DatabaseHelper;
import com.example.energotransbank.General;
import com.example.energotransbank.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Pin_code extends AppCompatActivity {
    private FirebaseAuth auth;
    private StringBuilder enteredPin = new StringBuilder();
    private View[] pinIndicators;
    private TextView pinInstruction;
    private boolean isFirstEntryComplete = false;
    private String firstEnteredPin;
    private boolean isLogin = false;
    private DatabaseHelper dbHelper;
    private String phoneNumber;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_code);

        dbHelper = new DatabaseHelper(this);
        auth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        isLogin = getIntent().getBooleanExtra("isLogin", false);
        pinInstruction = findViewById(R.id.pinInstruction);
        if (pinInstruction == null) {
            Log.e("PinCodeActivity", "pinInstruction TextView is null");
        } else {
            if (isLogin) {
                pinInstruction.setText("Введите ваш пин-код");
            } else {
                pinInstruction.setText("Придумайте код \n для входа в приложение");
            }
        }

        try {
            getSupportActionBar().hide();
        } catch (NullPointerException e) {
            // Обработка случая отсутствия ActionBar
        }

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            phoneNumber = currentUser.getPhoneNumber();
        }

        View[] buttons = {
                findViewById(R.id.button0),
                findViewById(R.id.button1),
                findViewById(R.id.button2),
                findViewById(R.id.button3),
                findViewById(R.id.button4),
                findViewById(R.id.button5),
                findViewById(R.id.button6),
                findViewById(R.id.button7),
                findViewById(R.id.button8),
                findViewById(R.id.button9),
                findViewById(R.id.buttonDelete)
        };

        pinIndicators = new View[]{
                findViewById(R.id.circle1),
                findViewById(R.id.circle2),
                findViewById(R.id.circle3),
                findViewById(R.id.circle4)
        };

        for (View button : buttons) {
            button.setOnTouchListener((view, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    view.setBackground(ContextCompat.getDrawable(Pin_code.this, R.drawable.round_button_pressed));
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    view.setBackground(ContextCompat.getDrawable(Pin_code.this, R.drawable.round_button));
                    handleButtonClick(view);
                    return true;
                }
                return false;
            });
        }
    }

    private void handleButtonClick(View view) {
        int id = view.getId();
        if (id == R.id.buttonDelete) {
            if (enteredPin.length() > 0) {
                enteredPin.deleteCharAt(enteredPin.length() - 1);
                updatePinIndicators(enteredPin.length());
            }
        } else if (enteredPin.length() < 4) {
            enteredPin.append(((Button)view).getText().toString());
            updatePinIndicators(enteredPin.length());
            if (enteredPin.length() == 4) {
                if (!isFirstEntryComplete && !isLogin) {
                    firstEnteredPin = enteredPin.toString();
                    enteredPin.setLength(0);
                    clearPinIndicators();
                    isFirstEntryComplete = true;
                    pinInstruction.setText("Повторите введенный пин-код");
                } else {
                    checkPinCorrectness();
                }
            }
        }
    }

    private void updatePinIndicators(int pinLength) {
        for (int i = 0; i < pinLength; i++) {
            pinIndicators[i].setBackground(ContextCompat.getDrawable(this, R.drawable.circle_black));
        }
        for (int i = pinLength; i < 4; i++) {
            pinIndicators[i].setBackground(ContextCompat.getDrawable(this, R.drawable.circle_gray));
        }
    }

    private void clearPinIndicators() {
        for (View indicator : pinIndicators) {
            indicator.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_gray));
        }
    }

    private void checkPinCorrectness() {
        if (isLogin) {
            String savedPin = sharedPreferences.getString("saved_pin", null);
            if (savedPin != null && savedPin.equals(enteredPin.toString())) {
                startActivity(new Intent(this, General.class)); // Переход на главную страницу
                finish();
            } else {
                showErrorIndicators();
            }
        } else {
            if (enteredPin.length() == 4) {
                if (!isFirstEntryComplete) {
                    firstEnteredPin = enteredPin.toString();
                    enteredPin.setLength(0);
                    clearPinIndicators();
                    isFirstEntryComplete = true;
                    pinInstruction.setText("Повторите введенный пин-код");
                } else if (enteredPin.toString().equals(firstEnteredPin)) {
                    savePinCodeToCache(enteredPin.toString());
                    startActivity(new Intent(this, Registraciya.class)); // Переход на страницу регистрации
                    finish();
                } else {
                    showErrorIndicators();
                }
            }
        }
    }

    private void savePinCodeToCache(String pinCode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("saved_pin", pinCode);
        editor.apply();
        Log.d("PinCodeActivity", "PIN saved to cache.");
    }

    private void showErrorIndicators() {
        for (View indicator : pinIndicators) {
            indicator.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_red));
        }
        new Handler().postDelayed(this::resetPinEntry, 1000);
    }

    private void resetPinEntry() {
        enteredPin.setLength(0);
        isFirstEntryComplete = false; // Позволяет пользователю ввести пин-код заново с начала
        pinInstruction.setText("Придумайте код \n для входа в приложение"); // Возвращаем начальный текст
        clearPinIndicators();
    }

    public void back(View view) {
        startActivity(new Intent(Pin_code.this, MainActivity.class));
    }
}
