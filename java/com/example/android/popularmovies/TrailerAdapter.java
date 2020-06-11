package com.example.android.popularmovies;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.popularmovies.model.Trailer;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    private Trailer[] trailerData;
    private static TextView trailerListTextView = null;
    private final String TRAILER_BASE_URL = "https://www.youtube.com/watch?v=";
    private Context context;

    TrailerAdapter(Trailer[] trailer, Context context) {
        trailerData = trailer;
        this.context = context;
    }

//        public interface TrailerAdapterOnClickHandler {
//            void onClick(int adapterPosition, String mTrailerURL);
//        }

    static class TrailerAdapterViewHolder extends RecyclerView.ViewHolder{


        TrailerAdapterViewHolder(View itemView) {
            super(itemView);
            trailerListTextView = itemView.findViewById(R.id.trailer_names);

        }

    }

    @NonNull
    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.trailers_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerAdapterViewHolder holder, int position) {
        //set Trailer for list item's position
        String TrailerToBind = trailerData[position].getName();
        final String TrailerToWatch = trailerData[position].getKey();
        trailerListTextView.setText(TrailerToBind);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri openTrailerVideo = Uri.parse(TRAILER_BASE_URL + TrailerToWatch);
                Intent intent = new Intent(Intent.ACTION_VIEW, openTrailerVideo);
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        if (null == trailerData) {
            return 0;
        }
        return trailerData.length;
    }
}

