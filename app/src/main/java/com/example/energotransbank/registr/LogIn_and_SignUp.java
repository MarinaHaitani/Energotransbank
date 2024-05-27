package com.example.energotransbank.registr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.energotransbank.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class LogIn_and_SignUp extends AppCompatActivity {

    private EditText editTextPhone;
    private Button buttonLogin;
    private FirebaseAuth auth;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_log_in_and_sign_up);
        Log.d("LoginSignUpActivity", "Activity loaded");

        // Инициализация Firebase
        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();

        editTextPhone = findViewById(R.id.editTextPhoneNumber);
        buttonLogin = findViewById(R.id.buttonSignUp);

        buttonLogin.setOnClickListener(v -> {
            String phoneNumber = editTextPhone.getText().toString().replaceAll("[^\\d]", ""); // Очистка номера от нецифровых символов
            if (!phoneNumber.isEmpty() && phoneNumber.length() >= 10) {
                sendVerificationCode(phoneNumber);
            } else {
                Toast.makeText(LogIn_and_SignUp.this, "Введите корректный номер телефона", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendVerificationCode(String phone) {
        String formattedPhoneNumber = formatPhoneNumber(phone);
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(formattedPhoneNumber) // Телефонный номер для отправки кода
                .setTimeout(60L, TimeUnit.SECONDS) // Время ожидания
                .setActivity(this) // Активность, в которой будет вызываться обратный вызов
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        // Автоматическое завершение верификации, если возможно
                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        // Обработка ошибок, например, неверный номер телефона
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Неверный формат номера телефона
                            Toast.makeText(LogIn_and_SignUp.this, "Неверный формат номера телефона. Введите номер в формате E.164.", Toast.LENGTH_LONG).show();
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // Превышение лимита запросов
                            Toast.makeText(LogIn_and_SignUp.this, "Превышено количество запросов. Попробуйте позже.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LogIn_and_SignUp.this, "Верификация не удалась: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        Log.e("LoginSignUpActivity", "Verification failed", e);
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                        super.onCodeSent(verificationId, token);
                        LogIn_and_SignUp.this.verificationId = verificationId;
                        Intent intent = new Intent(LogIn_and_SignUp.this, EmailKod.class);
                        intent.putExtra("verificationId", verificationId);
                        startActivity(intent);
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String phoneNumber = task.getResult().getUser().getPhoneNumber();

                        // Здесь сохраняем номер телефона в SharedPreferences
                        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                        prefs.edit().putString("phone_number", phoneNumber).apply();
                        Log.d("SavePhone", "Phone number saved: " + phoneNumber);

                        // Логирование для проверки сохранения
                        Log.d("LoginSignUpActivity", "Phone number saved: " + phoneNumber);

                        // Переход в другую активность
                        Intent intent = new Intent(LogIn_and_SignUp.this, Pin_code.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LogIn_and_SignUp.this, "Ошибка аутентификации", Toast.LENGTH_LONG).show();
                    }
                });
    }



    private void savePhoneNumber(String phoneNumber) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.child("phoneNumber").setValue(phoneNumber);
    }

    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber.startsWith("7") && phoneNumber.length() == 11) {
            return "+" + phoneNumber;
        } else if (phoneNumber.startsWith("8") && phoneNumber.length() == 11) {
            return "+7" + phoneNumber.substring(1);
        } else if (phoneNumber.startsWith("+7") &&
                phoneNumber.length() == 12) {
            return phoneNumber;
        } else {
            return "+" + phoneNumber;
        }
    }

    public void back(View view) {
        startActivity(new Intent(LogIn_and_SignUp.this, MainActivity.class));
    }

    public void skip(View view) {
        startActivity(new Intent(LogIn_and_SignUp.this, Pin_code.class));
    }
}
