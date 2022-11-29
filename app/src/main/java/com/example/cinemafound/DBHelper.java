package com.example.cinemafound;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

public class DBHelper  extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 6;
    public static final String DATABASE_NAME = "contactDb";
    public static final String TABLE_CONTACTS = "contacts";
    public static final String TABLE_MARKERS = "markers";
    public static final String TABLE_PHOTOS = "reviewPhoto";
    public static final String TABLE_REVIEWS = "reviews";
    public static final String TABLE_INQUIRIES = "inquiries";

    public static final String KEY_ID = "_id";
    public static final String KEY_LOGIN = "login";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USERTYPE = "userType";

    public static final String KEY_MARKER_ID = "_marker_id";
    public static final String KEY_MARKER_PHOTO = "marker_photo";
    public static final String KEY_MARKER_NAME = "marker_name";
    public static final String KEY_LAT = "latitude";
    public static final String KEY_LNG = "longitude";

    public static final String KEY_REVIEW_ID = "review_id";
    public static final String KEY_USER_REVIEW = "user_review";
    public static final String KEY_MARKER_REVIEW = "marker_review";
    public static final String KEY_REVIEW = "review";
    public static final String KEY_RATE = "rate";

    public static final String KEY_INQUIRE_ID = "_inquire_id";
    public static final String KEY_INQUIRE_PHOTO = "inquire_photo";
    public static final String KEY_INQUIRE_NAME = "inquire_name";
    public static final String KEY_LAT_INQUIRE = "latitude_inquire";
    public static final String KEY_LNG_INQUIRE = "longitude_inquire";

    public static final String KEY_PHOTO_ID = "_photo_id";
    public static final String KEY_PHOTO_USER_ID = "photo_user_id";
    public static final String KEY_PHOTO_MARKER_ID = "photo_marker_id";
    public static final String KEY_PHOTO_REVIEW_ID = "photo_review_id";
    public static final String KEY_PHOTO = "photo";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL("drop table if exists " + TABLE_CONTACTS);
        db.execSQL("create table " + TABLE_MARKERS + "(" + KEY_MARKER_ID
                + " integer primary key," + KEY_MARKER_NAME + " text," + KEY_MARKER_PHOTO + " blob," + KEY_LAT + " real," + KEY_LNG + " real" + ")");
        db.execSQL("create table " + TABLE_CONTACTS + "(" + KEY_ID
                + " integer primary key," + KEY_LOGIN + " text," + KEY_PASSWORD + " text," + KEY_IMAGE + " blob," + KEY_USERTYPE + " text" + ")");
        db.execSQL("create table " + TABLE_REVIEWS + "("
                + KEY_REVIEW_ID + " integer primary key," + KEY_USER_REVIEW + " integer," + KEY_MARKER_REVIEW + " integer," + KEY_REVIEW + " text,"
                + KEY_RATE + " real" + ")");
        db.execSQL("create table " + TABLE_PHOTOS +
                "(" + KEY_PHOTO_ID + " integer primary key," + KEY_PHOTO_USER_ID + " integer," + KEY_PHOTO_MARKER_ID + " integer," + KEY_PHOTO_REVIEW_ID
                + " integer," + KEY_PHOTO + " blob" + ")");
        db.execSQL("create table " + TABLE_INQUIRIES +
                "(" + KEY_INQUIRE_ID + " integer primary key," + KEY_INQUIRE_PHOTO + " blob," + KEY_INQUIRE_NAME + " text," + KEY_LAT_INQUIRE + " real,"
                + KEY_LNG_INQUIRE + " real" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_CONTACTS);
        db.execSQL("drop table if exists " + TABLE_MARKERS);
        db.execSQL("drop table if exists " + TABLE_REVIEWS);
        db.execSQL("drop table if exists " + TABLE_PHOTOS);
        db.execSQL("drop table if exists " + TABLE_INQUIRIES);
        onCreate(db);
    }
}