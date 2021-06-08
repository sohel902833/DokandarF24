package com.sohel.dokandarf24.Local.Model;

import java.io.Serializable;

public class RecordingItem implements Serializable {
    private  String name,path;
    private  long length,timeAdded;
    public RecordingItem(){

    }

    public RecordingItem(String name, String path, long length, long timeAdded) {
        this.name = name;
        this.path = path;
        this.length = length;
        this.timeAdded = timeAdded;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(long timeAdded) {
        this.timeAdded = timeAdded;
    }
}
