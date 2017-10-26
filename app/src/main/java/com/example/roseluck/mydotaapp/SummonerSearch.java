package com.example.roseluck.mydotaapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Created by Roseluck on 10/26/2017.
 */

public class SummonerSearch extends AppCompatActivity {

    private String myBasicSummoner = "TSM Roseluck";

    //Donut Steele plz

    private String APIKey = "RGAPI-5e745e86-76ad-45f6-b164-1a4f27aa3289";


    //Summoner searrch = https://na1.api.riotgames.com/lol/summoner/v3/summoners/by-name/TSM%20Roseluck?api_key=RGAPI-5e745e86-76ad-45f6-b164-1a4f27aa3289

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summoner_search);



        Button clickButton = (Button) findViewById(R.id.btSearch);
        clickButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String myString = "";
                try{
                    myString = new DownloadSummonerData(myBasicSummoner,0).execute().get();
                }catch(InterruptedException e) {
                    myString = null;
                }
                catch(ExecutionException e)
                {
                    myString = null;
                }

            }
        });

    }

    String FormatInputString(String inputName){

        inputName = inputName.replace(" ","%20");


        return inputName;
    }

    //This should return a JSON or String of a summoner that we search for
    private class DownloadSummonerData extends AsyncTask<Void, Void, String> {
        private String Name;
        private int type;
       // private Context context;
        private InputStream in;

        DownloadSummonerData( String name, int Type) {
            Name = name;
            type = Type;
            //DownloadSummonerData.this.context = context;
        }


        protected String doInBackground(Void... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            if (Name == "Wukong") {
                Name = "MonkeyKing";
            }
            //https://na1.api.riotgames.com/lol/summoner/v3/summoners/by-name/TSM%20Roseluck?api_key=RGAPI-5e745e86-76ad-45f6-b164-1a4f27aa3289
            String baseURL = "https://na1.api.riotgames.com/lol/summoner/v3/summoners/by-name/";
            String endURL = "?api_key=RGAPI-5e745e86-76ad-45f6-b164-1a4f27aa3289";
            String src2 = baseURL + FormatInputString(Name) + endURL;
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
}
