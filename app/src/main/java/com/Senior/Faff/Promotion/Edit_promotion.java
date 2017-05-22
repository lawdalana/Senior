package com.Senior.Faff.Promotion;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.Senior.Faff.R;
import com.Senior.Faff.RestaurantProfile.Edit_RestaurantProfile;
import com.Senior.Faff.model.Promotion;
import com.Senior.Faff.model.Restaurant_Promotion;
import com.Senior.Faff.utils.Helper;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Edit_promotion extends AppCompatActivity {

    private Context mContext;
    private Toolbar toolbar;
    private Button uploadPicture;
    private static final int request_code = 1;                          //request code for OnClick result

    public static ArrayList<Bitmap> bmap = new ArrayList<>();           //keep bitmap data
    public static ArrayList<String> imgPath = new ArrayList<>();        //keep uri
//    public static ArrayList<byte[]> imgByte = new ArrayList<>();        //keep byte data

    public static int image_count = 0;                                    //number of images
    private Edit_promotion_RecyclerView adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_promotion);

        mContext = this;
        Intent i = getIntent();
        String id = i.getExtras().getString("id");

        ListPromotion lsp = new ListPromotion(new ListPromotion.AsyncResponse() {
            @Override
            public void processFinish(String output) throws JSONException {
                JSONObject item = new JSONObject(output);

                //Log.i(TAG, "  item is : "+item.toString());

                Promotion data = new Gson().fromJson(item.toString(), Promotion.class);
                String[] arr_url = item.getString("promotionpictureurl").split(",");

                EditText textView1 = (EditText) findViewById(R.id.title);
                textView1.setText(data.getTitle());

                EditText textView2 = (EditText) findViewById(R.id.startDate);
                textView2.setText(data.getStartDate());

                EditText textView3 = (EditText) findViewById(R.id.endDate);
                textView3.setText(data.getEndDate());

                EditText textView4 = (EditText) findViewById(R.id.promotionDetail);
                textView4.setText(data.getPromotionDetail());


                toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(false);

                uploadPicture = (Button) findViewById(R.id.upload_button);

                uploadPicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent sdintent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        //sdintent.setType("image/*");
                        startActivityForResult(sdintent, request_code);

                    }
                });

                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rlist1);
                PromotionViewImageRecyclerViewAdapter adapter = new PromotionViewImageRecyclerViewAdapter(mContext, arr_url);
                LinearLayoutManager mLayoutManager  = new LinearLayoutManager(mContext);
                mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setAdapter(adapter);

            }
        });
        lsp.execute(id);



    }

    @Override
    protected void onDestroy() {
        image_count = 0;
        bmap = new ArrayList<>();
        imgPath = new ArrayList<>();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == request_code && data != null) {
                Uri selectedImg = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cur = getContentResolver().query(selectedImg, filePathColumn, null, null, null);
                if (cur == null) imgPath.add(null);
                else {
                    int column_index = cur.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cur.moveToFirst();
                    imgPath.add(cur.getString(column_index));
                    cur.close();
                }
                Bitmap bm = BitmapFactory.decodeFile(imgPath.get(image_count));
                try {
                    bm = new Helper().modifyOrientation(bm, imgPath.get(image_count));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bmap.add(bm);
                //convert to byte
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmap.get(image_count).compress(Bitmap.CompressFormat.PNG, 100, stream);
//                imgByte.add(stream.toByteArray());
                image_count++;

                adapter = new Edit_promotion_RecyclerView(this, bmap);
                RecyclerView lv = (RecyclerView) findViewById(R.id.rlist1);
                lv.setNestedScrollingEnabled(false);
                lv.setAdapter(adapter);
            }
        }
    }

    private class EditPromotion extends AsyncTask<Promotion, String, String> {

        private ArrayList<String> imgPath = new ArrayList<>();
        String result = "";

        @Override
        protected String doInBackground(Promotion... params) {
            try {
                Map<String, String> paras = new HashMap<>();
                paras.put(Promotion.Column.ID, String.valueOf(params[0].getId()));
                paras.put(Promotion.Column.Title, params[0].getTitle());
//                paras.put(Promotion.Column.Type, params[0].getType());
                paras.put(Promotion.Column.StartDate, params[0].getStartDate());
                paras.put(Promotion.Column.EndDate, params[0].getEndDate());
                paras.put(Promotion.Column.PromotionDetail, params[0].getPromotionDetail());
                paras.put(Promotion.Column.Location, params[0].getGoogleMapLink());

                String img_path_tmp = params[0].getPromotionpictureurl();
                Edit_promotion.EditPromotion.this.imgPath = new Gson().fromJson(img_path_tmp, ArrayList.class);

                URL url = new URL("https://faff-1489402013619.appspot.com/promotion_list/" + params[0].getId());

                Helper hp = new Helper();
                hp.setRequest_method("PUT");
                result = hp.multipartRequest(url.toString(), paras, Edit_promotion.EditPromotion.this.imgPath, "image", "image/jpeg");
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != "") {

                Edit_promotion.getCount gc = new Edit_promotion.getCount();
                gc.execute("");

                //Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class getCount extends AsyncTask<String, String, String> {

        private String result;

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("https://faff-1489402013619.appspot.com/promotion_list/get_count");

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
                //Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
                try {
                    JSONArray item = new JSONArray(result);
                    String n = item.getJSONObject(0).getString("n");

                    Edit_promotion.getCount.Linking lk = new Edit_promotion.getCount.Linking();
                    //lk.execute(new Restaurant_Promotion(resid, n));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                //Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
            }
        }

        private class Linking extends AsyncTask<Restaurant_Promotion, String, String> {
            private HttpURLConnection connection;
            private int responseCode;

            @Override
            protected String doInBackground(Restaurant_Promotion... params) {
                try {
                    JSONObject paras = new JSONObject();
                    paras.put(Restaurant_Promotion.Column.resid, params[0].getResid());
                    paras.put(Restaurant_Promotion.Column.promotionid, params[0].getPromotionid());

                    URL url = new URL("https://faff-1489402013619.appspot.com/restaurant_promotion/create");
                    //URL url = new URL("http://localhost:8080/promotion_list/new_promotion");

                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);

                    OutputStream out = new BufferedOutputStream(connection.getOutputStream());

                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                    bufferedWriter.write(getPostDataString(paras));
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    out.close();

                    responseCode = connection.getResponseCode();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                if (responseCode == 200) {
                    Log.i("TEST:", " res_promo This is success response status from server: " + responseCode);

                    return String.valueOf(responseCode);
                } else {
                    Log.i("Request Status", "Tsis is failure response status from server: " + responseCode);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != "") {
                    finish();
                    //Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
                } else {
                    //Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                }
            }

            public String getPostDataString(JSONObject params) throws Exception {

                StringBuilder result = new StringBuilder();
                boolean first = true;

                Iterator<String> itr = params.keys();

                while (itr.hasNext()) {

                    String key = itr.next();
                    Object value = params.get(key);

                    if (first)
                        first = false;
                    else
                        result.append("&");

                    result.append(URLEncoder.encode(key, "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(value.toString(), "UTF-8"));

                }
                return result.toString();
            }
        }
    }


    private static class ListPromotion extends AsyncTask<String, String, String> {

        String result = "";

        public interface AsyncResponse {
            void processFinish(String output) throws JSONException;
        }

        public Edit_promotion.ListPromotion.AsyncResponse delegate = null;

        public ListPromotion(Edit_promotion.ListPromotion.AsyncResponse delegate){
            this.delegate = delegate;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("https://faff-1489402013619.appspot.com/promotion_list/"+Integer.parseInt(params[0]));
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
                //Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
                try {
                    delegate.processFinish(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                //Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
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
}
