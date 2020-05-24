package com.example.musico.HelperClasses;

public class recItemArtist {
    private int imgResource;
    private String artist, song;

    public recItemArtist(int imgResource, String artist){
        this.imgResource = imgResource;
        this.artist = artist;
    }

    public recItemArtist(int imgResource, String artist, String song){
        this.imgResource = imgResource;
        this.artist = artist;
        this.song = song;
    }

    public int getImgResource(){
        return imgResource;
    }

    public String getArtist(){
        return artist;
    }
}
