package com.example.muhammadbahaa.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

public class Detail extends AppCompatActivity {

    private Bundle mBundle;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        MovieData movie = (MovieData) intent.getParcelableExtra(Intent.EXTRA_TEXT);
        getSupportActionBar().setTitle(movie.getOriginalTitle());

//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.movie_detail_container, new DetailFragment())
//                    .commit();
 //       }
    }
}
