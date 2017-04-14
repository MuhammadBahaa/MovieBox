package com.example.muhammadbahaa.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class TrailerAdapter extends ArrayAdapter<Trailer> {
    public TrailerAdapter(Context context, List<Trailer> trailers) {
        super(context, 0, trailers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Trailer trailer = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_trailer_item,
                    parent, false);
        }

        ViewHolder holder = new ViewHolder();
        holder.trailerName = (TextView) convertView.findViewById(R.id.movie_trailer_name);
        holder.youtubeLogo = (ImageView) convertView.findViewById(R.id.youtube_logo);
        convertView.setTag(holder);
        holder.trailerName.setText(trailer.getName());
        return convertView;
    }

    static class ViewHolder {
        TextView trailerName;
        ImageView youtubeLogo;
    }
}
