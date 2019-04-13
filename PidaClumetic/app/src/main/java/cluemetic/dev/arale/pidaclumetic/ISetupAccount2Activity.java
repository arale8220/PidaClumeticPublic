package cluemetic.dev.arale.pidaclumetic;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.List;

public class ISetupAccount2Activity extends AppCompatActivity {


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


        //현재 서버에 저장되어있는 유저의 정보 불러오고 update
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        json_url = sharedPreferences.getString("payment","");
        access_token = sharedPreferences.getString("access_token", "");
        Log.i("###", "pay1");
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
                final List<String> ListItems = new ArrayList<>();
                ListItems.add("KB국민카드");
                ListItems.add("비씨카드");
                ListItems.add("삼성카드");
                ListItems.add("신한카드");
                ListItems.add("현대카드");
                ListItems.add("하나카드");
                final CharSequence[] GenderItems =  ListItems.toArray(new String[ ListItems.size()]);
                final List SelectedGenderItems  = new ArrayList();
                SelectedGenderItems.add(0);

                AlertDialog.Builder builder0 = new AlertDialog.Builder(this);
                builder0.setTitle("카드사");
                builder0.setSingleChoiceItems(GenderItems, 0,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SelectedGenderItems.clear();
                                SelectedGenderItems.add(which);
                            }
                        });
                builder0.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (!SelectedGenderItems.isEmpty()) {
                                    //변경한 성별이 선택된 경우 toast로 알림
                                    int index = (int) SelectedGenderItems.get(0);
                                    com = ListItems.get(index);
                                    new Patch().execute();
                                }else {
                                    Toast.makeText(getApplicationContext(),"카드사를 선택하지 않아 값이 변경되지 않았습니다." , Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                builder0.show();
                break;



            case 1:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setTitle("카드번호");
                builder1.setMessage("- 없이 숫자만 입력해주세요");
                final EditText et = new EditText(ISetupAccount2Activity.this);
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
                builder2.setTitle("카드 유효기간");
                builder2.setMessage("\'/\'을 포함하여 MM/YY 형식으로 입력해주세요");
                final EditText et2 = new EditText(ISetupAccount2Activity.this);
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
                builder3.setTitle("CVC");
                builder3.setMessage("CVC 번호 세자리를 입력해주세요");
                final EditText et3 = new EditText(ISetupAccount2Activity.this);
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


            case 4:
                AlertDialog.Builder builder4 = new AlertDialog.Builder(this);
                builder4.setTitle("비밀번호");
                builder4.setMessage("카드 비밀번호 네자리를 입력해주세요");
                final EditText et4 = new EditText(ISetupAccount2Activity.this);
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




    private String getPwString(Integer count){
        StringBuilder buf = new StringBuilder(count);
        while (count-- > 0) {
            buf.append("*");
        }
        return buf.toString();
    }


    //서버 데이터 받기
    private class Connection extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() { }


        @Override
        protected Boolean doInBackground(String... params) {
            try{

                Log.i("###", "pay1");
                String strParams = "access_token=" + access_token;

                Log.i("###", json_url+"?"+strParams);
                URL url = new URL(json_url+"?"+strParams);
                Log.i("###", "pay1");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Content-Type", "application/json");
                con.setDoInput(true);
                con.connect();

                Log.i("###", "pay2");
                InputStream stream = con.getInputStream();
                Log.i("###", "pay22");
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                con.disconnect();
                reader.close();
                Log.i("###", "pay3");

                JSONObject Jres = new JSONObject(buffer.toString());
                com = Jres.getString("issuer");
                cvc = Jres.getString("cvc");
                date = Jres.getString("expiration_date");
                num = Jres.getString("card_number");
                pw = Jres.getString("password_hashed");

                Log.i("###", "pay4");

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

                JSONObject patch = new JSONObject();
                patch.put("issuer", com);
                patch.put("cvc", cvc);
                patch.put("expiration_date", date);
                patch.put("card_number", num);
                patch.put("password_hashed", pw);

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
                Log.i("###", "change payment at payment activity");
                Log.i("###", String.valueOf(Jres.getBoolean("valid")));
                editor.putBoolean("paymentOK", Jres.getBoolean("valid"));
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
                Toast.makeText(ISetupAccount2Activity.this, "필요한 정보를 모두 입력해야 주문이 가능합니다", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(ISetupAccount2Activity.this, "변경사항이 모두 저장되었습니다", Toast.LENGTH_SHORT).show();
            }
            update();
        }
    }
}
