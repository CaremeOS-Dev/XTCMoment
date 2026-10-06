package com.xtc.moment.module.publish.multi.bean;

import java.util.ArrayList;

/** Event carrying the current photo list and position between the picker and the preview. */
public class PhotoEvent {

    private int position;
    private ArrayList<String> photoLists;
    private boolean isDataChange;
    private boolean isEntry;

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ArrayList<String> getPhotoLists() {
        return this.photoLists;
    }

    public void setPhotoLists(ArrayList<String> photoLists) {
        this.photoLists = photoLists;
    }

    public boolean isDataChange() {
        return this.isDataChange;
    }

    public void setDataChange(boolean dataChange) {
        this.isDataChange = dataChange;
    }

    public boolean isEntry() {
        return this.isEntry;
    }

    public void setEntry(boolean entry) {
        this.isEntry = entry;
    }

    @Override
    public String toString() {
        return "PhotoPath{position=" + this.position + ", bigPhotoList=" + this.photoLists
                + ", isFish=" + this.isDataChange + ", isEntry=" + this.isEntry + '}';
    }
}