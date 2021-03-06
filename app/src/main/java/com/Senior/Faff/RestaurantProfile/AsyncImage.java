package com.Senior.Faff.RestaurantProfile;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.Senior.Faff.utils.BitmapImageManager;

/**
 * Created by InFiNity on 19-Feb-17.
 */

public class AsyncImage extends AsyncTask<String,Void,Bitmap>{
    Context mcontext;
    ImageView view;
    int res_id;
    public AsyncImage(Context context, ImageView view, int res_id){
        mcontext = context;
        this.view = view;
        this.res_id = res_id;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        BitmapImageManager bitmap = new BitmapImageManager();
        return  bitmap.decodeSampledBitmapFromResource(mcontext.getResources(),res_id,300,300);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        view.setImageBitmap(bitmap);
    }
}
