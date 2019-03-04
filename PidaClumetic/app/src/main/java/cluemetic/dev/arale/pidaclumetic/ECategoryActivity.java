package cluemetic.dev.arale.pidaclumetic;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class ECategoryActivity extends AppCompatActivity {
    Intent intent;
    String json_url="http://ec2-13-125-246-38.ap-northeast-2.compute.amazonaws.com/categories/";
    String[] forSubCategory;
    String finding="";
    String subTitle="";
    Integer count=1;
    private static final String CLOUD_VISION_API_KEY = "AIzaSyDgQF-22puFdfvoRjuEgZH0DeFNYjv-oqk";
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final int MAX_LABEL_RESULTS = 10;
    private static final int MAX_DIMENSION = 1200;

    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;

    private BottomNavigationView navigationView;
    ImageView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_category);


        TextView textView = findViewById(R.id.text_category);
        textView.setText("스킨케어");

        //이미지 검색 버튼을 누르면 리스트액티비티로 이동.
        ImageButton list = findViewById(R.id.btnSearch);
        list.setOnClickListener(v -> startNewActivity("검색할 제품명을 입력해주세요", false ));


        //네비게이션뷰 설정(클릭시 이동)
        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(item -> {
            navigationView.postDelayed(() -> {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_category) {
                    startActivity(new Intent(getBaseContext(), ECategoryActivity.class));
//            } else if (itemId == R.id.navigation_basket) {
//                startActivity(new Intent(this, BasketActivity.class));
//            } else if (itemId == R.id.navigation_group) {
//                startActivity(new Intent(this, TogetherActivity.class));
//            } else if (itemId == R.id.navigation_mypida) {
//                startActivity(new Intent(this, ShippingActivity.class));
            } else if (itemId == R.id.navigation_information) {
                startActivity(new Intent(this, ISetupActivity.class));
                }
                finish();
            }, 0);
            return true;
        });

        //현재는 카테고리이므로 카테고리에 체크 표시
        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        //누른 버튼에 따라 서브카테고리 액티비티에 나타낼 정보 달라짐
        Button.OnClickListener onClickListener = view -> {
            switch (view.getId()) {
                case R.id.cream :
                    count=1;
                    subTitle="크림";
                    new CategoryConnection().execute(1);
                    break ;
                case R.id.baby :
                    count=5;
                    subTitle="유아용품";
                    new CategoryConnection().execute(5);
                    break ;
            }
        };

        Button creamWatery = (Button) findViewById(R.id.cream) ;
        creamWatery.setOnClickListener(onClickListener) ;
        Button creamRecovery = (Button) findViewById(R.id.baby) ;
        creamRecovery.setOnClickListener(onClickListener) ;
    }

    //리스트 액티비티를 실행. 인텐트를 통해 리스트 액티비티에 string을 전달. 이후 이 string으로 검색한 결과를 리스트로 보여줄 예정
    void startNewActivity(String intentStr, Boolean send){
//        Intent find = new Intent(getApplicationContext(), GListActivity.class);
//        find.putExtra("finding", inf);
//        startActivity(find);
    }

    private class CategoryConnection extends AsyncTask<Integer, Void, Boolean> {
        @Override
        protected void onPreExecute() { }

        @Override
        protected Boolean doInBackground(Integer... params) {
            try {

                URL url = new URL(json_url + String.valueOf(params[0]));
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Cache-Control", "no-cache");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);
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
                    JSONArray responsearray = response.getJSONArray("products");
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
            return  null;
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

}
