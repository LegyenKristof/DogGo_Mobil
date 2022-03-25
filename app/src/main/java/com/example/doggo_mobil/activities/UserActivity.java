package com.example.doggo_mobil.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doggo_mobil.R;
import com.example.doggo_mobil.RequestHandler;
import com.example.doggo_mobil.Response;
import com.example.doggo_mobil.Token;
import com.example.doggo_mobil.User;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.io.IOException;

public class UserActivity extends AppCompatActivity {

    private TextView textViewUsername, textViewEmail;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        sharedPreferences = UserActivity.this.getSharedPreferences("token", Context.MODE_PRIVATE);
        init();
        new UserTask().execute();
    }

    private void init() {
        textViewUsername = findViewById(R.id.textViewUsername);
        textViewEmail = findViewById(R.id.textViewEmail);
    }

    private class UserTask extends AsyncTask<Void, Void, Response> {

        @Override
        protected Response doInBackground(Void... voids) {

            Response response = null;
            try {
                String data = sharedPreferences.getString("token", "nincs");
                response = RequestHandler.getBearer(MainActivity.URL + "user", data);
            } catch (IOException e) {
                e.printStackTrace();
            }


            return response;
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            if (response == null || response.getResponseCode() >= 400){
                Toast.makeText(UserActivity.this, "Hiba történt a kérés feldolgozása során", Toast.LENGTH_SHORT).show();
            }
            else {
                try {
                    Gson gson = new Gson();
                    User user = gson.fromJson(response.getContent(), User.class);
                    textViewUsername.setText(user.getUsername());
                    textViewEmail.setText(user.getEmail());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
}