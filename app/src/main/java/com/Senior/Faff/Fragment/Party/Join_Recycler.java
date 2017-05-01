package com.Senior.Faff.Fragment.Party;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Senior.Faff.R;
import com.Senior.Faff.model.Party;
import com.Senior.Faff.model.UserProfile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Join_Recycler extends RecyclerView.Adapter<Join_Recycler.ViewHolder>{

    Context mContext;
    Context context;
    ArrayList<Party> list;
    private int count=0;
    private int ccc=0;
    public String userid;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView image;
        public LinearLayout mix;
        public TextView appointment;
        public TextView maxpeople;
        public TextView RoomName;
        public TextView currentpeople;
        public TextView detail;
        public ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.imageView1);
            RoomName = (TextView) v.findViewById(R.id.textView1);
            appointment = (TextView) v.findViewById(R.id.Location_text);
            maxpeople = (TextView) v.findViewById(R.id.max);
            currentpeople = (TextView) v.findViewById(R.id.people);
            detail = (TextView) v.findViewById(R.id.detail);
            mix = (LinearLayout) v.findViewById(R.id.mix);
        }
    }

    public Join_Recycler(Context mcontext, ArrayList<Party> room_name, String userid) {
        this.mContext = mcontext;
        this.list = room_name;
        this.userid = userid;
    }


    @Override
    public Join_Recycler.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_join__recycler, parent, false);
        Join_Recycler.ViewHolder vh = new Join_Recycler.ViewHolder(view);
        context = view.getContext();
        return vh;

    }

    @Override
    public void onBindViewHolder(final Join_Recycler.ViewHolder holder, final int position) {

        final String pic_url = list.get(position).getPicture();
        if (pic_url != null) {
            String[] tmp = pic_url.split("/");
            StorageReference load = FirebaseStorage.getInstance().getReference().child(tmp[1]).child(tmp[2]);

            load.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL for 'users/me/profile.png'
                    // Pass it to Picasso to download, show in ImageView and caching
                    Log.i("TEST:", " recycler count : "+ccc);
                    ccc++;
                    try
                    {
                        Picasso.with(mContext).cancelRequest(holder.imageView);
                        Picasso.with(mContext).load(uri.toString()).resize(250, 250).into(holder.imageView);
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                    Log.i("TEST:", " join recycler uri " + " : " + uri.toString() + " postition : "+position);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors

                    Log.i("TEST: ", " join recycler in List err " +  " : " + exception.toString());
                    exception.printStackTrace();
                }
            });
        }

        if (list.get(position).getAccept() != null) {
            String[] people = list.get(position).getAccept().split(",");
            count = people.length;
        }

        holder.mix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Show_party_profile.class);
                intent.putExtra(Party.Column.RoomID, list.get(position).getRoomID());
                intent.putExtra(UserProfile.Column.UserID, userid);
                mContext.startActivity(intent);
            }
        });

        holder.RoomName.setText(list.get(position).getName());
        holder.appointment.setText(list.get(position).getAppointment());
        holder.maxpeople.setText(Integer.toString(list.get(position).getPeople()));
        holder.currentpeople.setText(Integer.toString(count));
        holder.detail.setText("     " + list.get(position).getDescription());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public ArrayList<Party> getlist(){
        return list;
    }

}