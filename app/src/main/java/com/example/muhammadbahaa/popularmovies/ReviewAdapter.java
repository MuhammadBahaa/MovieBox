package com.example.muhammadbahaa.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


public class ReviewAdapter extends ArrayAdapter<Review> {
    public ReviewAdapter(Context context, List<Review> reviews) {
        super(context, 0, reviews);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Review review = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_review_item,
                    parent, false);
        }

        TextView reviewAuthor = (TextView) convertView.findViewById(R.id.review_author);
        reviewAuthor.setText(review.getAuthor());

        TextView reviewContent = (TextView) convertView.findViewById(R.id.review_content);
        reviewContent.setText(review.getContent());
        return convertView;
    }

}
