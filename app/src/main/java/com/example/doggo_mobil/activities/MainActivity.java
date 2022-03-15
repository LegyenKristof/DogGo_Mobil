package com.example.doggo_mobil.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.doggo_mobil.R;
import com.example.doggo_mobil.RequestHandler;
import com.example.doggo_mobil.Response;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private TextView textViewLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        RequestTask task = new RequestTask();
        task.execute();
    }

    private void init() {
        textViewLocations = findViewById(R.id.textViewLocations);
    }

    private class RequestTask extends AsyncTask<Void, Void, Response> {

        @Override
        protected Response doInBackground(Void... voids) {

            Response response = new Response(404, "error xd");
            try {
               response = RequestHandler.get("http://10.0.2.2:8000/api/locations");
            } catch (IOException e) {
                e.printStackTrace();
                for (int i = 0; i < 100; i++) {
                    System.out.println("AAAAAAAAAAAAAAAAAAAAAAA");
                }
            }


            return response;
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            textViewLocations.setText(response.getContent() + "");
        }
    }
}