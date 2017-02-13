package com.devahoy.sample.Faff.Fragment.Adapter;

/**
 * Created by InFiNity on 09-Feb-17.
 */
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.devahoy.sample.Faff.Fragment.Content_MainFragment;
import com.devahoy.sample.Faff.Fragment.CreateGroup_MainFragment;
import com.devahoy.sample.Faff.Fragment.Search_MainFragment;
import com.devahoy.sample.Faff.R;

public class MainMenu_adpater extends FragmentPagerAdapter   {
    private final int PAGE_NUM = 3;
    private String tabTitles[] = new String[] { "ร้านอาหารแนะนำ", "แนะนำโปรโมชั่น", "ร้านอาหารยอดนิยม" };
    private Context context;
    private int[] imageResId = {
            R.drawable.find_res,
            R.drawable.find_res,
            R.drawable.find_res
    };
    public MainMenu_adpater(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0){
            return new CreateGroup_MainFragment();
        }
        else if(position == 1) {
            return new Content_MainFragment();
        }
        else if(position == 2){
            return  new Search_MainFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return PAGE_NUM;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
         return tabTitles[position];

        // getDrawable(int i) is deprecated, use getDrawable(int i, Theme theme) for min SDK >=21
        // or ContextCompat.getDrawable(Context context, int id) if you want support for older versions.
        //Drawable image = context.getResources().getDrawable(iconIds[position], context.getTheme());
        // Drawable image = context.getResources().getDrawable(imageResId[position]);

     /*    Drawable image = ContextCompat.getDrawable(context, imageResId[position]);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        SpannableString sb = new SpannableString(" " + tabTitles[position]);
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;*/
    }

}