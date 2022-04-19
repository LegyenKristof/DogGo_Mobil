package com.example.doggo_mobil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.Rating;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doggo_mobil.Location;
import com.example.doggo_mobil.LocationRating;
import com.example.doggo_mobil.R;
import com.example.doggo_mobil.RequestHandler;
import com.example.doggo_mobil.Response;
import com.example.doggo_mobil.databinding.RatingListItemBinding;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class LocationActivity extends AppCompatActivity {

    private TextView textViewLocationName;
    private TextView textViewLocationDescription;
    private int id;
    private ListView listViewRatings;
    public List<LocationRating> ratingList = new ArrayList<>();
    private Button buttonVissza, buttonRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        id = getIntent().getExtras().getInt("id");
        init();

        buttonVissza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buttonRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LocationActivity.this, RatingActivity.class);
                intent.putExtra("location_id", id + "");
                startActivity(intent);
            }
        });

        Optional<Location> location = MapsActivity.locationList.stream().filter(l -> l.getId() == id).findFirst();
        if(location == null) {
            Toast.makeText(LocationActivity.this, "Hiba történt a hely betöltése közben.", Toast.LENGTH_SHORT).show();
        }
        else {
            textViewLocationName.setText(location.get().getName());
            textViewLocationDescription.setText(location.get().getDescription());
        }

        RequestTaskGetRatings  ratingTask = new RequestTaskGetRatings();
        ratingTask.execute();
    }

    private void init() {
        textViewLocationName = findViewById(R.id.textViewLocationName);
        textViewLocationDescription = findViewById(R.id.textViewLocationDescription);
        listViewRatings = findViewById(R.id.listViewComments);
        buttonVissza = findViewById(R.id.buttonVissza);
        buttonRating = findViewById(R.id.buttonRating);
    }

    private class RatingAdapter extends BaseAdapter {
        LayoutInflater inflter;

        public RatingAdapter() {
            this.inflter = (LayoutInflater.from(LocationActivity.this));
        }

        @Override
        public int getCount() {
            return ratingList.size();
        }

        @Override
        public Object getItem(int i) {
            return ratingList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.rating_list_item, null);
            TextView name = view.findViewById(R.id.textViewRatingName);
            TextView stars = view.findViewById(R.id.textViewRatingStars);
            TextView comment = view.findViewById(R.id.textViewRatingComment);
            stars.setText("Értékelés: " + ratingList.get(i).getStars() + "");
            comment.setText(ratingList.get(i).getDescription() + "");
            name.setText(ratingList.get(i).getUsername() + "");
            return view;
        }
    }

    private class RequestTaskGetRatings extends AsyncTask<Void, Void, Response> {

        @Override
        protected Response doInBackground(Void... voids) {

            Response response = null;
            try {
                response = RequestHandler.get(MapsActivity.URL + "rating_by_location_with_username/" + id);
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

                RatingAdapter adapter = new RatingAdapter();
                listViewRatings.setAdapter(adapter);
            }

        }
    }

//    private class LocationTask extends AsyncTask<Void, Void, Response> {
//
//        @Override
//        protected Response doInBackground(Void... voids) {
//
//            Response response = null;
//            try {
//                response = RequestHandler.get(MainActivity.URL + "locations/" + id);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//
//            return response;
//        }
//
//        @Override
//        protected void onPostExecute(Response response) {
//            super.onPostExecute(response);
//            Gson converter = new Gson();
//            if (response == null || response.getResponseCode() >= 400){
//                Toast.makeText(LocationActivity.this, "Hiba történt a kérés feldolgozása során", Toast.LENGTH_SHORT).show();
//            }
//            else {
//                Location location = converter.fromJson(response.getContent(), Location.class);
//                textViewLocationName.setText(location.getName());
//                textViewLocationLatLng.setText("Lat: " + location.getLat() + "\nLng: " + location.getLng());
//                textViewLocationDescription.setText(location.getDescription());
//            }
//
//        }
//    }
}