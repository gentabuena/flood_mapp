package com.fea.floodmapp.main.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.fea.floodmapp.main.datamodels.UserInfoModel;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper sInstance;

    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "UserInfo.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "user_gmail_info";

    private static final String COLUMN_USER_ID_AUTOINC = "auto_id";
    private static final String COLUMN_USER_GMAIL_ID = "user_gmail_id";
    private static final String COLUMN_USER_EMAIL = "user_gmail";
    private static final String COLUMN_USER_FULL_NAME = "user_full_name";
    private static final String COLUMN_USER_AGE = "user_age";
    private static final String COLUMN_USER_GENDER = "user_gender";
    private static final String COLUMN_USER_CONTACT_NUMBER = "user_contact_number";
    private static final String COLUMN_USER_ADDRESS = "user_address";
    private static final String COLUMN_USER_DISPLAY_PHOTO = "user_display_photo";

    public static final String SQL_CREATE_TABLE_USER_GMAIL_INFO = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
            COLUMN_USER_ID_AUTOINC + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_USER_GMAIL_ID + " VARCHAR," +
            COLUMN_USER_EMAIL + " VARCHAR," +
            COLUMN_USER_FULL_NAME + " VARCHAR," +
            COLUMN_USER_AGE + " INTEGER," +
            COLUMN_USER_GENDER + " VARCHAR," +
            COLUMN_USER_CONTACT_NUMBER + " VARCHAR," +
            COLUMN_USER_ADDRESS + " VARCHAR," +
            COLUMN_USER_DISPLAY_PHOTO + " VARCHAR)";

    public static synchronized DatabaseHelper getInstance(Context context){
        if (sInstance == null) {
            Log.d(TAG, "getInstance: This is the first instance of DB");
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_USER_GMAIL_INFO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void insertGmailInfoToSQL(Context context, UserInfoModel userInfoModel){
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        long result;

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USER_GMAIL_ID, userInfoModel.getUserID());
        contentValues.put(COLUMN_USER_EMAIL,userInfoModel.getUserGmail());
        contentValues.put(COLUMN_USER_FULL_NAME,userInfoModel.getUserFullName());
        contentValues.put(COLUMN_USER_AGE,userInfoModel.getUserAge());
        contentValues.put(COLUMN_USER_GENDER,userInfoModel.getUserGender());
        contentValues.put(COLUMN_USER_CONTACT_NUMBER,userInfoModel.getUserContactNumber());
        contentValues.put(COLUMN_USER_ADDRESS,userInfoModel.getUserAddress());
        contentValues.put(COLUMN_USER_DISPLAY_PHOTO,userInfoModel.getUserDisplayPhoto());

        result = sqLiteDatabase.insert(TABLE_NAME,null, contentValues);
        if (result == -1) {
            Log.d(TAG, "Insert Failed");
            //Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(context, "Save Completed", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Insert Success");
        }
    }

    public UserInfoModel getUserInfoFromLocalDB(Context context){
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        UserInfoModel userInfoModel = null;

        try {
            String query = "SELECT * FROM " + TABLE_NAME;
            Cursor cursor = sqLiteDatabase.rawQuery(query, null);
            if (cursor.getCount() > 0){
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    String userID = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_GMAIL_ID));
                    String userEmail = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL));
                    String userFullName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_FULL_NAME));
                    int userAge = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_AGE));
                    String userGender = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_GENDER));
                    String userContact = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_CONTACT_NUMBER));
                    String userAddress = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ADDRESS));
                    String userDisplayPhoto = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_DISPLAY_PHOTO));
                    userInfoModel = new UserInfoModel(userID, userEmail, userFullName, userAge, userGender, userContact, userAddress, userDisplayPhoto);
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            userInfoModel = null;
            Log.d(TAG, "Issue here -- " + e);
        }
        return userInfoModel;
    }

    public void dropTable(Context context){
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        try {
            sqLiteDatabase.execSQL("delete from "+ TABLE_NAME);
        }catch (Exception e){
            Log.e(TAG, "dropTable: ", e);
        }
    }

    public void updateUserInfoOnLocalDB(Context context, String userID, int userAge, String userGender, String userMobileNumber, String userAddress){
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        String whereQuery = COLUMN_USER_GMAIL_ID + " = ?;";
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_USER_AGE, userAge);
        cv.put(COLUMN_USER_GENDER, userGender);
        cv.put(COLUMN_USER_CONTACT_NUMBER, userMobileNumber);
        cv.put(COLUMN_USER_ADDRESS, userAddress);
        sqLiteDatabase.update(TABLE_NAME, cv, whereQuery, new String[]{userID});
    }
}
