package com.example.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class Trailer implements Parcelable {
    private String trailerID;
    private String key;
    private String name;
    private String site;
    private String type;

    public enum TrailerType {
        TRAILER,
        TEASER,
        FEATURETTE
    }

    public enum TrailerSiteType{
        YOUTUBE
    }

    public Trailer(String id, String key, String name, String site, String type){
        trailerID = id;
        this.key = key;
        this.name = name;
        this.site = site;
        this.type = type;
    }

    public String getTrailerID() {
        return trailerID;
    }

    public void setTrailerID(String trailerID) {
        this.trailerID = trailerID;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.trailerID);
        dest.writeString(this.key);
        dest.writeString(this.name);
        dest.writeString(this.site);
        dest.writeString(this.type);
    }

    protected Trailer(Parcel in) {
        this.trailerID = in.readString();
        this.key = in.readString();
        this.name = in.readString();
        this.site = in.readString();
        this.type = in.readString();
    }

    public static final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel source) {
            return new Trailer(source);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };
}
