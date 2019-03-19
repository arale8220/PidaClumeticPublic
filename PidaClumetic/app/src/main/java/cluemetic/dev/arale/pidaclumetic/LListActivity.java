package cluemetic.dev.arale.pidaclumetic;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LListActivity extends AppCompatActivity {

    private ArrayList<HashMap<String,String>> Data = new ArrayList<HashMap<String, String>>();
    private HashMap<String,String> InputData1 = new HashMap<>();
    private EditText editText;
    private ImageButton search;
    String finding="";
    Boolean ocr;
    //for horizontal picker
    ViewPager viewPager;
    FSubCategoryAdapter adapter;
    List<Product> products;
    Intent intent;
    String[] productsUrls;
    Integer subCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_l_list);
        search = findViewById(R.id.btnSearch);
        editText = findViewById(R.id.editText);

        Intent intent = getIntent();
        ocr = intent.getBooleanExtra("ocr", false);
        finding = intent.getStringExtra("identifier");

        if (ocr){
            new ListConnection().execute(finding, "ocr_identifier=");
        } else{
            editText.setHint(finding);
        }


        //검색 버튼을 누를때마다 검색 실행
        search.setOnClickListener(v -> {
            finding = String.valueOf(editText.getText());
            new ListConnection().execute(finding, "search=");
        });
    }



    private class ListConnection extends AsyncTask<String, Void, JSONArray> {
        @Override
        protected void onPreExecute() { }

        @Override
        protected JSONArray doInBackground(String... params) {
            try {

                String search_url = "http://ec2-13-125-246-38.ap-northeast-2.compute.amazonaws.com/products/?" + params[1] + params[0];
                URL url = new URL(search_url);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setDoInput(true);
                con.connect();

                if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    StringBuilder result = new StringBuilder();
                    InputStream in = new BufferedInputStream(con.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    JSONArray res = new JSONArray(result.toString());
                    con.disconnect();
                    return  res;
                }

                con.disconnect();
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
        protected void onPostExecute(JSONArray result) {

            products = new ArrayList<>();
            try {
                for (int i = 0; i<result.length(); i++) {
                    JSONObject curr = result.getJSONObject(i);

                    String category = curr.getString("category");
                    Log.i("###", category);
                    category = category.substring(category.length()-2, category.length()-1);
                    Log.i("###", category);

                    subCount = Integer.valueOf(category);
                    Log.i("###", String.valueOf(subCount));
                    String price = "";
                    if (subCount == 1){
                        price = String.valueOf(curr.getInt("capacity")) + "ml   ￦"+String.valueOf(curr.getInt("price"));
                    }else if (subCount == 5){
                        price = String.valueOf(curr.getInt("capacity")) + "매   ￦"+String.valueOf(curr.getInt("price"));
                    }
                    String str1 = curr.getString("category");
                    String[] words = str1.split("/");

                    Product item = new Product(
                            curr.getString("name"),
                            price,
                            curr.getJSONObject("brand").getString("name"),
                            String.valueOf(curr.getInt("id")),
                            curr.getString("info_url"),
                            curr.getString("image"),
                            curr.getString("info_seller"),
                            curr.getString("info_manufacturer"),
                            curr.getString("info_country"),
                            words[words.length-2]);

                    products.add(item);
                }

            } catch (JSONException e) {
            e.printStackTrace();
            }

        showList();
        }
    }


    void showList(){
        adapter = new FSubCategoryAdapter(products, this);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
    }


}
