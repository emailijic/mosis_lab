package ema.mosis.elfak.rs;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class MyPlacesMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    static int NEW_PLACE = 1;
    public static final int SHOW_MAP = 0;
    public static final int CENTER_PLACE_ON_MAP = 1;
    public static final int SELECT_COORDINATES = 2;

    private int state = 0;
    private boolean selCoorsEnabled = false;
    private LatLng placeLoc;

    DatabaseReference database;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSIONS_REQUEST_CODE = 1234;

    private Boolean mLocationPermissionsGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Intent mapIntent = getIntent();
            Bundle mapBundle = mapIntent.getExtras();
            if (mapBundle != null) {
                state = mapBundle.getInt("state");
                if (state == CENTER_PLACE_ON_MAP) {
                    String placeLat = mapBundle.getString("lat");
                    String placeLon = mapBundle.getString("lon");
                    placeLoc = new LatLng(Double.parseDouble(placeLat), Double.parseDouble(placeLon));
                }
            }
        } catch(Exception e) {
            Log.d("Error", "Error reading state");
        }
        setContentView(R.layout.activity_my_places_maps);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        database = FirebaseDatabase.getInstance().getReference().child("my-places");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (state != SELECT_COORDINATES) {
            fab.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    Intent i=new Intent(MyPlacesMapsActivity.this,EditMyPlaceActivity.class);
                    startActivityForResult(i, NEW_PLACE);
                }
            });
        } else {
            ViewGroup layout = (ViewGroup) fab.getParent();
            if (null != layout)
                layout.removeView(fab);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (state == SELECT_COORDINATES && !selCoorsEnabled) {
            menu.add(0, 1, 1, "Select Coordinates");
            menu.add(0, 2, 2, "Cancel");
            menu.getItem(0).setIcon(R.drawable.ic_action_set_coordinates);
            menu.getItem(0).getIcon().setAlpha(255);
            menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            return super.onCreateOptionsMenu(menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_my_places_maps, menu);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (state == SELECT_COORDINATES && !selCoorsEnabled) {
            if (id == 1) {
                selCoorsEnabled = true;
                Toast.makeText(this, "Select coordinates", Toast.LENGTH_SHORT).show();
                item.setEnabled(false);
                item.getIcon().setAlpha(64);
            } else if (id == 2) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        } else {
            if (id == R.id.new_place_item) {
                Intent i = new Intent(this, EditMyPlaceActivity.class);
                startActivityForResult(i, NEW_PLACE);
            } else if (id == R.id.about_item) {
                Intent i = new Intent(this, About.class);
                startActivity(i);
            } else if (id == android.R.id.home) {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
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
    static final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ACCESS_FINE_LOCATION);
        }   else {
            if (state == SHOW_MAP)
                mMap.setMyLocationEnabled(true);
            else if (state == CENTER_PLACE_ON_MAP)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLoc, 15));
            else
                setOnMapClickListener();
            addMyPlaceMarkers();
        }
    }

    private HashMap<Marker, Integer> markerPlaceIdMap;

    private void addMyPlaceMarkers() {
        ArrayList<MyPlace> places = MyPlacesData.getInstance().getMyPlaces();
        markerPlaceIdMap = new HashMap<Marker, Integer>((int)((double)places.size()*1.2));
        for(int i=0;i<places.size();i++) {
            MyPlace place = places.get(i);
            String lat = place.getLatitude();
            String lon = place.getLongitude();
            LatLng loc = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(loc);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_map_place));
            markerOptions.title(place.getName());
            Marker marker = mMap.addMarker(markerOptions);
            markerPlaceIdMap.put(marker, i);
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent intent = new Intent(MyPlacesMapsActivity.this, ViewMyPlacesActivity.class);
                int i = markerPlaceIdMap.get(marker);
                intent.putExtra("position", i);
                startActivity(intent);
                return true;
            }
        });
    }

    private void setOnMapClickListener() {
        if (mMap != null) {
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if (state == SELECT_COORDINATES && selCoorsEnabled) {
                        String lon = Double.toString(latLng.longitude);
                        String lat = Double.toString(latLng.latitude);
                        Intent locationIntent = new Intent();
                        locationIntent.putExtra("lon", lon);
                        locationIntent.putExtra("lat", lat);
                        setResult(Activity.RESULT_OK, locationIntent);
                        finish();
                    }
                }
            });
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (state == SHOW_MAP)
                        mMap.setMyLocationEnabled(true);
                    else if (state == CENTER_PLACE_ON_MAP)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLoc, 15));

                    else
                        setOnMapClickListener();
                    addMyPlaceMarkers();
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        try
        {
            if (resultCode == Activity.RESULT_OK)
            {
                addMyPlaceMarkers();
            }
        } catch (Exception e)
        {
        }

    }
}