package cluemetic.dev.arale.pidaclumetic;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class RPurchaseDescriptionActivity extends AppCompatActivity {

    Button mgoPayment2, mgoDelivery3;
    ImageButton mImgBtn;
    ImageView mImgUrl;
    ListView listview;
    Button delivery;
    Intent intent;
    Integer type, status;
    String imgUrl, purchaseUrl, time, price;
    String pay, del;
    String access_token;
    RPurchaseDescrAdaptar adapter;
    ArrayList<ROnePurchaseItem> listViewItemList;
    ArrayList<String> urls = new ArrayList<>();
    ArrayList<Integer> integers = new ArrayList<>();
    Integer dp;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_r_purchase);

        intent = getIntent();
        purchaseUrl = intent.getStringExtra("url");
        imgUrl  = intent.getStringExtra("img");
        type = intent.getIntExtra("type", 0);
        time = intent.getStringExtra("time");
        status = intent.getIntExtra("status", 0);
        listview = (ListView) findViewById(R.id.list);
        adapter = new RPurchaseDescrAdaptar(new ArrayList<>()) ;
        listview.setAdapter(adapter);
        listViewItemList = new ArrayList<>();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float logicalDensity = metrics.density;
        dp = (int) Math.ceil(logicalDensity*72);

        ProgressBar progressBar = findViewById(R.id.progress);
        if (status == 0){
            TextView textView = findViewById(R.id.status1);
            textView.setTextColor(getResources().getColor(R.color.colorPrimary));
            progressBar.setProgress(5);
        }else if (status == 1){
            TextView textView = findViewById(R.id.status2);
            textView.setTextColor(getResources().getColor(R.color.colorPrimary));
            progressBar.setProgress(50);
        }else{
            TextView textView = findViewById(R.id.status3);
            textView.setTextColor(getResources().getColor(R.color.colorPrimary));
            progressBar.setProgress(100);
        }

        //set image
        mImgBtn = findViewById(R.id.imageButton);
        mImgUrl = findViewById(R.id.imgURI);
        new DownloadImageTask(mImgUrl, mImgBtn, status).execute(imgUrl);

        SharedPreferences sh = getSharedPreferences("user", MODE_PRIVATE);
        access_token = sh.getString("access_token", "");

        //set price and lists
        new SetPriceAndMakeList().execute();


        // 위에서 생성한 listview에 클릭 이벤트 핸들러 정의.
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                ROnePurchaseItem item = (ROnePurchaseItem) parent.getItemAtPosition(position) ;
                if (type == 2){
                    Intent go = new Intent(getBaseContext(), JGroupProductActivity.class);
                    go.putExtra("group_url", "http://ec2-13-125-246-38.ap-northeast-2.compute.amazonaws.com/group-purchase-events/" + item.getId() + "/");
                    startActivity(go);
                }
                else {
                    Intent go = new Intent(getBaseContext(), HProductActivity.class);
                    go.putExtra("id", item.getId());
                    startActivity(go);
                }

            }
        }) ;

    }

    class SetPriceAndMakeList extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String strParams = "?access_token=" + access_token;
                URL url = new URL(purchaseUrl + strParams);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setDoInput(true);
                con.connect();

                InputStream stream = con.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                con.disconnect();
                reader.close();

                JSONObject Jres = new JSONObject(buffer.toString());

                Log.i("###", "!!!!!!!!!!!!!!!!!!!!!!");

                if (type == 0){
                    price = Jres.getString("price");
                    pay = Jres.getString("payment_information");
                    del = Jres.getString("delivery_information");
                    Log.i("###", "!!!!!!!!!!!!!!!!!!!!!!");

                    JSONArray products = Jres.getJSONArray("products");
                    Log.i("###", "!!!!!!!!!!!!!!!!!!!!!!22222");
                    for (int i = 0; i<products.length(); i++){

                        urls.add(products.getString(i));
                        integers.add(0);
                    }

                }else if (type == 1){
                    price = Jres.getString("price");
                    pay = Jres.getString("payment_information");
                    del = Jres.getString("delivery_information");
                    Log.i("###", "!!!!!!!!!!!!!!!!!!!!!!");

                    JSONArray products = Jres.getJSONArray("items");
                    Log.i("###", "!!!!!!!!!!!!!!!!!!!!!!222222");
                    for (int i = 0; i<products.length(); i++){
                        JSONObject curr = products.getJSONObject(i);
                        urls.add(curr.getString("product"));
                        integers.add(curr.getInt("quantity"));
                    }

                }else{
                    Log.i("###", "!!!!!!!!!!!!!!!!!!!!!!");
                    price = "정가 결제 후, 할인율에 따라 환불됩니다";
                    pay = Jres.getString("payment_information");
                    del = Jres.getString("delivery_information");
                    urls.add(Jres.getString("event"));
                    integers.add(Jres.getInt("quantity"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            TextView inf1 = findViewById(R.id.inform1);
            TextView inf2 = findViewById(R.id.inform2);
            inf1.setText(time);
            inf2.setText(price);

//            payment = findViewById(R.id.payment);
//            payment.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(RPurchaseDescriptionActivity.this, ISetupAccount2ReadOnlyActivity.class);
//                    intent.putExtra("url", pay);
//                    startActivity(intent);
//                }
//            });

            delivery = findViewById(R.id.delivery);
            delivery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RPurchaseDescriptionActivity.this, ISetupAccount3ReadOnlyActivity.class);
                    intent.putExtra("url", del);
                    startActivity(intent);
                }
            });

            new AddItem().execute();


        }
    }

    class AddItem extends AsyncTask<Integer, Void, Boolean>{
        String productUrl, curr0;
        Integer curr1, curr2;


        @Override
        protected Boolean doInBackground(Integer... params) {
            try {

                for (int j = 0;j<urls.size(); j++){

                    URL url = new URL(urls.get(j));
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setDoInput(true);
                    con.connect();

                    InputStream stream = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuffer buffer = new StringBuffer();
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    con.disconnect();
                    reader.close();

                    JSONObject Jres = new JSONObject(buffer.toString());

                    if (type == 0){
                        adapter.addItem(type,
                                Jres.getString("image"),
                                Jres.getString("name"),
                                "정가 " + String.valueOf(Jres.getInt("price")) + "원",
                                String.valueOf(Jres.getInt("id")));

                    }else if (type == 1){
                        Log.i("###", "$$$$$$$$$$$$$$$$$$$");
                        adapter.addItem(type,
                                Jres.getString("image"),
                                Jres.getString("name"),
                                String.valueOf(Jres.getInt("price")) + "원  " + String.valueOf(integers.get(j)) + "개",
                                String.valueOf(Jres.getInt("id")));
                        Log.i("###", type.toString());
                        Log.i("###", Jres.getString("image"));
                        Log.i("###", Jres.getString("name"));
                        Log.i("###", String.valueOf(Jres.getInt("price")) + "원  " + String.valueOf(integers.get(j)) + "개");
                        Log.i("###", "product id is " + String.valueOf(Jres.getInt("id")));

                    }else{
                        Log.i("###", "@@@@@@@@@@@@@@@@@@");
                        curr0 = Jres.getString("product");
                        curr1 = integers.get(j);
                        curr2 = Jres.getInt("id");
                        return true;

                    }
                }




            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) new AddGroupItem(curr0).execute(curr1, curr2);
            adapter.refreshItem();
            setListViewHeightBasedOnChildren(listview);
        }
    }



    class AddGroupItem extends AsyncTask<Integer, Void, Void>{
        String productUrl;

        public AddGroupItem(String productUrl) {
            this.productUrl = productUrl;
        }

        @Override
        protected Void doInBackground(Integer... params) {
            try {
                Log.i("###", "&&&&&&&&&&&&&&&&&&&");
                URL url = new URL(productUrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setDoInput(true);
                con.connect();

                InputStream stream = con.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                con.disconnect();
                reader.close();

                JSONObject Jres = new JSONObject(buffer.toString());

                Log.i("###", "@@@@@@@@~~~~~~@@@@@@@@@@");
                adapter.addItem(type,
                        Jres.getString("image"),
                        Jres.getString("name"),
                        "정가 " + String.valueOf(Jres.getInt("price")) + "원  " + String.valueOf(params[0]) + "개",
                        String.valueOf(params[1]));
                Log.i("###", type.toString());
                Log.i("###", Jres.getString("image"));
                Log.i("###", Jres.getString("name"));
                Log.i("###", "정가 " + String.valueOf(Jres.getInt("price")) + "원  " + String.valueOf(params[0]) + "개");
                Log.i("###", "group event id is " + String.valueOf(params[1]));

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter.refreshItem();
            setListViewHeightBasedOnChildren(listview);
        }
    }


    void setListViewHeightBasedOnChildren(ListView listView) {
        Log.i("###", String.valueOf(dp));
        Log.i("###", String.valueOf(dp * urls.size()));

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = dp * urls.size();
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        ImageButton ImgB;
        Integer status;

        public DownloadImageTask(ImageView bmImage, ImageButton imgB, Integer status) {
            this.bmImage = bmImage;
            this.ImgB = imgB;
            this.status = status;
        }

        protected Bitmap doInBackground(String... urls) {
            if (type==0) return null;
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.i("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if (type==0) {
                ImgB.setImageResource(R.drawable.selection_3_selected);
                ImgB.setBackgroundResource(R.drawable.btn_mypida_not_transparent);
            }
            else bmImage.setImageBitmap(result);
        }
    }


}
