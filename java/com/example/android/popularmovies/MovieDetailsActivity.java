package com.example.android.popularmovies;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.popularmovies.data.FavoriteContract;
import com.example.android.popularmovies.data.FavoriteDbHelper;
import com.example.android.popularmovies.model.Review;
import com.example.android.popularmovies.model.Trailer;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.MovieDBJsonUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.Objects;


public class MovieDetailsActivity extends AppCompatActivity{

    private RecyclerView recyclerView;
    private RecyclerView recyclerViewReviews;
    private static Bundle bundleRecyclerViewState;
    private final String KEY_RECYCLER_STATE = "recycler_state";
    private TrailerAdapter trailerAdapter;
    private PeopleReviewAdapter reviewAdapter;
    private int id = 0;
    private String title = "";
    private String poster = "";
    private String rate = "";
    private String release = "";
    private String overview = "";
    private SQLiteDatabase mDb;
    private String[] projection =
            {
                    FavoriteContract.FavoritesAdd._ID,
                    FavoriteContract.FavoritesAdd.MOVIE_ID
            };

    private String[] selectionArgs = {""};
    private String selectionClause;
    private Uri newUri;


    private TextView trailerErrorMessage;
    private TextView reviewErrorMessage;
    private Button favorite_btn;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.moviedetails_activity);

        ImageView moviePosterDisplay = findViewById(R.id.movie_poster);
        TextView movieTitleDisplay = findViewById(R.id.movie_title);
        TextView movieRateDisplay = findViewById(R.id.movie_rate);
        TextView movieReleaseDisplay = findViewById(R.id.movie_release_date);
        TextView movieSynopsisDisplay = findViewById(R.id.moviePlot_synopsis);
         trailerErrorMessage= findViewById(R.id.trailer_error_message);
         reviewErrorMessage= findViewById(R.id.review_error_message);
        favorite_btn= findViewById(R.id.add_to_favorites);

        //favorites
        FavoriteDbHelper dbHelper = new FavoriteDbHelper(this);
         mDb = dbHelper.getWritableDatabase();



        //trailers
        recyclerView = findViewById(R.id.recyclerview_trailers);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(trailerAdapter);



        //reviews
        recyclerViewReviews =  findViewById(R.id.recyclerview_reviews);
        LinearLayoutManager reviewsLayoutManager = new LinearLayoutManager(this);
        recyclerViewReviews.setLayoutManager(reviewsLayoutManager);
        recyclerViewReviews.setHasFixedSize(true);
        recyclerViewReviews.setAdapter(reviewAdapter);



        poster = getIntent().getStringExtra("poster");
        title = getIntent().getStringExtra("title");
        rate = getIntent().getStringExtra("rate");
        release = getIntent().getStringExtra("release");
        overview = getIntent().getStringExtra("overview");
        id = getIntent().getIntExtra("id",0);


        movieTitleDisplay.setText(title);
        movieSynopsisDisplay.setText(overview);
        movieRateDisplay.setText(rate + getString(R.string.rate_out_of_ten));
        movieReleaseDisplay.setText(release);
        Picasso.get()
                .load(poster)
                .placeholder(R.drawable.image_loading)
                .error(R.drawable.image_not_found)
                .into(moviePosterDisplay);

        favorite_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isMovieFavorite(String.valueOf(id))) {
                    removeFavorites(String.valueOf(id));

                    Context context = getApplicationContext();
                    CharSequence removedFavorites = "This movie is removed from your favorites.";
                    Toast toast = Toast.makeText(context, removedFavorites, Toast.LENGTH_SHORT);
                    toast.show();

                    favorite_btn.setText(getString(R.string.add_to_favourite));
                } else {
                    addToFavorites(title, id, poster, rate, release, overview);
                    Context context = getApplicationContext();
                    CharSequence addedFavorites = "This movie is added to your favorites.";
                    Toast toast = Toast.makeText(context, addedFavorites, Toast.LENGTH_SHORT);
                    toast.show();

                    favorite_btn.setText(getString(R.string.remove_from_favorites));
                }
            }
        });


        loadTrailerData();
        loadReviewData();
        isMovieFavorite(String.valueOf(id));
    }


    private void loadTrailerData() {
        String trailerId = String.valueOf(id);
        new FetchTrailerTask().execute(trailerId);
    }

    private void loadReviewData() {
        String reviewId = String.valueOf(id);
        new FetchReviewTask().execute(reviewId);
    }

    // AsyncTask for trailers
    @SuppressLint("StaticFieldLeak")
    class FetchTrailerTask extends AsyncTask<String, Void, Trailer[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Trailer[] doInBackground(String... params) {
            if (params.length == 0){
                return null;
            }

            URL movieRequestUrl = NetworkUtils.buildTrailerUrl(id);

            try {
                String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);

                return MovieDBJsonUtils.getTrailerInformationFromJson(jsonMovieResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Trailer[] trailerData) {
            if (trailerData != null) {
                trailerAdapter = new TrailerAdapter(trailerData, MovieDetailsActivity.this);
                recyclerView.setAdapter(trailerAdapter);
            } else {
                trailerErrorMessage.setVisibility(View.VISIBLE);
            }

        }

    }


    //AsyncTask for reviews
    @SuppressLint("StaticFieldLeak")
    class FetchReviewTask extends AsyncTask<String, Void, Review[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Review[] doInBackground(String... params) {
            if (params.length == 0){
                return null;
            }

            URL movieRequestUrl = NetworkUtils.buildReviewUrl(id);

            try {
                String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);

                return MovieDBJsonUtils.getReviewInformationFromJson(jsonMovieResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Review[] reviewData) {
            if (reviewData != null) {
                reviewAdapter = new PeopleReviewAdapter(reviewData);
                recyclerViewReviews.setAdapter(reviewAdapter);
            } else {
                reviewErrorMessage.setVisibility(View.VISIBLE);
            }
        }

    }


    //add to favorites
    private void addToFavorites(String name, int id, String poster, String rate, String release, String overview){
        //create a ContentValues instance to pass the values onto the insert query
        ContentValues cv = new ContentValues();
        //call put to insert the values with the keys
        cv.put(FavoriteContract.FavoritesAdd.MOVIE_ID, id);
        cv.put(FavoriteContract.FavoritesAdd.MOVIE_NAME, name);
        cv.put(FavoriteContract.FavoritesAdd.MOVIE_POSTER, poster);
        cv.put(FavoriteContract.FavoritesAdd.MOVIE_RATE, rate);
        cv.put(FavoriteContract.FavoritesAdd.MOVIE_RELEASE_DATE, release);
        cv.put(FavoriteContract.FavoritesAdd.MOVIE_OVERVIEW, overview);
        //run an insert query on TABLE_NAME with the ContentValues created
        //return mDb.insert(FavoritesContract.FavoritesAdd.TABLE_NAME, null, cv);
        newUri = getContentResolver().insert(
                FavoriteContract.FavoritesAdd.CONTENT_URI,
                cv
        );
    }

    //remove favorites
    private void removeFavorites(String id){
        selectionClause = FavoriteContract.FavoritesAdd.MOVIE_ID + " LIKE ?";
        String[] selectionArgs = new String[] {id};
        //return mDb.delete(FavoritesContract.FavoritesAdd.TABLE_NAME,
        //      FavoritesContract.FavoritesAdd.COLUMN_MOVIE_ID + "=" + id, null) > 0;
        getContentResolver().delete(
                FavoriteContract.FavoritesAdd.CONTENT_URI,
                selectionClause,
                selectionArgs
        );
    }


    //store-restore
    @Override
    protected void onPause() {
        bundleRecyclerViewState = new Bundle();
        Parcelable listState = Objects.requireNonNull(recyclerViewReviews.getLayoutManager()).onSaveInstanceState();
        bundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bundleRecyclerViewState != null) {
            Parcelable listState = bundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            Objects.requireNonNull(recyclerViewReviews.getLayoutManager()).onRestoreInstanceState(listState);
        }
    }


    private boolean isMovieFavorite(String id){
        selectionClause = FavoriteContract.FavoritesAdd.MOVIE_ID + " = ?";
        selectionArgs[0] = id;
        Cursor mCursor = getContentResolver().query(
                FavoriteContract.FavoritesAdd.CONTENT_URI,
                projection,
                selectionClause,
                selectionArgs,
                null);

        assert mCursor != null;
        if(mCursor.getCount() <= 0){
            mCursor.close();
            favorite_btn.setText(getString(R.string.add_to_favourite));
            return false;
        }
        mCursor.close();
        favorite_btn.setText(getString(R.string.remove_from_favorites));
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemSelected = item.getItemId();

        if (menuItemSelected==R.id.home) {
            Intent intent = new Intent(MovieDetailsActivity.this,MainActivity.class);
            startActivity(intent);
        }
        if (menuItemSelected == R.id.favorite_movies) {
            Context context = this;
            Class destinationClass = FavoriteActivity.class;
            Intent intentToStartDetailActivity = new Intent(context, destinationClass);
            startActivity(intentToStartDetailActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

