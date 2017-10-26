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
import android.widget.SeekBar;
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
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.MalformedJsonException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Roseluck on 10/17/2017.
 */

public class ChampionPage extends AppCompatActivity {
    Hero myHero = new Hero();
    int myLevel = 0;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.champion_page);

        String jsonString = loadJSONFromAsset();
        String newString;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                newString= null;
            } else {
                newString= extras.getString("ChampionSelected");
            }
        } else {
            newString= (String) savedInstanceState.getSerializable("ChampionSelected");
        }
        myHero.name = newString;
        AssembleHero(jsonString,myHero);
        AssemblePage(myHero, 1);
        AssembleHeroAbilities();
        SeekBar mySeek = (SeekBar)findViewById(R.id.sbLevel);


        mySeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                progressChanged = progress;
                AssemblePage(myHero,progress);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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


    void AssemblePage(Hero theHero, int level){
        // list=(ListView)findViewById(R.id.list);
        TextView mytext = (TextView)findViewById((R.id.tvHeroName));
        mytext.setText(theHero.name);

        mytext = (TextView)findViewById((R.id.tvTitle));
        mytext.setText(theHero.title);

        ImageView myImage = (ImageView)findViewById(R.id.ivHero);
        myImage.setImageBitmap(GetChampImage());


        mytext = (TextView)findViewById((R.id.tvHP));
        mytext.setText(String.valueOf( Float.parseFloat(theHero.hp) + Float.parseFloat(theHero.hpperlevel) *  level));
        mytext = (TextView)findViewById((R.id.tvMP));
        mytext.setText(String.valueOf( Float.parseFloat(theHero.mp) + Float.parseFloat(theHero.mpperlevel) *  level));
        mytext = (TextView)findViewById((R.id.tvAD));
        mytext.setText(String.valueOf( Float.parseFloat(theHero.attackdmg) + Float.parseFloat(theHero.attackdmgperlevel) *  level));
        mytext = (TextView)findViewById((R.id.tvArmor));
        mytext.setText(String.valueOf( Float.parseFloat(theHero.armor) + Float.parseFloat(theHero.armorperlevel) *  level));



    }

    Bitmap GetChampImage(){
        //championImages.add(count,new DownloadFilesTask(this,line,count).execute().get());

        Bitmap ret;
        try {
            ret = new DownloadFilesTask(this,myHero.name,0).execute().get();
        }catch(InterruptedException e) {
            ret = null;
        }
        catch(ExecutionException e)
        {
            ret = null;
        }
        return ret;
    }

    //Gets all the official data that a hero has on them. A much more stringent JSON element than the other one.
    private class DownloadHeroData extends AsyncTask<Void, Void, String> {
        private String URL;
        private int type;
        private Context context;
        private InputStream in;

        DownloadHeroData(Context context, String Url, int Type)
        {
            URL = Url;
            type = Type;
            DownloadHeroData.this.context = context;
        }



        protected String doInBackground(Void... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            if (URL == "Wukong") {
                URL = "MonkeyKing";
            }
            String baseURL = "http://ddragon.leagueoflegends.com/cdn/6.24.1/data/en_US/champion/";
            String endURL = ".json ";
            String src2 = baseURL + URL + endURL;
            try {
                URL url = new URL(src2);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");

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

    Bitmap GetAbilityImages(String abilityName){
        Bitmap ret;

        try {
            ret = new DownloadAbilityBitmap(this,abilityName,0).execute().get();
        }catch(InterruptedException e) {
            ret = null;
        }
        catch(ExecutionException e)
        {
            ret = null;
        }
        return ret;

    }
    //Downloads the bitmap of the abilities
    private class DownloadAbilityBitmap extends AsyncTask<Void, Void, Bitmap> {
        private String URL;
        private int type;
        private Context context;
        private InputStream in;

        DownloadAbilityBitmap(Context context, String Url, int Type)
        {
            URL = Url;
            type = Type;
            DownloadAbilityBitmap.this.context = context;
        }


        protected Bitmap doInBackground(Void... params) {
            try {
                if(URL == "Wukong"){
                    URL = "MonkeyKing";
                }
                String baseURL = "http://ddragon.leagueoflegends.com/cdn/6.24.1/img/spell/";
                String endURL = ".png ";
                String src2 = baseURL + URL;
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

    //Gets the Champ Image
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
                return BitmapFactory.decodeStream(input);

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }


    void AssembleHeroAbilities(){
        String elem;
        try{
            elem = new DownloadHeroData(this,myHero.name,0).execute().get();
        }catch(InterruptedException e) {
            elem = null;
        }
        catch(ExecutionException e)
        {
            elem = null;
        }

        JsonElement jelement = new JsonParser().parse(elem);


        if(jelement.isJsonObject()) {
            try {
                JsonObject jobject = jelement.getAsJsonObject();
                jobject = jobject.getAsJsonObject("data");
                jobject = jobject.getAsJsonObject(myHero.name);
                JsonArray jArray = jobject.getAsJsonArray("spells");

                JsonElement jsonobject = jArray.get(0);//.getAsJsonObject();
                JsonObject JobJect = jsonobject.getAsJsonObject();
                TextView mytext = (TextView)findViewById((R.id.tvQ));
                mytext.setText(JobJect.get("id").toString().replace("\"", "").replace(myHero.name,""));
                mytext = (TextView)findViewById((R.id.tvQEffect));
                mytext.setText( JobJect.get("description").toString().replace("\"", ""));
                ImageView myImage = (ImageView)findViewById(R.id.ivQ);
                JobJect = JobJect.getAsJsonObject("image");
                myImage.setImageBitmap(GetAbilityImages(JobJect.get("full").toString().replace("\"","")));


                //spells image full
                jsonobject = jArray.get(1);//.getAsJsonObject();
                JobJect = jsonobject.getAsJsonObject();
                mytext = (TextView)findViewById((R.id.tvW));
                mytext.setText(JobJect.get("id").toString().replace("\"", "").replace(myHero.name,""));
                mytext = (TextView)findViewById((R.id.textvWEffect));
                mytext.setText( JobJect.get("description").toString().replace("\"", ""));
                myImage = (ImageView)findViewById(R.id.ivW);
                JobJect = JobJect.getAsJsonObject("image");
                myImage.setImageBitmap(GetAbilityImages(JobJect.get("full").toString().replace("\"","")));

                jsonobject = jArray.get(2);//.getAsJsonObject();
                JobJect = jsonobject.getAsJsonObject();
                mytext = (TextView)findViewById((R.id.tvE));
                mytext.setText(JobJect.get("id").toString().replace("\"", "").replace(myHero.name,""));
                mytext = (TextView)findViewById((R.id.tvEEffect));
                mytext.setText( JobJect.get("description").toString().replace("\"", ""));
                myImage = (ImageView)findViewById(R.id.ivE);
                JobJect = JobJect.getAsJsonObject("image");
                myImage.setImageBitmap(GetAbilityImages(JobJect.get("full").toString().replace("\"","")));

                jsonobject = jArray.get(3);//.getAsJsonObject();
                JobJect = jsonobject.getAsJsonObject();
                mytext = (TextView)findViewById((R.id.tvR));
                mytext.setText(JobJect.get("id").toString().replace("\"", "").replace(myHero.name,""));
                mytext = (TextView)findViewById((R.id.tvREffect));
                mytext.setText( JobJect.get("description").toString().replace("\"", ""));
                myImage = (ImageView)findViewById(R.id.ivR);
                JobJect = JobJect.getAsJsonObject("image");
                myImage.setImageBitmap(GetAbilityImages(JobJect.get("full").toString().replace("\"","")));

            }catch(NullPointerException e) {

            }
        }
    }

    void AssembleHero(String jsonString, Hero theHero){
        JsonElement jelement = new JsonParser().parse(jsonString);

        if(jelement.isJsonObject()) {
            try {
                JsonObject jobject = jelement.getAsJsonObject();
                jobject = jobject.getAsJsonObject("data");
                jobject = jobject.getAsJsonObject(theHero.name);

                //result = jobject.get("id").toString();
                theHero.key = jobject.get("key").toString().replace("\"", "");
                theHero.name = jobject.get("name").toString().replace("\"", "");
                theHero.title = jobject.get("title").toString().replace("\"", "");
                theHero.blurb = jobject.get("blurb").toString().replace("\"", "");

//                //Info Section
//                jobject = jelement.getAsJsonObject();
//                jobject = jobject.getAsJsonObject("data");
//                jobject = jobject.getAsJsonObject(theHero.name);
//                jobject = jobject.getAsJsonObject("info");


                jobject = jelement.getAsJsonObject();
                jobject = jobject.getAsJsonObject("data");
                jobject = jobject.getAsJsonObject(theHero.name);
                jobject = jobject.getAsJsonObject("stats");
                theHero.hp = jobject.get("hp").toString().replace("\"", "");
                theHero.hpperlevel = jobject.get("hpperlevel").toString().replace("\"", "");
                theHero.mp = jobject.get("mp").toString().replace("\"", "");
                theHero.mpperlevel = jobject.get("mpperlevel").toString().replace("\"", "");
                theHero.movespeed = jobject.get("movespeed").toString().replace("\"", "");
                theHero.armor = jobject.get("armor").toString().replace("\"", "");
                theHero.armorperlevel = jobject.get("armorperlevel").toString().replace("\"", "");
                theHero.spellblock = jobject.get("spellblock").toString().replace("\"", "");
                theHero.spellblockperlevel = jobject.get("spellblockperlevel").toString().replace("\"", "");
                theHero.attackrange = jobject.get("attackrange").toString().replace("\"", "");
                theHero.hpregen = jobject.get("hpregen").toString().replace("\"", "");
                theHero.hpregenperlevel = jobject.get("hpregenperlevel").toString().replace("\"", "");
                theHero.mpregen = jobject.get("mpregen").toString().replace("\"", "");
                theHero.mpregenperlevel = jobject.get("mpregenperlevel").toString().replace("\"", "");
                theHero.crit = jobject.get("crit").toString().replace("\"", "");
                theHero.critperlevel = jobject.get("critperlevel").toString().replace("\"", "");
                theHero.attackdmg = jobject.get("attackdamage").toString().replace("\"", "");
                theHero.attackdmgperlevel = jobject.get("attackdamageperlevel").toString().replace("\"", "");
                theHero.attackspeedoffset = jobject.get("attackspeedoffset").toString().replace("\"", "");
                theHero.attackspeedperlevel = jobject.get("attackspeedperlevel").toString().replace("\"", "");
            }
            catch(NullPointerException e) {

            }

        }
    }


    private class Hero{
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
        public Hero(){

        }
    }
}
