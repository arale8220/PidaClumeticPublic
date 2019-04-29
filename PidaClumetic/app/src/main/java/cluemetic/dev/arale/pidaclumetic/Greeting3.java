package cluemetic.dev.arale.pidaclumetic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Greeting3 extends Fragment {

    private RadioGroup type1, type2;
    private RadioButton type;
    private CheckBox a,b,c,d,e,f,A,B,C,D,E,F,agreementCB;
    private TextView aggrement;
    private Integer t;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
        View view = inflater.inflate( R.layout.signup_fragment_3, container, false );

        //피부타입
        type1 = view.findViewById(R.id.typeG1);
        type2 = view.findViewById(R.id.typeG2);
        type1.clearCheck();
        type1.setOnCheckedChangeListener(listener1);
        type2.clearCheck();
        type2.setOnCheckedChangeListener(listener2);

        //피부고민
        a = view.findViewById( R.id.worry );
        b = view.findViewById( R.id.worry2 );
        c = view.findViewById( R.id.worry3 );
        d = view.findViewById( R.id.worry4 );
        e = view.findViewById( R.id.worry5 );
        f = view.findViewById( R.id.worry6 );

        //알러지
        A = view.findViewById( R.id.allergy );
        B = view.findViewById( R.id.allergy2 );
        C = view.findViewById( R.id.allergy3 );
        D = view.findViewById( R.id.allergy4 );
        E = view.findViewById( R.id.allergy5 );
        F = view.findViewById( R.id.allergy6 );

        aggrement = view.findViewById( R.id.agreementtext2);
        agreementCB = view.findViewById(R.id.agreement);
        aggrement.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ISetupNoticeActivity.class);
            intent.putExtra("notice0faq1", 2);
            startActivity(intent);
        });

        //알러지에서 없음을 선택하는 경우 다른 모든 것들의 체크를 없앰
        F.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                A.setChecked(false);
                B.setChecked(false);
                C.setChecked(false);
                D.setChecked(false);
                E.setChecked(false);
            }
        });
        A.setOnCheckedChangeListener((buttonView, isChecked) -> { if (isChecked) F.setChecked(false); });
        B.setOnCheckedChangeListener((buttonView, isChecked) -> { if (isChecked) F.setChecked(false); });
        C.setOnCheckedChangeListener((buttonView, isChecked) -> { if (isChecked) F.setChecked(false); });
        D.setOnCheckedChangeListener((buttonView, isChecked) -> { if (isChecked) F.setChecked(false); });
        E.setOnCheckedChangeListener((buttonView, isChecked) -> { if (isChecked) F.setChecked(false); });

        return view;
    }

    //listener1과 2는 피부타입에서 두줄을 사용하기 때문에, 두줄에서 각각 하나씩을 중복해서 선택하지 못하기 위함임
    private RadioGroup.OnCheckedChangeListener listener1 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
                type2.setOnCheckedChangeListener(null);
                type2.clearCheck();
                type2.setOnCheckedChangeListener(listener2);
            }
        }
    };
    private RadioGroup.OnCheckedChangeListener listener2 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
                type1.setOnCheckedChangeListener(null);
                type1.clearCheck();
                type1.setOnCheckedChangeListener(listener1);
            }
        }
    };




    public String[] getData(){

        String curr;
        List preRes = new ArrayList();


        //skin type 0~3
        //피부타입 - 아무런 타입도 선택하지 않았을 때
        if ((type1.getCheckedRadioButtonId() == -1) && (type2.getCheckedRadioButtonId() == -1)){
            Toast.makeText(getContext(), "피부 타입을 선택해주세요", Toast.LENGTH_SHORT).show();
            return null;
        }

        //피부타입 - 올바르게 선택했을 때
        if (type2.getCheckedRadioButtonId() == -1){
            type = type1.findViewById(type1.getCheckedRadioButtonId());
            t = type1.indexOfChild(type);
        }else{
            type = type2.findViewById(type2.getCheckedRadioButtonId());
            t = type2.indexOfChild(type)+2;
        }
        preRes.add(String.valueOf(t));


        //skin concern
        curr = "";
        if ( a.isChecked() ){ curr = curr + "a"; }//주름
        if ( b.isChecked() ){ curr = curr + "b"; }//색소침착
        if ( c.isChecked() ){ curr = curr + "c"; }//붉은기
        if ( d.isChecked() ){ curr = curr + "d"; }//모공
        if ( e.isChecked() ){ curr = curr + "e"; }//피지
        if ( f.isChecked() ){ curr = curr + "f"; }//트러블
        if (curr.equals("")){
            Toast.makeText(getContext(), "피부고민을 하나 이상 선택해주세요", Toast.LENGTH_SHORT).show();
            return null;
        }
        preRes.add(curr);


        //allergy
        curr = "";
        if ( F.isChecked() ){ curr = "F"; }//없음
        else{
            if ( A.isChecked() ){ curr = curr + "A"; }//땅콩
            if ( B.isChecked() ){ curr = curr + "B"; }//알콜
            if ( C.isChecked() ){ curr = curr + "C"; }//천연추출물
            if ( D.isChecked() ){ curr = curr + "D"; }//향
            if ( E.isChecked() ){ curr = curr + "E"; }//기타
        }
        if (curr.equals("")){
            Toast.makeText(getContext(), "알러지를 선택해주세요", Toast.LENGTH_SHORT).show();
            return null;
        }
        preRes.add(curr);

        if (agreementCB.isChecked()){
            String[] res = new String[preRes.size()];
            preRes.toArray(res);
            return res;
        }else{
            Toast.makeText(getContext(), "약관을 읽고 동의해주세요", Toast.LENGTH_SHORT).show();
            return null;
        }


    }
}







