package com.example.muhammadbahaa.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailFragment extends Fragment {

    public static final String LOG_TAG = DetailFragment.class.getSimpleName();
    static final String DETAIL_MOVIE = "DETAIL_MOVIE";
    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;
    private MovieData movie;
    private Menu mMenu;
    private DBHelper db;

    public DetailFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null )
            movie = (MovieData) intent.getParcelableExtra(Intent.EXTRA_TEXT);

        Bundle arguments = getArguments();
        if (arguments != null) {
            movie = arguments.getParcelable(DetailFragment.DETAIL_MOVIE);
            String id = movie.getId();
        }



        trailerAdapter = new TrailerAdapter(getActivity(), new ArrayList<Trailer>());
        reviewAdapter = new ReviewAdapter(getActivity(), new ArrayList<Review>());

        if (movie != null) {

            ImageView imageView = (ImageView) view.findViewById(R.id.backdrop_img);
            TextView releaseDateTextView = (TextView) view.findViewById(R.id.detail_release_date);
            TextView voteRateTextView = (TextView) view.findViewById(R.id.detail_vote_average);
            TextView overviewTextView = (TextView) view.findViewById(R.id.overview_text);
            NonScrollListView reviews = (NonScrollListView) view.findViewById(R.id.movie_reviews_list);
            NonScrollListView trailers = (NonScrollListView) view.findViewById(R.id.movie_videos_list);

            Picasso.with(getContext()).load(movie.getPosterImgURL()).into(imageView);
            releaseDateTextView.setText(movie.getReleaseDate());
            voteRateTextView.setText(movie.getVoteAverage() + "/10");
            overviewTextView.setText(movie.getOverview());
            reviews.setAdapter(reviewAdapter);
            trailers.setAdapter(trailerAdapter);


            trailers.setOnItemClickListener(new ListView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Trailer trailer = trailerAdapter.getItem(position);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));
                    startActivity(intent);
                }

            });
        } else {
            /**
             * Initially the no_trailers and no_reviews views should be invisible until the
             * data is retrieved and the checks are made to know if there are any.
             */
            TextView noVideosView = (TextView) view.findViewById(R.id.no_trailers);
            noVideosView.setEnabled(false);
            noVideosView.setVisibility(View.GONE);
            TextView noReviewsView = (TextView) view.findViewById(R.id.no_reviews);
            noReviewsView.setEnabled(false);
            noReviewsView.setVisibility(View.GONE);
        }
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        if (movie != null) {
            new FetchTrailersTask().execute(movie.getId());
            new FetchReviewsTask().execute(movie.getId());
        } else {
            Toast.makeText(getActivity(), "Failed to fetch data!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.favourite, menu);

        this.mMenu = menu;
        db = new DBHelper(getActivity());
        MenuItem notSelectedFavIcon;
        MenuItem item;

        Intent intent = getActivity().getIntent();
        if (intent != null)
            movie = (MovieData) intent.getParcelableExtra(Intent.EXTRA_TEXT);

        Bundle arguments = getArguments();
        if (arguments != null) {
            movie = arguments.getParcelable(DetailFragment.DETAIL_MOVIE);
            String id = movie.getId();
        }


        if (db.Check(movie.getId())) {
            item = mMenu.findItem(R.id.selected);
            item.setEnabled(true);
            item.setVisible(true);

            notSelectedFavIcon = mMenu.findItem(R.id.select);
            notSelectedFavIcon.setEnabled(false);
            notSelectedFavIcon.setVisible(false);

        } else {
            item = mMenu.findItem(R.id.select);
            item.setEnabled(true);
            item.setVisible(true);

            notSelectedFavIcon = mMenu.findItem(R.id.selected);
            notSelectedFavIcon.setEnabled(false);
            notSelectedFavIcon.setVisible(false);

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        db = new DBHelper(getActivity());
        MenuItem notSelectedFavIcon;

        Intent intent = getActivity().getIntent();
        if (intent != null )
            movie = (MovieData) intent.getParcelableExtra(Intent.EXTRA_TEXT);

        Bundle arguments = getArguments();
        if (arguments != null) {
            movie = arguments.getParcelable(DetailFragment.DETAIL_MOVIE);
        }

        
        switch (item.getItemId()) {
            case R.id.select:

                item = mMenu.findItem(R.id.selected);
                item.setEnabled(true);
                item.setVisible(true);

                notSelectedFavIcon = mMenu.findItem(R.id.select);
                notSelectedFavIcon.setEnabled(false);
                notSelectedFavIcon.setVisible(false);

                db.insertMovie(movie.getId(), movie.getOriginalTitle(), movie.getPosterImgURL(), movie.getOverview(), movie.getReleaseDate(), movie.getVoteAverage());
                Toast.makeText(getContext(), "Added to Favourites", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.selected:

                item = mMenu.findItem(R.id.select);

                item.setEnabled(true);
                item.setVisible(true);

                notSelectedFavIcon = mMenu.findItem(R.id.selected);
                notSelectedFavIcon.setEnabled(false);
                notSelectedFavIcon.setVisible(false);

                db.deleteMovie(movie.getId());
                Toast.makeText(getContext(), "Removed from Favourites", Toast.LENGTH_SHORT).show();
                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    public class FetchReviewsTask extends AsyncTask<String, Void, List<Review>> {

        private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();

        @Override
        protected List<Review> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonStr = null;

            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/reviews";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, getString(R.string.api_key))
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                jsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getReviewsDataFromJson(jsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Review> reviews) {
            if (reviews != null) {
                if (reviews.size() > 0) {
                    if (reviewAdapter != null) {
                        reviewAdapter.clear();
                        for (Review review : reviews) {
                            reviewAdapter.add(review);
                        }
                    }
                } else {
                    if (reviewAdapter != null) {
                        reviewAdapter.clear();
                        reviewAdapter.add(new Review(null, "There is no reviews", null));
                    }
                }

            }
        }

        private List<Review> getReviewsDataFromJson(String jsonStr) throws JSONException {
            JSONObject reviewJson = new JSONObject(jsonStr);
            JSONArray reviewArray = reviewJson.getJSONArray("results");

            List<Review> results = new ArrayList<>();

            for (int i = 0; i < reviewArray.length(); i++) {
                JSONObject review = reviewArray.getJSONObject(i);
                String id = review.getString("id");
                String author = review.getString("author");
                String content = review.getString("content");
                results.add(new Review(id, author, content));
            }

            return results;
        }


    }

    public class FetchTrailersTask extends AsyncTask<String, Void, List<Trailer>> {

        private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();

        @Override
        protected List<Trailer> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr = null;

            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/videos";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, getString(R.string.api_key))
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                jsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getTrailersDataFromJson(jsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Trailer> trailers) {
            if (trailers != null) {
                if (trailers.size() > 0) {
                    if (trailerAdapter != null) {
                        trailerAdapter.clear();
                        for (Trailer trailer : trailers) {
                            trailerAdapter.add(trailer);
                        }
                    }
                }
            }
        }

        private List<Trailer> getTrailersDataFromJson(String jsonStr) throws JSONException {
            JSONObject trailerJson = new JSONObject(jsonStr);
            JSONArray trailerArray = trailerJson.getJSONArray("results");

            List<Trailer> results = new ArrayList<>();

            for (int i = 0; i < trailerArray.length(); i++) {
                JSONObject trailer = trailerArray.getJSONObject(i);

                // Only show Trailers which are on Youtube
                if (trailer.getString("site").equals("YouTube")) {
                    Trailer trailerModel = new Trailer(trailer.getString("id"),
                            trailer.getString("key"),
                            trailer.getString("name"),
                            trailer.getString("site"),
                            trailer.getString("type"));
                    results.add(trailerModel);
                }
            }
            return results;
        }
    }
}

