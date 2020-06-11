package com.example.android.popularmovies.utilities;


import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.Review;
import com.example.android.popularmovies.model.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MovieDBJsonUtils {

    public static Movie[] getMovieInformationFromJson(String json) throws JSONException {

        final String BASE_URL = "https://image.tmdb.org/t/p/";
        final String POSTER_SIZE = "w500";

        final String RESULTS = "results";
        final String POSTER_PATH = "poster_path";
        final String TITLE = "title";
        final String RATE = "vote_average";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";
        final String MOVIE_ID = "id";

        JSONObject movieJson = new JSONObject(json);

        JSONArray movieArray = movieJson.getJSONArray(RESULTS);

        Movie[] movieResults = new Movie[movieArray.length()];


        for (int i = 0; i < movieArray.length(); i++){
            String poster_path, title, vote_average, overview, release_date;
            int id;

            Movie movie = new Movie();

            poster_path = movieArray.getJSONObject(i).optString(POSTER_PATH);
            title = movieArray.getJSONObject(i).optString(TITLE);
            release_date = movieArray.getJSONObject(i).optString(RELEASE_DATE);
            vote_average = movieArray.getJSONObject(i).optString(RATE);
            overview = movieArray.getJSONObject(i).optString(OVERVIEW);
            id = movieArray.getJSONObject(i).optInt(MOVIE_ID);

            //setters
            movie.setPoster(BASE_URL + POSTER_SIZE + poster_path);
            movie.setTitle(title);
            movie.setRelease(release_date);
            movie.setRate(vote_average);
            movie.setOverview(overview);
            movie.setId(id);

            movieResults[i] = movie;
        }

        return movieResults;
    }
    public static Trailer[] getTrailerInformationFromJson(String json) throws JSONException {


        final String TRAILER_RESULTS = "results";
        final String TRAILER_KEY = "key";
        final String TRAILER_NAME = "name";

        JSONObject trailerJson = new JSONObject(json);

        JSONArray trailerArray = trailerJson.getJSONArray(TRAILER_RESULTS);

        Trailer[] trailerResults = new Trailer[trailerArray.length()];


        for (int i = 0; i < trailerArray.length(); i++){
            String trailer_key, trailer_name;

            Trailer trailer = new Trailer();

            trailer_key = trailerArray.getJSONObject(i).optString(TRAILER_KEY);

            trailer_name = trailerArray.getJSONObject(i).optString(TRAILER_NAME);

            //setters
            trailer.setKey(trailer_key);
            trailer.setName(trailer_name);

            trailerResults[i] = trailer;
        }

        return trailerResults;
    }


    public static Review[] getReviewInformationFromJson(String json) throws JSONException {

        final String REVIEW_RESULTS = "results";
        final String REVIEW_AUTHOR = "author";
        final String REVIEW_CONTENT = "content";

        JSONObject reviewJson = new JSONObject(json);

        JSONArray reviewArray = reviewJson.getJSONArray(REVIEW_RESULTS);

        Review[] reviewResults = new Review[reviewArray.length()];


        for (int i = 0; i < reviewArray.length(); i++){
            String review_author, review_content;

            Review review = new Review();

            review_author = reviewArray.getJSONObject(i).optString(REVIEW_AUTHOR);
            review_content = reviewArray.getJSONObject(i).optString(REVIEW_CONTENT);

            //setters
            review.setAuthor(review_author);
            review.setContent(review_content);

            reviewResults[i] = review;
        }

        return reviewResults;
    }


}
