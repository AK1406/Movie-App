package com.example.android.popularmovies;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.utilities.MovieDBJsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler{

    private RecyclerView recyclerView;
    private static Bundle bundleRecyclerViewState;
    private final String KEY_RECYCLER_STATE = "recycler_state";
    private MovieAdapter movieAdapter;
    private Movie[] jsonMovieData;
    private SQLiteDatabase mDb;

    private TextView errorMessage;
    private ProgressBar loadingIndicator;

    private String query = "popular";
    private static final String LIFECYCLE_CALLBACKS_TEXT_KEY = "callbacks";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            query = savedInstanceState.getString(LIFECYCLE_CALLBACKS_TEXT_KEY);
        }
        setContentView(R.layout.activity_main);

        recyclerView =  findViewById(R.id.recycler_movies);
        errorMessage = findViewById(R.id.error_message);
        loadingIndicator =findViewById(R.id.pb_loading_indicator);

        int noOfColumns = calNoOfColumns(getApplicationContext());

        GridLayoutManager layoutManager = new GridLayoutManager(this, noOfColumns);
        //set the layout manager
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(movieAdapter);

        loadMovieData();
    }

    private void loadMovieData() {
        String theMovieDbQueryType = query;
        showJsonDataResults();
        new FetchMovieTask().execute(theMovieDbQueryType);
    }

    @Override
    public void onClick(int adapterPosition) {
        Context context = this;
        Class destinationClass = MovieDetailsActivity.class;

        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, adapterPosition);
        intentToStartDetailActivity.putExtra("title", jsonMovieData[adapterPosition].getTitle());
        intentToStartDetailActivity.putExtra("poster", jsonMovieData[adapterPosition].getPoster());
        intentToStartDetailActivity.putExtra("rate", jsonMovieData[adapterPosition].getRate());
        intentToStartDetailActivity.putExtra("release", jsonMovieData[adapterPosition].getRelease());
        intentToStartDetailActivity.putExtra("overview", jsonMovieData[adapterPosition].getOverview());
        intentToStartDetailActivity.putExtra("id", jsonMovieData[adapterPosition].getId());

        startActivity(intentToStartDetailActivity);
    }

    private void showJsonDataResults() {
        errorMessage.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        recyclerView.setVisibility(View.INVISIBLE);
        errorMessage.setVisibility(View.VISIBLE);
    }

    @SuppressLint("StaticFieldLeak")
    class FetchMovieTask extends AsyncTask<String, Void, Movie[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Movie[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            String sortBy = params[0];
            URL movieRequestUrl = NetworkUtils.buildUrl(sortBy);

            try {
                String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);

                jsonMovieData
                        = MovieDBJsonUtils.getMovieInformationFromJson(jsonMovieResponse);

                return jsonMovieData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie[] movieData) {
            loadingIndicator.setVisibility(View.INVISIBLE);
            if (movieData != null) {
                showJsonDataResults();
                movieAdapter = new MovieAdapter(movieData, MainActivity.this);
                recyclerView.setAdapter(movieAdapter);
            } else {
                showErrorMessage();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemSelected = item.getItemId();

        if (menuItemSelected == R.id.popular_movies || menuItemSelected==R.id.home) {
            query = "popular";
            loadMovieData();
            return true;
        }

        if (menuItemSelected == R.id.top_rated_movies) {
            query = "top_rated";
            loadMovieData();
            return true;
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

    //auto calculate no. of columns
    public static int calNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 180);
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        String lifecycleSortBy = query;
        outState.putString(LIFECYCLE_CALLBACKS_TEXT_KEY, lifecycleSortBy);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        query = savedInstanceState.getString(LIFECYCLE_CALLBACKS_TEXT_KEY);
    }
    // store - restore
    @Override
    protected void onPause() {
        bundleRecyclerViewState = new Bundle();
        Parcelable listState = Objects.requireNonNull(recyclerView.getLayoutManager()).onSaveInstanceState();
        bundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bundleRecyclerViewState != null) {
            Parcelable listState = bundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            Objects.requireNonNull(recyclerView.getLayoutManager()).onRestoreInstanceState(listState);
        }
    }

}