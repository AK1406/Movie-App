package com.example.android.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

//Following codes are from Sunshine's RecyclerView and Click Handlers.
    private final Movie[] movieData;
    private final MovieAdapterOnClickHandler ClickHandler;

    public MovieAdapter(Movie[] movie, MovieAdapterOnClickHandler clickHandler) {
        movieData = movie;
        ClickHandler = clickHandler;
    }

    public interface MovieAdapterOnClickHandler {
        void onClick(int adapterPosition);
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView mMovieListImageView;

        private MovieAdapterViewHolder(View itemView) {
            super(itemView);
            mMovieListImageView = itemView.findViewById(R.id.iv_movie_posters);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            ClickHandler.onClick(adapterPosition);
        }
    }
    @NonNull
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder( MovieAdapterViewHolder holder, int position) {
        String movieToBind = movieData[position].getPoster();
        Picasso.get()
                .load(movieToBind)
                .placeholder(R.drawable.image_loading)
                .error(R.drawable.image_not_found)
                .into(holder.mMovieListImageView);
    }

    @Override
    public int getItemCount() {
        if (null == movieData) {
            return 0;
        }
        return movieData.length;
    }
}
