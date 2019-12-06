package cluemetic.dev.arale.pidaclumetic;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.ImageContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OPidaActivity extends AppCompatActivity {


    List<OPidaProduct> products;
    ViewPager viewPager;
    OPidaProductAdapter adapter;
    private BottomNavigationView navigationView;
    private long backKeyPressedTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_mypida);

        //제품들 보여주기
        new GetPidaData().execute();

        //네비게이션뷰 설정(클릭시 이동)
        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(menuItem -> {
            navigationView.postDelayed(() -> {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.navigation_category) {
                    startActivity(new Intent(getBaseContext(), ECategoryActivity.class));
                } else if (itemId == R.id.navigation_basket) {
                    startActivity(new Intent(this, NBasketActivity.class));
                } else if (itemId == R.id.navigation_group) {
                    startActivity(new Intent(getBaseContext(), JGroupActivity.class));
                } else if (itemId == R.id.navigation_mypida) {
                    return;
                } else if (itemId == R.id.navigation_information) {
                    startActivity(new Intent(getBaseContext(), ISetupActivity.class));
                }
                finish();
            }, 0);
            return true;
        });

        //현재는 카테고리이므로 카테고리에 체크 표시
        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);


        //이미지 검색 버튼을 누르면 리스트액티비티로 이동.
        ImageButton mlist = findViewById(R.id.btnSearch);
        mlist.setOnClickListener(v -> startNewActivity("검색할 제품명을 입력해주세요", false ));

    }



    //각 url마다 정보를 받아 PidaProduct 생성
    private class GetPidaData extends AsyncTask<String[], Void, Boolean> {
        @Override
        protected void onPreExecute() { }

        @Override
        protected Boolean doInBackground(String[]... params) {
            try {

                //get user data for send Http parameters
                SharedPreferences user = getSharedPreferences("user", MODE_PRIVATE);
                String username = user.getString("username", "");
                String token = user.getString("access_token", "");

                Log.i("###", "get PidaData at PidaActivity");
                //make information to string
                String urlStr = "http://ec2-13-125-246-38.ap-northeast-2.compute.amazonaws.com/users/"
                        + username
                        + "/?access_token="
                        + token;

                URL url = new URL(urlStr);
                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setRequestMethod("GET");
                urlConn.setDoInput(true);
                urlConn.setRequestProperty("Accept", "application/json");


                Log.i("###", "get PidaData at PidaActivity---codeNum : " + String.valueOf(urlConn.getResponseCode()));
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

                    Log.i("###", "get PidaData at PidaActivity--getting result done");

                    Log.i("###", "get PidaData at PidaActivity--make three kinds of order JSONArray");
                    JSONArray tester_orders = JsonResult.getJSONArray("tester_orders");
                    JSONArray purchase_orders = JsonResult.getJSONArray("purchase_orders");
                    JSONArray group_purchase_orders = JsonResult.getJSONArray("group_purchase_orders");

                    //모든 주문들 중에서 필요한 정보만 합한 JSONObject 추합
                    JSONArray all_orders = new JSONArray();
                    for (int i=0; i<tester_orders.length(); i++){
                        JSONObject temp = tester_orders.getJSONObject(i);
                        JSONObject currOrder = new JSONObject();
                        currOrder.put("type", 0);
                        currOrder.put("status", temp.getInt("status"));
                        currOrder.put("order_time", temp.getString("order_time"));
                        currOrder.put("imgUrl", "it is tester_order");
                        currOrder.put("url", temp.getString("url"));
                        currOrder.put("num", "");
                        all_orders.put(currOrder);
                    }
                    Log.i("###", "get PidaData at PidaActivity--make tester order JSONArray");

                    for (int i=0; i<purchase_orders.length(); i++){
                        Log.i("###", i + "and length is " + purchase_orders.length());
                        JSONObject temp = purchase_orders.getJSONObject(i);
                        JSONObject currOrder = new JSONObject();
                        currOrder.put("type", 1);
                        currOrder.put("order_time", temp.getString("order_time"));
                        currOrder.put("imgUrl", temp.getJSONArray("items").getJSONObject(0).getJSONObject("product").getString("image"));
                        currOrder.put("status", temp.getInt("status"));
                        currOrder.put("url", temp.getString("url"));
                        if (temp.getJSONArray("items").length()==1) currOrder.put("num", "");
                        else currOrder.put("num", "+" + String.valueOf(temp.getJSONArray("items").length()-1));
                        all_orders.put(currOrder);
                    }
                    Log.i("###", "get PidaData at PidaActivity--make purchase order JSONArray");

                    for (int i=0; i<group_purchase_orders.length(); i++){
                        JSONObject temp = group_purchase_orders.getJSONObject(i);
                        JSONObject currOrder = new JSONObject();
                        currOrder.put("type", 2);
                        currOrder.put("order_time", temp.getString("order_time"));
                        currOrder.put("imgUrl", temp.getJSONObject("event").getJSONObject("product").getString("image"));
                        currOrder.put("status", temp.getInt("status"));
                        currOrder.put("url", temp.getString("url"));
                        currOrder.put("num", "");
                        all_orders.put(currOrder);
                    }
                    Log.i("###", "get PidaData at PidaActivity--make group order JSONArray");


                    //use collection.sort, sort the JSONArray
                    Log.i("###", "get PidaData at PidaActivity--make sorted order JSONArray");
                    ArrayList<JSONObject> all_order_list = new ArrayList<JSONObject>();
                    for (int i = 0; i < all_orders.length(); i++) {
                        all_order_list.add(all_orders.getJSONObject(i));
                    }
                    all_order_list.sort((lhs, rhs) -> {
                        try {
                            return (rhs.getString("order_time").compareTo(lhs.getString("order_time")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    });

                    //make OPIDAPRODUCT
                    products = new ArrayList<>();
                    for (int i=0; i<all_order_list.size(); i++){
                        OPidaProduct curr = new OPidaProduct(
                                all_order_list.get(i).getInt("type"),
                                all_order_list.get(i).getInt("status"),
                                all_order_list.get(i).getString("url"),
                                all_order_list.get(i).getString("imgUrl"),
                                all_order_list.get(i).getString("order_time"),
                                all_order_list.get(i).getString("num")
                                );
                        products.add(curr);

                    }


                    return null;
                }

                urlConn.disconnect();
                return null;



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
        protected void onPostExecute(Boolean result) {
            showList();
        }
    }


    //products를 받아 화면에 리스트 띄워주기.
    //현재 사진을 로드하는 데에 시간이 조금 걸림.
    void showList(){
        if (products.size() > 0) findViewById(R.id.textView9).setVisibility(View.GONE);
        adapter = new OPidaProductAdapter(products, this);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
    }


    //리스트 액티비티를 실행. 인텐트를 통해 리스트 액티비티에 string을 전달. 이후 이 string으로 검색한 결과를 리스트로 보여줄 예정
    void startNewActivity(String intentStr, Boolean send){
        Intent find = new Intent(this, LListActivity.class);
        find.putExtra("identifier", intentStr);
        find.putExtra("ocr", send);
        startActivity(find);
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
        }
    }

}
