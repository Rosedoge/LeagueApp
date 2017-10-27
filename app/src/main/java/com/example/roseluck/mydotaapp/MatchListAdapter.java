package com.example.roseluck.mydotaapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * Created by Roseluck on 10/26/2017.
 */

public class MatchListAdapter extends ArrayAdapter<String> {


    private final Activity context;
    private final String[] matches;
    private final String[] wl;
    //private final String[] time;
    private Bitmap[] mybits;

    public MatchListAdapter(Activity context, String[] matches, Bitmap[] bits, String[] WL) {
        super(context, R.layout.heroes, matches);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.matches=matches;
        mybits = bits;
        wl = WL;
    }

    public void SetShitStraight(Bitmap[] images){

        mybits = images;
    }
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.match_history, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView extratxt = (TextView) rowView.findViewById(R.id.description);

        txtTitle.setText(matches[position]);
        imageView.setImageBitmap(mybits[position]);
        extratxt.setText("Description " + wl[position]);
        return rowView;


    };


}
