package ca.bcit.bloomapp;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.DrawFilter;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapsActivity extends FragmentActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_kwanzan:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new KwanzanFragment()).commit();
                break;
            case R.id.nav_akebono:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AkebonoFragment()).commit();
                break;
            case R.id.nav_ukon:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new UkonFragment()).commit();
                break;
            case R.id.nav_map:
                Intent i = new Intent(this, MapsActivity.class);
                startActivity(i);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private static final String[] INITIAL_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CONTACTS
    };

    private static final int INITIAL_REQUEST=1337;

    private GoogleMap mMap;

    private DrawerLayout drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // show hamburger with toggles
        Toolbar toolbar = findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar); -- this is supposed to make custom toolbar appear, but not working

        drawer = findViewById(R.id.drawer);

        // creating references to links on hamburger
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (!canAccessLocation()) {
            requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
        }

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
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        // counts how many trees on the map. (debugging purposes)
        int treeCounter = 0;

        Intent intent = getIntent();
        float[] longitude = intent.getExtras().getFloatArray("longitude");
        float[] latitude = intent.getExtras().getFloatArray("latitude");

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney")
//        .icon(BitmapDescriptorFactory.fromAsset("kwanzan-icon-png")));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        for (int i = 0; i < longitude.length; i++) {
            setTreeMarker(latitude[i], longitude[i], i);
            treeCounter++;
        }

        LatLng vancouver = new LatLng(-123.03911, 49.257458);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(vancouver));

        System.out.printf("Showing %d trees.", treeCounter);

        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

    }

    public void setTreeMarker(float latitude, float longitude, int treenum) {
        LatLng tree = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(tree).title("Marker at tree" + treenum)
                .icon(BitmapDescriptorFactory.fromAsset("kwanzan-icon-png")).alpha(0.6f));
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    // Permissions

    private boolean canAccessLocation() {
        return(hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    private boolean hasPermission(String perm) {
        return(PackageManager.PERMISSION_GRANTED==checkSelfPermission(perm));
    }
}
