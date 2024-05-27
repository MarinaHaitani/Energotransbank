package com.example.energotransbank;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.energotransbank.registr.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private CircleImageView profileImageView;
    private TextView lastNameTextView, firstNameTextView, patronymicTextView, dobTextView, genderTextView;
    private DatabaseHelper dbHelper;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_profile);

        dbHelper = new DatabaseHelper(this);

        profileImageView = findViewById(R.id.profileImageView);
        lastNameTextView = findViewById(R.id.lastNameTextView);
        firstNameTextView = findViewById(R.id.firstNameTextView);
        patronymicTextView = findViewById(R.id.patronymicTextView);
        dobTextView = findViewById(R.id.dobTextView);
        genderTextView = findViewById(R.id.genderTextView);

        userId = getSharedPreferences("user_prefs", MODE_PRIVATE).getLong("current_user_id", -1);
        if (userId != -1) {
            loadUserData();
        }

        profileImageView.setOnClickListener(v -> openImageChooser());
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profileImageView.setImageBitmap(bitmap);
                saveImageToCache(bitmap);
            } catch (IOException e) {
                Toast.makeText(this, "Ошибка при сохранении изображения", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    private void saveImageToCache(Bitmap bitmap) throws IOException {
        File cacheDir = getCacheDir();
        File imageFile = new File(cacheDir, "profile_" + userId + ".jpg");

        try (FileOutputStream out = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        }

        getSharedPreferences("user_prefs", MODE_PRIVATE).edit()
                .putString("profile_image_path", imageFile.getAbsolutePath())
                .apply();

        saveImagePathToDatabase(imageFile.getAbsolutePath());

        Intent resultIntent = new Intent();
        resultIntent.putExtra("profile_image_path", imageFile.getAbsolutePath());
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void saveImagePathToDatabase(String path) {
        dbHelper.updateUserProfilePicturePath(userId, path);
    }

    private void loadUserData() {
        Cursor cursor = dbHelper.getUserData(userId);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                lastNameTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LAST_NAME)));
                firstNameTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FIRST_NAME)));
                patronymicTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PATRONYMIC)));
                dobTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DOB)));
                genderTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_GENDER)));

                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROFILE_PICTURE_PATH));
                if (imagePath != null && !imagePath.isEmpty()) {
                    loadImageFromPath(imagePath);
                }
            } else {
                Toast.makeText(this, "Не удалось загрузить данные пользователя", Toast.LENGTH_SHORT).show();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void loadImageFromPath(String imagePath) {
        File imgFile = new File(imagePath);
        if (imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            profileImageView.setImageBitmap(bitmap);
        }
    }

    public void back(View view) {
        finish();
    }

    public void logOut(View view) {
        startActivity(new Intent(Profile.this, MainActivity.class));
        finish();
    }
}
