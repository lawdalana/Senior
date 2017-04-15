package com.Senior.Faff.Promotion;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.Senior.Faff.R;
import com.Senior.Faff.model.Promotion;
import com.Senior.Faff.model.PromotionPicture;
import com.Senior.Faff.model.promotion_view_list;

import java.util.ArrayList;

/**
 * Created by Not_Today on 3/17/2017.
 */

public class PromotionShowAdapter extends BaseAdapter{

    public static final String TAG = PromotionShowAdapter.class.getSimpleName();
    Context mContext;
    ArrayList<promotion_view_list> promotions = new ArrayList<>();

    public PromotionShowAdapter(Context mContext, ArrayList<promotion_view_list> promotions) {
        this.mContext = mContext;
        this.promotions = promotions;
    }

    @Override
    public int getCount() {
        return promotions.size();
    }

    @Override
    public Object getItem(int position) {
        return promotions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Integer.parseInt(promotions.get(position).getId());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null)
            convertView = mInflater.inflate(R.layout.listview_row_promotionshow, parent, false);

        TextView textView1 = (TextView)convertView.findViewById(R.id.textView1);
        textView1.setText(promotions.get(position).getTitle());

        TextView textView2 = (TextView)convertView.findViewById(R.id.textView2);
        textView2.setText(promotions.get(position).getPromotionDetail());


        ImageView imageView = (ImageView)convertView.findViewById(R.id.imageView1);
        imageView.setImageBitmap(promotions.get(position).getPicture().get(0));

        return convertView;
    }
}