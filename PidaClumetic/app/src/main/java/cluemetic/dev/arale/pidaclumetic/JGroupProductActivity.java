package cluemetic.dev.arale.pidaclumetic;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
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
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kr.co.bootpay.Bootpay;
import kr.co.bootpay.BootpayAnalytics;
import kr.co.bootpay.enums.Method;
import kr.co.bootpay.enums.PG;
import kr.co.bootpay.listner.CancelListener;
import kr.co.bootpay.listner.CloseListener;
import kr.co.bootpay.listner.ConfirmListener;
import kr.co.bootpay.listner.DoneListener;
import kr.co.bootpay.listner.ErrorListener;
import kr.co.bootpay.listner.ReadyListener;
import kr.co.bootpay.model.BootExtra;
import kr.co.bootpay.model.BootUser;

public class JGroupProductActivity extends AppCompatActivity {

    TextView mcompany, mtitle, mprice, mseller, mmanufac, mcountry, minfourl, mpriceNow, mdiscount;
    ImageView mimg;
    Bitmap bm = null;
    String id, com, tit, pri, sell, manu, coun, inf, img, group_url;
    ListView mreview, mingredient;
    HProductIngredientsAdapter ingredientAdapter;
    HProductReviewAdapter reviewAdapter;
    String json_url="http://ec2-13-125-246-38.ap-northeast-2.compute.amazonaws.com/products/";
    String sale1q, sale1r, sale2q, sale2r, sale3q, sale3r;
    TextView msale1q, msale1r, msale2q, msale2r, msale3q, msale3r, mpriceN;
    String productU;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    Integer quant = 0;

    Integer productPrice, tempDiscount=0;
    View discountView;
    LinearLayout discountLayout;
    JSONArray PriceArray;
    String username;
    Integer foruniquenum=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_j_group_product);

        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");

        Intent intent = getIntent();
        group_url = intent.getStringExtra("group_url");

        new UniqueNum().execute();
        new GroupProductCreate().execute();
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
                .setName("공동구매-"+tit) // 결제할 상품명
                .setOrderId(username+foruniquenum) // 결제 고유번호
                .setPrice(productPrice * quant) // 결제할 금액
                .addItem(tit, quant, "부분환불예정 (공동구매)" , productPrice) // 주문정보에 담길 상품정보, 통계를 위해 사용
                .onConfirm(message -> {
//                    if (0 < stuck) Bootpay.confirm(message); // 재고가 있을 경우.
//                    else Bootpay.removePaymentWindow(); // 재고가 없어 중간에 결제창을 닫고 싶을 경우
                    Log.d("confirm", message);
                })
                .onDone(message -> {
                    Toast.makeText(JGroupProductActivity.this, "결제가 완료되었습니다", Toast.LENGTH_LONG);
                    Log.d("done", message);
                    new Purchase(group_url, quant).execute();
                })
                .onReady(message -> {
                    Toast.makeText(JGroupProductActivity.this, "계좌번호가 발급되었습니다", Toast.LENGTH_LONG);
                    Log.d("ready", message);
                })
                .onCancel(message -> {
                    Toast.makeText(JGroupProductActivity.this, "결제가 취소되었습니다", Toast.LENGTH_LONG);
                    Log.d("cancel", message);
                })
                .onError(message -> {
                    Toast.makeText(JGroupProductActivity.this, "에러가 발생하여 결제가 취소되었습니다", Toast.LENGTH_LONG);
                    Log.d("error", message);
                })
                .onClose(message -> Log.d("close", "close"))
                .request();
        Log.i("###", "###########################");







    }



    class GroupProductCreate extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {


            try{
                URL url = new URL(group_url);
                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setRequestMethod("GET");
                urlConn.setDoInput(true);

                if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    InputStream stream = urlConn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuilder buffer = new StringBuilder();
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    reader.close();
                    JSONObject curr = new JSONObject(buffer.toString());
                    urlConn.disconnect();

                    productU = curr.getJSONObject("product").getString("url");
                    sale1q = String.valueOf(curr.getJSONArray("discount_rates").getJSONObject(0).getInt("quantity"));
                    sale1r = String.valueOf(curr.getJSONArray("discount_rates").getJSONObject(0).getInt("rate"));
                    sale2q = String.valueOf(curr.getJSONArray("discount_rates").getJSONObject(1).getInt("quantity"));
                    sale2r = String.valueOf(curr.getJSONArray("discount_rates").getJSONObject(1).getInt("rate"));
                    sale3q = String.valueOf(curr.getJSONArray("discount_rates").getJSONObject(2).getInt("quantity"));
                    sale3r = String.valueOf(curr.getJSONArray("discount_rates").getJSONObject(2).getInt("rate"));
                    PriceArray = curr.getJSONArray("orders");

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
        protected void onPostExecute(Void aVoid) {
            new ProductCreate().execute();
            new PriceCreate().execute();
        }
    }



    class ProductCreate extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {


            try{
                URL url = new URL(productU);
                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setRequestMethod("GET");
                urlConn.setDoInput(true);

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

                    com = JsonResult.getJSONObject("brand").getString("name");
                    tit = JsonResult.getString("name");
                    pri = String.valueOf(JsonResult.getInt("price"));
                    sell = JsonResult.getString("info_seller");
                    manu = JsonResult.getString("info_manufacturer");
                    coun = JsonResult.getString("info_country");
                    inf = JsonResult.getString("info_url");
                    img = JsonResult.getString("image");
                    id = String.valueOf(JsonResult.getInt("id"));

                    productPrice = JsonResult.getInt("price");
                    tempDiscount = JsonResult.getInt("temp_opening_discount");

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
        protected void onPostExecute(Void aVoid) {
            DoOnCreate();
        }
    }




    void DoOnCreate(){

        mimg = findViewById(R.id.img);
        new ShowImageConnection().execute(img);

        mtitle = findViewById(R.id.title);
        mcompany = findViewById(R.id.company);
        mprice = findViewById(R.id.priceT);
        mseller = findViewById(R.id.inform1);
        mmanufac = findViewById(R.id.inform2);
        mcountry = findViewById(R.id.inform3);
        minfourl = findViewById(R.id.inform4);
        msale1q = findViewById(R.id.sale1);
        msale1r = findViewById(R.id.sale11);
        msale2q = findViewById(R.id.sale2);
        msale2r = findViewById(R.id.sale22);
        msale3q = findViewById(R.id.sale3);
        msale3r = findViewById(R.id.sale33);

        discountLayout = findViewById(R.id.discountLayout);
        discountView = findViewById(R.id.discountView);
        if (tempDiscount==0){
            discountLayout.setVisibility(LinearLayout.GONE);
            discountView.setVisibility(View.GONE);
        }else{
            discountLayout.setVisibility(LinearLayout.VISIBLE);
            discountView.setVisibility(View.VISIBLE);
            mdiscount.setText(String.format("%d원 (자동 적용)", tempDiscount));
            productPrice = productPrice - tempDiscount;
        }

        mtitle.setText(tit);
        mcompany.setText(com);
        mprice.setText("정가 " + pri + "원");
        mseller.setText(sell);
        mmanufac.setText(manu);
        mcountry.setText(coun);
        msale1q.setText(sale1q + "개 ~ ");
        msale1r.setText(sale1r + "% 할인");
        msale2q.setText(sale2q + "개 ~ ");
        msale2r.setText(sale2r + "% 할인");
        msale3q.setText(sale3q + "개 ~ ");
        msale3r.setText(sale3r + "% 할인");

        minfourl.setOnClickListener(v -> {
            Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse(inf) );
            startActivity( browse );
        });

        //scrolls
        mingredient = findViewById(R.id.Ingredient);
        mreview = findViewById(R.id.review);
        new IngredientConnection().execute();

        Button order = findViewById(R.id.group_order);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });

    }


    class PriceCreate extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            Integer count = 0;
            for (int i = 0; i<PriceArray.length(); i++){
                try{
                    URL url = new URL(PriceArray.getString(i));
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

                        count += JsonResult.getInt("quantity");
                    }
                    urlConn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return count;
        }

        @Override
        protected void onPostExecute(Integer res) {
            ChangePriceN(res);
        }
    }

    void ChangePriceN(Integer count){
        mpriceNow = findViewById(R.id.priceN);
        mpriceNow.setText("  현재 "+count+"개 구매 중");
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










    private class IngredientConnection extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected void onPreExecute() { }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                Log.i("###", "111111");
                //send get request
                Log.i("###", productU);
                URL url = new URL(productU);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Content-Type", "application/json");
                con.setDoInput(true);
                con.connect();

                //get JSONObject result
                Log.i("###", "111111");
                StringBuilder result = new StringBuilder();
                InputStream in = new BufferedInputStream(con.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                //get response
                JSONObject response = new JSONObject(result.toString());

                Log.i("###", "good response");
                return response;


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
        protected void onPostExecute(JSONObject result) {
            JSONArray responseIngredeint;
            try {
                ArrayList<HIngredient> arr = new ArrayList<HIngredient>();
                responseIngredeint = result.getJSONArray("ingredients");
                for (int i = 0; i < responseIngredeint.length(); i++) {
                    String currName = responseIngredeint.getJSONObject(i).getString("name");
                    Integer currGrade = responseIngredeint.getJSONObject(i).getInt("ewg_grade");
                    Log.i("###", "in the switch, currGrade = " + String.valueOf(currGrade));
                    Log.i("###", "in the switch, name = " + currName);
                    switch (currGrade){
                        case 1:
                            arr.add(new HIngredient(ContextCompat.getDrawable(getBaseContext(), R.drawable.ingredients_icons_grade_1), currName));
                            break;
                        case 2:
                            arr.add(new HIngredient(ContextCompat.getDrawable(getBaseContext(), R.drawable.ingredients_icons_grade_2), currName));
                            break;
                        case 3:
                            arr.add(new HIngredient(ContextCompat.getDrawable(getBaseContext(), R.drawable.ingredients_icons_grade_3), currName));
                            break;
                        case 4:
                            arr.add(new HIngredient(ContextCompat.getDrawable(getBaseContext(), R.drawable.ingredients_icons_grade_4), currName));
                            break;
                        case 5:
                            arr.add(new HIngredient(ContextCompat.getDrawable(getBaseContext(), R.drawable.ingredients_icons_grade_5), currName));
                            break;
                        case 6:
                            arr.add(new HIngredient(ContextCompat.getDrawable(getBaseContext(), R.drawable.ingredients_icons_grade_6), currName));
                            break;
                        case 7:
                            arr.add(new HIngredient(ContextCompat.getDrawable(getBaseContext(), R.drawable.ingredients_icons_grade_7), currName));
                            break;
                        case 8:
                            arr.add(new HIngredient(ContextCompat.getDrawable(getBaseContext(), R.drawable.ingredients_icons_grade_8), currName));
                            break;
                        default:
                            arr.add(new HIngredient(ContextCompat.getDrawable(getBaseContext(), R.drawable.ingredients_icons_grade_9), currName));
                            break;
                    }
                    Log.i("###", arr.toString());

                }
                ingredientAdapter = new HProductIngredientsAdapter(getBaseContext(), arr);
                mingredient.setAdapter(ingredientAdapter);
                setListViewHeightBasedOnChildren(mingredient);

                JSONArray responseUrl = result.getJSONArray("reviews");
                startReviewConnection(responseUrl);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    Void startReviewConnection(JSONArray arr){
        new ReviewConnection().execute(arr);
        return null;
    }

    private class ReviewConnection extends AsyncTask<JSONArray, Void, ArrayList<HReview>> {
        @Override
        protected void onPreExecute() { }

        @Override
        protected ArrayList<HReview> doInBackground(JSONArray... params) {
            try {
                JSONArray reviewUrls = params[0];
                ArrayList<HReview> arr = new ArrayList<>();
                for (int i = 0; i < reviewUrls.length(); i++){

                    //send get request
                    URL url = new URL(reviewUrls.getString(i));
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setDoInput(true);
                    con.connect();

                    //get JSONObject result
                    StringBuilder result = new StringBuilder();
                    InputStream in = new BufferedInputStream(con.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    //get strings from response and add to adapter
                    JSONObject response = new JSONObject(result.toString());
                    arr.add(new HReview(response.getString("owner").substring(68,70)+"**", response.getString("content")));
                }
                return arr;

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
        protected void onPostExecute(ArrayList arr) {

            reviewAdapter = new HProductReviewAdapter(arr);
            mreview.setAdapter(reviewAdapter);
            setListViewHeightBasedOnChildrenReview(mreview);
        }
    }





    private class ShowImageConnection extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() { }

        @Override
        protected Boolean doInBackground(String... params) {

            try {
                URL aURL = new URL(params[0]);
                URLConnection conn = aURL.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mimg.setImageBitmap(bm);
        }
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < ingredientAdapter.getCount(); i++) {
            View listItem = ingredientAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (ingredientAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
    public void setListViewHeightBasedOnChildrenReview(ListView listView) {
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < reviewAdapter.getCount(); i++) {
            View listItem = reviewAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (ingredientAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }



    void show()
    {
        AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
        builder3.setTitle("공동구매 상품 개수");
        builder3.setMessage("구매할 상품의 개수를 선택해주세요. 공동구매의 경우 상품은 원가로 결제되며, 공동구매가 마감된 이후 할인액만큼 환불이 이루어집니다. \n\n 주의하세요! 현재 저장된 배송지역으로 배송됩니다. 설정에서 배송 지역은 변경 가능합니다.");
        final int[] num = {1};

        NumberPicker np = new NumberPicker(this);
        np.setMaxValue(10);
        np.setMinValue(1);
        NumberPicker.OnValueChangeListener npListener = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                num[0] = newVal;
            }
        };
        np.setOnValueChangedListener(npListener);

        builder3.setView(np);

        builder3.setPositiveButton("확인",
                (dialog, which) -> {
                    quant = num[0];
                    show2();
                });
        builder3.setNegativeButton("취소", (dialog, which) -> {
        });
        builder3.show();
    }

    void show2()
    {
        AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
        builder3.setTitle("공동구매 확정");
        builder3.setMessage("주의하세요!\n 이미 저장된 배송 지역으로 배송될 예정입니다. 설정에서 배송 지역은 변경할 수 있습니다.\n\n 확인을 누르면 결제창으로 이동합니다.");
        builder3.setPositiveButton("확인", (dialog, which) -> onClick_request());
        builder3.setNegativeButton("취소", (dialog, which) -> { });
        builder3.show();
    }



    private class Purchase extends AsyncTask<String, Void, Boolean> {
        String group_url_str;
        Integer num;

        public Purchase(String group_url_str, Integer num) {
            this.group_url_str = group_url_str;
            this.num = num;
        }

        @Override
        protected void onPreExecute() { }


        @Override
        protected Boolean doInBackground(String... params) {
            try{
                Log.i("###", "0000");
                SharedPreferences dd = getSharedPreferences("user", MODE_PRIVATE);
                String token = dd.getString("access_token", "");
                URL url = new URL("http://ec2-13-125-246-38.ap-northeast-2.compute.amazonaws.com/group-purchase-orders/?access_token="+token);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setDoOutput(true);
                con.setDoInput(true);
                con.connect();

                Log.i("###", "0001");
                SharedPreferences tut = getSharedPreferences("user", MODE_PRIVATE);
                JSONObject post = new JSONObject();
                post.put("event", group_url_str);
                post.put("quantity", num);
                post.put("delivery_information", tut.getString("delivery", ""));
                post.put("payment_information", tut.getString("payment", ""));

                OutputStreamWriter os= new OutputStreamWriter(con.getOutputStream());
                os.write( post.toString() );
                os.flush();


                Log.i("###", "group purchase at group product activity");
                Log.i("###", post.toString());
                Log.i("###", String.valueOf(con.getResponseCode()));
                Log.i("###", con.getResponseMessage());
                if ((con.getResponseCode() == HttpURLConnection.HTTP_OK) || (con.getResponseCode() == HttpURLConnection.HTTP_CREATED))
                    return true;


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
                i.putExtra("url", img);
                startActivity(i);
                finish();
            }

            else Toast.makeText(JGroupProductActivity.this, "주문에 실패하였습니다. 입력한 정보를 확인해주세요", Toast.LENGTH_SHORT).show();
        }
    }



}
