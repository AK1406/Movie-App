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
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.Review;
import com.example.android.popularmovies.model.Trailer;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.MovieDBJsonUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.Objects;


public class FavoriteDetailsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewTrailer;
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


    private  TextView trailerErrorMessage;
  private  TextView reviewErrorMessage;
  private  Button favorite_btn;
  private Uri newUri;



    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritedetails);

        ImageView mMoviePosterDisplay = findViewById(R.id.movie_poster);
        TextView mMovieTitleDisplay = findViewById(R.id.movie_title);
        TextView mMovieRateDisplay = findViewById(R.id.movie_rate);
        TextView mMovieReleaseDisplay = findViewById(R.id.movie_release_date);
        TextView mMoviePlotSynopsisDisplay = findViewById(R.id.moviePlot_synopsis);
        trailerErrorMessage= findViewById(R.id.trailer_error_message);
        reviewErrorMessage = findViewById(R.id.review_error_message);
        favorite_btn = findViewById(R.id.add_to_favorites);

        //favorites
        FavoriteDbHelper dbHelper = new FavoriteDbHelper(this);
        mDb = dbHelper.getWritableDatabase();



        //trailers
        recyclerViewTrailer = findViewById(R.id.recyclerview_trailers);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewTrailer.setLayoutManager(layoutManager);
        recyclerViewTrailer.setHasFixedSize(true);
        recyclerViewTrailer.setAdapter(trailerAdapter);



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


        mMovieTitleDisplay.setText(title);
        mMoviePlotSynopsisDisplay.setText(overview);
        mMovieRateDisplay.setText(rate + getString(R.string.rate_out_of_ten));
        mMovieReleaseDisplay.setText(release);
        Picasso.get()
                .load(poster)
                .placeholder(R.drawable.image_loading)
                .error(R.drawable.image_not_found)
                .into(mMoviePosterDisplay);

        favorite_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isMovieFavorite(String.valueOf(id))) {
                    removeFavorites(String.valueOf(id));

                    Context context = getApplicationContext();
                    CharSequence removedFavorites = "This movie is removed from your favorite Movies.";
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
    class FetchTrailerTask extends AsyncTask<String, Void, Movie[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Movie[] doInBackground(String... params) {
            if (params.length == 0){
                return null;
            }

            URL movieRequestUrl = NetworkUtils.buildTrailerUrl(id);

            try {
                String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);

                return MovieDBJsonUtils.getMovieInformationFromJson(jsonMovieResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }


        protected void onPostExecute(Trailer[] trailerData) {
            if (trailerData != null) {
                trailerAdapter = new TrailerAdapter(trailerData, FavoriteDetailsActivity.this);
                recyclerViewTrailer.setAdapter(trailerAdapter);
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
        ContentValues cv = new ContentValues();
        cv.put(FavoriteContract.FavoritesAdd.MOVIE_ID, id);
        cv.put(FavoriteContract.FavoritesAdd.MOVIE_NAME, name);
        cv.put(FavoriteContract.FavoritesAdd.MOVIE_POSTER, poster);
        cv.put(FavoriteContract.FavoritesAdd.MOVIE_RATE, rate);
        cv.put(FavoriteContract.FavoritesAdd.MOVIE_RELEASE_DATE, release);
        cv.put(FavoriteContract.FavoritesAdd.MOVIE_OVERVIEW, overview);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemSelected = item.getItemId();

        if (menuItemSelected==R.id.home) {
            Intent intent = new Intent(FavoriteDetailsActivity.this,MainActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}

