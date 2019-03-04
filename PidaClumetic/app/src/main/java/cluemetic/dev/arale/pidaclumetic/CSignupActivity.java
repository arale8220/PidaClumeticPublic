package cluemetic.dev.arale.pidaclumetic;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.regex.Pattern;

public class CSignupActivity extends AppCompatActivity {

    String email, pw, pwc = "";
    String gender, age = "0";
    String[] skinData;
    Boolean pcright, pright = false;
    String json_url_user = "http://ec2-13-125-246-38.ap-northeast-2.compute.amazonaws.com/users/";
    Greeting1 step1;
    Greeting2 step2;
    Greeting3 step3;
    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c_signup);

        //프래그먼트 설(get, transaction,...)
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        step1 = new Greeting1();
        step2 = new Greeting2();
        step3 = new Greeting3();
        transaction.add(R.id.signupFragment, step1);
        transaction.addToBackStack(null);
        transaction.commit();

        //현재 프래그먼트에 따라 다르게 행동하는 click listener
        Button.OnClickListener onClickListener = view -> {
            switch (fragmentManager.getBackStackEntryCount()) {
                case 1:
                    //먼저 현재 editText에 저장된 정보들 가져오기
                    step1 = (Greeting1) fragmentManager.findFragmentById(R.id.signupFragment);
                    email = step1.getEmail();
                    pw = step1.getPW();
                    pwc = step1.getPWC();

                    //이미 있는 이메일인지, 비밀번호는 영문,숫자로만 6~20자인지, 비밀번호 확인은 비밀번호와 같은지 확인하는 AsynkTask
                    pcright = pw.equals(pwc);
                    pright = (pw.length()>=6) && (pw.length()<=20) && Pattern.matches("^[0-9a-zA-Z]*$", pw);
                    CheckEmail checkEmail = new CheckEmail();
                    checkEmail.execute();
                    break ;

                case 2:
                    step2 = (Greeting2) fragmentManager.findFragmentById(R.id.signupFragment);
                    gender = step2.getGender(); //0 is female, 1 is male
                    age = step2.getAge(); // 0 ~19 / 1 20~26 / 2 27~36 / 3 37~50 / 4 51~

                    if ((gender != null) && (age != null)){
                        transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.signupFragment, step3);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        next.setText("완료");
                    }
                    break ;

                case 3:
                    step3 = (Greeting3) fragmentManager.findFragmentById(R.id.signupFragment);
                    skinData = step3.getData();

                    if (skinData != null){
                        new SignupConnection().execute();
                    }
                    break ;
            }
        };

        //버튼에 click listener 부과
        next = (Button) findViewById(R.id.next3) ;
        next.setOnClickListener(onClickListener) ;
    }


    //이미 존재하는 이메일인지 확인
    public class CheckEmail extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                URL url = new URL(json_url_user);
                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setRequestMethod("POST");
                urlConn.setRequestProperty("Content-Type", "application/json");
                urlConn.setDoInput(true);
                urlConn.setDoOutput(true);

                //to check only username, give only username
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", email);

                OutputStream outStream = urlConn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();

                //다른 설정들을 넣지 않아 BAD REQUEST로 뜰 것임
                if (urlConn.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {

                    InputStream stream = urlConn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuilder buffer = new StringBuilder();
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    reader.close();

                    //만약 이미 존재하는 유저이면 false 반환
                    JSONObject JsonResult = new JSONObject(buffer.toString());
                    JSONArray JsonArray = JsonResult.getJSONArray("username");
                    String responseStr = JsonArray.getString(0);
                    urlConn.disconnect();
                    return !(responseStr.equals("A user with that username already exists."));
                }

                urlConn.disconnect();
            } catch (JSONException e) {
                e.printStackTrace();
                return true; //만약 username에 대한 comments가 없으면 사용 가능한 아이디이므로 true 반환
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (result == null){
                signupDialog(-1);
            }
            else{
                //모두 사용 가능한 경우 다음 step으로 이동
                if (pright && pcright && result){
                    transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.signupFragment, step2);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
                //아닌 경우 무엇이 문제인지 display in fragment
                else {
                    //비밀번호 확인 여부
                    step1.confirmPWCText(pcright);
                    //비밀번호 가능 여부
                    step1.confirmPWText(pright);
                    //이메일 가능 여부
                    step1.confirmEmailText(result);
                }
            }

        }
    }


    private class SignupConnection extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() { }

        @Override
        protected Boolean doInBackground(String... params) {
            try{

                Log.i("###","1");
                JSONObject jsonObject = new JSONObject();
                try {

                    Log.i("###","2");
                    jsonObject.put("username",email);
                    jsonObject.put("password",pw);
                    jsonObject.put("age", Integer.valueOf(age));
                    jsonObject.put("gender", Integer.valueOf(gender));
                    jsonObject.put("skin_type",Integer.valueOf(skinData[0]));


                    JSONArray skinConcerns = new JSONArray();
                    for(int i=0;i<skinData[1].length();i++){
                        skinConcerns.put(Character.toString(skinData[1].charAt(i)));
                    }
                    jsonObject.put("skin_concerns", skinConcerns);

                    JSONArray allergies = new JSONArray();
                    for(int i=0;i<skinData[2].length();i++){
                        skinConcerns.put(Character.toString(skinData[2].charAt(i)));
                    }
                    jsonObject.put("allergies", allergies);
                    jsonObject.put("reviews", new JSONArray());


                    Log.i("###","3");
                } catch (JSONException e) {
                    e.printStackTrace();

                    Log.i("###","-1");
                }

                URL url = new URL(json_url_user);
                Log.i("###","4");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                Log.i("###","5");
                con.setRequestMethod("POST");
                con.setRequestProperty("Cache-Control", "no-cache");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);
                con.setDoInput(true);
                Log.i("###","6");
                con.connect();
                Log.i("###","7");
                OutputStream outStream = con.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();
                Log.i("###","8");
                Log.i("###",jsonObject.toString());

                System.out.println("status code="+con.getResponseCode());
                System.out.println("request method="+con.getRequestMethod());
                System.out.println("error message="+con.getErrorStream());
                System.out.println("Request Message : " + con.getResponseMessage());

                if (con.getResponseCode()==400){
                    Log.i("###","9");
                    return false;
                }

                Log.i("###","10");
                return true;


            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result == true ){
                signupDialog(1);
            }else{
                signupDialog(-1);
            }

        }
    }


    void signupDialog(Integer num)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (num) {
            case 0:
                builder.setTitle("이미 존재하는 아이디 입니다");
                builder.setMessage("이미 존재하는 이메일을 입력하여, 회원가입에 실패하셨습니다. 이전에 만든 아이디로 다시 로그인을 시도해보세요. 확인을 누르면 로그인 페이지로 이동합니다. 새로운 아이디로 회원가입을 시도하려면 취소를 누르세요");
                builder.setNegativeButton("취소", (dialog, which) -> {});
                break;
            case 1:
                builder.setTitle("환영합니다!");
                builder.setMessage("성공적으로 회원가입이 되셨습니다. 확인을 누르면 로그인 페이지로 이동합니다.");
                break;
            default:
                builder.setTitle("회원가입 실패");
                builder.setMessage("인터넷 문제로 인해 회원가입에 실패하였습니다. 다시 시도해주세요");

        }

        builder.setPositiveButton("확인",
                (dialog, which) -> {
                    Intent intent = new Intent(getBaseContext(), BLoginActivity.class);
                    startActivity(intent);
                    finish();
                });
        builder.show();
    }



    private static long back_pressed;
    @Override
    public void onBackPressed()
    {
        if (back_pressed + 2000 > System.currentTimeMillis()) super.onBackPressed();
        else Toast.makeText(getBaseContext(), "앱을 종료하시려면 한번 더 눌러주세요", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }
}
