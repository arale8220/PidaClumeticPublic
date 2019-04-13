package cluemetic.dev.arale.pidaclumetic;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ISetupAccount2ReadOnlyActivity extends AppCompatActivity {


    Toolbar mToolbar;
    String com, cvc, date, num, pw;
    Button mCom, mCVC, mDate, mNum, mPW;
    TextView tCom, tCVC, tDate, tNum, tPW;
    SharedPreferences sharedPreferences;
    String json_url;
    String access_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_i_account_2);


        //툴바 제목 : 내정보
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("결제 정보");


        //레이아웃의 컴포넌트들 불러오기
        mCom = (Button) findViewById(R.id.CardCompany);
        mCVC = (Button) findViewById(R.id.CardCVC);
        mDate = (Button)findViewById(R.id.CardDate);
        mNum = (Button)findViewById(R.id.CardNumber);
        mPW = (Button) findViewById(R.id.CardPW);
        tCom = findViewById(R.id.cardcompany);
        tCVC = findViewById(R.id.cardcvc);
        tDate = findViewById(R.id.carddate);
        tNum = findViewById(R.id.cardnumber);
        tPW = findViewById(R.id.cardpw);

        Intent i = getIntent();
        json_url = i.getStringExtra("url");

        //현재 서버에 저장되어있는 유저의 정보 불러오고 update
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        access_token = sharedPreferences.getString("access_token", "");
        Log.i("###", "pay1");
        new Connection().execute();

        View.OnClickListener onClickListener = view -> {
            show();
        };

        //show 함수에 모드 전달하는 리스너 설정하기
        mNum.setOnClickListener(onClickListener) ;
        mCVC.setOnClickListener(onClickListener) ;
        mCom.setOnClickListener(onClickListener) ;
        mPW.setOnClickListener(onClickListener) ;
        mDate.setOnClickListener(onClickListener) ;

    }

    // 변경된 사항들을 화면에 띄워줌
    void update(){
        tCom.setText(com);
        tPW.setText(pw);
        tCVC.setText(cvc);
        tDate.setText(date);
        tNum.setText(num);
    }


    //각 모드에 따라 변경 창 띄우기
    void show()
    {
        AlertDialog.Builder builder0 = new AlertDialog.Builder(this);
        builder0.setTitle("완료된 주문");
        builder0.setMessage("이미 완료된 주문은 결제 정보 수정이 불가능합니다. 공지사항을 참고해주세요.");
        builder0.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
        builder0.show();

    }


    //서버 데이터 받기
    private class Connection extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() { }


        @Override
        protected Boolean doInBackground(String... params) {
            try{

                String strParams = "access_token=" + access_token;

                URL url = new URL(json_url+"?"+strParams);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Content-Type", "application/json");
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
                com = Jres.getString("issuer");
                cvc = Jres.getString("cvc");
                date = Jres.getString("expiration_date");
                num = Jres.getString("card_number");
                pw = Jres.getString("password_hashed");


                return Jres.getBoolean("result");

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            update();
        }
    }

}
