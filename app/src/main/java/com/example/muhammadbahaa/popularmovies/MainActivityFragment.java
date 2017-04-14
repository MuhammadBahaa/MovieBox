package com.example.muhammadbahaa.popularmovies;

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
import android.widget.GridView;
import android.widget.Toast;

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


public  class MainActivityFragment extends Fragment  {

    private MovieAdapter movieAdapter;
    private ArrayList<MovieData> movieList;
    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();


    public MainActivityFragment() {
    }

    public interface Callback{
      void onItemSelected (MovieData movie);
    }

    @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if(savedInstanceState == null || !savedInstanceState.containsKey("movies")){
                movieList = new ArrayList<MovieData>();
            }
            else {
                movieList = savedInstanceState.getParcelableArrayList("movies");
            }
            setHasOptionsMenu(true);
        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview);
        movieAdapter = new MovieAdapter(getActivity(), new ArrayList<MovieData>());
        gridView.setAdapter(movieAdapter);


       gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
           MovieData  movie = movieAdapter.getItem(position);
          ((Callback) getActivity()).onItemSelected(movie);
        }
    });
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_popular) {
            updateData("popular");
            return true;
        }
        if(id == R.id.action_top_rated){
            updateData("top_rated");
            return true;
        }
       if(id == R.id.action_favourite) {
           DBHelper db = new DBHelper(getActivity());

           ArrayList<MovieData> movies_list = db.getMovies();

           if ( movies_list.isEmpty()) {
               Toast.makeText(getContext(), "No Movies Added to Favourites Yet", Toast.LENGTH_SHORT).show();
           }
          else {
                movieAdapter.clear();

               for (final MovieData movie : movies_list) {
                   movieAdapter.add(movie);
               }
           }
       }
                movieAdapter.notifyDataSetChanged();
                return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", movieList);
        super.onSaveInstanceState(outState);
    }


    private void updateData(String type){
        FetchMovieData fetchMovieData = new FetchMovieData();
        fetchMovieData.execute(type);
    }

    @Override
    public void onStart() {
        super.onStart();
        FetchMovieData fetchMovieData = new FetchMovieData();
        fetchMovieData.execute("popular");
    }

    public class FetchMovieData extends AsyncTask<String, Void, ArrayList<MovieData>> {

        private final String LOG_TAG = FetchMovieData.class.getSimpleName();

        @Override
        protected ArrayList<MovieData> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJsonStr = null;

            try {
                final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/" + params[0] + "?api_key=";
                final String API_KEY = getResources().getString(R.string.api_key);
                Uri builtUri = Uri.parse(MOVIE_BASE_URL + API_KEY).buildUpon().build();
                URL url = new URL(builtUri.toString());

                /* Create the request to MovieAPI, and open the connection */
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                /* Read the input stream into a String */
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
                movieJsonStr = buffer.toString();
            }catch (IOException e) {
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
                return getMovieDataFromJson(movieJsonStr);
            }catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieData> movies) {
            if(movies != null){
                movieAdapter.clear();
                for(MovieData movie: movies){
                    movieAdapter.add(movie);
                }
                movieList.addAll(movies);
            }
            else {
                Toast.makeText(getActivity(), "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }

        }

        private ArrayList<MovieData> getMovieDataFromJson(String movieJsonStr) throws JSONException {

            ArrayList<MovieData> moviesData = new ArrayList<MovieData>();

            final String RESULTS = "results";
            final String POSTER = "poster_path";
            final String OVERVIEW = "overview";
            final String REL_DATE = "release_date";
            final String TITLE = "title";
            final String VOTE_AVG = "vote_average";
            final String BACKDROP_IMG = "backdrop_path";
            final String ID = "id";
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(RESULTS);

            for(int i = 0; i < movieArray.length() ; i++){
                String movieTitle = movieArray.getJSONObject(i).getString(TITLE);
                String moviePoster = "http://image.tmdb.org/t/p/w500/" +
                        movieArray.getJSONObject(i).getString(POSTER);
                String movieOverview = movieArray.getJSONObject(i).getString(OVERVIEW);
                String movieRelDate = movieArray.getJSONObject(i).getString(REL_DATE);
                String movieVoteAvg = movieArray.getJSONObject(i).getString(VOTE_AVG);

                String id = movieArray.getJSONObject(i).getString(ID);
                MovieData movie = new MovieData(movieTitle, moviePoster, movieOverview, movieRelDate, movieVoteAvg, id);

                moviesData.add(i, movie);
            }

            return moviesData;
        }

    }
}
