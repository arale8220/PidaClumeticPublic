package cluemetic.dev.arale.pidaclumetic;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import java.util.List;

public class JGroupActivity extends AppCompatActivity {

    //for horizontal picker
    ViewPager viewPager;
    JGroupProductAdapter adapter;
    List<JGroupProduct> groupProducts;
    Integer subCount;
    private long backKeyPressedTime = 0;

    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_j_group);

        new ListConnection().execute();




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
                    return;
                } else if (itemId == R.id.navigation_mypida) {
                    startActivity(new Intent(this, OPidaActivity.class));
                } else if (itemId == R.id.navigation_information) {
                    startActivity(new Intent(getBaseContext(), ISetupActivity.class));
                }
                finish();
            }, 0);
            return true;
        });

        //현재는 카테고리이므로 카테고리에 체크 표시
        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);



        //이미지 검색 버튼을 누르면 리스트액티비티로 이동.
        ImageButton mlist = findViewById(R.id.btnSearch);
        mlist.setOnClickListener(v -> startNewActivity("검색할 제품명을 입력해주세요", false ));

    }


    //각 url마다 정보를 받아 product 생성
    private class ListConnection extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() { }

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                Log.i("###", "11");

                URL url = new URL("http://ec2-13-125-246-38.ap-northeast-2.compute.amazonaws.com/group-purchase-events");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Cache-Control", "no-cache");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setDoInput(true);
                con.connect();

                Log.i("###", "22");
                if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    StringBuilder result = new StringBuilder();
                    InputStream in = new BufferedInputStream(con.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    JSONArray response = new JSONArray(result.toString());


                    Log.i("###", "33");
                    groupProducts = new ArrayList<>();
                    for (int i = 0; i<response.length(); i++){
                        JSONObject curr = response.getJSONObject(i);

                        Log.i("###", "44");
                        new OpenConnection().execute(
                                String.valueOf(i==(response.length()-1)),
                                curr.getString("url"),
                                curr.getString("id"),
                                curr.getString("closing_time").substring(0,10),
                                String.valueOf(curr.getJSONArray("discount_rates").getJSONObject(0).getInt("quantity")),
                                String.valueOf(curr.getJSONArray("discount_rates").getJSONObject(0).getInt("rate")),
                                String.valueOf(curr.getJSONArray("discount_rates").getJSONObject(1).getInt("quantity")),
                                String.valueOf(curr.getJSONArray("discount_rates").getJSONObject(1).getInt("rate")),
                                String.valueOf(curr.getJSONArray("discount_rates").getJSONObject(2).getInt("quantity")),
                                String.valueOf(curr.getJSONArray("discount_rates").getJSONObject(2).getInt("rate")),
                                curr.getJSONObject("product").getString("url")
                        );

                    }
                con.disconnect();

                }


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
        protected void onPostExecute(Boolean result) {  }
    }


    private class OpenConnection extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try{

                Log.i("###", "55");
                Log.i("###", params[10]);
                URL url = new URL(params[10]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Cache-Control", "no-cache");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setDoInput(true);
                con.connect();


                if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    Log.i("###", "66");
                    StringBuilder result = new StringBuilder();
                    InputStream in = new BufferedInputStream(con.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    JSONObject response = new JSONObject(result.toString());


                    Log.i("###", "77");
                    JGroupProduct item = new JGroupProduct(
                            params[1],
                            params[3],
                            response.getString("name"),
                            "￦"+String.valueOf(response.getInt("price")),
                            response.getJSONObject("brand").getString("name"),
                            String.valueOf(response.getInt("id")),
                            response.getString("info_url"),
                            response.getString("image"),
                            response.getString("info_seller"),
                            response.getString("info_manufacturer"),
                            response.getString("info_country"),
                            params[4],
                            params[5],
                            params[6],
                            params[7],
                            params[8],
                            params[9],
                            response.getJSONArray("reviews").toString());

                    groupProducts.add(item);

                }
                con.disconnect();

            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return Boolean.getBoolean(params[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            Log.i("###", "88");
            showList();
        }

    }


    //products를 받아 화면에 리스트 띄워주기.
    //현재 사진을 로드하는 데에 시간이 조금 걸림.
    void showList(){

        Log.i("###", "99");
        Log.i("###", groupProducts.toString());

        adapter = new JGroupProductAdapter(groupProducts, this);
        viewPager = findViewById(R.id.groupViewPager);
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
