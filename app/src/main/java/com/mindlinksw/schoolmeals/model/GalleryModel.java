package com.mindlinksw.schoolmeals.model;

import android.net.Uri;

import java.io.File;

public class GalleryModel {

    public long id;
    public String data; // path
    public String displayName;
    public int mediaType;
    public String mineType;
    public long duration;
    public String durationDisplay;

    public Uri uri;
    public File file;
    private String encodeImage;

    public boolean isChecked = false;
    public int number = 0;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public String getMineType() {
        return mineType;
    }

    public void setMineType(String mineType) {
        this.mineType = mineType;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getDurationDisplay() {
        return durationDisplay;
    }

    public void setDurationDisplay(String durationDisplay) {
        this.durationDisplay = durationDisplay;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getEncodeImage() {
        return encodeImage;
    }

    public void setEncodeImage(String encodeImage) {
        this.encodeImage = encodeImage;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
