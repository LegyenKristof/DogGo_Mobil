package com.example.doggo_mobil.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doggo_mobil.Location;
import com.example.doggo_mobil.R;
import com.example.doggo_mobil.RequestHandler;
import com.example.doggo_mobil.Response;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Arrays;

public class LocationActivity extends AppCompatActivity {

    private TextView textViewLocationName;
    private TextView textViewLocationLatLng;
    private TextView textViewLocationDescription;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        id = getIntent().getExtras().getInt("id");
        init();
        RequestTask requestTask = new RequestTask();
        requestTask.execute();
    }

    private void init() {
        textViewLocationName = findViewById(R.id.textViewLocationName);
        textViewLocationLatLng = findViewById(R.id.textViewLocationLatLng);
        textViewLocationDescription = findViewById(R.id.textViewLocationDescription);
    }

    private class RequestTask extends AsyncTask<Void, Void, Response> {

        @Override
        protected Response doInBackground(Void... voids) {

            Response response = null;
            try {
//               response = RequestHandler.get("http://10.0.2.2:8000/api/locations");
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