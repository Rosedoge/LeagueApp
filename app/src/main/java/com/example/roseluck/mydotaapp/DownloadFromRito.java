package com.example.roseluck.mydotaapp;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Roseluck on 10/28/2017.
 */

public class DownloadFromRito extends AppCompatActivity {


    //gets the String of Match information
    public class DownloadMatches extends AsyncTask<Void, Void, String> {
        private String Id;
        private int type;
        // private Context context;
        private InputStream in;

        DownloadMatches( String id, int Type) {
            Id = id;
            type = Type;
            //DownloadSummonerData.this.context = context;
        }



        protected String doInBackground(Void... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            //https://na1.api.riotgames.com/lol/match/v3/matchlists/by-account/50222274/recent?api_key=RGAPI-5e745e86-76ad-45f6-b164-1a4f27aa3289
            String baseURL = "https://na1.api.riotgames.com/lol/match/v3/matchlists/by-account/";
            String endURL = "/recent?api_key=RGAPI-efe5c0c6-f0d9-4c4c-8e81-ab73deb25804";
            String src2 = baseURL + Id + endURL;

            try {
                java.net.URL url = new URL(src2);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    // e.printStackTrace();
                }
            }
            return null;
        }


    }
    //gets a single bitmap
    public class DownloadBitmap extends AsyncTask<Void, Void, Bitmap> {
        private String URL;
        private int type;
        private Context context;
        private InputStream in;

        DownloadBitmap(Context context, String Url, int Type)
        {
            URL = Url;
            type = Type;
            DownloadBitmap.this.context = context;
        }


        protected Bitmap doInBackground(Void... params) {
            try {
                if(URL == "Wukong"){
                    URL = "MonkeyKing";
                }
                String baseURL = "http://ddragon.leagueoflegends.com/cdn/6.24.1/img/champion/";
                String endURL = ".png ";
                String src2 = baseURL + URL + endURL;
                java.net.URL url = new java.net.URL(src2);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }


    public class DownloadIconBitmap extends AsyncTask<Void, Void, Bitmap> {
        ///
        private String URL;
        private int type;
        private Context context;
        private InputStream in;


        DownloadIconBitmap(Context context, String Url, int Type)
        {


            URL = Url;
            type = Type;
            DownloadIconBitmap.this.context = context;
            ;
        }


        protected Bitmap doInBackground(Void... params) {
            try {
                if(URL == "Wukong"){
                    URL = "MonkeyKing";
                }
                String baseURL = "http://ddragon.leagueoflegends.com/cdn/7.21.1/img/profileicon/";
                String endURL = ".png ";
                String src2 = baseURL+ URL + endURL;
                java.net.URL url = new java.net.URL(src2);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public class DownloadSummonerSpellICon extends AsyncTask<Void, Void, Bitmap> {
        ///
        private String URL;
        private int type;
        private Context context;
        private InputStream in;
        private String SummonerSpellString;

        DownloadSummonerSpellICon(Context context, String Url, int Type)
        {
            //TODO decode the summoner spell that is sent

            URL = Url;
            type = Type;
            DownloadSummonerSpellICon.this.context = context;
            SummonerSpellString = LoadSummonerSpellJson();
        }


        protected Bitmap doInBackground(Void... params) {
            try {
                if(URL == "Wukong"){
                    URL = "MonkeyKing";
                }
                String baseURL = "http://ddragon.leagueoflegends.com/cdn/7.21.1/img/spell/";
                String endURL = ".png ";
                String src2 = baseURL+ GetRealSummonerURL(URL) + endURL;
                java.net.URL url = new java.net.URL(src2);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public String LoadSummonerSpellJson() {
        String json = null;
        try {

            AssetManager assetManager = getResources().getAssets();
            InputStream inputStream = null;
            try {
                inputStream = assetManager.open("SummonerSpell.json");
                if ( inputStream != null)
                    Log.d("bloop", "It worked!");
            } catch (IOException e) {
                e.printStackTrace();
            }

            int size = inputStream.available();

            byte[] buffer = new byte[size];

            inputStream.read(buffer);

            inputStream.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    public String GetRealSummonerURL( String URL){

        switch( URL){
            case "21":
                URL = "SummonerBarrier";
                break;
            case "1":
                URL = "SummonerBoost";
                break;
            case "14":
                URL = "SummonerDot";
                break;
            case "3":
                URL = "SummonerExhaust";
                break;
            case "4":
                URL = "SummonerFlash";
                break;
            case "6":
                URL = "SummonerHaste";
                break;
            case "7":
                URL = "SummonerHeal";
                break;
            case "30":
                URL = "SummonerPoroRecall";
                break;
            case "11":
                URL = "SummonerSmite";
                break;
            case "12":
                URL = "SummonerTeleport";
                break;
            default:
                URL = "SummonerFlash";
                break;
        }
        return URL;
    }
}
