package com.Senior.Faff.RestaurantProfile;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.Senior.Faff.Main2Activity;
import com.Senior.Faff.MapsActivity;
import com.Senior.Faff.Promotion.PromotionActivity;
import com.Senior.Faff.Promotion.PromotionRecycleview;
import com.Senior.Faff.R;
import com.Senior.Faff.UserProfile.ListTypeNodel;
import com.Senior.Faff.Model.BookmarkList;
import com.Senior.Faff.Model.Party;
import com.Senior.Faff.Model.Promotion;
import com.Senior.Faff.Model.Restaurant;
import com.Senior.Faff.Model.UserProfile;
import com.Senior.Faff.utils.Helper;
import com.Senior.Faff.utils.LoadingFragment;
import com.Senior.Faff.utils.PermissionUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ShowRestaurantProfile extends AppCompatActivity implements OnMapReadyCallback {
    private static String TAG = ShowRestaurantProfile.class.getSimpleName();
    private GoogleMap mMap;
    private Location location;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 99;
    private LatLng myLocation;
    private com.google.android.gms.maps.model.Marker mCurrLocationMarker;
    private String getlocation;
    private TextView name,telephone,period,description,address,type_food;
    private RecyclerView mRecyclerView;
    private ArrayList<String> favourite_type;
    private ListTypeNodel list_adapter;
    private static Context mcontext;
    private String userid,resid;
    private Toolbar toolbar;
    private RatingBar rate;

    private FloatingActionButton fab;
    private String id;
    private String restaurantName;
    ImageView imageView;
    RecyclerView recyclerView;
    private int width;
    private int height;
    private Button fav_click,fav_unclick;
    private String key;
    private BookmarkList book;
    private String send_location;
    private String ownerid;
    private Button add_promotion;
    private TextView delete_res;
    private LinearLayout promotion_res_list;

    private static FrameLayout loading;
    private LoadingFragment loadingFragment;
    private CoordinatorLayout inc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show__restaurant_profile);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        id = getIntent().getExtras().getString("userid");

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        mcontext = getApplicationContext();
        promotion_res_list = (LinearLayout) findViewById(R.id.promotion_res_list);
        add_promotion = (Button) findViewById(R.id.add_promotion);
        name = (TextView)findViewById(R.id.name);
        telephone = (TextView)findViewById(R.id.telephone);
        period = (TextView)findViewById(R.id.period);
        description = (TextView)findViewById(R.id.description);
        address = (TextView)findViewById(R.id.address);
        type_food = (TextView)findViewById(R.id.address);
        mRecyclerView = (RecyclerView)findViewById(R.id.mRecyclerView);
        imageView = (ImageView) findViewById(R.id.image);
        fav_click = (Button)findViewById(R.id.fav_click);
        fav_unclick = (Button)findViewById(R.id.fav_unclick);
        delete_res  = (TextView)findViewById(R.id.delete_res);

        inc = (CoordinatorLayout) findViewById(R.id.inc);
        loading = (FrameLayout) inc.findViewById(R.id.loading);
        inc.bringChildToFront(loading);

        rate = (RatingBar)findViewById(R.id.rate);
        fab = (FloatingActionButton)findViewById(R.id.fab);
        book = new BookmarkList();
        Bundle args = getIntent().getExtras();
        if(args != null) {
            userid = args.getString(Restaurant.Column.UserID, null);
            resid = args.getString(Restaurant.Column.ResID,null);
            //ownerid =args.getString(UserProfile.Column.Ownerid,null);
        }
        /////////////////////////////////////////// Excute Aysntask ///////////////////////////////////////////////////////////////
        if(userid != null){
            new getData().execute(resid);
            new getBookmark().execute();
            new getPromotion().execute(resid);
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        add_promotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcontext, PromotionActivity.class);
                intent.putExtra(Restaurant.Column.ResID, resid);
                intent.putExtra(UserProfile.Column.UserID,userid);
                startActivity(intent);
            }
        });
        delete_res.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               new delete().execute(resid);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowRestaurantProfile.this,EditRestaurantProfile.class);
                intent.putExtra(Restaurant.Column.ResID,resid);
                intent.putExtra(Restaurant.Column.UserID,userid);
                startActivity(intent);
            }
        });



        GetUser getuser = new GetUser(new GetUser.AsyncResponse() {
            @Override
            public void processFinish(String output) throws JSONException {
                JSONObject item = new JSONObject(output);
                UserProfile user = new Gson().fromJson(item.toString(), UserProfile.class);
                Log.i(TAG, " user is : "+user.toString());

                Bundle b = new Bundle();
                b.putString("id", id);
                b.putString("resid", resid);
                b.putString("username", user.getName());

                CommentRestaurantFragment cmf = new CommentRestaurantFragment();
                cmf.setArguments(b);
                FragmentManager fm = getSupportFragmentManager();
                try {
                    fm.beginTransaction().replace(R.id.comment_restaurant_content, cmf).commit();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }

                if(loadingFragment!=null)
                {
                    loadingFragment.onStop();
                    hideLoading();
                }
            }
        });
        fav_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] list   =  book.getBookmarkID().split(",");
                boolean first = true;
                String getlist = null;
                for(int i = 0; i<list.length; i++){
                    if(!resid.equals(list[i])) {
                        if(first) {
                            getlist = list[i];
                            first = false;
                        }
                        else{
                            getlist = getlist + "," + list[i];
                        }
                    }
                }
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("All_Bookmark");
                mDatabase.child(key).child("bookmarkID").setValue(getlist);
                fav_click.setEnabled(false);
                fav_unclick.setEnabled(true);
                Toast.makeText(mcontext,"Remove to Whitelist",Toast.LENGTH_SHORT).show();
            }});

        fav_unclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getlist = null;
                if(book.getBookmarkID() != null){
                    getlist =  book.getBookmarkID() + "," + resid;
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("All_Bookmark");
                    mDatabase.child(key).child("bookmarkID").setValue(getlist);
                }else{
                    book.setBookmarkID(resid);
                    book.setUserID(userid);
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("All_Bookmark");
                    mDatabase.push().setValue(book);
                }
                fav_click.setEnabled(true);
                fav_unclick.setEnabled(false);
                Toast.makeText(mcontext,"Add to Whitelist",Toast.LENGTH_SHORT).show();
            }
        });
        getuser.execute(id);

    }
    private class getBookmark extends AsyncTask<String, String, Void> {

        String pass;
        int responseCode;
        HttpURLConnection connection;
        String resultjson;

        @Override
        protected Void doInBackground(String... args) {
            DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference().child("All_Bookmark");
            mRootRef.orderByChild("userID")
                    .equalTo(userid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange (DataSnapshot dataSnapshot){
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        key = postSnapshot.getKey();
                        book = postSnapshot.getValue(BookmarkList.class);
                        setbookmark();
                    }


                    //showlist(listview, party_list, resId,gender,age);
                    //Toast.makeText(getActivity(),"hi",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //Toast.makeText(getActivity(),"Cant connect database",Toast.LENGTH_SHORT).show();
                }

            });
          return null;
        }

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        double a = -34;
        double b = 151;
            LatLng sydney = new LatLng(a,b);
            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,10.0f));
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent intent = new Intent(ShowRestaurantProfile.this, MapsActivity.class);
                intent.putExtra(Restaurant.Column.RestaurantName,restaurantName);
                intent.putExtra(Party.Column.Location, send_location);
                startActivity(intent);
            }
        });
    }
    private void enableMyLocation(String lola,String res_name) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            //mMap.setMyLocationEnabled(true);
            LocationManager locationManager = (LocationManager)
                    getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            location = locationManager.getLastKnownLocation(locationManager
                    .getBestProvider(criteria, false));
            if(lola != null) {
                send_location = lola;
                String[] pos = lola.split(",");
                try {
                    myLocation = new LatLng(Double.parseDouble(pos[0]),
                            Double.parseDouble(pos[1]));
                } catch (NumberFormatException e) {
                    myLocation = new LatLng(0, 0);
                }
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,
                        11));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(myLocation);
                markerOptions.title(res_name);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                mCurrLocationMarker = mMap.addMarker(markerOptions);
                //    getlocation = location.getLatitude() + ","  + location.getLongitude();

            }
        }
    }
    private class getData extends AsyncTask<String, String, Restaurant>{
        String pass;
        int responseCode;
        HttpURLConnection connection;
        String resultjson;
        @Override
        protected Restaurant doInBackground(String... args) {
            try {
                showLoading();
                loadingFragment = new LoadingFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.loading, loadingFragment).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
            StringBuilder result = new StringBuilder();
            String url_api = "https://faff-1489402013619.appspot.com/res_profile/" + args[0];
            try {
                URL url = new URL(url_api);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                InputStream in = new BufferedInputStream(connection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                responseCode = connection.getResponseCode();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (responseCode == 200) {
                Log.i("Request Status", "This is success response status from server: " + responseCode);
                Gson gson = new Gson();
                Restaurant userPro  =  gson.fromJson(result.toString(),  Restaurant.class);
                return userPro;
            } else {
                Log.i("Request Status", "This is failure response status from server: " + responseCode);
                return null ;

            }

        }
        @Override
        protected void onPostExecute(Restaurant respro) {
            super.onPostExecute(respro);
            if (respro != null) {
                name.setText(respro.getRestaurantName());
                restaurantName = respro.getRestaurantName();
                address.setText("   "+respro.getAddress());
                telephone.setText(respro.getTelephone());
                description.setText("   "+respro.getDescription());
                period.setText(respro.getPeriod());
                rate.setRating(respro.getScore());

                enableMyLocation(respro.getLocation(),respro.getRestaurantName());
                ownerid = respro.getUserID();
                resid =  respro.getresId();
                toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                getSupportActionBar().setTitle(respro.getRestaurantName());

                if(userid.equals(ownerid)) {
                    View a = findViewById(R.id.fab);
                    a.setVisibility(View.VISIBLE);
                    delete_res.setVisibility(View.VISIBLE);
                    add_promotion.setVisibility(View.VISIBLE);
                    promotion_res_list.setVisibility(View.VISIBLE);
                }
                if(respro.getTypefood() != null){
                    String[] list = respro.getTypefood().split(",");
                    favourite_type = new ArrayList<String>();
                    for(int i = 0; i < list.length; i++){
                        favourite_type.add(list[i]);
                    }
                    list_adapter = new ListTypeNodel(favourite_type, mcontext);
                    LinearLayoutManager mLayoutManager  = new LinearLayoutManager(mcontext);
                    mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setAdapter(list_adapter);

                    String[] img_path = respro.getPicture().toString().split(",");
                    recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
                    ShowResRecAdapter sh = new ShowResRecAdapter(mcontext, img_path, width);
                    LinearLayoutManager mLayoutManager1  = new LinearLayoutManager(mcontext);
                    mLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
                    recyclerView.setLayoutManager(mLayoutManager1);
                    recyclerView.setAdapter(sh);

                }
            }
            else{
                String message = getString(R.string.login_error_message);
                Toast.makeText(mcontext, message, Toast.LENGTH_SHORT).show();
            }

        }

    }

    private static class GetUser extends AsyncTask<String, String, String> {

        String result = "";

        public interface AsyncResponse {
            void processFinish(String output) throws JSONException;
        }

        public ShowRestaurantProfile.GetUser.AsyncResponse delegate = null;

        public GetUser(ShowRestaurantProfile.GetUser.AsyncResponse delegate){
            this.delegate = delegate;
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                Log.i(TAG, "  params [0] is : "+params[0]);
                URL url = new URL("https://faff-1489402013619.appspot.com/user/"+params[0]);
                //URL url = new URL("http://localhost:8080/promotion_list");

                Helper hp = new Helper();
                hp.setRequest_method("GET");
                result = hp.getRequest(url.toString());

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != "") {
                //Toast.makeText(mcontext, result, Toast.LENGTH_LONG).show();
                try {
                    delegate.processFinish(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                //Toast.makeText(mcontext, "Fail", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private  class getPromotion extends AsyncTask<String , String, Promotion[]>{

        int responseCode;
        HttpURLConnection connection;
        @Override
        protected Promotion[] doInBackground(String... params) {
            StringBuilder result = new StringBuilder();
            String url_api = "https://faff-1489402013619.appspot.com/restaurant_promotion/resid/" + params[0];
          //  String url_api = "https://faff-1489402013619.appspot.com/restaurant_promotion/resid/test0914121493662297";
            try {
                URL url = new URL(url_api);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                InputStream in = new BufferedInputStream(connection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                responseCode = connection.getResponseCode();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (responseCode == 200) {
                Log.i("Request Status", "This is success response status from server: " + responseCode);
                Gson gson = new Gson();
                Promotion[] promotion_list  =  gson.fromJson(result.toString(),  Promotion[].class);
                return promotion_list;
            } else {
                Log.i("Request Status", "This is failure response status from server: " + responseCode);
                return null ;

            }
        }

        @Override
        protected void onPostExecute(Promotion[] promotions) {
            super.onPostExecute(promotions);
            if(promotions != null) {
                View a = findViewById(R.id.promotion_list);
                a.setVisibility(View.VISIBLE);
                promotion_res_list.setVisibility(View.VISIBLE);
                PromotionRecycleview list_adapter = new PromotionRecycleview(promotions, resid,userid);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(mcontext);
                mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                RecyclerView proRecyclerView = (RecyclerView) findViewById(R.id.promotion_list);
                proRecyclerView.setLayoutManager(mLayoutManager);
                proRecyclerView.setAdapter(list_adapter);
            }else{
                View a = findViewById(R.id.no_pro);
                a.setVisibility(View.VISIBLE);

            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // this takes the user 'back', as if they pressed the left-facing triangle icon on the main android toolbar.
                // if this doesn't work as desired, another possibility is to call `finish()` here.
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void setbookmark(){
        boolean have = false;
        if(book  == null) {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("All_Bookmark");
            BookmarkList bookmark = new BookmarkList(userid);
            mDatabase.push().setValue(bookmark);
        }else{
            if(book.getBookmarkID() != null) {
                String[] list = book.getBookmarkID().split(",");
                boolean first = true;
                for (int i = 0; i < list.length; i++) {
                    if (resid.equals(list[i])) {
                        have = true;
                        break;
                    }
                }
            }
            if(have){
                View sd = findViewById(R.id.fav_unclick);
                sd.setVisibility(View.GONE);
                View sv = findViewById(R.id.fav_click);
                sv.setVisibility(View.VISIBLE);
            }else{
                View sd = findViewById(R.id.fav_unclick);
                sd.setVisibility(View.VISIBLE);
                View sv = findViewById(R.id.fav_click);
                sv.setVisibility(View.GONE);
            }

        }

    }
    public class delete extends AsyncTask<String , Void, Boolean>{
        int responseCode;
        HttpURLConnection connection;
        @Override
        protected Boolean doInBackground(String... params) {
            try{
                JSONObject para = new JSONObject();
                String url_api = "https://faff-1489402013619.appspot.com/res_profile/del/" + params[0];
                URL url = new URL(url_api);
                connection = (HttpURLConnection)url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty(
                        "Content-Type", "application/x-www-form-urlencoded" );
                connection.setRequestMethod("DELETE");
                connection.connect();

                responseCode = connection.getResponseCode();

            }catch (Exception e){
                e.printStackTrace();
            }
            if (responseCode == 200) {
                Log.i("Request Status", "This is success response status from server: " + responseCode);
                return true;
            } else {
                Log.i("Request Status", "This is failure response status from server: " + responseCode);
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean){
                Toast.makeText(mcontext,"Restaurant deleted",Toast.LENGTH_SHORT);
                Intent intent = new Intent(mcontext, Main2Activity.class);
                intent.putExtra(UserProfile.Column.UserID,userid);
                startActivity(intent);
            }else{
                Toast.makeText(mcontext,"Cant Delete",Toast.LENGTH_SHORT);
            }
        }
    }

    public static void showLoading(){
        if(loading!=null)
        {
            if(loading.getVisibility()==View.GONE)
            {
                loading.setVisibility(View.VISIBLE);
            }
        }
    }

    public static void hideLoading(){
        if(loading!=null)
        {
            if(loading.getVisibility()==View.VISIBLE)
            {
                loading.setVisibility(View.GONE);
            }
        }
    }
}
