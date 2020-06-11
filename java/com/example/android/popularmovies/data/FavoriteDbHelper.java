package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmovies.data.FavoriteContract.*;


public class FavoriteDbHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 3;


    public FavoriteDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " +
                FavoritesAdd.TABLE_NAME + " (" +
                FavoritesAdd._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavoritesAdd.MOVIE_ID + " INTEGER NOT NULL," +
                FavoritesAdd.MOVIE_NAME + " TEXT NOT NULL," +
                FavoritesAdd.MOVIE_POSTER + " TEXT NOT NULL," +
                FavoritesAdd.MOVIE_RATE + " TEXT NOT NULL," +
                FavoritesAdd.MOVIE_RELEASE_DATE + " TEXT NOT NULL," +
                FavoritesAdd.MOVIE_OVERVIEW + " TEXT NOT NULL" +
                "); ";
        db.execSQL(SQL_CREATE_FAVORITES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoritesAdd.TABLE_NAME);
        onCreate(db);
    }

}
