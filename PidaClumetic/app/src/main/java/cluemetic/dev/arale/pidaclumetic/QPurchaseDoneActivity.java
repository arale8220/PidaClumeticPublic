package cluemetic.dev.arale.pidaclumetic;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.ImageContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class QPurchaseDoneActivity extends AppCompatActivity {

    Button mgoCategory, mgoMypida;
    ImageButton mImgBtn;
    ImageView mImgUrl;
    Intent intent;
    Integer type;
    String imgurl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_purchase_finish);

        intent = getIntent();
        imgurl  = intent.getStringExtra("url");
        type = intent.getIntExtra("type", 0);

        mImgBtn = findViewById(R.id.imgBtn);
        mImgUrl = findViewById(R.id.imgURI);

        new DownloadImageTask(mImgUrl, mImgBtn, type).execute(imgurl);

        mgoCategory = findViewById(R.id.goCategory);
        mgoMypida = findViewById(R.id.goMypida);

        mgoCategory.setOnClickListener(v -> {
            Intent i = new Intent(getBaseContext(), ECategoryActivity.class);
            startActivity(i);
            finish();
        });

        mgoMypida.setOnClickListener(v -> {
            Intent i = new Intent(getBaseContext(), OPidaActivity.class);
            startActivity(i);
            finish();
        });

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        ImageButton ImgB;
        Integer status;

        public DownloadImageTask(ImageView bmImage, ImageButton imgB, Integer status) {
            this.bmImage = bmImage;
            this.ImgB = imgB;
            this.status = status;
        }

        protected Bitmap doInBackground(String... urls) {
            if (status==0) return null;
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.i("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if (status==0) {
                ImgB.setImageResource(R.drawable.selection_3_selected);
                ImgB.setBackgroundResource(R.drawable.btn_mypida_not_transparent);
            }
            else {
                bmImage.setImageBitmap(result);
                bmImage.setBackgroundResource(R.drawable.btn_white);
            }
        }
    }

}
