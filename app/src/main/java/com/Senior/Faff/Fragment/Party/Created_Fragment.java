package com.Senior.Faff.Fragment.Party;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.Senior.Faff.R;
import com.Senior.Faff.model.Party;
import com.Senior.Faff.model.Restaurant;
import com.Senior.Faff.model.UserProfile;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Created_Fragment extends Fragment {

    private static final String Tag = Created_Fragment.class.getSimpleName();
    private Context mcontext;

    private ArrayList<Party> re_list;
    private ListView listview;
    private ArrayList<Party> party_list;
    private int gender,age;
    private String userid;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    private Button addRoom;
    private EditText roomName;
    int[] resId =  {R.drawable.restaurant1,R.drawable.restaurant2,R.drawable.restaurant3};
    public Created_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_created_, container, false);

        if(getArguments().getString(UserProfile.Column.UserID) != null) {
            userid = getArguments().getString(UserProfile.Column.UserID);
            gender = getArguments().getInt(UserProfile.Column.Gender);
            age = getArguments().getInt(UserProfile.Column.Age);

        }
        mcontext = getContext();
        re_list = new  ArrayList<>();
        listview = (ListView) root.findViewById(R.id.listView12);
        new getData().execute();
    return root;
    }

    public void showlist(ListView listview, ArrayList<Party> Pary_list, int[] resId, int gender, int age) {

        Map<String, String> rule_gender = new HashMap<>();
        if(gender == 0){
            rule_gender.put("gender","Female");
        }else{
            rule_gender.put("gender","Male");
        }
        Map<String, Integer> rule_age = new HashMap<>();
        rule_age.put("age", age);
        // Restaurant_manager res_manager = new Restaurant_manager(mcontext);
        if (Pary_list != null) {
            re_list = getcreate(Pary_list);
            if(re_list != null) {
                listview.setAdapter(new Customlistview_nearparty_adapter(getContext(), 0, re_list, resId));
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                        Intent intent = new Intent(mcontext, Show_party_profile.class);
                        intent.putExtra(Party.Column.RoomID, re_list.get(position).getRoomID());
                        intent.putExtra(UserProfile.Column.UserID, userid);
                        startActivity(intent);
                    }
                });
            }
        }
        else{
            Toast.makeText(getActivity(),"Dont have party",Toast.LENGTH_SHORT);
        }

    }
    private class getData extends AsyncTask<Void, Integer, Void> {
        protected Void doInBackground(Void... params)   {
            DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
            mRootRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange (DataSnapshot dataSnapshot){
                    long count = dataSnapshot.child("All_Room").getChildrenCount();
                    party_list = new ArrayList<>();
                    for (DataSnapshot postSnapshot: dataSnapshot.child("All_Room").getChildren()) {
                        Party post= postSnapshot.getValue(Party.class);
                        party_list.add(post);

                    }
                    showlist(listview, party_list, resId,gender,age);
                    //Toast.makeText(getActivity(),"hi",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getActivity(),"Cant connect database",Toast.LENGTH_SHORT).show();
                }
            });
            return null;
        }

        protected void onProgressUpdate(Integer... values) {


        }

        protected void onPostExecute(Void result)  {
            // Toast.makeText(getActivity(),"GEt dataaaa",Toast.LENGTH_SHORT).show();


        }
    }

    public ArrayList<Party> getcreate(ArrayList<Party> get_list){
        ArrayList<Party> list = new ArrayList<Party>();
        for(int i =0 ;i < get_list.size(); i++){
            if(userid.equals( get_list.get(i).getCreateid())){
                list.add(get_list.get(i));
            }
        }
        return list;
    }
}
