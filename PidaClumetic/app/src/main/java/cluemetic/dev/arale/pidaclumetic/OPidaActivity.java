package cluemetic.dev.arale.pidaclumetic;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OPidaActivity extends AppCompatActivity {


    List<OPidaProduct> products;
    ViewPager viewPager;
    OPidaProductAdapter adapter;
    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_mypida);

        //제품들 보여주기
        new GetPidaData().execute();

        //네비게이션뷰 설정(클릭시 이동)
        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(menuItem -> {
            navigationView.postDelayed(() -> {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.navigation_category) {
                    startActivity(new Intent(getBaseContext(), ECategoryActivity.class));
                } else if (itemId == R.id.navigation_basket) {
                    startActivity(new Intent(this, NBasketActivity.class));
                } else if (itemId == R.id.navigation_group) {
                    startActivity(new Intent(getBaseContext(), JGroupActivity.class));
                } else if (itemId == R.id.navigation_mypida) {
                    return;
                } else if (itemId == R.id.navigation_information) {
                    startActivity(new Intent(getBaseContext(), ISetupActivity.class));
                }
                finish();
            }, 0);
            return true;
        });

        //현재는 카테고리이므로 카테고리에 체크 표시
        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);


        //이미지 검색 버튼을 누르면 리스트액티비티로 이동.
        ImageButton mlist = findViewById(R.id.btnSearch);
        mlist.setOnClickListener(v -> startNewActivity("검색할 제품명을 입력해주세요", false ));


        //카메라 버튼을 누르면 사진 촬영 후 제일 위의 스트링을 반환.
        //그 스트링을 이용하여 startNewActivity로 전달.
        ImageButton mcamera = findViewById(R.id.btnCamera);
        mcamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCamera();
            }
        });

    }

    //각 url마다 정보를 받아 PidaProduct 생성
    private class GetPidaData extends AsyncTask<String[], Void, Boolean> {
        @Override
        protected void onPreExecute() { }

        @Override
        protected Boolean doInBackground(String[]... params) {
            try {

                //get user data for send Http parameters
                SharedPreferences user = getSharedPreferences("user", MODE_PRIVATE);
                String username = user.getString("username", "");
                String token = user.getString("access_token", "");

                Log.i("###", "get PidaData at PidaActivity");
                //make information to string
                String urlStr = "http://ec2-13-125-246-38.ap-northeast-2.compute.amazonaws.com/users/"
                        + username
                        + "/?access_token="
                        + token;

                URL url = new URL(urlStr);
                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setRequestMethod("GET");
                urlConn.setDoInput(true);
                urlConn.setRequestProperty("Accept", "application/json");


                Log.i("###", "get PidaData at PidaActivity---codeNum : " + String.valueOf(urlConn.getResponseCode()));
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

                    Log.i("###", "get PidaData at PidaActivity--getting result done");

                    Log.i("###", "get PidaData at PidaActivity--make three kinds of order JSONArray");
                    JSONArray tester_orders = JsonResult.getJSONArray("tester_orders");
                    JSONArray purchase_orders = JsonResult.getJSONArray("purchase_orders");
                    JSONArray group_purchase_orders = JsonResult.getJSONArray("group_purchase_orders");

                    //모든 주문들 중에서 필요한 정보만 합한 JSONObject 추합
                    JSONArray all_orders = new JSONArray();
                    for (int i=0; i<tester_orders.length(); i++){
                        JSONObject temp = tester_orders.getJSONObject(i);
                        JSONObject currOrder = new JSONObject();
                        currOrder.put("type", 0);
                        currOrder.put("status", temp.getInt("status"));
                        currOrder.put("order_time", temp.getString("order_time"));
                        currOrder.put("imgUrl", "");
                        currOrder.put("url", temp.getString("url"));
                        currOrder.put("num", "");
                        all_orders.put(currOrder);
                    }
                    Log.i("###", "get PidaData at PidaActivity--make tester order JSONArray");

                    for (int i=0; i<purchase_orders.length(); i++){
                        Log.i("###", i + "and length is " + purchase_orders.length());
                        JSONObject temp = purchase_orders.getJSONObject(i);
                        JSONObject currOrder = new JSONObject();
                        currOrder.put("type", 1);
                        currOrder.put("order_time", temp.getString("order_time"));
                        Log.i("###", "get PidaData at PidaActivity--make purchase order JSONArray111");
                        currOrder.put("imgUrl", temp.getJSONArray("items").getJSONObject(0).getString("product"));
                        Log.i("###", "get PidaData at PidaActivity--make purchase order JSONArray222");
                        currOrder.put("status", temp.getInt("status"));
                        Log.i("###", "get PidaData at PidaActivity--make purchase order JSONArray333");
                        currOrder.put("url", temp.getString("url"));
                        if (temp.getJSONArray("items").length()==1) currOrder.put("num", "");
                        else currOrder.put("num", "+" + String.valueOf(temp.getJSONArray("items").length()-1));
                        all_orders.put(currOrder);
                    }
                    Log.i("###", "get PidaData at PidaActivity--make purchase order JSONArray");

                    for (int i=0; i<group_purchase_orders.length(); i++){
                        JSONObject temp = group_purchase_orders.getJSONObject(i);
                        JSONObject currOrder = new JSONObject();
                        currOrder.put("type", 2);
                        currOrder.put("order_time", temp.getString("order_time"));
                        currOrder.put("imgUrl", temp.getJSONObject("event").getJSONObject("product").getString("image"));
                        currOrder.put("status", temp.getInt("status"));
                        currOrder.put("url", temp.getString("url"));
                        currOrder.put("num", "");
                        all_orders.put(currOrder);
                    }
                    Log.i("###", "get PidaData at PidaActivity--make group order JSONArray");


                    //use collection.sort, sort the JSONArray
                    Log.i("###", "get PidaData at PidaActivity--make sorted order JSONArray");
                    ArrayList<JSONObject> all_order_list = new ArrayList<JSONObject>();
                    for (int i = 0; i < all_orders.length(); i++) {
                        all_order_list.add(all_orders.getJSONObject(i));
                    }
                    all_order_list.sort((lhs, rhs) -> {
                        try {
                            return (rhs.getString("order_time").compareTo(lhs.getString("order_time")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    });

                    //make OPIDAPRODUCT
                    products = new ArrayList<>();
                    for (int i=0; i<all_order_list.size(); i++){
                        OPidaProduct curr = new OPidaProduct(
                                all_order_list.get(i).getInt("type"),
                                all_order_list.get(i).getInt("status"),
                                all_order_list.get(i).getString("url"),
                                all_order_list.get(i).getString("imgUrl"),
                                all_order_list.get(i).getString("order_time"),
                                all_order_list.get(i).getString("num")
                                );
                        products.add(curr);

                    }


                    return null;
                }

                urlConn.disconnect();
                return null;



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
        protected void onPostExecute(Boolean result) {
            getimgurl();
        }
    }

    public class DownloadImageUrl extends AsyncTask<Integer, Void, Integer>{

        @Override
        protected Integer doInBackground(Integer... nums) {
            URL url;
            try {
                url = new URL(products.get(nums[0]).imgUri);
                Log.i("###", "Download image url from" + url);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.connect();
                InputStream in = urlConnection.getInputStream();


                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder buffer = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                reader.close();
                JSONObject JsonResult = new JSONObject(buffer.toString());
                in.close();
                products.get(nums[0]).imgUri = JsonResult.getString("image");
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer i) {
            showList();
        }


    }

    void getimgurl(){
        for (int i=0; i<products.size(); i++){
            if (products.get(i).type == 1){
                new DownloadImageUrl().execute(i);
            }
        }
    }

    //products를 받아 화면에 리스트 띄워주기.
    //현재 사진을 로드하는 데에 시간이 조금 걸림.
    void showList(){
        if (products.size() > 0) findViewById(R.id.textView9).setVisibility(View.GONE);
        adapter = new OPidaProductAdapter(products, this);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
    }


    ////////////////////////////////////////////////////////////////////for ocr and search

    private static final String CLOUD_VISION_API_KEY = "AIzaSyDgQF-22puFdfvoRjuEgZH0DeFNYjv-oqk";
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final int MAX_LABEL_RESULTS = 10;
    private static final int MAX_DIMENSION = 1200;
    private static final String TAG = OPidaActivity.class.getSimpleName();
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;

    //리스트 액티비티를 실행. 인텐트를 통해 리스트 액티비티에 string을 전달. 이후 이 string으로 검색한 결과를 리스트로 보여줄 예정
    void startNewActivity(String intentStr, Boolean send){
        Intent find = new Intent(this, LListActivity.class);
        find.putExtra("identifier", intentStr);
        find.putExtra("ocr", send);
        startActivity(find);
    }


    public void startCamera() {
        if (PermissionUtils.requestPermission(
                this,
                CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    public File getCameraFile() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            uploadImage(data.getData());
        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            uploadImage(photoUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
                    startCamera();
                }
                break;
        }
    }

    public void uploadImage(Uri uri) {
        Toast.makeText(this, "사진을 분석 중입니다. 잠시만 기다려주세요", Toast.LENGTH_LONG).show();
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                MAX_DIMENSION);

                callCloudVision(bitmap);

            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }

    private Vision.Images.Annotate prepareAnnotationRequest(Bitmap bitmap) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        VisionRequestInitializer requestInitializer =
                new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                    /**
                     * We override this so we can inject important identifying fields into the HTTP
                     * headers. This enables use of a restricted cloud platform API key.
                     */
                    @Override
                    protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                            throws IOException {
                        super.initializeVisionRequest(visionRequest);

                        String packageName = getPackageName();
                        visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                        String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                        visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                    }
                };

        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(requestInitializer);

        Vision vision = builder.build();

        BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                new BatchAnnotateImagesRequest();
        batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

            // Add the image
            com.google.api.services.vision.v1.model.Image base64EncodedImage = new com.google.api.services.vision.v1.model.Image();
            // Convert the bitmap to a JPEG
            // Just in case it's a format that Android understands but Cloud Vision
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Base64 encode the JPEG
            base64EncodedImage.encodeContent(imageBytes);
            annotateImageRequest.setImage(base64EncodedImage);

            // add the features we want
            annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                Feature textDetection = new Feature();
                textDetection.setType("TEXT_DETECTION");
                textDetection.setMaxResults(10);
                add(textDetection);

            }});

            ImageContext imageContext = new ImageContext();
            List<String> languages = new ArrayList<>();
            languages.add("ko");
            languages.add("en");
            imageContext.setLanguageHints(languages);
            annotateImageRequest.setImageContext(imageContext);

            // Add the list of one thing to the request
            add(annotateImageRequest);
        }});

        Vision.Images.Annotate annotateRequest =
                vision.images().annotate(batchAnnotateImagesRequest);
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.setDisableGZipContent(true);
        Log.d(TAG, "created Cloud Vision request object, sending request");

        return annotateRequest;
    }

    private class LableDetectionTask extends AsyncTask<Object, Void, String> {
        private final WeakReference<OPidaActivity> mActivityWeakReference;
        private Vision.Images.Annotate mRequest;

        LableDetectionTask(OPidaActivity activity, Vision.Images.Annotate annotate) {
            mActivityWeakReference = new WeakReference<>(activity);
            mRequest = annotate;
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                Log.d(TAG, "created Cloud Vision request object, sending request");
                BatchAnnotateImagesResponse response = mRequest.execute();
                return convertResponseToString(response);

            } catch (GoogleJsonResponseException e) {
                Log.d("@@", "failed to make API request because " + e.getContent());
            } catch (IOException e) {
                Log.d("@@", "failed to make API request because of other IOException " +
                        e.getMessage());
            }
            return "제품 검색에 실패하였습니다. 검색어를 입력해주세요";
        }

        protected void onPostExecute(String result) {
            OPidaActivity activity = mActivityWeakReference.get();
            if (activity != null && !activity.isFinishing()) {
                if(result.equals("사진에서 문자가 발견되지 않았습니다. 다시 시도해주세요")) {
                    Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(activity, "발견되었습니다 : " + result, Toast.LENGTH_SHORT).show();
                    startNewActivity(result, true);
                }
            }
        }
    }

    private void callCloudVision(final Bitmap bitmap) throws IOException {

        // Do the real work in an async task, because we need to use the network anyway
        try {
            AsyncTask<Object, Void, String> labelDetectionTask = new LableDetectionTask(this, prepareAnnotationRequest(bitmap));
            labelDetectionTask.execute();
        } catch (IOException e) {
            Log.d(TAG, "failed to make API request because of other IOException " +
                    e.getMessage());
        }
    }

    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private static String convertResponseToString(BatchAnnotateImagesResponse response) {
        StringBuilder message = new StringBuilder();


        List<EntityAnnotation> labels = response.getResponses().get(0).getTextAnnotations();
        if (labels != null) {
            message.append(labels.get(0).getDescription());
            return message.toString().split("\\s+")[0];
        } else {
            message.append("제품 검색에 실패하였습니다. 다시 시도해주세요");
            return message.toString();
        }
    }
}
