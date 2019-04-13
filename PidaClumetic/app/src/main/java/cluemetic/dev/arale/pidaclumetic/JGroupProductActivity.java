package cluemetic.dev.arale.pidaclumetic;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class JGroupProductActivity extends AppCompatActivity {

    TextView mcompany, mtitle, mprice, mseller, mmanufac, mcountry, minfourl, mpriceNow;
    ImageView mimg;
    Bitmap bm = null;
    String id, com, tit, pri, sell, manu, coun, inf, img, group_url;
    ListView mreview, mingredient;
    HProductIngredientsAdapter ingredientAdapter;
    HProductReviewAdapter reviewAdapter;
    String json_url="http://ec2-13-125-246-38.ap-northeast-2.compute.amazonaws.com/products/";
    String sale1q, sale1r, sale2q, sale2r, sale3q, sale3r;
    TextView msale1q, msale1r, msale2q, msale2r, msale3q, msale3r;
    String productU;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_j_group_product);

        Intent intent = getIntent();
        group_url = intent.getStringExtra("group_url");

        new GroupProductCreate().execute();





    }




    class GroupProductCreate extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {


            try{
                URL url = new URL(group_url);
                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setRequestMethod("GET");
                urlConn.setDoInput(true);

                if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    InputStream stream = urlConn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuilder buffer = new StringBuilder();
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    reader.close();
                    JSONObject curr = new JSONObject(buffer.toString());
                    urlConn.disconnect();

                    productU = curr.getString("product");
                    sale1q = String.valueOf(curr.getJSONArray("discount_rates").getJSONObject(0).getInt("quantity"));
                    sale1r = String.valueOf(curr.getJSONArray("discount_rates").getJSONObject(0).getInt("rate"));
                    sale2q = String.valueOf(curr.getJSONArray("discount_rates").getJSONObject(1).getInt("quantity"));
                    sale2r = String.valueOf(curr.getJSONArray("discount_rates").getJSONObject(1).getInt("rate"));
                    sale3q = String.valueOf(curr.getJSONArray("discount_rates").getJSONObject(2).getInt("quantity"));
                    sale3r = String.valueOf(curr.getJSONArray("discount_rates").getJSONObject(2).getInt("rate"));


                    return null;
                }

                urlConn.disconnect();
                return null;


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new ProductCreate().execute();
        }
    }



    class ProductCreate extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {


            try{
                URL url = new URL(productU);
                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setRequestMethod("GET");
                urlConn.setDoInput(true);

                if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    InputStream stream = urlConn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuilder buffer = new StringBuilder();
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    reader.close();
                    JSONObject JsonResult = new JSONObject(buffer.toString());
                    urlConn.disconnect();

                    com = JsonResult.getJSONObject("brand").getString("name");
                    tit = JsonResult.getString("name");
                    pri = String.valueOf(JsonResult.getInt("price"));
                    sell = JsonResult.getString("info_seller");
                    manu = JsonResult.getString("info_manufacturer");
                    coun = JsonResult.getString("info_country");
                    inf = JsonResult.getString("info_url");
                    img = JsonResult.getString("image");
                    id = String.valueOf(JsonResult.getInt("id"));

                    return null;
                }

                urlConn.disconnect();
                return null;


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            DoOnCreate();
        }
    }




    void DoOnCreate(){

        mimg = findViewById(R.id.img);
        new ShowImageConnection().execute(img);

        mtitle = findViewById(R.id.title);
        mcompany = findViewById(R.id.company);
        mprice = findViewById(R.id.priceT);
        mpriceNow = findViewById(R.id.priceN);
        mseller = findViewById(R.id.inform1);
        mmanufac = findViewById(R.id.inform2);
        mcountry = findViewById(R.id.inform3);
        minfourl = findViewById(R.id.inform4);
        msale1q = findViewById(R.id.sale1);
        msale1r = findViewById(R.id.sale11);
        msale2q = findViewById(R.id.sale2);
        msale2r = findViewById(R.id.sale22);
        msale3q = findViewById(R.id.sale3);
        msale3r = findViewById(R.id.sale33);

        mtitle.setText(tit);
        mcompany.setText(com);
        mprice.setText("정가 " + pri + "원");
        mseller.setText(sell);
        mmanufac.setText(manu);
        mcountry.setText(coun);
        msale1q.setText(sale1q + "개 ~ ");
        msale1r.setText(sale1r + "% 할인");
        msale2q.setText(sale2q + "개 ~ ");
        msale2r.setText(sale2r + "% 할인");
        msale3q.setText(sale3q + "개 ~ ");
        msale3r.setText(sale3r + "% 할인");
        mpriceNow.setText(""); //현재 가격을 알 수 없음

        minfourl.setOnClickListener(v -> {
            Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse(inf) );
            startActivity( browse );
        });

        //scrolls
        mingredient = findViewById(R.id.Ingredient);
        mreview = findViewById(R.id.review);
        new IngredientConnection().execute();

        Button order = findViewById(R.id.group_order);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });

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
                    Log.i("###", arr.toString());

                }
                ingredientAdapter = new HProductIngredientsAdapter(getBaseContext(), arr);
                mingredient.setAdapter(ingredientAdapter);
                setListViewHeightBasedOnChildren(mingredient);

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
            setListViewHeightBasedOnChildrenReview(mreview);
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

    public void setListViewHeightBasedOnChildren(ListView listView) {
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < ingredientAdapter.getCount(); i++) {
            View listItem = ingredientAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (ingredientAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
    public void setListViewHeightBasedOnChildrenReview(ListView listView) {
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < reviewAdapter.getCount(); i++) {
            View listItem = reviewAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (ingredientAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }



    void show()
    {
        AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
        builder3.setTitle("공동구매");
        builder3.setMessage("구매할 상품의 개수를 선택해주세요. \n\n 공동구매의 경우 상품은 원가로 결제되며, 공동구매가 마감된 이후 할인액만큼 환불이 이루어집니다. \n\n 주의하세요! 확인을 누르면 구매가 확정됩니다.");
        final int[] num = {1};

        NumberPicker np = new NumberPicker(this);
        np.setMaxValue(10);
        np.setMinValue(1);
        NumberPicker.OnValueChangeListener npListener = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                num[0] = newVal;
            }
        };
        np.setOnValueChangedListener(npListener);

        builder3.setView(np);

        builder3.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new Purchase(group_url, num[0]).execute();
                    }
                });
        builder3.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder3.show();
    }

    private class Purchase extends AsyncTask<String, Void, Boolean> {
        String group_url_str;
        Integer num;

        public Purchase(String group_url_str, Integer num) {
            this.group_url_str = group_url_str;
            this.num = num;
        }

        @Override
        protected void onPreExecute() { }


        @Override
        protected Boolean doInBackground(String... params) {
            try{

                SharedPreferences dd = getSharedPreferences("user", MODE_PRIVATE);
                String token = dd.getString("access_token", "");
                URL url = new URL("http://ec2-13-125-246-38.ap-northeast-2.compute.amazonaws.com/group-purchase-orders/?access_token="+token);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setDoOutput(true);
                con.setDoInput(true);
                con.connect();

                SharedPreferences tut = getSharedPreferences("user", MODE_PRIVATE);
                JSONObject post = new JSONObject();
                post.put("event", group_url_str);
                post.put("quantity", num);
                post.put("delivery_information", tut.getString("delivery", ""));
                post.put("payment_information", tut.getString("payment", ""));

                OutputStreamWriter os= new OutputStreamWriter(con.getOutputStream());
                os.write( post.toString() );
                os.flush();


                Log.i("###", "group purchase at group product activity");
                Log.i("###", post.toString());
                Log.i("###", String.valueOf(con.getResponseCode()));
                Log.i("###", con.getResponseMessage());
                if ((con.getResponseCode() == HttpURLConnection.HTTP_OK) || (con.getResponseCode() == HttpURLConnection.HTTP_CREATED))
                    return true;


            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                Intent i = new Intent(getBaseContext(), QPurchaseDoneActivity.class);
                i.putExtra("type", 1);
                i.putExtra("url", img);
                startActivity(i);
                finish();
            }

            else Toast.makeText(JGroupProductActivity.this, "주문에 실패하였습니다. 입력한 정보를 입력해주세요", Toast.LENGTH_SHORT).show();
        }
    }



}
