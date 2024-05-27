package com.example.energotransbank;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "user_data.db";
    private static final int DATABASE_VERSION = 5;

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_PATRONYMIC = "patronymic";
    public static final String COLUMN_DOB = "dob";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_PROFILE_PICTURE_PATH = "profile_picture_path";
    public static final String COLUMN_PHONE_NUMBER = "phone_number"; // Ensure this column exists
    public static final String COLUMN_PIN_CODE = "pin_code"; // Ensure this column exists

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_LAST_NAME + " TEXT," +
                COLUMN_FIRST_NAME + " TEXT," +
                COLUMN_PATRONYMIC + " TEXT," +
                COLUMN_DOB + " TEXT," +
                COLUMN_GENDER + " TEXT," +
                COLUMN_PROFILE_PICTURE_PATH + " TEXT," +
                COLUMN_PHONE_NUMBER + " TEXT," +
                COLUMN_PIN_CODE + " TEXT)";
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_PHONE_NUMBER + " TEXT");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_PIN_CODE + " TEXT");
        }
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_PROFILE_PICTURE_PATH + " TEXT");
        }
    }

    public Cursor getUserData(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(userId)});
    }

    public String getProfilePicturePath(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_PROFILE_PICTURE_PATH}, COLUMN_ID + " = ?", new String[]{String.valueOf(userId)}, null, null, null);
        String imagePath = null;
        if (cursor != null && cursor.moveToFirst()) {
            imagePath = cursor.getString(cursor.getColumnIndex(COLUMN_PROFILE_PICTURE_PATH));
            cursor.close();
        }
        return imagePath;
    }

    public void updateUserProfilePicturePath(long userId, String path) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROFILE_PICTURE_PATH, path);
        db.update(TABLE_USERS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(userId)});
    }
}
