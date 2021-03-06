package com.Senior.Faff.RestaurantProfile;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.Senior.Faff.R;
import com.Senior.Faff.utils.PermissionUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class RestaurantMapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,LocationListener,
        ActivityCompat.OnRequestPermissionsResultCallback {


    private GoogleApiClient googleApiClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 99;
    private GoogleMap mMap;
    private Location mLastLocation;
    private com.google.android.gms.maps.model.Marker mCurrLocationMarker;
    private LatLng myLocation;
    private Location location;
    private boolean mPermissionDenied = false;
    private Context context;
    private TextView la,lo;
    private int latitude,logtitude;
    private String position;
    private Context mcontext;
    private String user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mcontext = this;
        la = (TextView)findViewById(R.id.la);
        lo = (TextView)findViewById(R.id.lo);
        Button add_location = (Button)findViewById(R.id.addlo);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        if(location!=null) {
            la.setText(String.valueOf(location.getLatitude()));
            lo.setText(String.valueOf(location.getLongitude()));
        }
        Button btn = (Button)findViewById(R.id.addlo);
        if(getIntent().getStringExtra("userid") != null) {
            user_id = getIntent().getStringExtra("userid");
        }
        add_location.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(position != null) {
                    Intent intent = new Intent(getApplicationContext(), AddRestaurantProfile.class);
                    intent.putExtra("Position",position);
                    intent.putExtra("userid",user_id);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(mcontext, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });


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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(37.62,-122.284);
        mMap.addMarker(new MarkerOptions().position(sydney).title("PPAP"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setOnMyLocationButtonClickListener(this);

        //enableMyLocation();
    }
    @Override
    public void onStart() {
        super.onStart();

        // Connect to Google API Client
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            // Disconnect Google API Client if available and connected
            googleApiClient.disconnect();
        }
    }
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
            LocationManager locationManager = (LocationManager)
                    getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            location = locationManager.getLastKnownLocation(locationManager
                    .getBestProvider(criteria, false));


        }
    }
    @Override
    public boolean onMyLocationButtonClick() {


        if (location != null) {
            myLocation = new LatLng(location.getLatitude(),
                    location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,
                    11));
            position = location.getLatitude()+ "," + location.getLongitude();
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(myLocation);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            lo.setText(position);
            mCurrLocationMarker = mMap.addMarker(markerOptions);
        }


        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).

        return false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){

                Toast.makeText(this,"Granted",Toast.LENGTH_SHORT);
            }
            else
                Toast.makeText(this,"Deny",Toast.LENGTH_SHORT);
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();


        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    int check = ContextCompat.checkSelfPermission(RestaurantMapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (check != PackageManager.PERMISSION_GRANTED) {


            if (!ActivityCompat.shouldShowRequestPermissionRationale(RestaurantMapsActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(RestaurantMapsActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_REQUEST_CODE);
                            }

                        })
                        .create()
                        .show();
            } else {

                ActivityCompat.requestPermissions(RestaurantMapsActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
            }
        }
        else {
            ActivityCompat.requestPermissions(RestaurantMapsActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
            //createLocationRequest();
            //mMap.setMyLocationEnabled(true);
            LocationAvailability locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient);
            if (locationAvailability != null) {


                LocationRequest locationRequest = new LocationRequest()  // ใช้สำหรับ onlicationchange ทำเรื่อยๆ
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setInterval(2000)
                        .setFastestInterval(2000);
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
                }

                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);  // ใช้สำหรัรับตำแหน่งแรกเลย ครั้งเดียว
                if (mLastLocation != null) {

                    Location target = new Location("Target");
                    target.setLatitude(37.5219983);
                    target.setLongitude(-122.184);
                    float d;
                    //  d = location.distanceTo(target);
                    //   la.setText(String.valueOf(d));
                    if(location!=null)
                        if(location.distanceTo(target) < 1000) {
                            Toast.makeText(context, "Hi", Toast.LENGTH_SHORT).show();
                        }
                   /* if(location.distanceTo(target) < METERS_100) {
                        // bingo!
                    }*/

                  /*  lo.setText(String.valueOf(mLastLocation.getLongitude()));
                    la.setText(String.valueOf(mLastLocation.getLatitude()));*/

                }

                // LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            }



        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        Toast.makeText(this,"On changed",Toast.LENGTH_SHORT);
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position changed");
        position = location.getLatitude()+ "," + location.getLongitude();
        lo.setText(position);

        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        // mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
    }

}
