package com.example.musico.HelperClasses;

public class recItem {
    private int imgResource;
    private String artist;

    public recItem(int imgResource, String artist){
        this.imgResource = imgResource;
        this.artist = artist;
    }

    public int getImgResource(){
        return imgResource;
    }

    public String getArtist(){
        return artist;
    }
}
