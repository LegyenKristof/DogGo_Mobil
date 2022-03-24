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
import android.widget.TextView;
import android.widget.Toast;

import com.example.doggo_mobil.Location;
import com.example.doggo_mobil.LocationRating;
import com.example.doggo_mobil.R;
import com.example.doggo_mobil.RequestHandler;
import com.example.doggo_mobil.Response;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LocationActivity extends AppCompatActivity {

    private TextView textViewLocationName;
    private TextView textViewLocationLatLng;
    private TextView textViewLocationDescription;
    private int id;
    private ListView listViewRatings;
    private List<LocationRating> ratingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        id = getIntent().getExtras().getInt("id");
        init();
        LocationTask locationTask = new LocationTask();
        locationTask.execute();
        RatingTask  ratingTask = new RatingTask();
        ratingTask.execute();
    }

    private void init() {
        textViewLocationName = findViewById(R.id.textViewLocationName);
        textViewLocationLatLng = findViewById(R.id.textViewLocationLatLng);
        textViewLocationDescription = findViewById(R.id.textViewLocationDescription);
        listViewRatings = findViewById(R.id.listViewComments);
        ratingList = new ArrayList<>();
    }

    private class RatingTask extends AsyncTask<Void, Void, Response> {

        @Override
        protected Response doInBackground(Void... voids) {

            Response response = null;
            try {
                response = RequestHandler.get(MainActivity.URL + "rating_by_location/" + id);
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
                Toast.makeText(LocationActivity.this, "Hiba történt a kérés feldolgozása során", Toast.LENGTH_SHORT).show();
            }
            else {
                LocationRating[] locationRatings = converter.fromJson(response.getContent(), LocationRating[].class);
                ratingList.clear();
                ratingList.addAll(Arrays.asList(locationRatings));
                ArrayAdapter<LocationRating> arrayAdapter = new ArrayAdapter<LocationRating>(
                        LocationActivity.this, R.layout.activity_ratinglistitem, R.id.textViewRatingListItemDescription,  ratingList);
                listViewRatings.setAdapter(arrayAdapter);
            }

        }
    }

    private class LocationTask extends AsyncTask<Void, Void, Response> {

        @Override
        protected Response doInBackground(Void... voids) {

            Response response = null;
            try {
                response = RequestHandler.get(MainActivity.URL + "locations/" + id);
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
                Toast.makeText(LocationActivity.this, "Hiba történt a kérés feldolgozása során", Toast.LENGTH_SHORT).show();
            }
            else {
                Location location = converter.fromJson(response.getContent(), Location.class);
                textViewLocationName.setText(location.getName());
                textViewLocationLatLng.setText("Lat: " + location.getLat() + "\nLng: " + location.getLng());
                textViewLocationDescription.setText(location.getDescription());
            }

        }
    }
}