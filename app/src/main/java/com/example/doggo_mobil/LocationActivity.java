package com.example.doggo_mobil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private TextView textViewLocationDescription, textViewLocationAverage;
    private int id;
    private ListView listViewRatings;
    public List<LocationRating> ratingList = new ArrayList<>();
    private Button buttonVissza, buttonRating;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        id = getIntent().getExtras().getInt("id");
        init();

        new RequestTaskGetAverageRating().execute();

        sharedPreferences = LocationActivity.this.getSharedPreferences("token", Context.MODE_PRIVATE);

        if(sharedPreferences.getString("token", "").isEmpty()) {
            buttonRating.setVisibility(View.GONE);
        }


        buttonVissza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buttonRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonRating.setEnabled(false);
                Intent intent = new Intent(LocationActivity.this, RatingActivity.class);
                intent.putExtra("location_id", id + "");
                startActivity(intent);
            }
        });

        Optional<Location> location = MapsActivity.locationList.stream().filter(l -> l.getId() == id).findFirst();
        if(location == null) {
            Toast.makeText(LocationActivity.this, "Hiba t??rt??nt a hely bet??lt??se k??zben.", Toast.LENGTH_SHORT).show();
        }
        else {
            textViewLocationName.setText(location.get().getName());
            textViewLocationDescription.setText(location.get().getDescription());
        }

        RequestTaskGetRatings  ratingTask = new RequestTaskGetRatings();
        ratingTask.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new RequestTaskGetRatings().execute();
    }

    private void init() {
        textViewLocationName = findViewById(R.id.textViewLocationName);
        textViewLocationDescription = findViewById(R.id.textViewLocationDescription);
        textViewLocationAverage = findViewById(R.id.textViewLocationAverage);
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
            stars.setText("??rt??kel??s: " + ratingList.get(i).getStars() + "");
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
            if (response == null){
                Toast.makeText(LocationActivity.this, "Hiba t??rt??nt a k??r??s feldolgoz??sa sor??n", Toast.LENGTH_SHORT).show();
            }
            else if(response.getResponseCode() >= 400) {
                try {
                    ErrorMessage errorMessage = converter.fromJson(response.getContent(), ErrorMessage.class);
                    Toast.makeText(LocationActivity.this, errorMessage.getMessage(), Toast.LENGTH_SHORT).show();
                }
                catch (Exception e) {
                    Toast.makeText(LocationActivity.this, response.getContent(), Toast.LENGTH_SHORT).show();
                }
            }
            else {
                LocationRating[] locationRatings = converter.fromJson(response.getContent(), LocationRating[].class);
                ratingList.clear();
                ratingList.addAll(Arrays.asList(locationRatings));

                RatingAdapter adapter = new RatingAdapter();
                listViewRatings.setAdapter(adapter);

                new RequestTaskGetUser().execute();
            }

        }
    }

    private class RequestTaskGetUser extends AsyncTask<Void, Void, Response> {

        @Override
        protected Response doInBackground(Void... voids) {

            Response response = null;
            try {
                response = RequestHandler.getBearer(MapsActivity.URL + "user", sharedPreferences.getString("token", ""));
            } catch (IOException e) {
                e.printStackTrace();
            }


            return response;
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            Gson converter = new Gson();
            if (response == null){
                Toast.makeText(LocationActivity.this, "Hiba t??rt??nt a felhaszn??l?? bet??lt??se sor??n", Toast.LENGTH_SHORT).show();
            }
            else if(response.getResponseCode() >= 400) {
                try {
                    ErrorMessage errorMessage = converter.fromJson(response.getContent(), ErrorMessage.class);
                    Toast.makeText(LocationActivity.this, errorMessage.getMessage(), Toast.LENGTH_SHORT).show();
                }
                catch (Exception e) {
                    Toast.makeText(LocationActivity.this, response.getContent(), Toast.LENGTH_SHORT).show();
                }
            }
            else {
                User user = converter.fromJson(response.getContent(), User.class);
                boolean alreadyRated = false;
                for(LocationRating r : ratingList) {
                    if(r.getUser_id() == user.getId()) {
                        alreadyRated = true;
                        break;
                    }
                }
                if(alreadyRated) {
                    buttonRating.setText("M??r ??rt??kelte ezt a helyet");
                }
                else {
                    buttonRating.setEnabled(true);
                }
            }
        }
    }

    private class RequestTaskGetAverageRating extends AsyncTask<Void, Void, Response> {

        @Override
        protected Response doInBackground(Void... voids) {

            Response response = null;
            try {
                response = RequestHandler.get(MapsActivity.URL + "location_avgrating/" + id);
            } catch (IOException e) {
                e.printStackTrace();
            }


            return response;
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            Gson converter = new Gson();
            if (response == null){
                Toast.makeText(LocationActivity.this, "Hiba t??rt??nt a k??r??s feldolgoz??sa sor??n", Toast.LENGTH_SHORT).show();
            }
            else if(response.getResponseCode() >= 400) {
                try {
                    ErrorMessage errorMessage = converter.fromJson(response.getContent(), ErrorMessage.class);
                    Toast.makeText(LocationActivity.this, errorMessage.getMessage(), Toast.LENGTH_SHORT).show();
                }
                catch (Exception e) {
                    Toast.makeText(LocationActivity.this, response.getContent(), Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Location location = converter.fromJson(response.getContent(), Location.class);
                textViewLocationAverage.setText("??tlagos ??rt??kel??s: " + location.getAtlag());
            }

        }
    }
}