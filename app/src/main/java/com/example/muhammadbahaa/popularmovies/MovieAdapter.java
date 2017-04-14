package com.example.muhammadbahaa.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;


public class MovieAdapter extends ArrayAdapter<MovieData> {

    public MovieAdapter(Context context, List<MovieData> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MovieData movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_movie,
                    parent, false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.grid_item);
        Picasso.with(getContext()).load(movie.getPosterImgURL()).into(imageView);

        TextView movieTitle = (TextView) convertView.findViewById(R.id.movie_title);
        movieTitle.setText(movie.getOriginalTitle());

        return convertView;
    }
}
