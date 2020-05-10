package com.example.musico.HelperClasses;

public class recItem {
    private int imgResource;
    private String song;
    private String artist;

    public recItem(int imgResource, String song, String artist){
        this.imgResource = imgResource;
        this.song = song;
        this.artist = artist;
    }

    public int getImgResource(){
        return imgResource;
    }

    public String getSong(){
        return song;
    }

    public String getArtist(){
        return artist;
    }
}
