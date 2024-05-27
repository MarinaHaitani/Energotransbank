package com.example.energotransbank.registr;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.energotransbank.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class EmailKod extends AppCompatActivity {
    private EditText[] codeInputs = new EditText[6];
    private TextView timerTextView;
    private CountDownTimer countDownTimer;
    private String verificationId; // Получаем этот ID из Intent
    private static final long START_TIME_IN_MILLIS = 60000; // 15 секунд
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_email_kod);

        initializeElements();
        startTimer();
        // Получаем verificationId и code (если нужно)
        // В onCreate
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            verificationId = extras.getString("verificationId");
            if (verificationId == null) {
                Log.e("VerificationID", "No verification ID received.");
                finish(); // Завершаем активность, если ID не получен
            }
        } else {
            Log.e("VerificationID", "Intent extras are null.");
            finish(); // Завершаем активность, если extras отсутствуют
        }

        int[] editTextIds = {R.id.enterEMail1, R.id.enterEMail2, R.id.enterEMail3, R.id.enterEMail4, R.id.enterEMail5, R.id.enterEMail6};
        for (int i = 0; i < codeInputs.length; i++) {
            codeInputs[i] = findViewById(editTextIds[i]);
            if (i > 0) addTextWatcher(codeInputs[i - 1], codeInputs[i]);
        }
        setupLastEditText(codeInputs[5]);
    }
    private void initializeElements() {
        timerTextView = findViewById(R.id.timerTextView);
        verificationId = getIntent().getStringExtra("verificationId");

        // Инициализация codeInputs и addTextWatcher аналогично вашему текущему коду
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(START_TIME_IN_MILLIS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String timeLeftFormatted = String.format(Locale.getDefault(), "Повторная отправка через %02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                timerTextView.setText(timeLeftFormatted);
            }

            @Override
            public void onFinish() {
                timerTextView.setText("Отправить код повторно");
                timerTextView.setOnClickListener(v -> resendVerificationCode());
            }
        }.start();
    }
    private void resendVerificationCode() {
        sendVerificationCode(verificationId); // Предполагается, что у вас есть метод повторной отправки
        startTimer(); // Перезапуск таймера
    }
    private void sendVerificationCode(String phone) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                .setPhoneNumber(phone)       // Телефонный номер для отправки кода
                .setTimeout(60L, TimeUnit.SECONDS) // Время ожидания до истечения кода
                .setActivity(this)           // Активность, в которой будет вызываться обратный вызов
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        Log.d("Verification", "onVerificationCompleted:" + credential);
                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Log.e("Verification", "onVerificationFailed", e);
                        Toast.makeText(EmailKod.this, "Верификация не удалась: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }


                    @Override
                    public void onCodeSent(String newVerificationId, PhoneAuthProvider.ForceResendingToken token) {
                        super.onCodeSent(newVerificationId, token);
                        verificationId = newVerificationId;  // Обновляем verificationId новым ID
                        Log.d("Verification", "Code sent to " + phone);
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void addTextWatcher(final EditText currentEditText, final EditText nextEditText) {
        currentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    nextEditText.requestFocus();
                }
            }
        });
    }


    private void verifyCode() {
        String code = "";
        for (EditText editText : codeInputs) {
            code += editText.getText().toString();
        }
        if (code.length() == 6) {
            Log.d("VerifyCode", "Attempting to verify code: " + code);
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            signInWithPhoneAuthCredential(credential);
        } else {
            Toast.makeText(this, "Please enter complete code", Toast.LENGTH_SHORT).show();
        }
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("SignIn", "Sign in with credential: success");
                        startActivity(new Intent(this, Pin_code.class));
                        finish();
                    } else {
                        if (task.getException() != null) {
                            Log.e("SignIn", "Sign in failed", task.getException());
                            Toast.makeText(this, "Sign in failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                        for (EditText editText : codeInputs) {
                            editText.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                        }
                    }
                });
    }
    private void setupLastEditText(EditText editText) {
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                verifyCode();
                return true;
            }
            return false;
        });
    }
    private void handleSignInFailure() {
        // Сюда можно добавить логику по повторной отправке кода или предложения пользователю повторить попытку
        for (EditText editText : codeInputs) {
            editText.getText().clear(); // Очищаем поля ввода для нового ввода кода
        }
        codeInputs[0].requestFocus(); // Установка фокуса на первое поле ввода
    }
    public void back(View view) {
        startActivity(new Intent(EmailKod.this, MainActivity.class));
    }


}