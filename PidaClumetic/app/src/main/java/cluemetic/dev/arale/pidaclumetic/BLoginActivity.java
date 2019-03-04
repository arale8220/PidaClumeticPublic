package cluemetic.dev.arale.pidaclumetic;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class BLoginActivity extends AppCompatActivity {

    private static final String getTokenUrl = "http://ec2-13-125-246-38.ap-northeast-2.compute.amazonaws.com/o/token/";
    Button login;
    EditText usernameEdit, passwordEdit;
    String username="", password="", access_token="", refresh_token="";
    String grant_type = "password";
    String client_id = "yg30yWvkbNXIjDbA4mDLimNkyCgZpriBy6c5k8yU";
    String client_secret = "4yRNL6m1LUsHPP8ohiDLfnnPVQ8Vikh1EMGYSRhsTKzDRZoAKYZm2HZPe4Ls9HTJuTwjJddcFJmivKCVtAve5yPzmJ9M6pO5XGmh3DmARscXu4L8cRSrk8XpkoXHYFZW";
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b_login);

        //get activity components
        login = (Button) findViewById(R.id.btn_login) ;
        usernameEdit = (EditText) findViewById(R.id.email);
        passwordEdit = (EditText) findViewById(R.id.pw);

        //when click the login button, check information from server
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = usernameEdit.getText().toString();
                password = passwordEdit.getText().toString();

                getAccessToken loginTask = new getAccessToken();
                loginTask.execute();
            }
        });


        //when click the signup button, go to signup activity
        TextView signup = (TextView) findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToOther(0);
            }
        });


    }


    private void goToOther(Integer activity){
        switch (activity){
            case 0: //signup activity
                intent = new Intent(getApplicationContext(), CSignupActivity.class);
                startActivity(intent);
                break;
            case 1: //category activity
                intent = new Intent(getBaseContext(), ECategoryActivity.class);
                startActivity(intent);
                break;
        }
        finish();
    }

    //give information and get tokens
    public class getAccessToken extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            try{
                //make information to string
                String strParams = "username=" + username +
                        "&password=" + password +
                        "&grant_type=" + grant_type +
                        "&client_id=" + client_id +
                        "&client_secret=" + client_secret;


                URL url = new URL(getTokenUrl);
                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setRequestMethod("POST"); // URL 요청에 대한 메소드 설정 : POST.
                urlConn.setRequestProperty("Content-Type", "application/json");
                urlConn.setDoInput(true);
                urlConn.setDoOutput (true);

                OutputStream outputStream = urlConn.getOutputStream();
                outputStream.write(strParams.getBytes( "UTF-8" ));
                outputStream.flush();
                outputStream.close();


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

                    return JsonResult;
                }

                urlConn.disconnect();
                return null;


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (result != null){

                try {
                    access_token = result.getString("access_token");
                    refresh_token = result.getString("refresh_token");

                } catch (JSONException e) {
                    e.printStackTrace();
                    showRetryDialog();
                }

                //get the saved token
                SharedPreferences getval = getSharedPreferences("user", MODE_PRIVATE);
                SharedPreferences.Editor editor = getval.edit();
                editor.putString("access_token", access_token);
                editor.putString("refresh_token", refresh_token);
                editor.putString("username", username);
                editor.putString("password", password);
                editor.apply();

                //show login is successful
                Toast.makeText(getBaseContext(), "로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                goToOther(1);

            }
            else {
                showRetryDialog();
            }


        }
    }




    private static long back_pressed;
    @Override
    public void onBackPressed()
    {
        if (back_pressed + 2000 > System.currentTimeMillis()) super.onBackPressed();
        else Toast.makeText(getBaseContext(), "앱을 종료하시려면 이전 버튼을 한번 더 눌러주세요", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }

    //if login failed
    void showRetryDialog(){
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setMessage("이메일과 비밀번호를 다시 확인해주세요.");
        dialog.setTitle("로그인 실패");
        dialog.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }
}
