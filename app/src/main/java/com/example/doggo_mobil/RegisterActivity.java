package com.example.doggo_mobil;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.doggo_mobil.Location;
import com.example.doggo_mobil.R;
import com.example.doggo_mobil.RequestHandler;
import com.example.doggo_mobil.Response;
import com.example.doggo_mobil.User;
import com.google.gson.Gson;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextEmail, editTextPassword;
    private Button buttonRegister, buttonCancel;
    private String username, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = editTextUsername.getText().toString().trim();
                email = editTextEmail.getText().toString().trim();
                password = editTextPassword.getText().toString();
                if(username.isEmpty() || username.length() < 5 || username.length() > 20) {
                    Toast.makeText(RegisterActivity.this, "A felhasználónév minimum 5, maximum 20 karakterből állhat!", Toast.LENGTH_SHORT).show();
                }
                else if(email.isEmpty() || email.length()  > 255) {
                    Toast.makeText(RegisterActivity.this, "Az email nem lehet üres, maximum 255 karakterből állhat!", Toast.LENGTH_SHORT).show();
                }
                else if(password.isEmpty() || password.length() < 8) {
                    Toast.makeText(RegisterActivity.this, "A jelszó minimum 8 karakterből állhat!", Toast.LENGTH_SHORT).show();
                }
                else {
                    new RequestTaskRegister().execute();
                }
            }
        });
    }

    private void init() {
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        buttonCancel = findViewById(R.id.buttonCancel);
    }

    private class RequestTaskRegister extends AsyncTask<Void, Void, Response> {

        @Override
        protected Response doInBackground(Void... voids) {

            Response response = null;
            try {
                String data = String.format("{\"username\":\"%s\",\"email\":\"%s\",\"password\":\"%s\"}", username, email, password);
                response = RequestHandler.post(MapsActivity.URL + "users", data);
            } catch (IOException e) {
                e.printStackTrace();
            }


            return response;
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            if (response == null || response.getResponseCode() >= 400){
                Toast.makeText(RegisterActivity.this, "Hiba történt a regisztráció során", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(RegisterActivity.this, "Sikeres regisztráció!", Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }
}