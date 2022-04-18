package com.example.doggo_mobil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.doggo_mobil.R;
import com.example.doggo_mobil.RequestHandler;
import com.example.doggo_mobil.Response;
import com.example.doggo_mobil.Token;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin, buttonCancel;
    private String username, password;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = LoginActivity.this.getSharedPreferences("token", Context.MODE_PRIVATE);
        init();

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = editTextUsername.getText().toString().trim();
                password = editTextPassword.getText().toString();
                if(username.isEmpty() || username.length() < 5 || username.length() > 20) {
                    Toast.makeText(LoginActivity.this, "A felhasználónév minimum 5, maximum 20 karakterből állhat!", Toast.LENGTH_SHORT).show();
                }
                else if(password.isEmpty() || password.length() < 8) {
                    Toast.makeText(LoginActivity.this, "A jelszó minimum 8 karakterből állhat!", Toast.LENGTH_SHORT).show();
                }
                else {
                    new RequestTaskLogin().execute();
                }
            }
        });
    }

    private void init() {
        editTextUsername = findViewById(R.id.editTextLoginUsername);
        editTextPassword = findViewById(R.id.editTextLoginPassword);
        buttonLogin = findViewById(R.id.buttonLoginLogin);
        buttonCancel = findViewById(R.id.buttonLoginCancel);

    }

    private class RequestTaskLogin extends AsyncTask<Void, Void, Response> {

        @Override
        protected Response doInBackground(Void... voids) {

            Response response = null;
            try {
                String data = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
                response = RequestHandler.post(MapsActivity.URL + "login", data);
            } catch (IOException e) {
                e.printStackTrace();
            }


            return response;
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            if (response == null){
                Toast.makeText(LoginActivity.this, "Hiba történt a bejelentkezés során", Toast.LENGTH_SHORT).show();
            }
            else if (response.getResponseCode() >= 400) {
//                Toast.makeText(LoginActivity.this, response.getContent(), Toast.LENGTH_SHORT).show();
                Toast.makeText(LoginActivity.this, "Hiba történt a bejelentkezés során", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(LoginActivity.this, "Sikeres bejelentkezés!", Toast.LENGTH_SHORT).show();
                try {
                    Gson gson = new Gson();
                    Token token = gson.fromJson(response.getContent(), Token.class);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token", token.getToken());
                    editor.apply();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MapsActivity.navigationViewMaps.getMenu().findItem(R.id.drawer_menu_login).setVisible(false);
                MapsActivity.navigationViewMaps.getMenu().findItem(R.id.drawer_menu_register).setVisible(false);
                MapsActivity.navigationViewMaps.getMenu().findItem(R.id.drawer_menu_profile).setVisible(true);
                MapsActivity.navigationViewMaps.getMenu().findItem(R.id.drawer_menu_logout).setVisible(true);
                finish();
            }
        }
    }
}