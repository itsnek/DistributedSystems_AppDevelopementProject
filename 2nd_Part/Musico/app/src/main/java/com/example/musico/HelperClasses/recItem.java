package com.example.musico.HelperClasses;

public class recItem {
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
}
