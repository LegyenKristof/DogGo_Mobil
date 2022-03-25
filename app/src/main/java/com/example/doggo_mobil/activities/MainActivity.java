package com.example.doggo_mobil.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.doggo_mobil.Location;
import com.example.doggo_mobil.R;
import com.example.doggo_mobil.RequestHandler;
import com.example.doggo_mobil.Response;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ListView listViewLocations;
    private List<Location> locationList;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView nav_view;
    private SharedPreferences sharedPreferences;
//    public static final String URL = "http://10.0.2.2:8000/api/";
    public static final String URL = "http://192.168.0.199:8000/api/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = MainActivity.this.getSharedPreferences("token", Context.MODE_PRIVATE);
        init();

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, toolbar,
            R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        nav_view.setNavigationItemSelectedListener(this);

        LocationTask task = new LocationTask();
        task.execute();
    }

    private void init() {
        listViewLocations = findViewById(R.id.listViewLocations);
        locationList = new ArrayList<>();
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        nav_view = findViewById(R.id.nav_view);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_login:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
            case R.id.nav_register:
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                break;
            case R.id.nav_profile:
                String token = sharedPreferences.getString("token", "nincs");
                if (token == "nincs") {
                    Toast.makeText(MainActivity.this, "Ön nincs bejelentkezve.", Toast.LENGTH_SHORT).show();
                }
                else {
                    startActivity(new Intent(MainActivity.this, UserActivity.class));
                }
                break;
            case R.id.nav_logout:
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String token1 = sharedPreferences.getString("token", "nincs");
                if(token1 == "nincs") {
                    Toast.makeText(MainActivity.this, "Ön nincs bejelentkezve.", Toast.LENGTH_SHORT).show();
                }
                else {
                    editor.remove("token");
                    editor.apply();
                    Toast.makeText(MainActivity.this, "Sikeres kijelentkezés", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private class LocationTask extends AsyncTask<Void, Void, Response> {

        @Override
        protected Response doInBackground(Void... voids) {

            Response response = null;
            try {
                response = RequestHandler.get(URL + "locations");
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
                Toast.makeText(MainActivity.this, "Hiba történt a kérés feldolgozása során", Toast.LENGTH_SHORT).show();
            }
            else {
                Location[] locations = converter.fromJson(response.getContent(), Location[].class);
                locationList.clear();
                locationList.addAll(Arrays.asList(locations));
                ArrayAdapter<Location> arrayAdapter = new ArrayAdapter<Location>(MainActivity.this, R.layout.activity_locationlistitem, R.id.textViewListItem, locationList);
                listViewLocations.setAdapter(arrayAdapter);
                listViewLocations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Location location = locationList.get(i);
                        Intent intent = new Intent(MainActivity.this, LocationActivity.class);
                        intent.putExtra("id", location.getId());
                        startActivity(intent);
                    }
                });
            }

        }
    }
}