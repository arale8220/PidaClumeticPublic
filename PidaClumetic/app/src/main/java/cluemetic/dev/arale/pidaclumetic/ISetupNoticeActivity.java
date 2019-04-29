package cluemetic.dev.arale.pidaclumetic;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ISetupNoticeActivity extends AppCompatActivity {
    ArrayList<ISetupNoticeItem> listViewItemList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_i_notice);

        Intent i = getIntent();
        Integer type = i.getIntExtra("notice0faq1", 0);
        listViewItemList = new ArrayList<ISetupNoticeItem>();

        if (type == 0){

            Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle("공지사항");

            new Connection("http://ec2-13-125-246-38.ap-northeast-2.compute.amazonaws.com/notices").execute();

        }else if (type == 1){

            Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle("FAQ");

            new Connection("http://ec2-13-125-246-38.ap-northeast-2.compute.amazonaws.com/faqs").execute();

        }else {
            Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle("이용약관");

            new Connection("http://ec2-13-125-246-38.ap-northeast-2.compute.amazonaws.com/agreements?visible=true").execute();

        }


    }


    //서버 데이터 받기
    private class Connection extends AsyncTask<String, Void, Boolean> {
        String json_url;

        public Connection(String url) {
            this.json_url = url;
        }

        @Override
        protected void onPreExecute() { }


        @Override
        protected Boolean doInBackground(String... params) {
            try{

                URL url = new URL(json_url);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
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

                JSONArray Jres = new JSONArray(buffer.toString());

                for (int i = 0; i<Jres.length(); i++){
                    JSONObject curr = Jres.getJSONObject(i);
                    listViewItemList.add(
                            new ISetupNoticeItem(
                                    curr.getString("title"),
                                    curr.getString("content"),
                                    false));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            show();
        }
    }

    void show(){

        ListView listview ;
        ISetupNoticeAdapter adapter;

        adapter = new ISetupNoticeAdapter(listViewItemList) ;

        listview = (ListView) findViewById(R.id.list);
        listview.setAdapter(adapter);


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listViewItemList.get(position).setClicked(!listViewItemList.get(position).getClicked());
                adapter.updateResults(listViewItemList);
            }
        });

    }



}
