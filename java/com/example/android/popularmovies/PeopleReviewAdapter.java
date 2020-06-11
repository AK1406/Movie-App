package com.example.android.popularmovies;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.popularmovies.model.Review;

public class PeopleReviewAdapter extends RecyclerView.Adapter<PeopleReviewAdapter.ReviewAdapterViewHolder> {

    private Review[] reviewData;
    private static TextView reviewListTextView = null;
    private static TextView authorListTextView = null;

    PeopleReviewAdapter(Review[] review) {
        reviewData = review;
    }

    static class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {

        ReviewAdapterViewHolder(View itemView) {
            super(itemView);
            reviewListTextView = itemView.findViewById(R.id.people_reviews);
            authorListTextView = itemView.findViewById(R.id.author);
        }
    }

    @NonNull
    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new ReviewAdapterViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ReviewAdapterViewHolder holder, int position) {

        String AuthorToBind = reviewData[position].getAuthor();
        String ReviewToBind = reviewData[position].getContent();
        reviewListTextView.setText(ReviewToBind);
        authorListTextView.setText(AuthorToBind + " said: ");

    }

    @Override
    public int getItemCount() {
        if (null == reviewData) {
            return 0;
        }
        return reviewData.length;
    }
}
