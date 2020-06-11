package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.popularmovies.data.FavoriteContract;
import com.squareup.picasso.Picasso;



public class FavoriteCursorAdapter extends RecyclerView.Adapter<FavoriteCursorAdapter.FavoriteViewHolder> {

    private Cursor cursor;
    private Context context;
    public String name = "";


    FavoriteCursorAdapter(Context mContext){
        context = mContext;
    }


    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View  view = LayoutInflater.from(context).inflate(R.layout.movie_list_item, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavoriteViewHolder holder, int position){
        int idIndex = cursor.getColumnIndex(FavoriteContract.FavoritesAdd._ID);
        int posterIndex = cursor.getColumnIndex(FavoriteContract.FavoritesAdd.MOVIE_POSTER);

        cursor.moveToPosition(position);

        int id = cursor.getInt(idIndex);
        String poster = cursor.getString(posterIndex);


        holder.itemView.setTag(id);
        Picasso.get()
                .load(poster)
                .placeholder(R.drawable.image_loading)
                .error(R.drawable.image_not_found)
                .into(holder.mMovieListImageView);


    }

    @Override
    public int getItemCount() {
        if (cursor == null) return 0;
        return cursor.getCount();
    }

    //update the cursor when a new data is added
    void swapCursor(Cursor c){
        if (cursor == c){
            return;
        }

        this.cursor = c;

        if (c != null){
            this.notifyDataSetChanged();
        }
    }

     class FavoriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView mMovieListImageView;

        private FavoriteViewHolder(View itemView){
            super(itemView);

            mMovieListImageView = itemView.findViewById(R.id.iv_movie_posters);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            cursor.moveToPosition(adapterPosition);
            Class destinationClass = FavoriteDetailsActivity.class;

            String name = cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoritesAdd.MOVIE_NAME));
            int movieId = cursor.getInt(cursor.getColumnIndex(FavoriteContract.FavoritesAdd.MOVIE_ID));
            String overview = cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoritesAdd.MOVIE_OVERVIEW));
            String rate = cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoritesAdd.MOVIE_RATE));
            String release = cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoritesAdd.MOVIE_RELEASE_DATE));
            String poster = cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoritesAdd.MOVIE_POSTER));

            Intent intentToStartDetailActivity = new Intent(context, destinationClass);
            intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, adapterPosition);
            intentToStartDetailActivity.putExtra("title", name);
            intentToStartDetailActivity.putExtra("poster", poster);
            intentToStartDetailActivity.putExtra("rate", rate);
            intentToStartDetailActivity.putExtra("release", release);
            intentToStartDetailActivity.putExtra("overview", overview);
            intentToStartDetailActivity.putExtra("id", movieId);

            context.startActivity(intentToStartDetailActivity);

        }
    }

}
