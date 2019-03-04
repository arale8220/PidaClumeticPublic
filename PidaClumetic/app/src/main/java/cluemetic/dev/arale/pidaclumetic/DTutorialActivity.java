package cluemetic.dev.arale.pidaclumetic;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class DTutorialActivity extends AppCompatActivity {
    private Integer count=0;
    Button btn;
    ImageView img;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_d_tutorial);

        img=findViewById(R.id.imageView);
        Log.i("###", String.valueOf(count));
        img.setImageResource(R.drawable.tutorial1);

        btn=findViewById(R.id.next);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (count) {
                    case 0:
                        Log.i("###", String.valueOf(count));
                        img.setImageResource(R.drawable.tutorial2);
                        count = 1;
                        break;
                    case 1:
                        Log.i("###", String.valueOf(count));
                        img.setImageResource(R.drawable.tutorial3);
                        count = 2;
                        break;
                    case 2:
                        Log.i("###", String.valueOf(count));
                        img.setImageResource(R.drawable.tutorial4);
                        count = 3;
                        break;
                    case 3:
                        Log.i("###", String.valueOf(count));
                        img.setImageResource(R.drawable.tutorial5);
                        count = 4;
                        break;
                    case 4:
                        Log.i("###", String.valueOf(count));
                        Intent notlogged = new Intent(DTutorialActivity.this.getBaseContext(), BLoginActivity.class);
                        DTutorialActivity.this.startActivity(notlogged);
                        finish();
                        count=5;
                        break;
                }
            }
        });
    }
}
