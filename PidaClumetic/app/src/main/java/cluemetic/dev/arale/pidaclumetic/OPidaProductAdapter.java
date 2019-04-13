package cluemetic.dev.arale.pidaclumetic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.InputStream;
import java.util.List;

public class OPidaProductAdapter extends PagerAdapter {
    List<OPidaProduct> products;
    private LayoutInflater layoutInflater;
    private Context context;

    public OPidaProductAdapter(List<OPidaProduct> products, Context context) {
        this.products = products;
        this.context = context;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view.equals(o);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.mypida_item, container, false);

        ImageView imageView;
        ImageButton imgB;
        TextView date, num, status1, status2, status3;
        ProgressBar progressBar;
        Integer type = products.get(position).getType();

        imageView = view.findViewById(R.id.imgURI);
        imgB = view.findViewById(R.id.imageButton);
        new DownloadImageTask(imageView, imgB, type).execute(products.get(position).getImgUri());
        date = view.findViewById(R.id.date);
        num = view.findViewById(R.id.number);
        status1 = view.findViewById(R.id.status1);
        status2 = view.findViewById(R.id.status2);
        status3 = view.findViewById(R.id.status3);
        progressBar = view.findViewById(R.id.progress);


        String Month = products.get(position).getTime().substring(5,7);
        String Date = products.get(position).getTime().substring(8,10);
        date.setText(Month + "월 " + Date + "일");
        num.setText(products.get(position).getNumber());

        Integer stt = products.get(position).getState();
        if (stt == 0){
            progressBar.setProgress(5);
            status1.setTextColor(Color.argb(255,255,82,89));
        }else if (stt == 1){
            progressBar.setProgress(50);
            status2.setTextColor(Color.argb(255,255,82,89));
        }else {
            progressBar.setProgress(100);
            status3.setTextColor(Color.argb(255,255,82,89));
        }

        View.OnClickListener onClickListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RPurchaseDescriptionActivity.class);
                intent.putExtra("url", products.get(position).getOrderUri());
                intent.putExtra("img", products.get(position).getImgUri());
                intent.putExtra("type", products.get(position).getType());
                intent.putExtra("status", products.get(position).getState());

                String Year = products.get(position).getTime().substring(0,4);
                String Month = products.get(position).getTime().substring(5,7);
                String Date = products.get(position).getTime().substring(8,10);

                intent.putExtra("time", Year + "년 " + Month + "월 " + Date + "일");
                context.startActivity(intent);

            }
        };


        view.setOnClickListener(onClickListener);
        imageView.setOnClickListener(onClickListener);
        imgB.setOnClickListener(onClickListener);
        date.setOnClickListener(onClickListener);
        num.setOnClickListener(onClickListener);


        container.addView(view,0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
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
            else bmImage.setImageBitmap(result);
        }
    }


}
