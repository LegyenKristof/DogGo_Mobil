package com.example.doggo_mobil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.doggo_mobil.databinding.ActivityMapsBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, OnMapClickListener {

    private GoogleMap mMap;
    private SharedPreferences sharedPreferences;
    private DrawerLayout drawerLayoutMaps;
    public static NavigationView navigationViewMaps;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private boolean newLocation = false;

    //    public static final String URL = "http://10.0.2.2:8000/api/";
    public static final String URL = "http://192.168.0.199:8000/api/";
    public static List<Location> locationList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        sharedPreferences = MapsActivity.this.getSharedPreferences("token", Context.MODE_PRIVATE);

        menuInit();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    private void menuInit() {
        navigationViewMaps = findViewById(R.id.navigationViewMaps);
        drawerLayoutMaps = findViewById(R.id.drawerLayoutMaps);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayoutMaps, R.string.nav_open, R.string.nav_close);
        drawerLayoutMaps.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(!sharedPreferences.getString("token", "").isEmpty()) {
            navigationViewMaps.getMenu().findItem(R.id.drawer_menu_login).setVisible(false);
            navigationViewMaps.getMenu().findItem(R.id.drawer_menu_register).setVisible(false);
            navigationViewMaps.getMenu().findItem(R.id.drawer_menu_new_location).setVisible(true);
            navigationViewMaps.getMenu().findItem(R.id.drawer_menu_profile).setVisible(true);
            navigationViewMaps.getMenu().findItem(R.id.drawer_menu_logout).setVisible(true);
        }

        navigationViewMaps.getMenu().findItem(R.id.drawer_menu_feedback).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                drawerLayoutMaps.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(MapsActivity.this, FeedbackActivity.class);
                startActivity(intent);
                return false;
            }
        });

        navigationViewMaps.getMenu().findItem(R.id.drawer_menu_new_location).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                drawerLayoutMaps.closeDrawer(GravityCompat.START);
                Toast.makeText(MapsActivity.this, "Jelöljön ki egy helyet a térképen", Toast.LENGTH_SHORT).show();
                newLocation = true;
                return false;
            }
        });

        navigationViewMaps.getMenu().findItem(R.id.drawer_menu_register).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                drawerLayoutMaps.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(MapsActivity.this, RegisterActivity.class);
                startActivity(intent);
                return false;
            }
        });

        navigationViewMaps.getMenu().findItem(R.id.drawer_menu_login).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                drawerLayoutMaps.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(MapsActivity.this, LoginActivity.class);
                startActivity(intent);
                return false;
            }
        });

        navigationViewMaps.getMenu().findItem(R.id.drawer_menu_profile).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                drawerLayoutMaps.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(MapsActivity.this, RegisterActivity.class);
                startActivity(intent);
                return false;
            }
        });

        navigationViewMaps.getMenu().findItem(R.id.drawer_menu_logout).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                drawerLayoutMaps.closeDrawer(GravityCompat.START);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("token");
                editor.apply();
                new CountDownTimer(200, 200) {

                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        navigationViewMaps.getMenu().findItem(R.id.drawer_menu_login).setVisible(true);
                        navigationViewMaps.getMenu().findItem(R.id.drawer_menu_register).setVisible(true);
                        navigationViewMaps.getMenu().findItem(R.id.drawer_menu_new_location).setVisible(false);
                        navigationViewMaps.getMenu().findItem(R.id.drawer_menu_profile).setVisible(false);
                        navigationViewMaps.getMenu().findItem(R.id.drawer_menu_logout).setVisible(false);
                    }

                }.start();
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(47.489017, 19.067812), 11));
        mMap.setMinZoomPreference(11);
        mMap.setMaxZoomPreference(20);
        LatLngBounds budapestBounds = new LatLngBounds(
                new LatLng(47.352892, 18.913316), // SW bounds
                new LatLng(47.624326, 19.358949)  // NE bounds
        );
        mMap.setLatLngBoundsForCameraTarget(budapestBounds);

        RequestTaskGetLocations requestTask = new RequestTaskGetLocations();
        requestTask.execute();

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                Intent intent = new Intent(MapsActivity.this, LocationActivity.class);
                intent.putExtra("id", (int) marker.getTag());
                startActivity(intent);
                return true;
            }
        });

        googleMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        if(newLocation) {
            newLocation = false;
            Intent intent = new Intent(MapsActivity.this, NewLocationActivity.class);
            intent.putExtra("lat", latLng.latitude + "");
            intent.putExtra("lng", latLng.longitude + "");
            startActivity(intent);
        }
    }

    private class RequestTaskGetLocations extends AsyncTask<Void, Void, Response> {

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
                Toast.makeText(MapsActivity.this, "Hiba történt a helyek betöltése közben.", Toast.LENGTH_SHORT).show();
            }
            else {
                Location[] locations = converter.fromJson(response.getContent(), Location[].class);
                locationList = new ArrayList<>();
                locationList.addAll(Arrays.asList(locations));
                for (Location l : locationList) {
                    LatLng koordinata = new LatLng(l.getLat(), l.getLng());
                    Marker marker = mMap.addMarker(new MarkerOptions().position(koordinata));
                    marker.setTag(l.getId());
                }
            }
        }
    }
}