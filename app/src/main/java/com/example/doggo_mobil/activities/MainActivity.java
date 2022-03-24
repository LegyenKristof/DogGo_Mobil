package com.example.doggo_mobil.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.doggo_mobil.Location;
import com.example.doggo_mobil.R;
import com.example.doggo_mobil.RequestHandler;
import com.example.doggo_mobil.Response;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listViewLocations;
    private List<Location> locationList;
    public static final String URL = "http://192.168.0.199:8000/api/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        RequestTask task = new RequestTask();
        task.execute();
    }

    private void init() {
        listViewLocations = findViewById(R.id.listViewLocations);
        locationList = new ArrayList<>();
    }

    private class RequestTask extends AsyncTask<Void, Void, Response> {

        @Override
        protected Response doInBackground(Void... voids) {

            Response response = null;
            try {
//               response = RequestHandler.get("http://10.0.2.2:8000/api/locations");
                response = RequestHandler.get(URL + "locations");
            } catch (IOException e) {
                e.printStackTrace();
            }


            return response;
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            Gson converter = new Gson();
            if (response == null || response.getResponseCode() >= 400){
                Toast.makeText(MainActivity.this, "Hiba történt a kérés feldolgozása során", Toast.LENGTH_SHORT).show();
            }
            else {
                Location[] locations = converter.fromJson(response.getContent(), Location[].class);
                locationList.clear();
                locationList.addAll(Arrays.asList(locations));
                ArrayAdapter<Location> arrayAdapter = new ArrayAdapter<Location>(MainActivity.this, R.layout.activity_listitem, R.id.textViewListItem, locationList);
                listViewLocations.setAdapter(arrayAdapter);
                listViewLocations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Location location = locationList.get(i);
                        Intent intent = new Intent(MainActivity.this, LocationActivity.class);
                        intent.putExtra("id", location.getId());
                        startActivity(intent);
                    }
                });
            }

        }
    }
}