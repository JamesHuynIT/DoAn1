package com.tdt.project.wearhelper;

import android.content.Intent;

public class ListViewItem {

    private int imageRes;
    private String text;
    private Intent intent;
    public int getImageRes() {
        return imageRes;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }


    public ListViewItem(int imageRes, String text,Intent intent) {
        this.imageRes = imageRes;
        this.text = text;
        this.intent = intent;
    }
}
