package cluemetic.dev.arale.pidaclumetic;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ISetupActivity extends AppCompatActivity {


    private BottomNavigationView navigationView;
    Toolbar mToolbar;
    MenuItem mSearch,mCamera;
    Intent intent;
    String email, pw, gender, age, skintype, concern, allergy, userid;
    Button emailBtn, pwBtn, genderBtn, ageBtn, skintypeBtn, concernBtn, allergyBtn;
    SharedPreferences sharedPreferences;
    String json_url;
    String access_token, refresh_token, payment, delivery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_i_setup);



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
                    startActivity(new Intent(this, OPidaActivity.class));
                } else if (itemId == R.id.navigation_information) {
                    return;
                }
                finish();
            }, 0);
            return true;
        });

        //현재는 카테고리이므로 카테고리에 체크 표시
        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.getItem(4);
        menuItem.setChecked(true);



        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("설정");

        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        email = sharedPreferences.getString("username", "     ");
        pw = sharedPreferences.getString("password","     ");
        access_token = sharedPreferences.getString("access_token","     ");
        new getUserData().execute();


        Button.OnClickListener onClickListener = view -> {
            switch (view.getId()) {
                case R.id.security1 :
                    goToOther(7);
                    break ;
                case R.id.security2 :
                    goToOther(8);
                    break ;
                case R.id.account1 :
                    goToOther(1);
                    break ;
//                case R.id.account2 :
//                    goToOther(2);
//                    break ;
                case R.id.account3 :
                    goToOther(3);
                    break;


                case R.id.center1 :
                    goToOther(4);
                    break;
                case R.id.center2 :
                    goToOther(5);
                    break ;



                case R.id.center3 :
                    goToOther(6);
                    break ;
            }
        };

        Button security1 = (Button) findViewById(R.id.security1) ;
        security1.setOnClickListener(onClickListener) ;
        Button security2 = (Button) findViewById(R.id.security2) ;
        security2.setOnClickListener(onClickListener) ;
        Button account1 = (Button) findViewById(R.id.account1) ;
        account1.setOnClickListener(onClickListener) ;
//        Button account2 = (Button) findViewById(R.id.account2) ;
//        account2.setOnClickListener(onClickListener) ;
        Button account3 = (Button) findViewById(R.id.account3) ;
        account3.setOnClickListener(onClickListener) ;
        Button center1 = (Button) findViewById(R.id.center1) ;
        center1.setOnClickListener(onClickListener) ;
        Button center2 = (Button) findViewById(R.id.center2) ;
        center2.setOnClickListener(onClickListener) ;
        Button center3 = (Button) findViewById(R.id.center3) ;
        center3.setOnClickListener(onClickListener) ;

    }






    private void goToOther(Integer activity){
        switch (activity){
            case 1:
                intent = new Intent(getBaseContext(), ISetupAccount1Activity.class);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent(getBaseContext(), ISetupAccount2Activity.class);
                startActivity(intent);
                break;
            case 3:
                intent = new Intent(getBaseContext(), ISetupAccount3Activity.class);
                startActivity(intent);
                break;
            case 4:
                intent = new Intent(getBaseContext(), ISetupNoticeActivity.class);
                intent.putExtra("notice0faq1", 0);
                startActivity(intent);
                break;
            case 5:
                intent = new Intent(getBaseContext(), ISetupNoticeActivity.class);
                intent.putExtra("notice0faq1", 1);
                startActivity(intent);
                break;
            case 6:
                intent = new Intent(getBaseContext(), KAppVersionActivity.class);
                startActivity(intent);
                break;
            case 7:
                //remove tokens
                SharedPreferences remove7 = getSharedPreferences("user", MODE_PRIVATE);
                SharedPreferences.Editor editor7 = remove7.edit();
                editor7.putString("access_token","");
                editor7.putString("refresh_token","");
                editor7.apply();
                intent = new Intent(getBaseContext(), BLoginActivity.class);
                startActivity(intent);
                break;
            case 8:
                //remove tokens
                SharedPreferences remove8 = getSharedPreferences("user", MODE_PRIVATE);
                SharedPreferences.Editor editor8 = remove8.edit();
                editor8.putString("access_token","");
                editor8.putString("refresh_token","");
                editor8.apply();
                intent = new Intent(getBaseContext(), BLoginActivity.class);
                startActivity(intent);
                break;
        }
    }

    //get user information from server
    public class getUserData extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            try{
                //make information to string
                String strParams = "?access_token=" + access_token;

                Log.i("###", "get user data at setup activity");
                URL url = new URL("http://ec2-13-125-246-38.ap-northeast-2.compute.amazonaws.com/users/" + email + strParams);
                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setRequestMethod("GET");
                urlConn.setRequestProperty("Content-Type", "application/json");
                urlConn.setDoInput(true);
                Log.i("###", "get user data at setup activity");


                if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Log.i("###", "get user data at setup activity");

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


                    Log.i("###", "get user data at setup activity");
                    userid = String.valueOf(JsonResult.getInt("id"));
                    gender = String.valueOf(JsonResult.getInt("gender"));
                    age = String.valueOf(JsonResult.getInt("age"));
                    skintype = String.valueOf(JsonResult.getInt("skin_type"));
                    concern = "";
                    JSONArray curr = JsonResult.getJSONArray("skin_concerns");
                    for (int i = 0; i<curr.length(); i++){
                        concern = concern + curr.getString(i);
                    }
                    allergy = "";
                    curr = JsonResult.getJSONArray("allergies");
                    for (int i = 0; i<curr.length(); i++){
                        allergy = allergy + curr.getString(i).toUpperCase();
                    }

                    Log.i("###", "get user data at setup activity");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("skintype", skintype);
                    editor.putString("userid", userid);
                    editor.putString("gender", gender);
                    editor.putString("age", age);
                    editor.putString("concern", concern);
                    editor.putString("allergy", allergy);
                    editor.putString("payment", JsonResult.getString("default_payment_information"));
                    editor.putString("delivery", JsonResult.getString("default_delivery_information"));
                    editor.apply();
                    Log.i("###", "get user data finished at setup activity");



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


}
