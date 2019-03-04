package cluemetic.dev.arale.pidaclumetic;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class Greeting2 extends Fragment {
    private RadioGroup ageGroup, genderGroup;
    private RadioButton ageBtn, genderBtn;
    public Integer agenum, gendernum;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
        View view = inflater.inflate( R.layout.signup_fragment_2, container, false );

        ageGroup = view.findViewById(R.id.age);
        genderGroup = view.findViewById(R.id.gender);

        return view;
    }


//    gender = step2.getGender(); //0 is female, 1 is male
//    age = step2.getAge(); // 0 ~19 / 1 20~26 / 2 27~36 / 3 37~50 / 4 51~

    public String getGender(){
        int radioButtonIDG = genderGroup.getCheckedRadioButtonId();
        if (radioButtonIDG==-1){
            Toast.makeText(getContext(), "성별을 선택해주세요", Toast.LENGTH_SHORT).show();
            return null;
        }else {
            genderBtn = genderGroup.findViewById(radioButtonIDG);
            gendernum = genderGroup.indexOfChild(genderBtn);
            return String.valueOf(gendernum);}
    }


    public String getAge(){
        int radioButtonIDA = ageGroup.getCheckedRadioButtonId();
        if(radioButtonIDA==-1){
            Toast.makeText(getContext(), "나이를 선택해주세요", Toast.LENGTH_SHORT).show();
            return null;
        }else{
            ageBtn = ageGroup.findViewById(radioButtonIDA);
            agenum = ageGroup.indexOfChild(ageBtn);
            return String.valueOf(agenum);
        }
    }
}