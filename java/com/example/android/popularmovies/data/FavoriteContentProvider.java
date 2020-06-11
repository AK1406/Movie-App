package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import static com.example.android.popularmovies.data.FavoriteContract.FavoritesAdd.TABLE_NAME;


public class FavoriteContentProvider extends ContentProvider{
//LESSON 11 - BUILDING A CONTENT PROVIDER

    private static final int FAVORITES = 700;
    private static final int FAVORITES_ID = 701;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(FavoriteContract.AUTHORITY, FavoriteContract.FAVORITES_PATH, FAVORITES);
        uriMatcher.addURI(FavoriteContract.AUTHORITY, FavoriteContract.FAVORITES_PATH + "/#", FAVORITES_ID);
        return uriMatcher;
    }

    private FavoriteDbHelper favoritesDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        favoritesDbHelper = new FavoriteDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = favoritesDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor returnCursor;

        switch (match){
            case FAVORITES:
                returnCursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case FAVORITES_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{id};

                returnCursor = db.query(TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

                default:
                    throw new UnsupportedOperationException("Unknown uri: "+ uri);
        }
        returnCursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);
        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);

        switch (match){
            case FAVORITES:
                return "vnd.android.cursor.dir" + "/" + FavoriteContract.AUTHORITY + "/" + FavoriteContract.FAVORITES_PATH;
            case FAVORITES_ID:
                return "vnd.android.cursor.item" + "/" + FavoriteContract.AUTHORITY + "/" + FavoriteContract.FAVORITES_PATH;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = favoritesDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;

        if (match == FAVORITES) {
            long id = db.insert(TABLE_NAME, null, values);
            if (id > 0) {
                returnUri = ContentUris.withAppendedId(FavoriteContract.FavoritesAdd.CONTENT_URI, id);
            } else {
                throw new SQLException("Failed to insert row into" + uri);
            }
        } else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = favoritesDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int favoritesDeleted;

        if (match == FAVORITES) favoritesDeleted = db.delete(TABLE_NAME, selection, selectionArgs);
        else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (favoritesDeleted != 0){
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri,null);
        }
        return favoritesDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int favoriteUpdated;
        int match = sUriMatcher.match(uri);

        if (match == FAVORITES_ID) {
            String id = uri.getPathSegments().get(1);
            favoriteUpdated = favoritesDbHelper.getWritableDatabase()
                    .update(TABLE_NAME, values, "_id=?", new String[]{id});
        } else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (favoriteUpdated != 0){
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }
        return favoriteUpdated;
    }
}
