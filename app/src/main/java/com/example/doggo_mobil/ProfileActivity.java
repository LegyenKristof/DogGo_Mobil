package com.example.doggo_mobil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    private TextView textViewUsername, textViewEmail;
    private EditText editTextEditUsername, editTextEditEmail;
    private Button buttonProfileCancel, buttonProfileEdit;
    private SharedPreferences sharedPreferences;


    private boolean editing = false;
    private User user;
    private String username, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
        sharedPreferences = ProfileActivity.this.getSharedPreferences("token", Context.MODE_PRIVATE);
        new RequestTaskGetUser().execute();

        buttonProfileCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editing) {
                    finish();
                }
                else {
                    editing = false;
                    editTextEditUsername.setVisibility(View.GONE);
                    editTextEditEmail.setVisibility(View.GONE);
                    textViewUsername.setVisibility(View.VISIBLE);
                    textViewEmail.setVisibility(View.VISIBLE);
                    buttonProfileCancel.setText("Vissza");
                    buttonProfileEdit.setText("Módosítás");
                }
            }
        });

        buttonProfileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!editing) {
                    editing = true;
                    editTextEditUsername.setVisibility(View.VISIBLE);
                    editTextEditEmail.setVisibility(View.VISIBLE);
                    textViewUsername.setVisibility(View.GONE);
                    textViewEmail.setVisibility(View.GONE);
                    buttonProfileCancel.setText("Mégse");
                    buttonProfileEdit.setText("Mentés");
                }
                else {
                    username = editTextEditUsername.getText().toString().trim();
                    email = editTextEditEmail.getText().toString().trim();
                    if(username.isEmpty() || username.length() < 5 || username.length() > 20) {
                        Toast.makeText(ProfileActivity.this, "A felhasználónév minimum 5, maximum 20 karakterből állhat", Toast.LENGTH_SHORT).show();
                    }
                    else if(email.isEmpty() || email.length()  > 255) {
                        Toast.makeText(ProfileActivity.this, "Az email nem lehet üres, maximum 255 karakterből állhat", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        new RequestTaskEditUser().execute();
                    }
                }
            }
        });
    }

    private void init() {
        textViewUsername = findViewById(R.id.textViewUsername);
        textViewEmail = findViewById(R.id.textViewEmail);
        editTextEditUsername = findViewById(R.id.editTextEditUsername);
        editTextEditEmail = findViewById(R.id.editTextEditEmail);
        buttonProfileCancel = findViewById(R.id.buttonProfileCancel);
        buttonProfileEdit = findViewById(R.id.buttonProfileEdit);
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
                Toast.makeText(ProfileActivity.this, "Hiba történt a felhasználó betöltése során", Toast.LENGTH_SHORT).show();
            }
            else if(response.getResponseCode() >= 400) {
                try {
                    ErrorMessage errorMessage = converter.fromJson(response.getContent(), ErrorMessage.class);
                    Toast.makeText(ProfileActivity.this, errorMessage.getMessage(), Toast.LENGTH_SHORT).show();
                }
                catch (Exception e) {
                    Toast.makeText(ProfileActivity.this, response.getContent(), Toast.LENGTH_SHORT).show();
                }
            }
            else {
                user = converter.fromJson(response.getContent(), User.class);

                textViewEmail.setText(user.getEmail());
                textViewUsername.setText(user.getUsername());
                editTextEditUsername.setText(user.getUsername());
                editTextEditEmail.setText(user.getEmail());
            }
        }
    }

    private class RequestTaskEditUser extends AsyncTask<Void, Void, Response> {

        @Override
        protected Response doInBackground(Void... voids) {

            Response response = null;
            try {
                String data = String.format("{\"username\":\"%s\",\"email\":\"%s\"}", username, email);
                response = RequestHandler.put(MapsActivity.URL + "users/" + user.getId(), data);
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
                Toast.makeText(ProfileActivity.this, "Hiba történt a módosítás során", Toast.LENGTH_SHORT).show();
            }
            else if(response.getContent().contains("Duplicate entry")) {
                Toast.makeText(ProfileActivity.this, "The username has already been taken.", Toast.LENGTH_SHORT).show();
            }
            else if(response.getResponseCode() >= 400) {
                try {
                    ErrorMessage errorMessage = converter.fromJson(response.getContent(), ErrorMessage.class);
                    Toast.makeText(ProfileActivity.this, errorMessage.getMessage(), Toast.LENGTH_SHORT).show();
                }
                catch (Exception e) {
                    Toast.makeText(ProfileActivity.this, response.getContent(), Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(ProfileActivity.this, "Sikeres módosítás", Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }
}