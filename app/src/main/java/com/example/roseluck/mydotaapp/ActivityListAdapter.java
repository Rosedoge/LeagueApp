package com.example.roseluck.mydotaapp;

/**
 * Created by Roseluck on 10/26/2017.
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ActivityListAdapter extends ArrayAdapter<String> {


    private final Activity context;


    public ActivityListAdapter(Activity context) {
        super(context, R.layout.select_activity);
        // TODO Auto-generated constructor stub

        this.context=context;

    }




}