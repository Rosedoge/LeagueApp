package com.example.roseluck.mydotaapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Roseluck on 10/26/2017.
 */

public class MatchHistory extends AppCompatActivity {
    String bigOlJson;
    final List<String> champions = new ArrayList<String>();
    List<Bitmap> championImages = new ArrayList<Bitmap>();
    List<String> selectedMatchID = new ArrayList<String>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.match_history);

        String summonerMatches = "";
        bigOlJson = loadJSONFromAsset();

        String newString;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                newString= null;
            } else {
                newString= extras.getString("acctID");
            }
        } else {
            newString= (String) savedInstanceState.getSerializable("acctID");
        }
        try{
            summonerMatches = new DownloadMatches(newString, 0).execute().get();
        }
        catch(InterruptedException e) {
             summonerMatches = null;
        }
        catch(ExecutionException e)
        {
             summonerMatches = "null";
        }
        AssembleMatchRows(summonerMatches);
        CustomListAdapter adapter=new CustomListAdapter(this, champions.toArray(new String[champions.size()]), championImages.toArray(new Bitmap[championImages.size()]));
        ListView list=(ListView)findViewById(R.id.liMatchList);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
//                String Selecteditem= champions.get(position).toString();
//                Toast.makeText(getApplicationContext(), Selecteditem, Toast.LENGTH_SHORT).show();
                Intent i=new Intent(MatchHistory.this, IndividualMatchHistory.class);
                i.putExtra("MatchID", selectedMatchID.get(position));
                startActivity(i);

            }
        });

    }

    void AssembleMatchRows(String myMatches){
        JsonElement jelement = new JsonParser().parse(myMatches);
        ;
        if(jelement.isJsonObject()) {
            JsonObject jobject = jelement.getAsJsonObject();
            for(int i=0; i<jobject.getAsJsonArray("matches").size();i++){

                try {
                    jobject = jelement.getAsJsonObject();
                    JsonArray jArray = jobject.getAsJsonArray("matches");
                    JsonElement jsonobject = jArray.get(i);
                    JsonObject JobJect = jsonobject.getAsJsonObject();
                    String myString = CheckForID(JobJect.getAsJsonPrimitive("champion").getAsInt());
                    champions.add(i,myString);
                    try{
                        championImages.add(i,new DownloadFilesTask(this,champions.get(i),i).execute().get());
                        selectedMatchID.add(i, JobJect.getAsJsonPrimitive("gameId").getAsString());
                    }catch(InterruptedException e) {

                    }
                    catch(ExecutionException e)
                    {

                    }
                }
                catch(NullPointerException e) {

                }
            }
        }
    }
    //return the hero name that has the id
    String CheckForID( int ID){
        try{
            final InputStream file = getAssets().open("champs.txt");

            BufferedReader reader;
            reader = new BufferedReader(new InputStreamReader(file));
            String line = "";


            while(line != null){
                //Log.d("StackOverflow", line);

                line = reader.readLine();
                if(ActuallyCheckJSONHere(line, ID)){
                    return line;
                }

            }
        } catch(IOException ioe){
            ioe.printStackTrace();
        }




        return "";
    }

    boolean ActuallyCheckJSONHere(String hero, int id){
        JsonElement jelement = new JsonParser().parse(bigOlJson);
        String result = "";
        if(jelement.isJsonObject()) {
            try {
                JsonObject jobject = jelement.getAsJsonObject();
                jobject = jobject.getAsJsonObject("data");
                jobject = jobject.getAsJsonObject(hero);

               if(jobject.get("key").getAsInt() == id){

                   return true;
                }

            }
            catch(NullPointerException e) {

            }

        }



        return false;
    }
    //Ard vark pays off
    public String loadJSONFromAsset() {
        String json = null;
        try {

            AssetManager assetManager = getResources().getAssets();
            InputStream inputStream = null;
            try {
                inputStream = assetManager.open("heros.json");
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

    //gets a single bitmap
    private class DownloadFilesTask extends AsyncTask<Void, Void, Bitmap> {
        private String URL;
        private int type;
        private Context context;
        private InputStream in;

        DownloadFilesTask(Context context, String Url, int Type)
        {
            URL = Url;
            type = Type;
            DownloadFilesTask.this.context = context;
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

    //gets the String of Match information
    private class DownloadMatches extends AsyncTask<Void, Void, String> {
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
            String endURL = "/recent?api_key=RGAPI-dc98a5db-c341-4de2-9d7d-d33cc05f84d2";
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
