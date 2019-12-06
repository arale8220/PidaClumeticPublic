package cluemetic.dev.arale.pidaclumetic;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
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

public class FSubCategoryActivity extends AppCompatActivity {
    //for horizontal picker
    ViewPager viewPager;
    FSubCategoryAdapter adapter;
    List<Product> products;
    Intent intent;
    String[] productsUrls;
    Integer subCount;
    ImageButton picker;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_f_subcategory);

        //카테고리에서 넘겨준 url들을 리스트화하여 보여줌
        intent = getIntent();
        productsUrls = intent.getStringArrayExtra("products");
        subCount = intent.getIntExtra("count", 1);
        new ListConnection().execute(productsUrls);

        //서브카테고리의 이름 설정
        String intentTitle=intent.getStringExtra("title");
        TextView textView = findViewById(R.id.text_category);
        textView.setText(intentTitle);

        picker=(ImageButton) findViewById(R.id.button);
        showBtn();
        picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tester = new Intent(FSubCategoryActivity.this, MTesterActivity.class);
                startActivity(tester);
            }
        });

        //이미지 검색 버튼을 누르면 리스트액티비티로 이동.
        ImageButton mlist = findViewById(R.id.btnSearch);
        mlist.setOnClickListener(v -> startNewActivity("검색할 제품명을 입력해주세요", false ));

    }


    //각 url마다 정보를 받아 product 생성
   private class ListConnection extends AsyncTask<String[], Void, Boolean> {
        @Override
        protected void onPreExecute() { }

        @Override
        protected Boolean doInBackground(String[]... params) {
            try {

                String[] urls = params[0];
                products = new ArrayList<>();
                for (int i = 0; i<urls.length; i++){

                    Log.i("###",urls[i]);
                    URL url = new URL(urls[i]);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("Cache-Control", "no-cache");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("Accept", "application/json");
                    con.setDoInput(true);
                    con.connect();

                    if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        StringBuilder result = new StringBuilder();
                        InputStream in = new BufferedInputStream(con.getInputStream());
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }

                        JSONObject response = new JSONObject(result.toString());


                        String price = "";
                        if (subCount == 1){
                            price = String.valueOf(response.getInt("capacity")) + "ml   ￦"+String.valueOf(response.getInt("price"));
                        }else if (subCount == 5){
                            price = String.valueOf(response.getInt("capacity")) + "매   ￦"+String.valueOf(response.getInt("price"));
                        }

                        Log.i("###", response.getString("info_url"));

                        Product item = new Product(
                                response.getString("name"),
                                price,
                                response.getJSONObject("brand").getString("name"),
                                String.valueOf(response.getInt("id")),
                                response.getString("info_url"),
                                response.getString("image"),
                                response.getString("info_seller"),
                                response.getString("info_manufacturer"),
                                response.getString("info_country"),
                                String.valueOf(subCount));

                        products.add(item);


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
        protected void onPostExecute(Boolean result) {
            showList();
        }
    }

    //products를 받아 화면에 리스트 띄워주기.
    //현재 사진을 로드하는 데에 시간이 조금 걸림.
    void showList(){
        adapter = new FSubCategoryAdapter(products, this);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
    }

    void showBtn(){
        SharedPreferences tut = getSharedPreferences("tester", MODE_PRIVATE);
        Integer mode = tut.getInt("count", 0);
        Log.i("###", String.valueOf(mode));
        switch (mode){
            case 1:
                picker.setImageResource(R.drawable.selection_1_selected);
                break;
            case 2:
                picker.setImageResource(R.drawable.selection_2_selected);
                break;
            case 3:
                picker.setImageResource(R.drawable.selection_3_selected);
                break;
            default:
                picker.setImageResource(R.drawable.selection_0_selected);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showBtn();
    }


    //리스트 액티비티를 실행. 인텐트를 통해 리스트 액티비티에 string을 전달. 이후 이 string으로 검색한 결과를 리스트로 보여줄 예정
    void startNewActivity(String intentStr, Boolean send){
        Intent find = new Intent(this, LListActivity.class);
        find.putExtra("identifier", intentStr);
        find.putExtra("ocr", send);
        startActivity(find);
    }

}
