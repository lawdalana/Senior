package com.Senior.Faff.RestaurantProfile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.Senior.Faff.R;
import com.Senior.Faff.UserProfile.ProfileManager;
import com.Senior.Faff.model.Comment;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

/**
 * Created by Not_Today on 4/6/2017.
 */

public class Comment_RestaurantFragment extends Fragment {

    FirebaseDatabase storage = FirebaseDatabase.getInstance();
    String id;
    DatabaseReference comment;
    private Button add_comment;
    private TextView comment_text;
    private ListView listView;
//    private ArrayList<String> list_of_comment = new ArrayList<>();
    private ArrayList<Comment> list = new ArrayList<>();
    private CommentAdapter adapter;
    private RatingBar rating_star;
    private TextView score;
    private DatabaseReference rate = storage.getReference("Restaurant").child("score");

    public Comment_RestaurantFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        id = getArguments().getString("id");
        comment = storage.getReference("Restaurant").child("Comment");

        View root = inflater.inflate(R.layout.comment_restaurant, container, false);

        add_comment = (Button)root.findViewById(R.id.comment_send);
        comment_text = (TextView)root.findViewById(R.id.comment_text);

        add_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String c = comment_text.getText().toString();
                Map<String, Object> map = new HashMap<String, Object>();
                Map<String, Object> map1 = new HashMap<String, Object>();
                Map<String, Object> map2 = new HashMap<String, Object>();
                Map<String, Object> map3 = new HashMap<String, Object>();

                String tmp = comment.push().getKey();

                map.put("id", id);

                ProfileManager pm = new ProfileManager(getActivity());
                String userName = pm.getUserName(id);
                map1.put("name", userName);

                map2.put("comment", c);

                DateFormat dateformat = DateFormat.getDateTimeInstance();
                dateformat.setTimeZone(TimeZone.getTimeZone("GMT+7"));
                Date date = new Date();
                map3.put("date", dateformat.format(date));

                comment.child(tmp).updateChildren(map);
                comment.child(tmp).updateChildren(map1);
                comment.child(tmp).updateChildren(map2);
                comment.child(tmp).updateChildren(map3);
            }
        });

        adapter = new CommentAdapter(getContext(), list);
        listView = (ListView)root.findViewById(R.id.listView);
        listView.setAdapter(adapter);

        comment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> setId = new ArrayList<String>();
                ArrayList<String> setName = new ArrayList<String>();
                ArrayList<String> setComment = new ArrayList<String>();
                ArrayList<String> setDate = new ArrayList<String>();

                Iterator i = dataSnapshot.getChildren().iterator();

                int x=0;

                while (i.hasNext())
                {
                    Iterator j = ((DataSnapshot) i.next()).getChildren().iterator();

                    while (j.hasNext())
                    {
                        if(x%4==0)
                            setComment.add(((DataSnapshot) j.next()).getValue().toString());
                        else if(x%4==1)
                            setDate.add(((DataSnapshot) j.next()).getValue().toString());
                        else if(x%4==2)
                            setId.add(((DataSnapshot) j.next()).getValue().toString());
                        else if(x%4==3)
                            setName.add(((DataSnapshot) j.next()).getValue().toString());
                        x++;
                    }
                }

//                list_of_comment.clear();
//                list_of_comment.addAll(setComment);


                list.clear();
                for(int q=0; q<setComment.size();q++)
                {
                    Comment com = new Comment(Integer.parseInt(setId.get(q)),setName.get(q),setComment.get(q),setDate.get(q));
                    list.add(com);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        rating_star = (RatingBar)root.findViewById(R.id.ratingBar);

        rate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null)
                {
                    Iterator i = dataSnapshot.getChildren().iterator();
                    float sum = 0;
                    long n = dataSnapshot.getChildrenCount();

                    while (i.hasNext())
                    {
                        String t = ((DataSnapshot) i.next()).getValue().toString();
                        float tmp = Float.parseFloat(t);
                        sum+=tmp;
                    }

                    rating_star.setOnRatingBarChangeListener(null);
                    rating_star.setRating(sum/n);
                    score.setText(String.valueOf(new DecimalFormat("#.##").format(sum/n)));
                    rating_star.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                            String tmp = String.valueOf(rating);
                            score.setText(tmp);
                            rate.child(id).setValue(tmp);
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        score = (TextView)root.findViewById(R.id.comment_score);

        rating_star.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                String tmp = String.valueOf(rating);
                score.setText(tmp);
                rate.child(id).setValue(tmp);
            }
        });

        return root;
    }

}