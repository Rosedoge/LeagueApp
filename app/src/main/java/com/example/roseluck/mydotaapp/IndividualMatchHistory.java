package com.example.roseluck.mydotaapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Roseluck on 10/26/2017.
 */

public class IndividualMatchHistory extends AppCompatActivity {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.individual_match_history);
        String thisMatch;//  = new DownloadMatche(this.getExtraData())
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                thisMatch = null;
            } else {
                thisMatch = extras.getString("MatchID");
            }
        } else {
            thisMatch = (String) savedInstanceState.getSerializable("MatchID");
        }
        try {
            thisMatch = new DownloadMatche(thisMatch).execute().get();

        } catch (ExecutionException e) {

        } catch (InterruptedException e) {

        }
        List<Summoner> sumList = new ArrayList<Summoner>();
        //Let's get the participant JSON
        for (int x = 0; x < 10; x++) {
            sumList.add(x, GetParticipant(thisMatch, x));
            try {
                //sumList.get(x).summonerSpell1 = new DownloadAbilityBitmap(this, sumList.get(x).summonerSpellid1, 0).execute().get();
                //sumList.get(x).summonerSpell2 = new DownloadAbilityBitmap(this, sumList.get(x).summonerSpellid2, 0).execute().get();
                 sumList.get(x).mySummonerIcon = new DownloadIconBitmap(this, sumList.get(x).summonerIconID, 0).execute().get();
            } catch (InterruptedException e) {

            } catch (ExecutionException e) {

            }


        }

        final String thismatchExtra = thisMatch;
        //Temporary
        Summoner[] books = {};
        GridView gridView = (GridView) findViewById(R.id.gvPlayers);

        MatchHeroAdapter booksAdapter = new MatchHeroAdapter(this, sumList.toArray(books));
        gridView.setAdapter(booksAdapter);
        SetMatchHistory(thisMatch);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Intent i=new Intent(IndividualMatchHistory.this, IndividualMatchPage.class);
                i.putExtra("matchjson", thismatchExtra);
                i.putExtra("position", position);
                startActivity(i);
            }
        });
    }

    public void SetMatchHistory(String jsonLine){
        JsonElement jelement = new JsonParser().parse(jsonLine);

        List<String> TeamThatGotIt = new ArrayList<String>();
        if(jelement.isJsonObject()){
            JsonObject jobject = jelement.getAsJsonObject();
            JsonArray jArray = jobject.getAsJsonArray("teams");
            JsonElement jsonobject = jArray.get(0);
            JsonObject JobJect = jsonobject.getAsJsonObject();
            if(JobJect.get("firstDragon").getAsBoolean()){
                TeamThatGotIt.add(0, "Blue");
                TextView myAwesomeTextView = (TextView)findViewById(R.id.tvfDragon);
                myAwesomeTextView.setTextColor(Color.BLUE);
            }else {
                TeamThatGotIt.add(0, "Red");
                TextView myAwesomeTextView = (TextView)findViewById(R.id.tvfDragon);
                myAwesomeTextView.setTextColor(Color.RED);
            }
            if(JobJect.get("riftHeraldKills").getAsInt() < 0){
              //Blue team killed Rift Herald
                TeamThatGotIt.add(1, "Blue");
                TextView myAwesomeTextView = (TextView)findViewById(R.id.tvFHerald);
                myAwesomeTextView.setTextColor(Color.BLUE);
            }
            else
            { //Blue team did not do it
                jArray = jobject.getAsJsonArray("teams");
                jsonobject = jArray.get(1);
                JobJect = jsonobject.getAsJsonObject();
                if (JobJect.get("riftHeraldKills").getAsInt() < 0) { //Red team killed Rift Herals
                    TeamThatGotIt.add(1, "Red");
                    TextView myAwesomeTextView = (TextView)findViewById(R.id.tvFHerald);
                    myAwesomeTextView.setTextColor(Color.RED);
                }else{
                    TeamThatGotIt.add(1, "None");
                }
                //reset the shit from checking
                jsonobject = jArray.get(0);
                JobJect = jsonobject.getAsJsonObject();
            }
            //WinnerWinner Chicken Dinner
                if(JobJect.get("win").getAsString().equals("Win")){
                TeamThatGotIt.add(2, "Blue");
                TextView myAwesomeTextView = (TextView)findViewById(R.id.tvWinner);
                myAwesomeTextView.setTextColor(Color.BLUE);
            }else{
                TeamThatGotIt.add(2, "Red");
                TextView myAwesomeTextView = (TextView)findViewById(R.id.tvWinner);
                myAwesomeTextView.setTextColor(Color.RED);
            }

            // globally
            TextView myAwesomeTextView = (TextView)findViewById(R.id.tvfDragon);
            myAwesomeTextView.setText(TeamThatGotIt.get(0));
            myAwesomeTextView = (TextView)findViewById(R.id.tvFHerald);
            myAwesomeTextView.setText(TeamThatGotIt.get(1));
            myAwesomeTextView = (TextView)findViewById(R.id.tvWinner);
            myAwesomeTextView.setText(TeamThatGotIt.get(2));
        }
    }


    public Summoner GetParticipant(String jsonLine, int num) {
        Summoner curSum = new Summoner();
        JsonElement jelement = new JsonParser().parse(jsonLine);
        String result = "";
        if(jelement.isJsonObject()) {
            try {
                JsonObject jobject = jelement.getAsJsonObject();
                JsonArray jArray = jobject.getAsJsonArray("participantIdentities"); //this gets the goddamn name

                JsonElement jsonobject = jArray.get(num);
                JsonObject JobJect = jsonobject.getAsJsonObject();
                JobJect = JobJect.getAsJsonObject("player");
//              JobJect = JobJect.getAsJsonObject("image");

                curSum.mySummonerName = JobJect.get("summonerName").toString();
                curSum.summonerIconID = JobJect.get("profileIcon").toString();
                //Log.d("SumName", curSum.mySummonerName);
                //here we need to get the summoner spell numbers
                jobject = jelement.getAsJsonObject();
                jArray = jobject.getAsJsonArray("participants");
                jsonobject = jArray.get(num);
                JobJect = jsonobject.getAsJsonObject();
                curSum.summonerSpellid2 = JobJect.get("spell2Id").toString();
                curSum.summonerSpellid1 = JobJect.get("spell1Id").toString();

            }
            catch(NullPointerException e) {
                Log.d("Didnunufin", e.toString());
            }

        }

        return curSum;

    }


    //Downloads an Icon Bitmap
    private class DownloadIconBitmap extends AsyncTask<Void, Void, Bitmap> {
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
    //Gets a summoner URL. Will need to be updated if, god forbid, anyone ever makes a new summoner spell
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

    ///This code is so that someone can download the bitmap of a summoner spell
    private class DownloadAbilityBitmap extends AsyncTask<Void, Void, Bitmap> {
        ///
        private String URL;
        private int type;
        private Context context;
        private InputStream in;
        private String SummonerSpellString;

        DownloadAbilityBitmap(Context context, String Url, int Type)
        {


            URL = Url;
            type = Type;
            DownloadAbilityBitmap.this.context = context;
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

    //gets the String of Match information
    private class DownloadMatche extends AsyncTask<Void, Void, String> {
        private String Id;

        // private Context context;
        private InputStream in;

        DownloadMatche( String id) {
            Id = id;

            //DownloadMatche.this.context = context;
        }



        protected String doInBackground(Void... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            //https://na1.api.riotgames.com/lol/match/v3/matchlists/by-account/50222274/recent?api_key=RGAPI-5e745e86-76ad-45f6-b164-1a4f27aa3289
            String baseURL = "https://na1.api.riotgames.com/lol/match/v3/matches/";
            String endURL = "?api_key=RGAPI-20548e97-e82d-4907-b875-365869748d76";
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
}
