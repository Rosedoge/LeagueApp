package com.example.roseluck.mydotaapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MatchHeroAdapter extends BaseAdapter {

    private final Context mContext;
    private final Summoner[] books;

    // 1
    public MatchHeroAdapter(Context context, Summoner[] books) {
        this.mContext = context;
        this.books = books;
    }

    // 2
    @Override
    public int getCount() {
        return books.length;
    }

    // 3
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 4
    @Override
    public Object getItem(int position) {
        return null;
    }

    // 5
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1
        final Summoner book = books[position];

        // 2
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.summoner_grid_layout, null);
        }

        // 3
        final ImageView imageView = (ImageView)convertView.findViewById(R.id.tvSummonerIcon);
        final TextView nameTextView = (TextView)convertView.findViewById(R.id.tvSummonerName);

        final ImageView summonerspell1 = (ImageView)convertView.findViewById(R.id.ivSummonerSpell1);
        final ImageView summonerspell2 = (ImageView)convertView.findViewById(R.id.ivSummonerSpell2);
        // 4
        imageView.setImageBitmap(book.mySummonerIcon);
        summonerspell1.setImageBitmap(book.summonerSpell1);
        summonerspell2.setImageBitmap(book.summonerSpell2);
        nameTextView.setText(book.mySummonerName);
//        authorTextView.setText(mContext.getString(book.getAuthor()));

        return convertView;
    }

}

 class Book{
     int myID;
     Book(int id){

         myID = id;
     }
 }

 class Summoner{

     public Bitmap mySummonerIcon;
     public String summonerIconID;
     public String mySummonerName;
     public String summonerSpellid1;
     public String summonerSpellid2;
     public Bitmap summonerSpell1;
     public Bitmap summonerSpell2;

     Summoner(Bitmap bm, String st, Bitmap ss1, Bitmap ss2){
         mySummonerIcon = bm;
         mySummonerName = st;
         summonerSpell1 = ss1;
         summonerSpell2 = ss2;
     }
     Summoner(){

     }
 }
