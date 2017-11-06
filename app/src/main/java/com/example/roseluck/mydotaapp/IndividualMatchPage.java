package com.example.roseluck.mydotaapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.roseluck.mydotaapp.R.id.grGraph;


/**
 * Created by Roseluck on 10/31/2017.
 */

public class IndividualMatchPage extends AppCompatActivity {

    String thisMatch; //BAD IDEA MAN
    int myPos;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_match_layout);


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                thisMatch = null;
            } else {
                thisMatch = extras.getString("matchjson");
                myPos = extras.getInt("position");
            }
        } else {
            thisMatch = (String) savedInstanceState.getSerializable("matchjson");
            myPos = (int)savedInstanceState.getSerializable("position");
        }

        JsonElement jelement = new JsonParser().parse(thisMatch);
        JsonObject jobject = jelement.getAsJsonObject();
        JsonArray jArray = jobject.getAsJsonArray("participants");
        JsonElement jsonobject = jArray.get(myPos); //sure hope it starts at 0
        JsonObject JobJect = jsonobject.getAsJsonObject();
        JsonObject myLane = JobJect.getAsJsonObject("timeline");
        String myString = myLane.getAsJsonPrimitive("lane").getAsString();

        int enemypos = FindOutWhoTheOpponentLanerIs(myString);
        GraphView graph = (GraphView) findViewById(grGraph);


        ThreadGraph1 myRunnable = new ThreadGraph1(graph, JobJect, myPos, Color.BLUE);
        jelement = new JsonParser().parse(thisMatch);
        jobject = jelement.getAsJsonObject();
        jArray = jobject.getAsJsonArray("participants");
        jsonobject = jArray.get(enemypos);
        ThreadGraph1 enemyTable = new ThreadGraph1(graph, JobJect, enemypos, Color.RED);
        Thread t = new Thread(myRunnable);
        t.start();


        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(0);
        nf.setMinimumIntegerDigits(1);

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(nf, nf));
}

    //This one is about GPM
    public class ThreadGraph1 implements Runnable {

        private GraphView var;
        private JsonObject JobJect;
        private int pos;
        int color;
        public ThreadGraph1(GraphView var, JsonObject JobJect, int pos, int color ) {
            this.var = var;
            this.JobJect = JobJect;
            this.pos = pos;
            this.color = color;
        }

        public void run() {
            BarGraphSeries<DataPoint> series;
            JobJect = JobJect.getAsJsonObject("timeline");
            JobJect = JobJect.getAsJsonObject("csDiffPerMinDeltas");
            if(JobJect.size() == 1) {
                series = new BarGraphSeries<>(new DataPoint[]{
                        new DataPoint(0, JobJect.getAsJsonPrimitive("0-10").getAsFloat()),


                });
            }else if(JobJect.size() == 2) {
                series = new BarGraphSeries<>(new DataPoint[]{
                        new DataPoint(0, JobJect.getAsJsonPrimitive("0-10").getAsFloat()),
                        new DataPoint(10, JobJect.getAsJsonPrimitive("10-20").getAsFloat()),
                });
            }else if(JobJect.size() == 3) {
                series = new BarGraphSeries<>(new DataPoint[]{
                        new DataPoint(0, JobJect.getAsJsonPrimitive("0-10").getAsFloat()),
                        new DataPoint(10, JobJect.getAsJsonPrimitive("10-20").getAsFloat()),
                        new DataPoint(20, JobJect.getAsJsonPrimitive("20-30").getAsFloat())
                });
            }else if(JobJect.size() == 4) {
                series = new BarGraphSeries<>(new DataPoint[]{
                        new DataPoint(0, JobJect.getAsJsonPrimitive("0-10").getAsFloat()),
                        new DataPoint(10, JobJect.getAsJsonPrimitive("10-20").getAsFloat()),
                        new DataPoint(20, JobJect.getAsJsonPrimitive("20-30").getAsFloat()),
                        new DataPoint(30, JobJect.getAsJsonPrimitive("30-40").getAsFloat())
                });
            }else if(JobJect.size() == 5) {
                series = new BarGraphSeries<>(new DataPoint[]{
                        new DataPoint(0, JobJect.getAsJsonPrimitive("0-10").getAsFloat()),
                        new DataPoint(10, JobJect.getAsJsonPrimitive("10-20").getAsFloat()),
                        new DataPoint(20, JobJect.getAsJsonPrimitive("20-30").getAsFloat()),
                        new DataPoint(30, JobJect.getAsJsonPrimitive("30-40").getAsFloat()),
                        new DataPoint(40, JobJect.getAsJsonPrimitive("40-50").getAsFloat())
                });
            }else{

                series = new BarGraphSeries<>(new DataPoint[]{
                        new DataPoint(0, 1)
                });
            }
            series.setColor(color);
            var.addSeries(series);
        }
    }

    int FindOutWhoTheOpponentLanerIs(String myLane){

        for(int x = 0;x<9;x++){
            if(x != myPos){
                JsonElement jelement = new JsonParser().parse(thisMatch);
                JsonObject jobject = jelement.getAsJsonObject();
                JsonArray jArray = jobject.getAsJsonArray("participants");
                JsonElement jsonobject = jArray.get(x); //sure hope it starts at 0
                JsonObject JobJect = jsonobject.getAsJsonObject() ;
                JobJect = JobJect.getAsJsonObject("timeline");
                String EnemyRole = JobJect.getAsJsonPrimitive("lane").getAsString();
                if(EnemyRole.trim().toUpperCase().equals(myLane.trim().toUpperCase())){ //trimmed and uppered
                    return x;
                }
            }
        }
        //all else fails
        return 0;
    }
}
