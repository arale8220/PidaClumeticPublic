package cluemetic.dev.arale.pidaclumetic;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.stream.Collectors;

public class MTesterActivity extends AppCompatActivity {

    ImageButton picker;
    Button x1, x2, x3;
    Button payment, delivery, purchase;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_tester);

        picker = findViewById(R.id.button);
        showBtn();

        x1 = findViewById(R.id.tester1);
        x2 = findViewById(R.id.tester2);
        x3 = findViewById(R.id.tester3);
        showTesters();


        Button.OnClickListener onClickListener = view -> {

            String result="";
            switch (view.getId()) {
                case R.id.tester1 :
                    SharedPreferences tut = getSharedPreferences("tester", MODE_PRIVATE);
                    Integer mode = Integer.valueOf(tut.getString("count", "0"));
                    String[] ids = tut.getString("testers", "").split(" ");

                    if (ids.length==3){
                        result = ids[1]+" ";
                    }else if (ids.length>=4){
                        result = ids[1]+" "+ids[2]+" ";
                    }

                    SharedPreferences.Editor editor = tut.edit();
                    editor.putString("count", String.valueOf(mode-1));
                    editor.putString("testers", result);
                    editor.apply();

                    showTesters();
                    showBtn();

                    break ;
                case R.id.tester2 :
                    SharedPreferences tut2 = getSharedPreferences("tester", MODE_PRIVATE);
                    Integer mode2 = Integer.valueOf(tut2.getString("count", "0"));
                    String[] ids2 = tut2.getString("testers", "").split(" ");

                    if (ids2.length==3){
                        result = ids2[0]+" ";
                    }else if (ids2.length>=4){
                        result = ids2[0]+" "+ids2[2]+" ";
                    }

                    SharedPreferences.Editor editor2 = tut2.edit();
                    editor2.putString("count", String.valueOf(mode2-1));
                    editor2.putString("testers", result);
                    editor2.apply();

                    showTesters();
                    showBtn();

                    break ;
                case R.id.tester3 :
                    SharedPreferences tut3 = getSharedPreferences("tester", MODE_PRIVATE);
                    Integer mode3 = Integer.valueOf(tut3.getString("count", "0"));
                    String[] ids3 = tut3.getString("testers", "").split(" ");

                    if (ids3.length>=3){
                        result = ids3[0]+" "+ids3[1]+" ";
                    }

                    SharedPreferences.Editor editor3 = tut3.edit();
                    editor3.putString("count", String.valueOf(mode3-1));
                    editor3.putString("testers", result);
                    editor3.apply();

                    showTesters();
                    showBtn();

                    break ;
            }
        };
        x1.setOnClickListener(onClickListener);
        x2.setOnClickListener(onClickListener);
        x3.setOnClickListener(onClickListener);

        payment = findViewById(R.id.payment);
        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MTesterActivity.this, ISetupAccount2Activity.class);
                startActivity(intent);
            }
        });

        delivery = findViewById(R.id.delivery);
        delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MTesterActivity.this, ISetupAccount3Activity.class);
                startActivity(intent);
            }
        });

        purchase = findViewById(R.id.tester);
        purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences tut = getSharedPreferences("user", MODE_PRIVATE);
                String[] ids = tut.getString("testers", "").split(" ");
                new Purchase(tut.getBoolean("paymentOk", false), tut.getBoolean("deliveryOk", false), tut.getString("access_token", ""))
                    .execute(tut.getString("count", "0"), ids[0], ids[1], ids[2]);
            }
        });



    }




    private class Purchase extends AsyncTask<String, Void, Boolean> {
        Boolean pay, del;
        String token;

        public Purchase(Boolean pay, Boolean del, String t) {
            this.pay = pay;
            this.del = del;
            this.token = t;
        }

        @Override
        protected void onPreExecute() { }


        @Override
        protected Boolean doInBackground(String... params) {
            try{
                if ((Integer.valueOf(params[0])>=3) && pay && del){
                    URL url = new URL("http://ec2-13-125-246-38.ap-northeast-2.compute.amazonaws.com/tester-orders/?access_token="+token);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    con.connect();

                    JSONArray products = new JSONArray();
                    products.put("http://ec2-13-125-246-38.ap-northeast-2.compute.amazonaws.com/products/" + params[1] + "/");
                    products.put("http://ec2-13-125-246-38.ap-northeast-2.compute.amazonaws.com/products/" + params[2] + "/");
                    products.put("http://ec2-13-125-246-38.ap-northeast-2.compute.amazonaws.com/products/" + params[3] + "/");

                    SharedPreferences tut = getSharedPreferences("user", MODE_PRIVATE);
                    JSONObject post = new JSONObject();
                    post.put("products", products);
                    post.put("category", "http://ec2-13-125-246-38.ap-northeast-2.compute.amazonaws.com/categories/1/");
                    post.put("delivery_information", tut.getString("delivery", ""));
                    post.put("payment_information", tut.getString("payment", ""));

                    OutputStreamWriter os= new OutputStreamWriter(con.getOutputStream());
                    os.write( post.toString() );
                    os.flush();


                    if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        SharedPreferences tut2 = getSharedPreferences("tester", MODE_PRIVATE);
                        SharedPreferences.Editor ed = tut2.edit();
                        ed.putString("count", "0");
                        ed.putString("testers", "");
                        ed.apply();
                        return true;
                    }


                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) Toast.makeText(MTesterActivity.this, "테스터 주문에 성공하였습니다. My피다에서 확인해주세요", Toast.LENGTH_SHORT).show();
            else Toast.makeText(MTesterActivity.this, "테스터 주문에 실패하였습니다. 서로 다른 테스터들과 옳은 배송/결제정보를 입력해주세요", Toast.LENGTH_SHORT).show();
        }
    }



    private class Getname extends AsyncTask<String, Void, String> {
        Integer num;

        public Getname(Integer num) {
            this.num = num;
        }

        @Override
        protected void onPreExecute() { }


        @Override
        protected String doInBackground(String... params) {
            try{
                URL url = new URL("http://ec2-13-125-246-38.ap-northeast-2.compute.amazonaws.com/products/"+params[0]);
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
                return Jres.getString("name");

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            switch (num){
                case 1:
                    x1.setText(result);
                    break;
                case 2:
                    x2.setText(result);
                    break;
                case 3:
                    x3.setText(result);
                    break;
            }
        }
    }

    void showTesters(){
        SharedPreferences tut = getSharedPreferences("tester", MODE_PRIVATE);
        String[] ids = tut.getString("testers", "").split(" ");
        for (int i = 0; i<ids.length; i++){
            if (i==0) new Getname(1).execute(ids[i]);
            else if (i==1) new Getname(2).execute(ids[i]);
            else if (i==3) new Getname(3).execute(ids[i]);
        }
    }

    void showBtn(){
        SharedPreferences tut = getSharedPreferences("tester", MODE_PRIVATE);
        Integer mode = Integer.valueOf(tut.getString("count", "0"));
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
}
