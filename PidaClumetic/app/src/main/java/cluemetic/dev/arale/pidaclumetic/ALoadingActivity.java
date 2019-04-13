package cluemetic.dev.arale.pidaclumetic;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ALoadingActivity extends AppCompatActivity {
    public final int MY_PERMISSIONS_REQUEST_INTERNET = 1;
    ImageView logo;
    String token="";
    String refresh="";
    String email, pw, gender, age, skintype, concern, allergy, userid;
    String access_token, refresh_token, payment, delivery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_loading);

        Log.i("###", "loading0");
        //for animation of logo
        logo=findViewById(R.id.loading);
        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(4000);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setRepeatCount(Animation.INFINITE);
        logo.startAnimation(rotate);


        Log.i("###", "loading00");
        //check permission. In the callback method(onRequestPermissionsResult), check the machine is already logged or not
        checkPermission();

    }

    //get internet permission
    void checkPermission(){
        Log.i("###", "loading01");
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET},  MY_PERMISSIONS_REQUEST_INTERNET);
    }

    //권한 요청에 따른 callback
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        Log.i("###", "loading04");
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_INTERNET: {
                //권한 승인시
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.i("###", "loading05");
                    checkLogin();
                }
                //권한 미승인시
                else {

                    Log.i("###", "loading06");
                    showQuitDialog();
                }

                return;
            }

        }
    }

    //if the token exists, go to category page
    //else go to login page
    private void checkLogin() {
        Handler handler = new Handler();
        Log.i("###","loading1");

        //get the saved token
        SharedPreferences tut = getSharedPreferences("first", MODE_PRIVATE);
        Boolean firstTime = tut.getBoolean("first", true);

        //get the saved token
        SharedPreferences getval = getSharedPreferences("user", MODE_PRIVATE);
        token = getval.getString("access_token", "");
        refresh = getval.getString("refresh_token", "");
        email = getval.getString("username", "");

        //for animation, go to other activity after 2 seconds delay.
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (firstTime){
                    SharedPreferences.Editor editor = tut.edit();
                    editor.putBoolean("first", false); //save that after accesses are not the first time.
                    editor.apply();
                    Intent i = new Intent(getBaseContext(), DTutorialActivity.class);
                    startActivity(i);
                    finish();
                }
                else if (token.equals("")){
                    Intent notlogged = new Intent(getBaseContext(), BLoginActivity.class);
                    startActivity(notlogged);
                    finish();
                }
                else{
                    new RefreshToken().execute();
                }
            }
        }, 1500);

    }


    //if the permissions denied, show quit dialog
    void showQuitDialog(){
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setMessage("로그인을 위해 해당 권한이 필요합니다. 앱을 다시 실행해주세요");
        dialog.setTitle("권한 요청 실패");
        dialog.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                        System.exit(0);
                    }
                });
        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }



    private class RefreshToken extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() { }


        @Override
        protected Boolean doInBackground(String... params) {
            try{
                Log.i("###", "refresh token at Loading");
                URL url = new URL("http://ec2-13-125-246-38.ap-northeast-2.compute.amazonaws.com/o/token/?client_id=yg30yWvkbNXIjDbA4mDLimNkyCgZpriBy6c5k8yU&client_secret=4yRNL6m1LUsHPP8ohiDLfnnPVQ8Vikh1EMGYSRhsTKzDRZoAKYZm2HZPe4Ls9HTJuTwjJddcFJmivKCVtAve5yPzmJ9M6pO5XGmh3DmARscXu4L8cRSrk8XpkoXHYFZW&grant_type=refresh_token&refresh_token="+
                    refresh + "&access_token=" + token);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setDoInput(true);
                con.connect();

                Log.i("###", "refresh token at Loading");
                InputStream stream = con.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                Log.i("###", "refresh token at Loading");
                con.disconnect();
                reader.close();
                JSONObject Jres = new JSONObject(buffer.toString());
                token = Jres.getString("access_token");
                refresh = Jres.getString("refresh_token");

                return null;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.i("###", "refresh token finished at Loading");
            new getUserData().execute();
            SharedPreferences getvall = getSharedPreferences("user", MODE_PRIVATE);
            SharedPreferences.Editor editor = getvall.edit();
            editor.putString("access_token", token);
            editor.putString("refresh_token", refresh);
            editor.apply();

        }
    }



    //get user information from server
    public class getUserData extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            try{
                Log.i("###", "getUserData at Loading");
                //make information to string
                String strParams = "?access_token=" + token;

                URL url = new URL("http://ec2-13-125-246-38.ap-northeast-2.compute.amazonaws.com/users/" + email + strParams);
                Log.i("###", "getUserData at Loading");
                Log.i("###", "http://ec2-13-125-246-38.ap-northeast-2.compute.amazonaws.com/users/" + email + strParams);
                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                Log.i("###", "getUserData at Loading");
                urlConn.setRequestMethod("GET");
                Log.i("###", "getUserData at Loading000");
                urlConn.setDoInput(true);
                urlConn.setRequestProperty("Accept", "application/json");
                urlConn.setConnectTimeout(500);
                Log.i("###", "getUserData at Loading");


                Log.i("###", String.valueOf(urlConn.getResponseCode()) + "getUserData at Loading");
                if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Log.i("###", "getUserData at Loading----");

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

                    Log.i("###", "getUserData at Loading");

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

                    Log.i("###", "getUserData at Loading");
                    SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
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

                    Log.i("###", "getUserData at Loading finished");


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
        protected void onPostExecute(JSONObject jsonObject) {
            Log.i("###", "getUserData did anyway............");
            Intent alreadyLogin = new Intent(getBaseContext(), ECategoryActivity.class);
            startActivity(alreadyLogin);
            finish();
        }
    }

}
