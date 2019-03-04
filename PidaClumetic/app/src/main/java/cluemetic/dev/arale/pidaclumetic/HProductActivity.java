package cluemetic.dev.arale.pidaclumetic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class HProductActivity extends AppCompatActivity {
    Intent intent;
    Integer count;
    String[] ingredients, reviews;
    String json_url="http://ec2-13-125-246-38.ap-northeast-2.compute.amazonaws.com/products/";
    String info_url="";
    TextView mcompany, mtitle, mprice, mseller, mmanufac, mcountry, minfourl;
    ImageView mimg;
    Bitmap bm = null;
    String id, com, tit, pri, sell, manu, coun, inf, img;
    ListView mreview, mingredient;
    HProductIngredientsAdapter ingredientAdapter;
    HProductReviewAdapter reviewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h_product);


        intent = getIntent();
        id = intent.getStringExtra("id");

        //get bitmap image from img and set mimg
        img = intent.getStringExtra("image");
        mimg = findViewById(R.id.img);
        new ShowImageConnection().execute(img);

        //set views
        com = intent.getStringExtra("brand");
        tit = intent.getStringExtra("title");
        pri = intent.getStringExtra("price");
        sell = intent.getStringExtra("info_seller");
        manu = intent.getStringExtra("info_manufacturer");
        coun = intent.getStringExtra("info_country");
        inf = intent.getStringExtra("info_url");
        mtitle = findViewById(R.id.title);
        mcompany = findViewById(R.id.company);
        mprice = findViewById(R.id.price);
        mseller = findViewById(R.id.inform1);
        mmanufac = findViewById(R.id.inform2);
        mcountry = findViewById(R.id.inform3);
        minfourl = findViewById(R.id.inform4);
        mtitle.setText(tit);
        mcompany.setText(com);
        mprice.setText(pri);
        mseller.setText(sell);
        mmanufac.setText(manu);
        mcountry.setText(coun);
        minfourl.setOnClickListener(v -> {
            Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse(inf) );
            startActivity( browse );
        });

        //scrolls
        mingredient = findViewById(R.id.Ingredient);
        mreview = findViewById(R.id.review);
        new IngredientConnection().execute();

    }






    private class IngredientConnection extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected void onPreExecute() { }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                Log.i("###", "111111");
                //send get request
                URL url = new URL(json_url + id);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Content-Type", "application/json");
                con.setDoInput(true);
                con.connect();

                //get JSONObject result
                Log.i("###", "111111");
                StringBuilder result = new StringBuilder();
                InputStream in = new BufferedInputStream(con.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                //get response
                JSONObject response = new JSONObject(result.toString());

                Log.i("###", "good response");
                return response;


            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return  null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            JSONArray responseIngredeint = null;
            try {
                ArrayList<HIngredient> arr = new ArrayList<HIngredient>();
                responseIngredeint = result.getJSONArray("ingredients");
                for (int i = 0; i < responseIngredeint.length(); i++) {
                    String currName = responseIngredeint.getJSONObject(i).getString("name");
                    Integer currGrade = responseIngredeint.getJSONObject(i).getInt("ewg_grade");
                    Log.i("###", "in the switch, currGrade = " + String.valueOf(currGrade));
                    Log.i("###", "in the switch, name = " + currName);
                    switch (currGrade){
                        case 1:
                            arr.add(new HIngredient(ContextCompat.getDrawable(getBaseContext(), R.drawable.ingredients_icons_grade_1), currName));
                            break;
                        case 2:
                            arr.add(new HIngredient(ContextCompat.getDrawable(getBaseContext(), R.drawable.ingredients_icons_grade_2), currName));
                            break;
                        case 3:
                            arr.add(new HIngredient(ContextCompat.getDrawable(getBaseContext(), R.drawable.ingredients_icons_grade_3), currName));
                            break;
                        case 4:
                            arr.add(new HIngredient(ContextCompat.getDrawable(getBaseContext(), R.drawable.ingredients_icons_grade_4), currName));
                            break;
                        case 5:
                            arr.add(new HIngredient(ContextCompat.getDrawable(getBaseContext(), R.drawable.ingredients_icons_grade_5), currName));
                            break;
                        case 6:
                            arr.add(new HIngredient(ContextCompat.getDrawable(getBaseContext(), R.drawable.ingredients_icons_grade_6), currName));
                            break;
                        case 7:
                            arr.add(new HIngredient(ContextCompat.getDrawable(getBaseContext(), R.drawable.ingredients_icons_grade_7), currName));
                            break;
                        case 8:
                            arr.add(new HIngredient(ContextCompat.getDrawable(getBaseContext(), R.drawable.ingredients_icons_grade_8), currName));
                            break;
                        default:
                            arr.add(new HIngredient(ContextCompat.getDrawable(getBaseContext(), R.drawable.ingredients_icons_grade_9), currName));
                            break;
                    }

                }
                ingredientAdapter = new HProductIngredientsAdapter(arr);
                mingredient.setAdapter(ingredientAdapter);

                JSONArray responseUrl = result.getJSONArray("reviews");
                startReviewConnection(responseUrl);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    Void startReviewConnection(JSONArray arr){
        new ReviewConnection().execute(arr);
        return null;
    }

    private class ReviewConnection extends AsyncTask<JSONArray, Void, ArrayList<HReview>> {
        @Override
        protected void onPreExecute() { }

        @Override
        protected ArrayList<HReview> doInBackground(JSONArray... params) {
            try {
                JSONArray reviewUrls = params[0];
                ArrayList<HReview> arr = new ArrayList<>();
                for (int i = 0; i < reviewUrls.length(); i++){

                    //send get request
                    URL url = new URL(reviewUrls.getString(i));
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setDoInput(true);
                    con.connect();

                    //get JSONObject result
                    StringBuilder result = new StringBuilder();
                    InputStream in = new BufferedInputStream(con.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    //get strings from response and add to adapter
                    JSONObject response = new JSONObject(result.toString());
                    arr.add(new HReview(response.getString("owner").substring(68,70)+"**", response.getString("content")));
                }
                return arr;

            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return  null;
        }

        @Override
        protected void onPostExecute(ArrayList arr) {

            reviewAdapter = new HProductReviewAdapter(arr);
            mreview.setAdapter(reviewAdapter);
        }
    }





    private class ShowImageConnection extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() { }

        @Override
        protected Boolean doInBackground(String... params) {

            try {
                URL aURL = new URL(params[0]);
                URLConnection conn = aURL.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mimg.setImageBitmap(bm);
        }
    }


}