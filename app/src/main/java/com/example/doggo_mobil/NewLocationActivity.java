package com.example.doggo_mobil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

public class NewLocationActivity extends AppCompatActivity {

    private Button buttonNewLocationCancel, buttonNewLocationSend;
    private EditText editTextNewLocationName, editTextNewLocationDescription;
    private SharedPreferences sharedPreferences;


    private String name, description, lat, lng;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_location);
        init();
        Intent intent = getIntent();
        lat = intent.getStringExtra("lat");
        lng = intent.getStringExtra("lng");
        sharedPreferences = NewLocationActivity.this.getSharedPreferences("token", Context.MODE_PRIVATE);

        buttonNewLocationCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buttonNewLocationSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = editTextNewLocationName.getText().toString();
                description = editTextNewLocationDescription.getText().toString();
                if(name.isEmpty() || name.length() > 40 || name.length() < 5) {
                    Toast.makeText(NewLocationActivity.this, "A hely neve minimum 5, maximum 40 karakterből állhat", Toast.LENGTH_SHORT).show();
                }
                else if(name.length() > 255) {
                    Toast.makeText(NewLocationActivity.this, "A hely leírása maximum 255 karakterből állhat", Toast.LENGTH_SHORT).show();
                }
                else {
                    new RequestTaskGetUser().execute();
                }
            }
        });
    }

    private void init() {
        buttonNewLocationCancel = findViewById(R.id.buttonNewLocationCancel);
        buttonNewLocationSend = findViewById(R.id.buttonNewLocationSend);
        editTextNewLocationName = findViewById(R.id.editTextNewLocationName);
        editTextNewLocationDescription = findViewById(R.id.editTextNewLocationDescription);
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
            if (response == null || response.getResponseCode() >= 400){
                Toast.makeText(NewLocationActivity.this, "Hiba történt a hely hozzáadása során", Toast.LENGTH_SHORT).show();
            }
            else {
                User user = converter.fromJson(response.getContent(), User.class);
                id = user.getId();
                new RequestTaskNewLocation().execute();
            }
        }
    }

    private class RequestTaskNewLocation extends AsyncTask<Void, Void, Response> {

        @Override
        protected Response doInBackground(Void... voids) {

            Response response = null;
            try {
                String data = String.format("{\"name\":\"%s\",\"description\":\"%s\",\"lat\":\"%s\",\"lng\":\"%s\",\"user_id\":\"%d\"}", name, description, lat, lng, id);
                response = RequestHandler.post(MapsActivity.URL + "locations", data);
            } catch (IOException e) {
                e.printStackTrace();
            }


            return response;
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            if (response == null || response.getResponseCode() >= 400){
                Toast.makeText(NewLocationActivity.this, "Hiba történt a hely hozzáadása során", Toast.LENGTH_SHORT).show();
                System.out.println(response.getContent());
            }
            else {
                Toast.makeText(NewLocationActivity.this, "Hely hozzáadása sikeres", Toast.LENGTH_SHORT).show();
                Toast.makeText(NewLocationActivity.this, "A hely látható lesz amint egy adminisztrátor megerősíti a helyet", Toast.LENGTH_LONG).show();
                finish();
            }

        }
    }
}