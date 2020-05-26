package se.astacus.smartsoundswitch_raju;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.libraries.places.api.*;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
/*import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;*/

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ACMapAct extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, PlaceSelectionListener {
    //FragmentActivity
    private GoogleMap mMap;
    private final int m_GPSLOCATION_REQUEST_CODE = 100;
    private int AUTOCOMPLETE_REQUEST_CODE = 1;
    private Marker m_markerOnMap;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.e("ACMapAct", "OnCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_acmaps);

        i.helper.changeStatusBarColor(ACMapAct.this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        TextView txt_set = (TextView)findViewById(R.id.id_txtLeft);
        TextView txt_heading = (TextView)findViewById(R.id.id_txtHeading);
        TextView txt_gps = (TextView)findViewById(R.id.id_txtRight);
        TextView txt_message = (TextView)findViewById(R.id.id_txtMessage);

        txt_set.setText("Set Location");
        txt_heading.setText("Map");
        txt_gps.setText("GPS");

        //For Places Sdk
        String apiKey = "AIzaSyDmDjKClQgwce7xhY869OQhXGuIAEGtFY8";
        Places.initialize(getApplicationContext(), apiKey);
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

        final AutocompleteSupportFragment autocompleteSupportFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteSupportFragment.setPlaceFields(fields);
        autocompleteSupportFragment.setOnPlaceSelectedListener(this);
    }

    public void onClick(View view)
    {
        int id = view.getId();

        if (id == R.id.id_txtRight)
        {
            ACGPSTracker tracker = new ACGPSTracker(this);
            Location pLocation = tracker.getLocation();

            if (pLocation != null)
            {
                LatLng pLatlng = new LatLng(pLocation.getLatitude(), pLocation.getLongitude());
                updateMarkerLocation(pLatlng, "Marker");
                moveToLocation(pLatlng);

                i.helper.getAddress(pLocation.getLatitude(), pLocation.getLongitude(), this);
            }
        }
        else if (id == R.id.id_txtLeft)
        {
            i.helper.locationName.setText(i.helper.getLocationName(i.helper.completeAddress));
            finish();
        }
        else if ((id == R.id.txt_acmap_normal) || (id == R.id.txt_acmap_satellite) || (id == R.id.txt_acmap_hybrid))
        {
            TextView txt_normal = (TextView)findViewById(R.id.txt_acmap_normal);
            TextView txt_satellite = (TextView)findViewById(R.id.txt_acmap_satellite);
            TextView txt_hybrid = (TextView)findViewById(R.id.txt_acmap_hybrid);

            setDeaultColors(txt_normal);
            setDeaultColors(txt_satellite);
            setDeaultColors(txt_hybrid);

            if(id == R.id.txt_acmap_normal)
            {
                this.mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                setHiliteColors(txt_normal);
            }
            else if(id == R.id.txt_acmap_satellite)
            {
                this.mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                setHiliteColors(txt_satellite);
            }
            else if(id == R.id.txt_acmap_hybrid)
            {
                this.mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                setHiliteColors(txt_hybrid);
            }
        }
    }

    private void setDeaultColors(TextView pTextView)
    {
        pTextView.setTextColor(getResources().getColor(R.color.black));
        pTextView.setTypeface(pTextView.getTypeface(), Typeface.NORMAL);
    }

    private void setHiliteColors(TextView pTextView)
    {
        //pTextView.setBackgroundColor(getResources().getColor(R.color.black));
        pTextView.setTextColor(getResources().getColor(R.color.blue));
        pTextView.setTypeface(pTextView.getTypeface(), Typeface.BOLD);
    }

    private void moveToLocation(LatLng currentLocation)
    {
        Log.e("currentLocation", String.valueOf(currentLocation));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,18));
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        Log.e("ACMapAct", "onMapReady");
        mMap = googleMap;
        mMap.setOnMapClickListener(this);

        this.m_markerOnMap = mMap.addMarker(new MarkerOptions().position(new LatLng(17.7133, 83.3151)).title("Marker"));

        Intent pIntent = getIntent();
        String pAddOrEdit = (String) pIntent.getStringExtra("AddOrEdit");

        if (pAddOrEdit.equalsIgnoreCase("Edit"))
        {
            if (i.helper.currentLocation != null)
            {
                LatLng pLatlng = new LatLng(i.helper.currentLocation.getLatitude(), i.helper.currentLocation.getLongitude());
                updateMarkerLocation(pLatlng, "Marker");
                moveToLocation(pLatlng);
            }
        }
        else
        {
            callLocationGpsTracker();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)  //doubt
    {
        Log.e("ACMapAct", "onRequestPermissionsResult");

        switch (requestCode)
        {
            case m_GPSLOCATION_REQUEST_CODE:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    {
                        callLocationGpsTracker();
                    }
                }
                else
                {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }

                return;
            }
        }
    }

    private void callLocationGpsTracker()
    {
        Log.e("callLocationGpsTracker", "callLocationGpsTracker");

        ACGPSTracker tracker = new ACGPSTracker(this);
        Location pLocation = tracker.getLocation();

        double latitude = pLocation.getLatitude();
        double longitude = pLocation.getLongitude();

        if (pLocation != null)
        {
            Log.e("callLocationGpsTracker", "pLocation");
            //i.helper.currentLocation = pLocation;
            i.helper.updateiHelperCurrentLocation(pLocation);

            LatLng pLatlng = new LatLng(latitude, longitude);
            updateMarkerLocation(pLatlng, "Marker");
            moveToLocation(pLatlng);

            i.helper.getAddress(latitude, longitude, this);
        }
        else
        {
            i.helper.singleButtonAlert(this, "No Network", "Please check your internet connection", ACMapAct.this);
        }
    }

    @Override
    public void onMapClick(LatLng latLng)
    {
        Log.e("onMapClick", "onMapClick");

        i.helper.getAddress(latLng.latitude, latLng.longitude, this);

        String[] address = i.helper.completeAddress.split(",");

        updateMarkerLocation(latLng, address[1]);
    }

    private void updateMarkerLocation(LatLng latLng, String title)
    {
        Log.e("onMapClick", "updateMarkerLocation");
        this.m_markerOnMap.setPosition(latLng);
        this.m_markerOnMap.setTitle(title);
        this.m_markerOnMap.showInfoWindow();

        i.helper.currentLocation.setLatitude(latLng.latitude);
        i.helper.currentLocation.setLongitude(latLng.longitude);

        //getAddress(latLng.latitude, latLng.longitude);

        Log.e("onMapClick", String.valueOf(i.helper.currentLocation));
    }

    @Override //places related
    public void onPlaceSelected(Place place)
    {
        Log.e("onMapClick", "onPlaceSelected");

        if (place != null)
        {
            //i.helper.nameOfLocation = place.getName();
            i.helper.completeAddress = place.getName();
            LatLng markerLocation = place.getLatLng();
            updateMarkerLocation(markerLocation, place.getName());
            moveToLocation(markerLocation);

            //Log.e("locationName", i.helper.nameOfLocation);
        }
    }

    @Override
    public void onError(Status status)
    {
        Log.e("onError", "onError");
    }


}
