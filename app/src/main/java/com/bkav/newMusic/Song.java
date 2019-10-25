package com.bkav.newMusic;

public class Song {
    private int mID;

    private  String mTitle;

    private  String mFile;

    private String mAtist;

    private int mDuration;


    public int getId() {
        return mID;
    }

    public String getTitle() {
        return mTitle;
    }


    public String getFile() {
        return mFile;
    }

    public String getArtist() {
        return mAtist;
    }

    public int getDuration() {
        return mDuration;
    }



    public Song(int id, String title, String file , String artist, int duration) {
        this.mTitle =title;
        this.mID =id;
       this.mFile =file;
        this.mAtist = artist;
        this.mDuration = duration;
    }

    public Song(){

    }

    public void setId(int id) {
        this.mID = id;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setFile(String file) {
        this.mFile = file;
    }

    public void setArtist(String artist) {
        this.mAtist = artist;
    }

    public void setDuration(int duration) {
        this.mDuration = duration;
    }
}
