package com.example.fa_bhautikpethani_c0854487_android.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.fa_bhautikpethani_c0854487_android.Model.Place;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "place_db";

    private static final String TABLE_NAME = "place";
    private static final String COLUMN_ID = "place_id";
    private static final String COLUMN_ADDRESS = "place_address";
    private static final String COLUMN_LATITUDE = "place_latitude";
    private static final String COLUMN_LONGITUDE = "place_longitude";
    private static final String COLUMN_STATUS = "place_status";

    public DBHelper(Context context) {super(context, DATABASE_NAME, null, DATABASE_VERSION);}

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sqlQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER NOT NULL CONSTRAINT product_pk PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ADDRESS + " VARCHAR(100) NOT NULL, " +
                COLUMN_LATITUDE + " DOUBLE NOT NULL, " +
                COLUMN_LONGITUDE + " DOUBLE NOT NULL, " +
                COLUMN_STATUS + " INTEGER NOT NULL);";
        sqLiteDatabase.execSQL(sqlQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String sqlQuery = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
        sqLiteDatabase.execSQL(sqlQuery);
        onCreate(sqLiteDatabase);
    }

    public boolean addNewPlace(Place place) {

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ADDRESS, place.getPlaceAddress());
        contentValues.put(COLUMN_LATITUDE, place.getLatitude());
        contentValues.put(COLUMN_LONGITUDE, place.getLongitude());
        contentValues.put(COLUMN_STATUS, place.getStatus());

        return sqLiteDatabase.insert(TABLE_NAME, null, contentValues) != -1;
    }

    public List<Place> getAllPlaces(int status){
        List<Place> placeList = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_STATUS + "=" + status + ";", null);
        if (cursor.moveToFirst()) {
            do {
                // create an employee instance
                placeList.add(new Place(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getDouble(2),
                        cursor.getDouble(3),
                        cursor.getInt(4)));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return placeList;
    }

    public Place getPlace(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = " + id + ";" , null);
        c.moveToFirst();
        return new Place(
                c.getInt(0),
                c.getString(1),
                c.getDouble(2),
                c.getDouble(3),
                c.getInt(4));
    }

    public List<Place> searchPlace(String keyword, int status) {
        List<Place> placeList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor =  sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ADDRESS +" LIKE '%"+keyword+"%' AND " + COLUMN_STATUS + "=" + status + ";", null);
        if (cursor.moveToFirst()) {
            do {
                // create an employee instance
                placeList.add(new Place(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getDouble(2),
                        cursor.getDouble(3),
                        cursor.getInt(4)));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return placeList;
    }

    public boolean deletePlace(int id) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        return sqLiteDatabase.delete(TABLE_NAME,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}) > 0;
    }

    public boolean updatePlace(Place place) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ADDRESS, place.getPlaceAddress());
        contentValues.put(COLUMN_LATITUDE, place.getLatitude());
        contentValues.put(COLUMN_LONGITUDE, place.getLongitude());
        contentValues.put(COLUMN_STATUS, String.valueOf(place.getStatus()));

        return sqLiteDatabase.update(TABLE_NAME,
                contentValues,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(place.getId())}) > 0;
    }
}
