package com.Senior.Faff.Fragment.SearchNearby;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.Senior.Faff.R;
import com.Senior.Faff.RestaurantProfile.Customlistview_addvice_adapter;
import com.Senior.Faff.RestaurantProfile.Restaurant_manager;
import com.Senior.Faff.model.Restaurant;
import com.Senior.Faff.utils.PermissionUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nearby_Fragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,LocationListener{


    public Nearby_Fragment() {
        // Required empty public constructor
    }

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Context mcontext;
    private GoogleApiClient googleApiClient;
    private Location mLastLocation;
    private LatLng myLocation;
    private Location location;
    private ArrayList<Restaurant> re_list;
    private ListView listview;
    int[] resId =  {R.drawable.restaurant1,R.drawable.restaurant2,R.drawable.restaurant3};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_nearby_, container, false);

        mcontext  = getContext();

        Restaurant model = new Restaurant();

       listview = (ListView)root.findViewById(R.id.listView12);

        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        Log.d("Myactivity", "omCreateview");



        return root;
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("Myactivity", "onConnected");
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
               new AlertDialog.Builder(mcontext)
                       .setTitle("")
                       .setMessage("")
                       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_REQUEST_CODE);
                           }
                       })
                       .create()
                       .show();
            } else {
                /*      ActivityCompat.requestPermissions(
                        this, new String[]{Manifest.permission.LOCATION_FINE},
                        ACCESS_FINE_LOCATION);*/
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
        else {
            //createLocationRequest();

            LocationAvailability locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient);
            if (locationAvailability != null) {


                LocationRequest locationRequest = new LocationRequest()  // ใช้สำหรับ onlicationchange ทำเรื่อยๆ
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setInterval(2000)
                        .setFastestInterval(2000);
                if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
                }

                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);  // ใช้สำหรัรับตำแหน่งแรกเลย ครั้งเดียว
                if (mLastLocation != null) {

                    Location target = new Location("Target");
                    target.setLatitude(37.5219983);
                    target.setLongitude(-122.184);
                    float d = mLastLocation.distanceTo(target);
                    //  d = location.distanceTo(target);
                    //   la.setText(String.valueOf(d));
                    if( mLastLocation!=null)
                        if( mLastLocation.distanceTo(target) > 1000) {
                            Toast.makeText(getContext(), "Hi", Toast.LENGTH_SHORT).show();
                        }
                   /* if(location.distanceTo(target) < METERS_100) {
                        // bingo!
                    }*/

                  /*  lo.setText(String.valueOf(mLastLocation.getLongitude()));
                    la.setText(String.valueOf(mLastLocation.getLatitude()));*/

                    showlist(listview,re_list,resId);

                }

                // LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            }



        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }
    public ArrayList<Restaurant> calculatedistance(int de_distance,int[] typeID,ArrayList<Restaurant> listRes){
        int distance;
        float latitude,longtitude;
        ArrayList<Restaurant> res  = new ArrayList<>();
        for(int i =0; i<listRes.size(); i++){
          String[] position = listRes.get(i).getLocation().split(",");
            latitude = Float.parseFloat(position[0]);
            longtitude = Float.parseFloat(position[1]);
            Location target = new Location("Target");
            target.setLatitude(latitude);
            target.setLongitude(longtitude);
            distance  =  (int)mLastLocation.distanceTo(target);
            if(distance <= de_distance ){
                for(int j =0; j<typeID.length;j++) {
                    if(listRes.get(i).getTypeID() == typeID[j] )
                    res.add(listRes.get(i));
                }
            }
        }

        return res;
    }
    public void showlist(ListView listview,ArrayList<Restaurant> re_list,int[] resId){
        int[] h = {1,2};
        Restaurant_manager res_manager = new Restaurant_manager(mcontext);
        re_list =  calculatedistance(900 ,h,res_manager.showAllRestaurant());
        listview.setAdapter( new Customlistview_addvice_adapter(getContext(),0,re_list,resId));
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                if(position == 0){
                    Toast.makeText(getContext(),"position 0",Toast.LENGTH_SHORT).show();
                }
                if(position== 1){
                    Toast.makeText(getContext(),"position 1",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
