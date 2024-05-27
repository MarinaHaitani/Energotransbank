package com.example.energotransbank;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.energotransbank.databinding.ActivityGeneralBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class General extends AppCompatActivity {
    private static final int REQUEST_CODE_PROFILE = 1;
    private ActivityGeneralBinding binding;
    private CircleImageView profileImageButton;
    private DatabaseHelper dbHelper;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        binding = ActivityGeneralBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);

        userId = getSharedPreferences("user_prefs", MODE_PRIVATE).getLong("current_user_id", -1);
        profileImageButton = findViewById(R.id.profileImageButton); // Ensure this ID matches the one in the layout
        if (userId != -1) {
            loadUserProfilePicture();
        }

        profileImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(General.this, Profile.class);
            startActivityForResult(intent, REQUEST_CODE_PROFILE);
        });

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_transfers, R.id.navigation_history, R.id.navigation_support, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_general);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    private void loadUserProfilePicture() {
        String imagePath = dbHelper.getProfilePicturePath(userId);
        if (imagePath != null && !imagePath.isEmpty()) {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                profileImageButton.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PROFILE && resultCode == RESULT_OK) {
            String imagePath = data.getStringExtra("profile_image_path");
            if (imagePath != null && !imagePath.isEmpty()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                profileImageButton.setImageBitmap(bitmap);
            }
        }
    }
}
