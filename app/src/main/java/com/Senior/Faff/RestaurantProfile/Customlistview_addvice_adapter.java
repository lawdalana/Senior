package com.Senior.Faff.RestaurantProfile;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.Senior.Faff.R;
import com.Senior.Faff.model.Restaurant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by InFiNity on 19-Feb-17.
 */

public class Customlistview_addvice_adapter extends ArrayAdapter<Restaurant> {
    Context mcontext;
    int[] res_id;
    Restaurant[] res_name;
    LayoutInflater inflater;
    private String type_result;

    public Customlistview_addvice_adapter(Context context, int tv, Restaurant[] res_name, int[] res_id) {
        super(context, tv, res_name);
        this.res_name = res_name;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = null;
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_view_advice_restaurant, parent, false);
            imageView = (ImageView) convertView.findViewById(R.id.image);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            imageView = (ImageView) convertView.findViewById(R.id.image);
        }

        String[] img_path = res_name[position].getPicture().split(",");
        Log.i("TEST: ", img_path[0]);
        Picasso.with(getContext()).load(img_path[0]).resize(300, 300).into(imageView);

        viewHolder.ResName.setText(res_name[position].getRestaurantName());
        viewHolder.detail.setText("   " + res_name[position].getDescription());
        viewHolder.period.setText(res_name[position].getPeriod());

        return convertView;
    }

    private class ViewHolder {
        public TextView ResName;
        public TextView detail;
        public TextView period;

        public ViewHolder(View convertView) {
            ResName = (TextView) convertView.findViewById(R.id.textView1);
            detail = (TextView) convertView.findViewById(R.id.detail);
            period = (TextView) convertView.findViewById(R.id.open_text);
        }
    }

}
