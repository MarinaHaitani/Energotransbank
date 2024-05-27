package com.example.energotransbank.registr;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.energotransbank.DatabaseHelper;
import com.example.energotransbank.General;
import com.example.energotransbank.R;

import java.util.Calendar;

public class Registraciya extends AppCompatActivity {
    EditText lastNameEditText, firstNameEditText, patronymicEditText, dobEditText;
    RadioGroup genderRadioGroup;
    RadioButton maleRadioButton, femaleRadioButton;
    Button saveButton;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registraciya);
        getSupportActionBar().hide();

        // Инициализация компонентов
        lastNameEditText = findViewById(R.id.lastNameEditText);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        patronymicEditText = findViewById(R.id.patronymicEditText);
        dobEditText = findViewById(R.id.dobEditText);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        maleRadioButton = findViewById(R.id.maleRadioButton);
        femaleRadioButton = findViewById(R.id.femaleRadioButton);
        saveButton = findViewById(R.id.buttonSignUp);

        dbHelper = new DatabaseHelper(this);

        setupTextWatchers();
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long userId = saveData();
                if (userId != -1) {
                    // Сохранение ID пользователя в SharedPreferences
                    getSharedPreferences("user_prefs", MODE_PRIVATE)
                            .edit()
                            .putLong("current_user_id", userId)
                            .apply();

                    // Переход к профилю с передачей ID пользователя
                    Intent intent = new Intent(Registraciya.this, General.class);
                    intent.putExtra("user_id", userId);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    private void setupTextWatchers() {
        dobEditText.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d]", "");
                    String cleanC = current.replaceAll("[^\\d]", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }

                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length());
                    } else {
                        int day  = Integer.parseInt(clean.substring(0, 2));
                        int mon  = Integer.parseInt(clean.substring(2, 4));
                        int year = Integer.parseInt(clean.substring(4, 8));

                        if (mon > 12) mon = 12;
                        cal.set(Calendar.MONTH, mon - 1);
                        year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                        cal.set(Calendar.YEAR, year);

                        day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                        clean = String.format("%02d%02d%02d", day, mon, year);
                    }

                    clean = String.format("%s.%s.%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    dobEditText.setText(current);
                    dobEditText.setSelection(sel < current.length() ? sel : current.length());
                }
            }
        });

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                boolean isFilled = !lastNameEditText.getText().toString().isEmpty() &&
                        !firstNameEditText.getText().toString().isEmpty() &&
                        !patronymicEditText.getText().toString().isEmpty() &&
                        !dobEditText.getText().toString().isEmpty() &&
                        genderRadioGroup.getCheckedRadioButtonId() != -1;
                saveButton.setEnabled(isFilled);
            }
        };

        lastNameEditText.addTextChangedListener(watcher);
        firstNameEditText.addTextChangedListener(watcher);
        patronymicEditText.addTextChangedListener(watcher);
        dobEditText.addTextChangedListener(watcher);
        genderRadioGroup.setOnCheckedChangeListener((group, checkedId) -> watcher.afterTextChanged(null));
    }

    private long saveData() {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_LAST_NAME, lastNameEditText.getText().toString());
        values.put(DatabaseHelper.COLUMN_FIRST_NAME, firstNameEditText.getText().toString());
        values.put(DatabaseHelper.COLUMN_PATRONYMIC, patronymicEditText.getText().toString());
        values.put(DatabaseHelper.COLUMN_DOB, dobEditText.getText().toString());
        values.put(DatabaseHelper.COLUMN_GENDER, ((RadioButton) findViewById(genderRadioGroup.getCheckedRadioButtonId())).getText().toString());

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long userId = db.insert(DatabaseHelper.TABLE_USERS, null, values);
        if (userId == -1) {
            Toast.makeText(this, "Error saving data", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = getSharedPreferences("user_prefs", MODE_PRIVATE).edit();
            editor.putLong("current_user_id", userId);
            editor.apply();
        }
        return userId;
    }


}
