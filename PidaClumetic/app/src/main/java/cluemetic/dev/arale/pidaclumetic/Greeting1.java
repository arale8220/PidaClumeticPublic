package cluemetic.dev.arale.pidaclumetic;

import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Greeting1 extends Fragment {
    EditText emailE, pwE, pwcE;
    TextView etext, ptext, pctext;
    Button ebtn, pbtn, pcbtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
        View view = inflater.inflate( R.layout.signup_fragment_1, container, false );

        emailE = view.findViewById(R.id.email);
        pwE = view.findViewById(R.id.pw);
        pwcE = view.findViewById(R.id.pwconfirm);
        etext = view.findViewById(R.id.emailConfirmText);
        ptext = view.findViewById(R.id.pwConfirmText);
        pctext = view.findViewById(R.id.pwConfirmTextCheck);
        ebtn = view.findViewById(R.id.emailBtn);
        pbtn = view.findViewById(R.id.pwBtn);
        pcbtn = view.findViewById(R.id.pwconfirmBtn);


        return view;
    }

    public  String getEmail(){
        return String.valueOf(emailE.getText());
    }

    public  String getPW(){
        return String.valueOf(pwE.getText());
    }

    public  String getPWC(){
        return String.valueOf(pwcE.getText());
    }

    public  void confirmEmailText(Boolean content){
        if (content) {
            etext.setText("사용가능한 메일입니다");
            ebtn.setBackgroundResource(R.drawable.ic_check_circle);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ebtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            }
            //ebtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }else {
            etext.setText("존재하거나 탈퇴한 이메일입니다");
            ebtn.setBackgroundResource(R.drawable.ic_cancel);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ebtn.setBackgroundTintList( ColorStateList.valueOf( getResources().getColor(R.color.lgt_gray )) );
            }
        }
    }

    public  void confirmPWText(Boolean content){
        if (content) {
            ptext.setText("사용가능한 비밀번호입니다");
            pbtn.setBackgroundResource(R.drawable.ic_check_circle);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                pbtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            }
        }else {
            ptext.setText("(알파벳과 숫자로 구성된 6~20자)");
            pbtn.setBackgroundResource(R.drawable.ic_cancel);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                pbtn.setBackgroundTintList( ColorStateList.valueOf( getResources().getColor(R.color.lgt_gray )) );
            }
        }
    }

    public  void confirmPWCText(Boolean content){
        if (content) {
            pctext.setText("확인되었습니다");
            pcbtn.setBackgroundResource(R.drawable.ic_check_circle);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                pcbtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            }
        }else {
            pctext.setText("비밀번호를 다시 확인해주세요");
            pcbtn.setBackgroundResource(R.drawable.ic_cancel);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                pcbtn.setBackgroundTintList( ColorStateList.valueOf( getResources().getColor(R.color.lgt_gray )) );
            }
        }
    }


}