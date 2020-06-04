package com.example.musico.HelperClasses;

public class recItem {
    private int imgResource, deleteImg;
    private String artist, song, path;

    public recItem(int imgResource, String artist){
        this.imgResource = imgResource;
        this.artist = artist;
    }

    public recItem(int imgResource, int deleteImg, String artist, String song, String path){
        this.imgResource = imgResource;
        this.deleteImg = deleteImg;
        this.artist = artist;
        this.song = song;
        this.path = path;
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

    public String getPath() { return path; }
}
