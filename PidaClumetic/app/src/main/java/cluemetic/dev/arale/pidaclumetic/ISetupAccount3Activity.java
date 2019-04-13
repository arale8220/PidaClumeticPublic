package cluemetic.dev.arale.pidaclumetic;

import android.content.DialogInterface;
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

public class ISetupAccount3Activity extends AppCompatActivity {


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
        setContentView(R.layout.activity_i_account_3);


        //툴바 제목 : 내정보
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("배송 정보");


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


        //현재 서버에 저장되어있는 유저의 정보 불러오고 update
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        json_url = sharedPreferences.getString("delivery","");
        access_token = sharedPreferences.getString("access_token", "");
        new Connection().execute();



        //클릭리스너가 show 함수에 모드 전달
        //이후 show 함수에서 모드별로 변경 가능한 새로운 창의 띄워 변경, update 함수 호출
        View.OnClickListener onClickListener = view -> {
            switch (view.getId()) {
                case R.id.CardCompany :
                    show(0);
                    break ;
                case R.id.CardNumber :
                    show(1);
                    break ;
                case R.id.CardDate :
                    show(2);
                    break ;
                case R.id.CardCVC :
                    show(3);
                    break ;
                case R.id.CardPW :
                    show(4);
                    break;
            }
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
    void show(Integer mode)
    {
        switch (mode) {

            case 0:
                AlertDialog.Builder builder0 = new AlertDialog.Builder(this);
                builder0.setTitle("수령인");
                builder0.setMessage("수령인의 이름을 입력해주세요");
                final EditText et0 = new EditText(ISetupAccount3Activity.this);
                builder0.setView(et0);
                builder0.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                com = et0.getText().toString();
                                new Patch().execute();
                            }
                        });
                builder0.show();
                break;



            case 1:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setTitle("연락처");
                builder1.setMessage("- 없이 숫자만 입력해주세요");
                final EditText et = new EditText(ISetupAccount3Activity.this);
                builder1.setView(et);
                builder1.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                num = et.getText().toString();
                                new Patch().execute();
                            }
                        });
                builder1.show();
                break;


            case 2:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setTitle("우편번호");
                builder2.setMessage("우편번호 다섯자리를 입력해주세요");
                final EditText et2 = new EditText(ISetupAccount3Activity.this);
                builder2.setView(et2);
                builder2.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                date = et2.getText().toString();
                                new Patch().execute();
                            }
                        });
                builder2.show();
                break;


            case 3:
                AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
                builder3.setTitle("도로명");
                builder3.setMessage("도로명 주소를 정확하게 입력해주세요");
                final EditText et3 = new EditText(ISetupAccount3Activity.this);
                builder3.setView(et3);
                builder3.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                cvc = et3.getText().toString();
                                new Patch().execute();
                            }
                        });
                builder3.show();
                break;


            //change skintype
            case 4:
                AlertDialog.Builder builder4 = new AlertDialog.Builder(this);
                builder4.setTitle("상세주소");
                builder4.setMessage("상세주소를 정확하게 입력해주세요");
                final EditText et4 = new EditText(ISetupAccount3Activity.this);
                builder4.setView(et4);
                builder4.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                pw = et4.getText().toString();
                                new Patch().execute();
                            }
                        });
                builder4.show();
                break;

        }

    }


    private class Connection extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() { }


        @Override
        protected Boolean doInBackground(String... params) {
            try{

                String strParams = "access_token=" + access_token;

                URL url = new URL(json_url + "?" + strParams);
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
                com = Jres.getString("name");
                cvc = Jres.getString("address_line_road");
                date = Jres.getString("postal_code");
                num = Jres.getString("contact");
                pw = Jres.getString("address_line_detail");

                return null;

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

    //서버에 변경된 데이터 send
    private class Patch extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() { }


        @Override
        protected Boolean doInBackground(String... params) {
            try{
                String strParams = "access_token=" + access_token;

                URL url = new URL(json_url+"?"+strParams);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("PATCH");
                con.setRequestProperty("Content-Type", "application/json");
                con.setDoOutput(true);
                con.setDoInput(true);
                con.connect();

//                com name
//                num contact
//                date postal_code
//                cvc address_line_road
//                pw address_line detail
                JSONObject patch = new JSONObject();
                patch.put("name", com);
                patch.put("address_line_road", cvc);
                patch.put("postal_code", date);
                patch.put("contact", num);
                patch.put("address_line_detail", pw);

                OutputStream outStream = con.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                writer.write(patch.toString());
                writer.flush();
                writer.close();

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
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Log.i("###", "change delivery at delivery activity");
                Log.i("###", String.valueOf(Jres.getBoolean("valid")));
                editor.putBoolean("deliveryOK", Jres.getBoolean("valid"));
                editor.commit();

                return Jres.getBoolean("valid");

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if ((result==null) || (result==false)) {
                Toast.makeText(ISetupAccount3Activity.this, "필요한 정보를 모두 입력해야 주문이 가능합니다", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(ISetupAccount3Activity.this, "변경사항이 모두 저장되었습니다", Toast.LENGTH_SHORT).show();
            }
            update();
        }
    }
}
