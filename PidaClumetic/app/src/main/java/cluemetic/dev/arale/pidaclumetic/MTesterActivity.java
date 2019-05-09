package cluemetic.dev.arale.pidaclumetic;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import kr.co.bootpay.Bootpay;
import kr.co.bootpay.BootpayAnalytics;
import kr.co.bootpay.enums.Method;
import kr.co.bootpay.enums.PG;

public class MTesterActivity extends AppCompatActivity {

    ImageButton picker;
    Button x1, x2, x3;
    Button payment, delivery, purchase;
    ImageView x11, x22, x33;
    String username;
    Integer foruniquenum=0;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_tester);

        username = getSharedPreferences("user", MODE_PRIVATE).getString("username", "");
        new UniqueNum().execute();

        picker = findViewById(R.id.button);
        showBtn();

        x1 = findViewById(R.id.tester1);
        x2 = findViewById(R.id.tester2);
        x3 = findViewById(R.id.tester3);
        showTesters();


        View.OnClickListener onClickListener = view -> {

            switch (view.getId()) {
                case R.id.imageView :
                    SharedPreferences tut = getSharedPreferences("tester", MODE_PRIVATE);
                    try {
                        JSONArray products = new JSONArray(tut.getString("products", "[]"));
                        Integer count = tut.getInt("count", 0);
                        JSONArray newProducts = new JSONArray();
                        for (int i = 0; i<count; i++){
                            if (i!=0) newProducts.put(products.getJSONObject(i));
                        }
                        SharedPreferences.Editor editor = tut.edit();
                        editor.putInt("count", count-1);
                        editor.putString("products", newProducts.toString());
                        editor.apply();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    showTesters();
                    showBtn();

                    break ;

                case R.id.imageView2 :
                    SharedPreferences tut2 = getSharedPreferences("tester", MODE_PRIVATE);
                    try {
                        JSONArray products2 = new JSONArray(tut2.getString("products", "[]"));
                        Integer count2 = tut2.getInt("count", 0);
                        JSONArray newProducts = new JSONArray();
                        for (int i = 0; i<count2; i++){
                            if (i!=0) newProducts.put(products2.getJSONObject(i));
                        }
                        SharedPreferences.Editor editor2 = tut2.edit();
                        editor2.putInt("count", count2-1);
                        editor2.putString("products", newProducts.toString());
                        editor2.apply();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    showTesters();
                    showBtn();

                    break ;

                case R.id.imageView3 :
                    SharedPreferences tut3 = getSharedPreferences("tester", MODE_PRIVATE);
                    try {
                        JSONArray products3 = new JSONArray(tut3.getString("products", "[]"));
                        Integer count3 = tut3.getInt("count", 0);
                        JSONArray newProducts = new JSONArray();
                        for (int i = 0; i<count3; i++){
                            if (i!=0) newProducts.put(products3.getJSONObject(i));
                        }
                        SharedPreferences.Editor editor3 = tut3.edit();
                        editor3.putInt("count", count3-1);
                        editor3.putString("products", newProducts.toString());
                        editor3.apply();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    showTesters();
                    showBtn();

                    break ;
            }
        };

        x11 = findViewById(R.id.imageView);
        x22 = findViewById(R.id.imageView2);
        x33 = findViewById(R.id.imageView3);
        x11.setOnClickListener(onClickListener);
        x22.setOnClickListener(onClickListener);
        x33.setOnClickListener(onClickListener);

//        payment = findViewById(R.id.payment);
//        payment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MTesterActivity.this, ISetupAccount2Activity.class);
//                startActivity(intent);
//            }
//        });

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
                SharedPreferences user = getSharedPreferences("user", MODE_PRIVATE);
                onClick_request();

//                new Purchase(
//                        user.getBoolean("paymentOK", false),
//                        user.getBoolean("deliveryOK", false),
//                        user.getString("access_token", ""))
//                        .execute();
            }
        });



    }



    public void onClick_request() {

        // 초기설정 - 해당 프로젝트(안드로이드)의 application id 값을 설정합니다. 결제와 통계를 위해 꼭 필요합니다.
        BootpayAnalytics.init(this, "5c46b055b6d49c2299e2a9e6");

        Log.i("###", "##########################2222#");
        long mNow = System.currentTimeMillis();
        Date mDate = new Date(mNow);

        Log.i("###", "##########################3333#");
        // 결제가 진행되기 바로 직전 호출되는 함수로, 주로 재고처리 등의 로직이 수행
        // 결제완료시 호출, 아이템 지급 등 데이터 동기화 로직을 수행합니다
        // 가상계좌 입금 계좌번호가 발급되면 호출되는 함수입니다.
        // 결제 취소시 호출
        // 에러가 났을때 호출되는 부분
        //결제창이 닫힐때 실행되는 부분
        Bootpay.init(getFragmentManager())
                .setApplicationId("5c46b055b6d49c2299e2a9e6") // 해당 프로젝트(안드로이드)의 application id 값
                .setPG(PG.KCP)
                .setMethod(Method.CARD) // 결제수단
                .setName("PIDA 테스터 구매") // 결제할 상품명
                .setOrderId(username+foruniquenum) // 결제 고유번호
                .setPrice(2500) // 결제할 금액
//                .addItem(tit, quant, "부분환불예정 (공동구매)" , productPrice) // 주문정보에 담길 상품정보, 통계를 위해 사용
                .onConfirm(message -> {
//                    if (0 < stuck) Bootpay.confirm(message); // 재고가 있을 경우.
//                    else Bootpay.removePaymentWindow(); // 재고가 없어 중간에 결제창을 닫고 싶을 경우
                    Log.d("confirm", message);
                })
                .onDone(message -> {
                    Toast.makeText(MTesterActivity.this, "결제가 완료되었습니다", Toast.LENGTH_LONG);
                    Log.d("done", message);
//                    new Purchase(group_url, quant).execute();
                })
                .onReady(message -> {
                    Toast.makeText(MTesterActivity.this, "계좌번호가 발급되었습니다", Toast.LENGTH_LONG);
                    Log.d("ready", message);
                })
                .onCancel(message -> {
                    Toast.makeText(MTesterActivity.this, "결제가 취소되었습니다", Toast.LENGTH_LONG);
                    Log.d("cancel", message);
                })
                .onError(message -> {
                    Toast.makeText(MTesterActivity.this, "에러가 발생하여 결제가 취소되었습니다", Toast.LENGTH_LONG);
                    Log.d("error", message);
                })
                .onClose(message -> Log.d("close", "close"))
                .request();
        Log.i("###", "###########################");
    }




    class UniqueNum extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Integer count = 0;
            //get the saved token
            SharedPreferences getval = getSharedPreferences("user", MODE_PRIVATE);
            String token = getval.getString("access_token", "");
            String email = getval.getString("username", "");
            try{
                URL url = new URL("http://ec2-13-125-246-38.ap-northeast-2.compute.amazonaws.com/users/"+email+"?access_token="+token);
                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setRequestMethod("GET");
                urlConn.setDoInput(true);

                if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream stream = urlConn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuilder buffer = new StringBuilder();
                    String line = "";
                    while ((line = reader.readLine()) != null) { buffer.append(line); }
                    reader.close();
                    JSONObject JsonResult = new JSONObject(buffer.toString());
                    urlConn.disconnect();

                    count += JsonResult.getJSONArray("tester_orders").length();
                    count += JsonResult.getJSONArray("purchase_orders").length();
                    count += JsonResult.getJSONArray("group_purchase_orders").length();

                    foruniquenum = count;
                }
                urlConn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
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
                Log.i("###", "tester ordering");
                SharedPreferences testers = getSharedPreferences("tester", MODE_PRIVATE);
                Integer count = testers.getInt("count", 0);
                Log.i("###", "tester ordering");

                Log.i("###", testers.getString("products", "[]"));
                JSONArray testersUrls = new JSONArray(testers.getString("products", "[]"));
                String url1 = testersUrls.getJSONObject(0).getString("url");
                String url2 = testersUrls.getJSONObject(1).getString("url");
                String url3 = testersUrls.getJSONObject(2).getString("url");

                Log.i("###", "tester ordering===");
                Log.i("###", String.valueOf((count>=3)));
                Log.i("###", String.valueOf(pay));
                Log.i("###", String.valueOf(del));
                Log.i("###", "http://ec2-13-125-246-38.ap-northeast-2.compute.amazonaws.com/tester-orders/?access_token="+token);
                if ((count>=3) && pay && del){
                    URL url = new URL("http://ec2-13-125-246-38.ap-northeast-2.compute.amazonaws.com/tester-orders/?access_token="+token);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Accept", "application/json");
                    con.setRequestProperty("Content-type", "application/json");
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    con.connect();

                    Log.i("###", "tester ordering----");
                    JSONArray products = new JSONArray();
                    products.put(url1);
                    products.put(url2);
                    products.put(url3);

                    Log.i("###", "tester ordering");
                    SharedPreferences user = getSharedPreferences("user", MODE_PRIVATE);
                    JSONObject post = new JSONObject();
                    post.put("products", products);
                    post.put("category", "http://ec2-13-125-246-38.ap-northeast-2.compute.amazonaws.com/categories/1/");
                    post.put("delivery_information", user.getString("delivery", ""));
                    post.put("payment_information", user.getString("payment", ""));

                    Log.i("###", "tester ordering");
                    Log.i("###", post.toString());

                    OutputStreamWriter os= new OutputStreamWriter(con.getOutputStream());
                    os.write( post.toString() );
                    os.flush();


                    Log.i("###", "tester ordering---" + String.valueOf(con.getResponseCode()));
                    if (con.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
                        SharedPreferences.Editor ed = testers.edit();
                        ed.putInt("count", 0);
                        ed.putString("products", "[]");
                        ed.apply();
                        return true;
                    }


                }
                else return false;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                Intent i = new Intent(getBaseContext(), QPurchaseDoneActivity.class);
                i.putExtra("type", 0);
                i.putExtra("url", "");
                startActivity(i);
                finish();
            }
            else Toast.makeText(MTesterActivity.this, "테스터 주문에 실패하였습니다. 테스터/배송/결제정보를 확인해주세요", Toast.LENGTH_SHORT).show();
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
        x1.setText("");
        x2.setText("");
        x3.setText("");
        SharedPreferences tut = getSharedPreferences("tester", MODE_PRIVATE);
        try {
            JSONArray products = new JSONArray(tut.getString("products", "[]"));
            Integer count = tut.getInt("count", 0);
            for (int i = 0; i<count; i++){
                if (i==0) x1.setText(products.getJSONObject(i).getString("name"));
                else if (i==1) x2.setText(products.getJSONObject(i).getString("name"));
                else if (i==2) x3.setText(products.getJSONObject(i).getString("name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    void showBtn(){
        SharedPreferences tut = getSharedPreferences("tester", MODE_PRIVATE);
        Integer mode = tut.getInt("count", 0);
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
        showTesters();
    }
}
