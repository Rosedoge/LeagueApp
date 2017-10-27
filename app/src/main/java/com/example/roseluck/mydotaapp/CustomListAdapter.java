package com.example.roseluck.mydotaapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomListAdapter extends ArrayAdapter<String> {


    private final Activity context;
    private final String[] heronames;
    private final Bitmap[] imgid;

    public CustomListAdapter(Activity context, String[] heronames, Bitmap[] imgid) {
        super(context, R.layout.heroes, heronames);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.heronames=heronames;
        this.imgid=imgid;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.heroes, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView extratxt = (TextView) rowView.findViewById(R.id.description);

        txtTitle.setText(heronames[position]);
        imageView.setImageBitmap(imgid[position]);
        extratxt.setText("Description "+heronames[position]);
        return rowView;


    };
}