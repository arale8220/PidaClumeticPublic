package cluemetic.dev.arale.pidaclumetic;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class HProductActivity extends AppCompatActivity {
    Intent intent;
    Integer count;
    String[] ingredients, reviews;
    String json_url="http://ec2-13-125-246-38.ap-northeast-2.compute.amazonaws.com/products/";
    String info_url="";
    TextView mcompany, mtitle, mprice, mseller, mmanufac, mcountry, minfourl;
    ImageView mimg;
    Bitmap bm = null;
    String id, com, tit, pri, sell, manu, coun, inf, img, subCount;
    ListView mreview, mingredient;
    HProductIngredientsAdapter ingredientAdapter;
    HProductReviewAdapter reviewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h_product);


        intent = getIntent();
        id = intent.getStringExtra("id");

        new ProductCreate().execute();

    }


    class ProductCreate extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {


            try{
                URL url = new URL("http://ec2-13-125-246-38.ap-northeast-2.compute.amazonaws.com/products/"+id);
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
                    String[] tokens = JsonResult.getString("category").split("/");
                    Log.i("###", "~~~~~"+tokens.toString());
                    String lastToken = tokens[tokens.length-1];
                    Log.i("###", lastToken + "$$$$$$$$$$$$$$$$$$$");
                    subCount = lastToken;
                    img = JsonResult.getString("image");

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
        mprice = findViewById(R.id.price);
        mseller = findViewById(R.id.inform1);
        mmanufac = findViewById(R.id.inform2);
        mcountry = findViewById(R.id.inform3);
        minfourl = findViewById(R.id.inform4);

        mtitle.setText(tit);
        mcompany.setText(com);
        mprice.setText(pri);
        mseller.setText(sell);
        mmanufac.setText(manu);
        mcountry.setText(coun);
        minfourl.setOnClickListener(v -> {
            Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse(inf) );
            startActivity( browse );
        });

        Button tester = findViewById(R.id.tester);
        tester.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getSharedPreferences("tester", MODE_PRIVATE);
                Integer tester_count = pref.getInt("count", 0);

                if ("4321".contains(subCount)) {
                    if (tester_count>2) show(1);
                    else show(0);
                }else show(2);

            }
        });
        Button order = findViewById(R.id.order);
        order.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                show(3);
            }
        });

        //scrolls
        mingredient = findViewById(R.id.Ingredient);
        mreview = findViewById(R.id.review);
        new IngredientConnection().execute();
    }






    private class IngredientConnection extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected void onPreExecute() { }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                Log.i("###", "111111");
                //send get request
                URL url = new URL(json_url + id);
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
            JSONArray responseIngredeint = null;
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
                    String temp = response.getString("owner").substring(68,69) + Strings.repeat("*", response.getString("owner").length() - 70);
                    arr.add(new HReview(temp, response.getString("content")));
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

    void addP (boolean temp){
        if (temp){
            try {
                SharedPreferences pref = getSharedPreferences("tester", MODE_PRIVATE);
                Integer tester_count = pref.getInt("count",0);
                JSONArray products = new JSONArray(pref.getString("products", "[]"));

                SharedPreferences.Editor editor = pref.edit();
                JSONObject newP = new JSONObject();

                newP.put("name", tit);
                newP.put("url", json_url+id+"/");
                products.put(newP);

                editor.putInt("count", tester_count + 1);
                editor.putString("products", products.toString());
                editor.commit();
                Log.i("###", String.valueOf(tester_count+1));
                Log.i("###", products.toString());


            } catch (JSONException e) {
            e.printStackTrace();
            }

        }
    }


    //각 모드에 따라 변경 창 띄우기
    void show(Integer mode) {
        switch (mode) {

            case 0:
                AlertDialog.Builder builder0 = new AlertDialog.Builder(this);
                builder0.setTitle("테스터");

                SharedPreferences pref = getSharedPreferences("tester", MODE_PRIVATE);
                Integer tester_count = pref.getInt("count", 0);
                JSONArray products = new JSONArray();
                try {
                    products = new JSONArray(pref.getString("products", "[]"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (tester_count >= 3){
                    builder0.setMessage("이미 세개의 테스터를 선택하셨습니다.");
                    builder0.setPositiveButton("확인",(dialog, which) -> {});
                    builder0.show();
                    break;
                }else{
                    boolean temp = true;
                    for (int i=0 ; i<tester_count ; i++){
                        try {
                            if (tit.equals(products.getJSONObject(i).getString("name"))){
                                temp = false;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    String tempstr;
                    if (temp) tempstr = "해당 제품이 테스터에 추가됩니다.";
                    else tempstr = "이미 테스터로 추가된 상품입니다.";
                    builder0.setMessage(tempstr);
                    boolean finalTemp = temp;
                    builder0.setPositiveButton("확인",(dialog, which) -> {addP(finalTemp);});
                    builder0.show();
                    break;
                }




            case 1:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setTitle("테스터");
                builder1.setMessage("이미 세개의 테스터를 선택하셨습니다.");
                builder1.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                builder1.show();
                break;


            case 2:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setTitle("테스터");
                builder2.setMessage("테스터 신청은 '크림' 카테고리에서만 가능합니다.");
                builder2.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                builder2.show();
                break;


            case 3:
                AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
                builder3.setTitle("장바구니");
                builder3.setMessage("장바구니에 추가할 상품의 개수를 고르고, 확인을 눌러주세요. 취소를 누를 경우 추가되지 않습니다.");
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
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    SharedPreferences pref = getSharedPreferences("order", MODE_PRIVATE);
                                    JSONArray order = new JSONArray(pref.getString("products","[]"));
                                    int orderN = pref.getInt("count",0);

                                    SharedPreferences.Editor editor = pref.edit();
                                    JSONArray newProducts = new JSONArray();

                                    if (orderN == 0){
                                        JSONObject newJ = new JSONObject();
                                        newJ.put("url", json_url+id+"/");
                                        newJ.put("img", img);
                                        newJ.put("id", id);
                                        newJ.put("price", pri);
                                        newJ.put("quantity", num[0]);
                                        newJ.put("name", tit);
                                        newProducts.put(newJ);
                                        editor.putString("products", newProducts.toString());
                                        editor.putInt("count",1);
                                        editor.commit();
                                    }

                                    else{
                                        boolean notExist = true;
                                        for (int i = 0; i < orderN; i++){

                                            JSONObject curr = order.getJSONObject(i);
                                            if (id.equals(curr.getString("id"))){
                                                curr.put("quantity", curr.getInt("quantity")+num[0]);
                                                notExist = false;
                                            }
                                            newProducts.put(curr);

                                            if((i == orderN-1) & notExist){
                                                JSONObject newJ = new JSONObject();
                                                newJ.put("url", json_url+id+"/");
                                                newJ.put("img", img);
                                                newJ.put("id", id);
                                                newJ.put("price", pri);
                                                newJ.put("quantity", num[0]);
                                                newJ.put("name", tit);
                                                newProducts.put(newJ);
                                                editor.putString("products", newProducts.toString());
                                                editor.putInt("count", orderN+1);
                                                editor.commit();
                                            }
                                        }
                                    }

                                    Toast.makeText(HProductActivity.this, "상품이 추가되었습니다", Toast.LENGTH_SHORT).show();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                builder3.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder3.show();
                break;

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


}