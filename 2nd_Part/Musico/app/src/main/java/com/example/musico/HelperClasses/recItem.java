package com.example.musico.HelperClasses;

import android.os.Parcel;
import android.os.Parcelable;

public class recItem implements Parcelable {
    private int imgResource, deleteImg;
    private String artist, song;

    public recItem(int imgResource, String artist){
        this.imgResource = imgResource;
        this.artist = artist;
    }

    public recItem(int imgResource, int deleteImg, String artist, String song){
        this.imgResource = imgResource;
        this.deleteImg = deleteImg;
        this.artist = artist;
        this.song = song;
    }

    protected recItem(Parcel in) {
        imgResource = in.readInt();
        deleteImg = in.readInt();
        artist = in.readString();
        song = in.readString();
    }

    public static final Creator<recItem> CREATOR = new Creator<recItem>() {
        @Override
        public recItem createFromParcel(Parcel in) {
            return new recItem(in);
        }

        @Override
        public recItem[] newArray(int size) {
            return new recItem[size];
        }
    };

    public int getImgResource(){
        return imgResource;
    }

    public int getDeleteImg(){
        return deleteImg;
    }

    public String getArtist(){
        return artist;
    }

    public String getSong(){
        return song;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(imgResource);
        dest.writeInt(deleteImg);
        dest.writeString(artist);
        dest.writeString(song);
    }
}
