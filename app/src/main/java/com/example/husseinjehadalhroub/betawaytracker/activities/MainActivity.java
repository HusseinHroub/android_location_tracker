package com.example.husseinjehadalhroub.betawaytracker.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.husseinjehadalhroub.betawaytracker.data.DBHelperReaderOnly;
import com.example.husseinjehadalhroub.betawaytracker.R;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    DBHelperReaderOnly db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DBHelperReaderOnly(this);
        ListView listView = findViewById(R.id.listView);
        ArrayList<String> data = null;
        try {
            data = new GetTripsData().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ReaderMapsActivity.class);
                intent.putExtra("triptable", position);
                System.out.println("Position was =" + position);
                startActivity(intent);

            }
        });
    }




    private class GetTripsData extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            return db.readingTrips();
        }
    }
}
