package com.example.roseluck.mydotaapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


//
public class MainActivity extends AppCompatActivity {
    ListView list;
    public  String returnedJSON;
            public String yourJsonStringUrl;

//champion data http://ddragon.leagueoflegends.com/cdn/6.24.1/data/en_US/champion.json
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


       //this should load the json file from meory getting like 8 games.
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
        }
        catch(JSONException e){

        }
        returnedJSON = loadJSONFromAsset();


        // Construct the data source
        ArrayList<Hero> arrayOfUsers = new ArrayList<Hero>();
// Create the adapter to convert the array to Hero


        JsonParser jsonParser = new JsonParser();
        JsonElement element = jsonParser.parse(returnedJSON);
        final List<String> champions = new ArrayList<String>();
        List<Bitmap> championImages = new ArrayList<Bitmap>();

        //set up asset manager to read text and get heronames from that
        AssetManager assetManager = getAssets();
        try{
            final InputStream file = getAssets().open("champs.txt");

            BufferedReader reader;
            reader = new BufferedReader(new InputStreamReader(file));
            String line = "";
            int count = 0;

            while(line != null){
                //Log.d("StackOverflow", line);

                line = reader.readLine();
                //Gets the Champion's Image
                //DownloadFilesTask task = new DownloadFilesTask(this,line,count);

                //gets the hero name of each hero
                champions.add(count, GetHeroNames(returnedJSON, line));

                try {
                    //Gets the Champion's Image
                    championImages.add(count,new DownloadFilesTask(this,line,count).execute().get());
                }catch(InterruptedException e) {
                }
                catch(ExecutionException e)
                {

                }
                String[] mygod = {"Obligatory", "false"};
                mygod[0] = line;

                count+=1;
            }
        } catch(IOException ioe){
            ioe.printStackTrace();
        }

        CustomListAdapter adapter=new CustomListAdapter(this, champions.toArray(new String[champions.size()]), championImages.toArray(new Bitmap[championImages.size()]));
        list=(ListView)findViewById(R.id.list);
        list.setAdapter(adapter);

        //Sets CLick events
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
//                String Selecteditem= champions.get(position).toString();
//                Toast.makeText(getApplicationContext(), Selecteditem, Toast.LENGTH_SHORT).show();
                Intent i=new Intent(MainActivity.this, ChampionPage.class);
                i.putExtra("ChampionSelected", champions.get(position));
                startActivity(i);

            }
        });
    }




    //DownloadFileASync
    //Gets images, see Bitmap
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


    public Bitmap getBitmapFromURL(String src) {
        return null;
    }


    public String GetHeroNames(String jsonLine, String hero) {
        JsonElement jelement = new JsonParser().parse(jsonLine);
        String result = "";
        if(jelement.isJsonObject()) {
            try {
                JsonObject jobject = jelement.getAsJsonObject();
                jobject = jobject.getAsJsonObject("data");
                jobject = jobject.getAsJsonObject(hero);
                //jobject = jobject.getAsJsonObject("id");
//        jobject = jobject.getAsJsonObject("version");
//        jobject = jobject.getAsJsonObject("data");
                result = jobject.get("id").toString();

            }
            catch(NullPointerException e) {

            }

        }

            return result.replace("\"", "");

    }


    public class getData extends AsyncTask<String, String, String> {

        HttpURLConnection urlConnection;

        @Override
        protected String doInBackground(String... args) {

            StringBuilder result = new StringBuilder();

            try {
                URL url = new URL(yourJsonStringUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }


            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            //returnedJSON = result.toString();
            returnedJSON = result;
            //Do something with the JSON string

        }


    }
    public class UsersAdapter extends ArrayAdapter<Hero> {
        public UsersAdapter(Context context, ArrayList<Hero> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Hero hero = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.heroes, parent, false);
            }
            // Lookup view for data population
            TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
            TextView tvHome = (TextView) convertView.findViewById(R.id.tvHome);
            // Populate the data into the template view using the data object
            tvName.setText(hero.id);
            tvHome.setText(hero.picURL);
            // Return the completed view to render on screen
            return convertView;
        }
    }

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



    public class Game {
        public String id;
        public String region;

        public Game(String region, String id) {
            this.region = region;
            this.id = id;
        }
    }

    public class Hero{
        public String version;
        public String id;
        public String picURL;
        public String key;
        public String name;
        public String title;
        public String blurb;

        public String attack;
        public String defense;
        public String magic;
        public String difficulty;
        public String image;
        public String sprite;
        public String group;
        public String tags;
        public String partype;
        public String hp;
        public String hpperlevel;
        public String mp;
        public String mpperlevel;
        public String movespeed;
        public String armor;
        public String armorperlevel;
        public String spellblock;
        public String spellblockperlevel;
        public String attackrange;
        public String hpregen;
        public String hpregenperlevel;
        public String mpregen;
        public String mpregenperlevel;
        public String crit;
        public String critperlevel;
        public String attackdmg;
        public String attackdmgperlevel;
        public String attackspeedoffset;
        public String attackspeedperlevel;

        public Hero(String id, String picURL){
            this.id = id;
            this.picURL = picURL;
        }
    }
}

