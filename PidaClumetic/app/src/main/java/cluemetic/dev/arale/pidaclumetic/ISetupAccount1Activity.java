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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
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

public class ISetupAccount1Activity extends AppCompatActivity {


    Toolbar mToolbar;
    String email, pw, gender, age, skintype, concern, allergy;
    Button emailBtn, pwBtn, genderBtn, ageBtn, skintypeBtn, concernBtn, allergyBtn;
    TextView tEmail, tPw, tGender, tAge, tSkintype, tConcern, tAllergy;
    SharedPreferences sharedPreferences;
    String json_url;
    String access_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_i_account_1);


        //툴바 제목 : 내정보
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("내 정보");


        //레이아웃의 컴포넌트들 불러오기
        emailBtn = findViewById(R.id.emailBtn);
        pwBtn = findViewById(R.id.pwBtn);
        genderBtn = findViewById(R.id.genderBtn);
        ageBtn = findViewById(R.id.ageBtn);
        skintypeBtn = findViewById(R.id.skintypeBtn);
        concernBtn = findViewById(R.id.concernBtn);
        allergyBtn = findViewById(R.id.allergyBtn);
        tEmail = findViewById(R.id.email);
        tPw = findViewById(R.id.pw);
        tGender = findViewById(R.id.gender);
        tAge = findViewById(R.id.age);
        tSkintype = findViewById(R.id.skintype);
        tConcern = findViewById(R.id.concern);
        tAllergy = findViewById(R.id.allergy);


        //현재 저장되어있는 유저의 정보 불러오기, 이후 update 함수를 통해 정보 띄움
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        email = sharedPreferences.getString("username", "     ");
        pw = sharedPreferences.getString("password","     ");
        access_token = sharedPreferences.getString("access_token","     ");
        json_url = "http://ec2-13-125-246-38.ap-northeast-2.compute.amazonaws.com/users/"
                + email + "/?access_token=" + access_token;

        new GETConnection().execute();



        //클릭리스너가 show 함수에 모드 전달
        //이후 show 함수에서 모드별로 변경 가능한 새로운 창의 띄워 변경, update 함수 호출
        Button.OnClickListener onClickListener = view -> {
            switch (view.getId()) {
                case R.id.emailBtn :
                    show(0);
                    break ;
                case R.id.pwBtn :
                    show(1);
                    break ;
                case R.id.genderBtn :
                    show(2);
                    break ;
                case R.id.ageBtn :
                    show(3);
                    break ;
                case R.id.skintypeBtn :
                    show(4);
                    break;
                case R.id.concernBtn :
                    show(5);
                    break;
                case R.id.allergyBtn :
                    show(6);
                    break ;
            }
        };

        //show 함수에 모드 전달하는 리스너 설정하기
        emailBtn.setOnClickListener(onClickListener) ;
        pwBtn.setOnClickListener(onClickListener) ;
        genderBtn.setOnClickListener(onClickListener) ;
        ageBtn.setOnClickListener(onClickListener) ;
        skintypeBtn.setOnClickListener(onClickListener) ;
        concernBtn.setOnClickListener(onClickListener) ;
        allergyBtn.setOnClickListener(onClickListener) ;

    }

    // 변경된 사항들을 화면에 띄워줌
    void update(){
        tEmail.setText(email);
        tPw.setText(getPwString(pw.length()));
        tGender.setText(getGenderString(gender));
        tAge.setText(getAgeString(age));
        tSkintype.setText(getSkintypeString(skintype));
        tConcern.setText(getConcernString(concern));
        tAllergy.setText(getAllergyString(allergy));
    }


    //각 모드에 따라 변경 창 띄우기
    void show(Integer mode)
    {
        switch (mode) {
            //change email?
            case 0:
                Toast.makeText(getApplicationContext(), "이메일은 변경할 수 없습니다.", Toast.LENGTH_SHORT).show();
                break;


            //change pw
            case 1:
                Toast.makeText(getApplicationContext(), "비밀번호는 변경할 수 없습니다.", Toast.LENGTH_SHORT).show();
                break;


            //change gender
            case 2:
                final List<String> ListGenderItems = new ArrayList<>();
                ListGenderItems.add("여성");
                ListGenderItems.add("남성");
                final CharSequence[] GenderItems =  ListGenderItems.toArray(new String[ ListGenderItems.size()]);
                final List SelectedGenderItems  = new ArrayList();
                int defaultGenderItem = Integer.valueOf(gender);
                SelectedGenderItems.add(defaultGenderItem);

                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setTitle("성별");
                builder2.setSingleChoiceItems(GenderItems, defaultGenderItem,
                        (dialog, which) -> {
                            SelectedGenderItems.clear();
                            SelectedGenderItems.add(which);
                        });
                builder2.setPositiveButton("확인", (dialog, which) -> {
                            if (!SelectedGenderItems.isEmpty()) {
                                //변경한 성별이 선택된 경우 toast로 알림
                                int index = (int) SelectedGenderItems.get(0);
                                String msg = ListGenderItems.get(index);
                                gender = String.valueOf(index);

                                Toast.makeText(getApplicationContext(),"성별이 변경되었습니다." , Toast.LENGTH_LONG).show();
                                new Connection().execute();
                            }else {
                                Toast.makeText(getApplicationContext(),"성별을 선택하지 않아 값이 변경되지 않았습니다." , Toast.LENGTH_LONG).show();
                            }
                        });
                builder2.setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                builder2.show();
                break;


            //change age
            case 3:
                final List<String> ListAgeItems = new ArrayList<>();
                ListAgeItems.add("19세 이하");
                ListAgeItems.add("20세 ~ 26세");
                ListAgeItems.add("27세 ~ 36세");
                ListAgeItems.add("37세 ~ 50세");
                ListAgeItems.add("51세 이상");
                final CharSequence[] AgeItems =  ListAgeItems.toArray(new String[ ListAgeItems.size()]);
                final List SelectedAgeItems  = new ArrayList();
                int defaultAgeItem = Integer.valueOf(age);
                SelectedAgeItems.add(defaultAgeItem);

                AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
                builder3.setTitle("나이");
                builder3.setSingleChoiceItems(AgeItems, defaultAgeItem,
                        (dialog, which) -> {
                            SelectedAgeItems.clear();
                            SelectedAgeItems.add(which);
                        });
                builder3.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (!SelectedAgeItems.isEmpty()) {
                                    //변경한 나이가 선택된 경우 toast로 알림
                                    int index = (int) SelectedAgeItems.get(0);
                                    String msg = ListAgeItems.get(index);
                                    age = String.valueOf(index);
                                    Toast.makeText(getApplicationContext(),"나이가 변경되었습니다." , Toast.LENGTH_LONG).show();
                                    //서버로 변경 알림
                                    new Connection().execute();
                                }else {
                                    Toast.makeText(getApplicationContext(),"나이를 선택하지 않아 값이 변경되지 않았습니다." , Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                builder3.setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                builder3.show();
                break;

            //change skintype
            case 4:
                final List<String> ListSkinItems = new ArrayList<>();
                ListSkinItems.add("건성");
                ListSkinItems.add("중성");
                ListSkinItems.add("지성");
                ListSkinItems.add("복합성");
                final CharSequence[] SkinItems =  ListSkinItems.toArray(new String[ ListSkinItems.size()]);
                final List SelectedSkinItems  = new ArrayList();
                int defaultSkinItem = Integer.valueOf(skintype);
                SelectedSkinItems.add(defaultSkinItem);

                AlertDialog.Builder builder4 = new AlertDialog.Builder(this);
                builder4.setTitle("피부 타입");
                builder4.setSingleChoiceItems(SkinItems, defaultSkinItem,
                        (dialog, which) -> {
                            SelectedSkinItems.clear();
                            SelectedSkinItems.add(which);
                        });
                builder4.setPositiveButton("확인",
                        (dialog, which) -> {
                            if (!SelectedSkinItems.isEmpty()) {
                                //변경한 나이가 선택된 경우 toast로 알림
                                int index = (int) SelectedSkinItems.get(0);
                                String msg = ListSkinItems.get(index);
                                skintype = String.valueOf(index);
                                Toast.makeText(getApplicationContext(),"피부 타입이 변경되었습니다." , Toast.LENGTH_LONG).show();

                                //서버로 변경 알림
                                new Connection().execute();

                            }else {
                                Toast.makeText(getApplicationContext(),"피부 타입을 선택하지 않아 값이 변경되지 않았습니다." , Toast.LENGTH_LONG).show();
                            }
                        });
                builder4.setNegativeButton("취소",
                        (dialog, which) -> {
                        });
                builder4.show();
                break;


            //change concern
            case 5:final List<String> ListConcernItems = new ArrayList<>();
                ListConcernItems.add("주름");
                ListConcernItems.add("색소 침착");
                ListConcernItems.add("붉은 기");
                ListConcernItems.add("모공");
                ListConcernItems.add("피지");
                ListConcernItems.add("트러블");
                final CharSequence[] ConcernItems =  ListConcernItems.toArray(new String[ ListConcernItems.size()]);
                final List SelectedConcernItems  = new ArrayList();

                AlertDialog.Builder builder5 = new AlertDialog.Builder(this);
                builder5.setTitle("피부 고민");
                builder5.setMultiChoiceItems(ConcernItems, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    //사용자가 체크한 경우 리스트에 추가
                                    SelectedConcernItems.add(which);
                                } else if (SelectedConcernItems.contains(which)) {
                                    //이미 리스트에 들어있던 아이템이면 제거
                                    SelectedConcernItems.remove(Integer.valueOf(which));
                                }
                            }
                        });
                builder5.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                StringBuilder msg=new StringBuilder();
                                for (int i = 0; i < SelectedConcernItems.size(); i++) {
                                    int index = (int) SelectedConcernItems.get(i);
                                    if (index == 0) {msg.append("a");}
                                    else if (index == 1) {msg.append("b");}
                                    else if (index == 2) {msg.append("c");}
                                    else if (index == 3) {msg.append("d");}
                                    else if (index == 4) {msg.append("e");}
                                    else if (index == 5) {msg.append("f");}
                                    else if (index == 6) {msg.append("g");}
                                }
                                concern = msg.toString();
                                Toast.makeText(getApplicationContext(),"피부 고민이 변경되었습니다." , Toast.LENGTH_LONG).show();

                                //서버로 변경 알림
                                new Connection().execute();
                            }
                        });
                builder5.setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder5.show();
                break;



            //change allergy
            case 6:final List<String> ListItems = new ArrayList<>();
                ListItems.add("땅콩");
                ListItems.add("알콜");
                ListItems.add("천연추출물");
                ListItems.add("향");
                ListItems.add("기타");
                ListItems.add("없음");
                final CharSequence[] items =  ListItems.toArray(new String[ ListItems.size()]);
                final List SelectedItems  = new ArrayList();

                AlertDialog.Builder builder6 = new AlertDialog.Builder(this);
                builder6.setTitle("AlertDialog Title");
                builder6.setMultiChoiceItems(items, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    //사용자가 체크한 경우 리스트에 추가
                                    SelectedItems.add(which);
                                } else if (SelectedItems.contains(which)) {
                                    //이미 리스트에 들어있던 아이템이면 제거
                                    SelectedItems.remove(Integer.valueOf(which));
                                }
                            }
                        });
                builder6.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                StringBuilder msg=new StringBuilder();
                                for (int i = 0; i < SelectedItems.size(); i++) {
                                    int index = (int) SelectedItems.get(i);
                                    if (index == 0) {msg.append("A");}
                                    else if (index == 1) {msg.append("B");}
                                    else if (index == 2) {msg.append("C");}
                                    else if (index == 3) {msg.append("D");}
                                    else if (index == 4) {msg.append("E");}
                                    else if (index == 5) {msg.append("F");}
                                    else if (index == 6) {msg.append("G");}
                                }
                                allergy = msg.toString();
                                Toast.makeText(getApplicationContext(),"알러지가 변경되었습니다." , Toast.LENGTH_LONG).show();

                                new Connection().execute();

                            }
                        });
                builder6.setNegativeButton("Cancel",
                        (dialog, which) -> {

                        });
                builder6.show();
                break;
        }

    }



    ///아래 6개의 함수는 저장된 데이터에서 띄워야할 텍스트로 바꾸어주는 함수들(성별, 비밀번호, 나이, 피부타입, 피부고민, 알러지)
    @Nullable
    private String getAgeString(String data) {
        Integer dataNum = Integer.valueOf(data);
        switch (dataNum){
            case 0 : return "19세 이하";
            case 1 : return "20세 ~ 26세";
            case 2 : return "27세 ~ 36세";
            case 3 : return "37세 ~ 50세";
            default: return "51세 이상";
        }
    }

    private String getPwString(Integer count){
        StringBuilder buf = new StringBuilder(count);
        while (count-- > 0) {
            buf.append("*");
        }
        return buf.toString();
    }

    private String getConcernString(String data){
        String result = "";
        while (data.length()>0){
            String curr = data.substring(0,1);
            String currtext = "";

            if (curr.equals("a")) {currtext = "주름";}
            else if (curr.equals("b")) {currtext = "색소침착";}
            else if (curr.equals("c")) {currtext = "붉은 기";}
            else if (curr.equals("d")) {currtext = "모공";}
            else if (curr.equals("e")) {currtext = "피지";}
            else if (curr.equals("f")) {currtext = "트러블";}

            if (data.length()==1) { return result + currtext; }
            else { result = result + currtext + ", "; }

            data = data.substring(1);
        }
        return result;
    }

    private String getAllergyString(String data){
        String result = "";
        while (data.length()>0){
            String curr = data.substring(0,1);
            String currtext = "";

            if (curr.equals("A")) {currtext = "땅콩";}
            else if (curr.equals("B")) {currtext = "알콜";}
            else if (curr.equals("C")) {currtext = "천연추출물";}
            else if (curr.equals("D")) {currtext = "향";}
            else if (curr.equals("E")) {currtext = "기타";}
            else if (curr.equals("F")) {currtext = "없음";}

            if (data.length()==1) { return result + currtext; }
            else { result = result + currtext + ", "; }

            data = data.substring(1);
        }
        return result;
    }

    private String getSkintypeString(String data){
        Integer dataNum = Integer.valueOf(data);
        switch (dataNum){
            case 0 : return "건성";
            case 1 : return "중성";
            case 2 : return "지성";
            default: return "복합성";
        }
    }

    private String getGenderString(String data){
        if (data.equals("0")) {return "여성";}
        else {return "남성";}
    }

    //서버에 변경된 데이터 send
    private class Connection extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() { }


        @Override
        protected Boolean doInBackground(String... params) {
            try{

                //make json
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("gender", Integer.valueOf(gender));
                jsonObject.put("age", Integer.valueOf(age));
                jsonObject.put("skin_type", Integer.valueOf(skintype));
                char[] temp = concern.toCharArray();
                JSONArray tempJ = new JSONArray(temp);
                jsonObject.put("skin_concerns", tempJ);
                temp = allergy.toCharArray();
                tempJ = new JSONArray(temp);
                jsonObject.put("allergies", tempJ);

                URL url = new URL(json_url);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("PATCH");
                con.setRequestProperty("Content-Type", "application/json");
                con.setDoOutput(true);
                con.setDoInput(true);
                con.connect();

                OutputStream outStream = con.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                writer.write(jsonObject.toString());
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
                gender = String.valueOf(Jres.getInt("gender"));
                age = String.valueOf(Jres.getInt("age"));
                skintype = String.valueOf(Jres.getInt("skin_type"));
                concern = "";
                JSONArray concernArray = Jres.getJSONArray("skin_concerns");
                for (int i = 0; i<concernArray.length(); i++) concern += concernArray.getString(i);
                allergy = "";
                JSONArray allergyArray = Jres.getJSONArray("allergies");
                for (int i = 0; i<allergyArray.length(); i++) allergy += allergyArray.getString(i).toUpperCase();

                return Jres.getBoolean("result");

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            //기기에 저장된 정보 변경
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("gender", gender);
            editor.putString("age", age);
            editor.putString("skintype", skintype);
            editor.putString("concern", concern);
            editor.putString("allergy", allergy);
            editor.apply();

            update();
        }
    }

    //서버에 변경된 데이터 send
    private class GETConnection extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() { }


        @Override
        protected Boolean doInBackground(String... params) {
            try{
                Log.i("###", "!!!!!!!!!!!!");
                URL url = new URL(json_url);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Content-Type", "application/json");
                //con.setDoOutput(true);
                con.setDoInput(true);
                con.connect();

                Log.i("###", "!!!!!!!!!!!!");
                InputStream stream = con.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                Log.i("###", "!!!!!!!!!!!!");
                con.disconnect();
                reader.close();
                JSONObject Jres = new JSONObject(buffer.toString());


                Log.i("###", "!!!!!!!!!!!!");
                gender = String.valueOf(Jres.getInt("gender"));
                age = String.valueOf(Jres.getInt("age"));
                skintype = String.valueOf(Jres.getInt("skin_type"));
                Log.i("###", "!!!!!!!!!!!!");
                concern = "";
                JSONArray concernArray = Jres.getJSONArray("skin_concerns");
                for (int i = 0; i<concernArray.length(); i++) concern += concernArray.getString(i);
                Log.i("###", "!!!!!!!!!!!!");
                allergy = "";
                JSONArray allergyArray = Jres.getJSONArray("allergies");
                for (int i = 0; i<allergyArray.length(); i++) allergy += allergyArray.getString(i).toUpperCase();




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
