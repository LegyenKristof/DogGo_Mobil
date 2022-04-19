package com.example.doggo_mobil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

public class RatingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Button buttonRatingSend, buttonRatingCancel;
    private Spinner spinnerStars;
    private EditText editTextRating;
    private SharedPreferences sharedPreferences;

    private String description, stars;
    private String location_id;
    private int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        location_id = getIntent().getStringExtra("location_id");

        init();

        sharedPreferences = RatingActivity.this.getSharedPreferences("token", Context.MODE_PRIVATE);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.stars_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStars.setAdapter(adapter);
        spinnerStars.setOnItemSelectedListener(this);

        buttonRatingCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buttonRatingSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                description = editTextRating.getText().toString();
                if(description.length() > 255) {
                    Toast.makeText(RatingActivity.this, "Az értékelés maximum 255 karakterből állhat", Toast.LENGTH_SHORT).show();
                }
                else {
                    new RequestTaskGetUser().execute();
                }
            }
        });
    }

    private void init() {
        buttonRatingSend = findViewById(R.id.buttonRatingSend);
        buttonRatingCancel = findViewById(R.id.buttonRatingCancel);
        spinnerStars = findViewById(R.id.spinnerStars);
        editTextRating = findViewById(R.id.editTextRating);
        stars = "5";
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        stars = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        stars = "5";
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
                Toast.makeText(RatingActivity.this, "Hiba történt az értékelés során", Toast.LENGTH_SHORT).show();
            }
            else if(response.getResponseCode() >= 400) {
                ErrorMessage errorMessage = converter.fromJson(response.getContent(), ErrorMessage.class);
                Toast.makeText(RatingActivity.this, errorMessage.getMessage(), Toast.LENGTH_SHORT).show();
            }
            else {
                User user = converter.fromJson(response.getContent(), User.class);
                user_id = user.getId();
                new RequestTaskRating().execute();
            }
        }
    }

    private class RequestTaskRating extends AsyncTask<Void, Void, Response> {

        @Override
        protected Response doInBackground(Void... voids) {

            Response response = null;
            try {
                String data = String.format("{\"stars\":\"%s\",\"description\":\"%s\",\"user_id\":\"%d\",\"location_id\":\"%s\"}", stars, description, user_id, location_id);
                response = RequestHandler.post(MapsActivity.URL + "ratings", data);
            } catch (IOException e) {
                e.printStackTrace();
            }


            return response;
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            if (response == null || response.getResponseCode() >= 400){
                Toast.makeText(RatingActivity.this, "Hiba történt az értékelés hozzáadása során", Toast.LENGTH_SHORT).show();
                System.out.println(response.getContent());
            }
            else {
                Toast.makeText(RatingActivity.this, "Értékelés hozzáadása sikeres", Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }
}