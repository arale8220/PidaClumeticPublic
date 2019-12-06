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
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.api.services.vision.v1.model.Image;
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
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ECategoryActivity extends AppCompatActivity {
    Intent intent;
    String json_url="http://ec2-13-125-246-38.ap-northeast-2.compute.amazonaws.com/categories/";
    String[] forSubCategory;
    String finding="";
    String subTitle="";
    Integer count=1;
    private long backKeyPressedTime = 0;

    private BottomNavigationView navigationView;
    ImageView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_category);


        TextView textView = findViewById(R.id.text_category);
        textView.setText("스킨케어");


        //이미지 검색 버튼을 누르면 리스트액티비티로 이동.
        ImageButton mlist = findViewById(R.id.btnSearch);
        mlist.setOnClickListener(v -> startNewActivity("검색할 제품명을 입력해주세요", false ));


        //이용약관 보여주기
        TextView magreements = findViewById(R.id.textView6);
        magreements.setOnClickListener(v -> {
            intent = new Intent(getBaseContext(), ISetupNoticeActivity.class);
            intent.putExtra("notice0faq1", 2);
            startActivity(intent);
        });

//        //사업자정보 보여주기
//        TextView mcompany = findViewById(R.id.textView8);
//        LinearLayout babypick = findViewById(R.id.babypick);
//        mcompany.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (babypick.getVisibility() == LinearLayout.GONE) babypick.setVisibility(LinearLayout.VISIBLE);
//                else babypick.setVisibility(LinearLayout.GONE);
//            }
//        });

        //네비게이션뷰 설정(클릭시 이동)
        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(menuItem -> {
            navigationView.postDelayed(() -> {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.navigation_category) {
                    return;
                } else if (itemId == R.id.navigation_basket) {
                    startActivity(new Intent(this, NBasketActivity.class));
                } else if (itemId == R.id.navigation_group) {
                    startActivity(new Intent(getBaseContext(), JGroupActivity.class));
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
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        //항상 결제/배송정보의 타당성 확인
        SharedPreferences userData = getSharedPreferences("user", MODE_PRIVATE);
        new getPayDeliveryData(userData.getString("delivery", ""), "deliveryOK").execute();
        new getPayDeliveryData(userData.getString("payment", ""), "paymentOK").execute();


        //누른 버튼에 따라 서브카테고리 액티비티에 나타낼 정보 달라짐
        Button.OnClickListener onClickListener = view -> {
            switch (view.getId()) {
                case R.id.cream :
                    count=1;
                    subTitle="크림 전체";
                    new CategoryConnection().execute(1);
                    break ;
//                case R.id.baby :
//                    count=5;
//                    subTitle="유아용품";
//                    new CategoryConnection().execute(5);
//                    break ;
            }
        };

        ImageButton creamWatery = (ImageButton) findViewById(R.id.cream) ;
        creamWatery.setOnClickListener(onClickListener) ;
//        ImageButton creamRecovery = (ImageButton) findViewById(R.id.baby) ;
//        creamRecovery.setOnClickListener(onClickListener) ;
    }


    //get user information from server
    public class getPayDeliveryData extends AsyncTask<String, Void, JSONObject> {
        String geturl, spOK;

        public getPayDeliveryData(String url, String spOK) {
            this.geturl = geturl;
            this.spOK = spOK;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try{
                SharedPreferences user = getSharedPreferences("user", MODE_PRIVATE);
                String access_token = user.getString("access_token", "");


                //make information to string
                String strParams = "?access_token=" + access_token;

                URL url = new URL(geturl + strParams);
                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setRequestMethod("GET");
                urlConn.setRequestProperty("Content-Type", "application/json");
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

                    SharedPreferences.Editor editor = user.edit();
                    editor.putBoolean(spOK, JsonResult.getBoolean("valid"));
                    editor.apply();

                    return JsonResult;
                }

                urlConn.disconnect();
                return null;


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class CategoryConnection extends AsyncTask<Integer, Void, Boolean> {
        @Override
        protected void onPreExecute() { }

        @Override
        protected Boolean doInBackground(Integer... params) {
            try {

                URL url = new URL(json_url + String.valueOf(params[0]) + "/");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setDoInput(true);
                con.connect();


                Log.i("###", "cat1");
                if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    StringBuilder result = new StringBuilder();
                    InputStream in = new BufferedInputStream(con.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    Log.i("###", "cat2");

                    JSONObject response = new JSONObject(result.toString());
                    JSONArray responsearray = response.getJSONArray("products");
                    Log.i("###", "cat3");
                    ArrayList<String> ar = new ArrayList<String>();
                    for (int i = 0; i < responsearray.length(); i++) {
                        ar.add(responsearray.getString(i));
                    }
                    forSubCategory = ar.toArray(new String[ar.size()]);
                    return true;
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
            return  false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if ( result == true ){
                intent = new Intent(getBaseContext(), FSubCategoryActivity.class);
                intent.putExtra("products",forSubCategory);
                intent.putExtra("count",count);
                intent.putExtra("title",subTitle);
                startActivity(intent);
            }
            else {
                Toast.makeText(getBaseContext(), "인터넷 문제로 인해 다음 화면으로 넘어갈 수 없습니다.", Toast.LENGTH_SHORT).show();
            }

        }
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

        // 마지막으로 뒤로가기 버튼을 눌렀던 시간에 2초를 더해 현재시간과 비교 후
        // 마지막으로 뒤로가기 버튼을 눌렀던 시간이 2초가 지났으면 Toast Show
        // 2000 milliseconds = 2 seconds
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        // 마지막으로 뒤로가기 버튼을 눌렀던 시간에 2초를 더해 현재시간과 비교 후
        // 마지막으로 뒤로가기 버튼을 눌렀던 시간이 2초가 지나지 않았으면 종료
        // 현재 표시된 Toast 취소
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
        }
    }

}

