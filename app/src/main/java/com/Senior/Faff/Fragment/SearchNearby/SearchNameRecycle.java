package com.Senior.Faff.Fragment.SearchNearby;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.Senior.Faff.R;
import com.Senior.Faff.RestaurantProfile.ShowRestaurantProfile;
import com.Senior.Faff.Model.Restaurant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by InFiNity on 01-May-17.
 */

public class SearchNameRecycle extends RecyclerView.Adapter<SearchNameRecycle.ViewHolder>{
    private ArrayList<Restaurant> list ;
    private Context context;
    private String key;
    private String  setuserid;
    private  Context mcontext;
    private boolean host;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout item;
        public TextView list_name,open,detail;
        public ImageView image;
        public RatingBar rate;
        public ViewHolder(View v) {
            super(v);
            item = (LinearLayout) v.findViewById(R.id.item);
            list_name = (TextView)v.findViewById(R.id.textView1);
            image =  (ImageView)v.findViewById(R.id.image);
            open = (TextView) v.findViewById(R.id.open_text);
            detail = (TextView) v.findViewById(R.id.detail);
            rate = (RatingBar)v.findViewById(R.id.ratingBar);
        }
    }

    public SearchNameRecycle(ArrayList<Restaurant> list){
        this.list = list;
    }
    @Override
    public SearchNameRecycle.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurant_recycle_list, parent, false);
        ViewHolder vh = new ViewHolder(view);
        mcontext = view.getContext();
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.list_name.setText(list.get(position).getRestaurantName());
        holder.open.setText(list.get(position).getPeriod());
        getRate(list.get(position).getresId(),holder);
        holder.detail.setText(list.get(position).getDescription());
        String url = list.get(position).getPicture();
        String[] tmp = url.split(",");

        if (tmp[0].contains(" "))
        {
            url = tmp[0].replaceAll(" ", "%20");
            Log.i("TEST:", " url in search nearby : " +url);
            try {
                Picasso.with(mcontext).load(url).fit().into(holder.image);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
        {
            Log.i("TEST:", " url in search nearby : " +tmp[0]);
            try {
                Picasso.with(mcontext).load(tmp[0]).fit().into(holder.image);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mcontext, ShowRestaurantProfile.class);
                i.putExtra(Restaurant.Column.ResID, list.get(position).getresId());
                i.putExtra(Restaurant.Column.UserID,list.get(position).getUserID());
                mcontext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public void getRate(String res_id, final ViewHolder holder){
        DatabaseReference rate;
        FirebaseDatabase storage = FirebaseDatabase.getInstance();
        rate = storage.getReference("Restaurant").child("score").child(res_id);
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

                    float scor = sum/n;
                    holder.rate.setRating(scor);
                    // score.setText(String.valueOf(new DecimalFormat("#.##").format(sum/n)));

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public ArrayList<Restaurant> getlist(){
        return  list;
    }
}

