package cluemetic.dev.arale.pidaclumetic;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.NumberPicker;
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
import com.google.api.services.vision.v1.model.ImageContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kr.co.bootpay.Bootpay;
import kr.co.bootpay.BootpayAnalytics;
import kr.co.bootpay.enums.Method;
import kr.co.bootpay.enums.PG;

public class NBasketActivity extends AppCompatActivity {

    Button payment, delivery, purchase;
    private BottomNavigationView navigationView;
    ListView listview;
    NBasketAdapter adapter;
    TextView mtotalP;
    String username;
    Integer foruniquenum=0, totalPrice=0;
    private long backKeyPressedTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_n_basket);

        new UniqueNum().execute();
        username = getSharedPreferences("user", MODE_PRIVATE).getString("username","");

        //네비게이션뷰 설정(클릭시 이동)
        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(menuItem -> {
            navigationView.postDelayed(() -> {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.navigation_category) {
                    startActivity(new Intent(getBaseContext(), ECategoryActivity.class));
                } else if (itemId == R.id.navigation_basket) {
                    return;
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
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        mtotalP = findViewById(R.id.purchase);


        delivery = findViewById(R.id.delivery);
        delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NBasketActivity.this, ISetupAccount3Activity.class);
                startActivity(intent);
            }
        });

        //이미지 검색 버튼을 누르면 리스트액티비티로 이동.
        ImageButton mlist = findViewById(R.id.btnSearch);
        mlist.setOnClickListener(v -> startNewActivity("검색할 제품명을 입력해주세요", false ));


        //현재 들어있는 장바구니 상품들 확인
        SharedPreferences temp = getSharedPreferences("order", MODE_PRIVATE);
        Log.i("###", temp.getString("products", "[]"));


        listview = findViewById(R.id.listView);
        updatelist();
        // 위에서 생성한 listview에 클릭 이벤트 핸들러 정의.
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                NBasketItem item = (NBasketItem) parent.getItemAtPosition(position) ;

                AlertDialog.Builder builder3 = new AlertDialog.Builder(NBasketActivity.this);
                builder3.setTitle("개수 변경");
                builder3.setMessage("상품의 개수를 변경해주세요. 0을 선택하고 확인을 누르면 상품이 제거됩니다. 취소를 누를 경우 변경되지 않습니다.");
                final int[] num = {0};

                NumberPicker np = new NumberPicker(NBasketActivity.this);
                np.setMaxValue(10);
                np.setMinValue(0);
                NumberPicker.OnValueChangeListener npListener = (picker, oldVal, newVal) -> num[0] = newVal;
                np.setOnValueChangedListener(npListener);
                builder3.setView(np);
                builder3.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    SharedPreferences pref = getSharedPreferences("order", MODE_PRIVATE);
                                    JSONArray order = new JSONArray(pref.getString("products","[]"));
                                    JSONObject change = order.getJSONObject(position);
                                    change.put("quantity", num[0]);
                                    order.put(position,change);

                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putString("products", order.toString());
                                    editor.commit();

                                    Toast.makeText(NBasketActivity.this, "변경되었습니다", Toast.LENGTH_SHORT).show();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                updatelist();
                            }
                        });
                builder3.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder3.show();
            }
        }) ;

        //주문 버튼 누르면 purchase
        purchase = findViewById(R.id.purchase);
        purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick_request();
                //new Purchase().execute();

            }
        });


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
                .setName("PIDA 상품 구매") // 결제할 상품명
                .setOrderId(username+foruniquenum) // 결제 고유번호
                .setPrice(totalPrice) // 결제할 금액
//                .addItem(tit, quant, "부분환불예정 (공동구매)" , productPrice) // 주문정보에 담길 상품정보, 통계를 위해 사용
                .onConfirm(message -> {
//                    if (0 < stuck) Bootpay.confirm(message); // 재고가 있을 경우.
//                    else Bootpay.removePaymentWindow(); // 재고가 없어 중간에 결제창을 닫고 싶을 경우
                    Log.d("confirm", message);
                })
                .onDone(message -> {
                    Toast.makeText(NBasketActivity.this, "결제가 완료되었습니다", Toast.LENGTH_LONG);
                    Log.d("done", message);
//                    new Purchase(group_url, quant).execute();
                })
                .onReady(message -> {
                    Toast.makeText(NBasketActivity.this, "계좌번호가 발급되었습니다", Toast.LENGTH_LONG);
                    Log.d("ready", message);
                })
                .onCancel(message -> {
                    Toast.makeText(NBasketActivity.this, "결제가 취소되었습니다", Toast.LENGTH_LONG);
                    Log.d("cancel", message);
                })
                .onError(message -> {
                    Toast.makeText(NBasketActivity.this, "에러가 발생하여 결제가 취소되었습니다", Toast.LENGTH_LONG);
                    Log.d("error", message);
                })
                .onClose(message -> Log.d("close", "close"))
                .request();
        Log.i("###", "###########################");
    }



    void updatelist(){

        totalPrice=0;

        SharedPreferences sharedPreferences = getSharedPreferences("order", MODE_PRIVATE);
        try {
            JSONArray products = new JSONArray(sharedPreferences.getString("products","[]"));
            JSONArray productsSP = new JSONArray();

            // Adapter 생성
            adapter = new NBasketAdapter(new ArrayList<NBasketItem>()) ;

            // 리스트뷰 참조 및 Adapter달기
            listview = (ListView) findViewById(R.id.listView);
            listview.setAdapter(adapter);

            int count=0;
            ArrayList<NBasketItem> listViewItemList = new ArrayList<>();
            for (int i = 0; i<products.length(); i++){
                JSONObject product = products.getJSONObject(i);

                if (product.getInt("quantity")!=0){
                    adapter.addItem(product.getString("img"),
                            product.getString("name"),
                            product.getInt("price"),
                            product.getInt("quantity"));

                    totalPrice += product.getInt("price") * product.getInt("quantity");


                    productsSP.put(product);
                    count++;
                }
            }

            mtotalP.setText("총 " + totalPrice + "원 주문하기");

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("products", productsSP.toString());
            editor.putInt("count", count);
            editor.commit();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    private class Purchase extends AsyncTask<String, Void, Boolean> {
        Boolean payOK, delOK;
        String token, payment, delivery;
        String productsStr;
        JSONArray items;
        Integer count;
        String imgUrlForOtherActivity;


        @Override
        protected void onPreExecute() { }


        @Override
        protected Boolean doInBackground(String... params) {
            try{
                Log.i("###", "purchase -get user/products data- at NBasketActivity");
                SharedPreferences order = getSharedPreferences("order", MODE_PRIVATE);
                count = order.getInt("count", 0);
                productsStr = order.getString("products", "[]");

                SharedPreferences user = getSharedPreferences("user", MODE_PRIVATE);
                token = user.getString("access_token", "");
                payOK = user.getBoolean("paymentOK", false);
                delOK = user.getBoolean("deliveryOK", false);
                payment = user.getString("payment", "");
                delivery = user.getString("delivery", "");

                Log.i("###", "purchase at NBasketActivity===");
                Log.i("###", String.valueOf(payOK));
                Log.i("###", String.valueOf(delOK));
                //if (payOK && delOK){
                if (delOK){
                    URL url = new URL("http://ec2-13-125-246-38.ap-northeast-2.compute.amazonaws.com/purchase-orders/?access_token="+token);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Accept", "application/json");
                    con.setRequestProperty("Content-type", "application/json");
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    con.connect();

                    Log.i("###", "purchase at NBasketActivity----make items");
                    JSONArray products = new JSONArray(productsStr);
                    items = new JSONArray();
                    for (int i=0; i<count; i++){
                        JSONObject currProduct = products.getJSONObject(i);
                        JSONObject currItem = new JSONObject();
                        currItem.put("product", currProduct.getString("url"));
                        currItem.put("quantity", currProduct.getInt("quantity"));
                        items.put(currItem);
                    }
                    Log.i("###", productsStr);
                    Log.i("###", items.toString());


                    Log.i("###", "purchase at NBasketActivity----make post body");
                    JSONObject post = new JSONObject();
                    post.put("items", items);
                    post.put("delivery_information", delivery);
                    post.put("payment_information", payment);
                    Log.i("###", post.toString());

                    OutputStreamWriter os= new OutputStreamWriter(con.getOutputStream());
                    os.write( post.toString() );
                    os.flush();

                    Log.i("###", "purchase at NBasketActivity---Code : " + String.valueOf(con.getResponseCode()));
                    if (con.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
                        SharedPreferences.Editor ed = order.edit();
                        ed.putInt("count", 0);
                        ed.putString("products", "[]");
                        ed.apply();


                        InputStream stream = con.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                        StringBuilder buffer = new StringBuilder();
                        String line = "";
                        while ((line = reader.readLine()) != null) {
                            buffer.append(line);
                        }
                        reader.close();
                        JSONObject JsonResult = new JSONObject(buffer.toString());
                        con.disconnect();

                        imgUrlForOtherActivity = JsonResult.getString("image");


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
                i.putExtra("type", 1);
                i.putExtra("url", imgUrlForOtherActivity);
                startActivity(i);
                finish();
            }
            else Toast.makeText(NBasketActivity.this, "주문에 실패하였습니다. 배송/결제정보를 확인해주세요", Toast.LENGTH_SHORT).show();
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
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
        }
    }

}
