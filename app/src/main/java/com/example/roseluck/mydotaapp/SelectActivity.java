package com.example.roseluck.mydotaapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Roseluck on 10/26/2017.
 */

public class SelectActivity extends AppCompatActivity {

    private ListView mainListView ;
    private ArrayAdapter<String> listAdapter ;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_activity);

        // Find the ListView resource.
        mainListView = (ListView) findViewById( R.id.mainListView );
        // Create and populate a List of planet names.
        String[] planets = new String[] {"Champions!", "News!", "Summoners!"};
        ArrayList<String> planetList = new ArrayList<String>();
        planetList.addAll( Arrays.asList(planets) );

        // Create ArrayAdapter using the planet list.
        listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, planetList);


        mainListView.setAdapter( listAdapter );

        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                Intent i = null;
                if(position == 0){
                    i=new Intent(SelectActivity.this, MainActivity.class);
                    startActivity(i);
                }
                if(position == 1){
                    i=new Intent(SelectActivity.this, ChampionPage.class);
                    startActivity(i);
                }
                if(position == 2){
                    i=new Intent(SelectActivity.this, SummonerSearch.class);
                    startActivity(i);
                }

                //i.putExtra("ChampionSelected", champions.get(position));


            }
        });
    }



}
