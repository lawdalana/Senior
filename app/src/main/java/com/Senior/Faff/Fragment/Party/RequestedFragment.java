package com.Senior.Faff.Fragment.Party;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.Senior.Faff.R;
import com.Senior.Faff.Model.Party;
import com.Senior.Faff.Model.UserProfile;
import com.Senior.Faff.utils.LoadingFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class RequestedFragment extends Fragment {


    //    private CustomlistviewNearpartyAdapter cus;
    private RequestRecycler cus;


    public RequestedFragment() {
        // Required empty public constructor
    }

    private Context mcontext;

    private ArrayList<Party> re_list;
    //    private ListView listview;
    private RecyclerView listview;

    private ArrayList<Party> party_list;
    private int gender, age;
    private String userid;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    private Button addRoom;
    private EditText roomName;
    int[] resId = {R.drawable.restaurant1, R.drawable.restaurant2, R.drawable.restaurant3};

    private static FrameLayout loading;
    private LoadingFragment loadingFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root = inflater.inflate(R.layout.fragment_requested_, container, false);

        if (getArguments().getString(UserProfile.Column.UserID) != null) {
            userid = getArguments().getString(UserProfile.Column.UserID);
            gender = getArguments().getInt(UserProfile.Column.Gender);
            age = getArguments().getInt(UserProfile.Column.Age);
        }
        mcontext = getContext();
        re_list = new ArrayList<>();
        listview = (RecyclerView) root.findViewById(R.id.listView12);
        loading = (FrameLayout) root.findViewById(R.id.loading);
        new getData_r().execute();
        return root;
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        if(listview.getChildCount()>0)
//        {
//            showLoading();
//            try {
//                loadingFragment = new LoadingFragment();
//                getFragmentManager().beginTransaction().replace(R.id.loading, loadingFragment).commit();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        else {
//            hideLoading();
//        }
//    }

    public void showlist_re(RecyclerView listview, ArrayList<Party> Pary_list, int[] resId, int gender, int age) {

        Map<String, String> rule_gender = new HashMap<>();
        if (gender == 0) {
            rule_gender.put("gender", "Female");
        } else {
            rule_gender.put("gender", "Male");
        }
        Map<String, Integer> rule_age = new HashMap<>();
        rule_age.put("age", age);
        // RestaurantManager res_manager = new RestaurantManager(mcontext);
        if (Pary_list != null) {
            re_list = getcreate(Pary_list);
            if (re_list != null) {
//                listview.setAdapter(new CustomlistviewNearpartyAdapter(mcontext, 0, re_list, resId));

                debug("In request : ");


                cus = new RequestRecycler(mcontext, re_list, userid);
                LinearLayoutManager mLayoutManager  = new LinearLayoutManager(mcontext);
                mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                listview.setLayoutManager(mLayoutManager);
                listview.setAdapter(cus);
                if(loadingFragment!=null)
                {
                    loadingFragment.onStop();
                    hideLoading();
                }

//                listview.setAdapter(new CustomlistviewNearpartyAdapter(mcontext, re_list));
//                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
//                        Intent intent = new Intent(mcontext, ShowPartyProfile.class);
//                        intent.putExtra(Party.Column.RoomID, re_list.get(position).getRoomID());
//                        intent.putExtra(UserProfile.Column.UserID, userid);
//                        startActivity(intent);
//                    }
//                });
            }
        } else {
            Toast.makeText(getActivity(), "Dont have party", Toast.LENGTH_SHORT);
        }

    }

    private class getData_r extends AsyncTask<Void, Integer, Void> {
        protected Void doInBackground(Void... params) {

            try {
                showLoading();
                loadingFragment = new LoadingFragment();
                getFragmentManager().beginTransaction().replace(R.id.loading, loadingFragment).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }

            DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
            mRootRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    long count = dataSnapshot.child("All_Room").getChildrenCount();
                    party_list = new ArrayList<>();
                    for (DataSnapshot postSnapshot : dataSnapshot.child("All_Room").getChildren()) {
                        Party post = postSnapshot.getValue(Party.class);
                        party_list.add(post);

                    }
                    showlist_re(listview, party_list, resId, gender, age);
                    //Toast.makeText(getActivity(),"hi",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getActivity(), "Cant connect database", Toast.LENGTH_SHORT).show();
                }
            });
            return null;
        }

        protected void onProgressUpdate(Integer... values) {


        }

        protected void onPostExecute(Void result) {
            // Toast.makeText(getActivity(),"GEt dataaaa",Toast.LENGTH_SHORT).show();


        }
    }

    public ArrayList<Party> getcreate(ArrayList<Party> get_list) {
        ArrayList<Party> list = new ArrayList<Party>();

        for (int i = 0; i < get_list.size(); i++) {
            if (get_list.get(i).getRequest() != null) {
                String[] join_list = get_list.get(i).getRequest().split(",");
                for (int j = 0; j < join_list.length; j++) {
                    if (userid.equals(join_list[j])) {
                        list.add(get_list.get(i));
                    }
                }
            }
        }
        return list;
    }

    private void debug(String d) {
        Log.i("TEST:", " debug : " + d);
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
