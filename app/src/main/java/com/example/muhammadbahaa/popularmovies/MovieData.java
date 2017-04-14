package com.example.muhammadbahaa.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieData implements Parcelable {
    private String originalTitle;
    private String posterImgURL;
    private String overview;
    private String voteAverage;
    private String releaseDate;
    private String id;

    public MovieData(String originalTitle, String posterImgURL, String overview, String releaseDate, String voteAverage, String id) {

        setOriginalTitle(originalTitle);
        setPosterImgURL(posterImgURL);
        setOverview(overview);
        setReleaseDate(releaseDate);
        setVoteAverage(voteAverage);
        setId(id);
    }

    private MovieData(Parcel in) {
        this.originalTitle = in.readString();
        this.posterImgURL = in.readString();
        this.overview = in.readString();
        this.releaseDate = in.readString();
        this.voteAverage = in.readString();
        this.id = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return getOriginalTitle() + "-" + getPosterImgURL() + "-" + getReleaseDate();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.originalTitle);
        parcel.writeString(this.posterImgURL);
        parcel.writeString(this.overview);
        parcel.writeString(this.releaseDate);
        parcel.writeString(this.voteAverage);
        parcel.writeString(this.id);
    }

    public static final Creator<MovieData> CREATOR = new Creator<MovieData>() {
        @Override
        public MovieData createFromParcel(Parcel parcel) {
            return new MovieData(parcel);
        }

        @Override
        public MovieData[] newArray(int i) {
            return new MovieData[i];
        }

    };

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }


    public String getPosterImgURL() {
        return posterImgURL;
    }

    public void setPosterImgURL(String posterImgURL) {
        this.posterImgURL = posterImgURL;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
