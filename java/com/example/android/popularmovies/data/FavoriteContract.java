package com.example.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class FavoriteContract {
    static final String AUTHORITY = "com.example.android.popularmovies";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    static final String FAVORITES_PATH = "favorites";

    public static final class FavoritesAdd implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(FAVORITES_PATH).build();

        static final String TABLE_NAME = "favorites";

        public static final String MOVIE_ID = "movieId";
        public static final String MOVIE_NAME = "movieName";
        public static final String MOVIE_POSTER = "moviePoster";
        public static final String MOVIE_RATE = "movieRate";
        public static final String MOVIE_RELEASE_DATE = "movieRelease";
        public static final String MOVIE_OVERVIEW = "movieOverview";
    }
}
