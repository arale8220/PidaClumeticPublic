package cluemetic.dev.arale.pidaclumetic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

//        ImageView imageView;
//        TextView company, title, price;
//
//        imageView = view.findViewById(R.id.imgURI);
//        new DownloadImageTask(imageView).execute(products.get(position).getImg_url());
//        company = view.findViewById(R.id.company);
//        title = view.findViewById(R.id.title);
//        price = view.findViewById(R.id.price);
//
//
//        title.setText(products.get(position).getTitle());
//        company.setText(products.get(position).getCompany());
//        price.setText(products.get(position).getPrice());
//
//        view.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, HProductActivity.class);
//                intent.putExtra("id", products.get(position).getId());
//                intent.putExtra("title", products.get(position).getTitle());
//                intent.putExtra("brand", products.get(position).getCompany());
//                intent.putExtra("price", products.get(position).getPrice());
//                intent.putExtra("info_url", products.get(position).getInfo_url());
//                intent.putExtra("image", products.get(position).getImg_url());
//                intent.putExtra("info_seller", products.get(position).getInfo_seller());
//                intent.putExtra("info_manufacturer", products.get(position).getInfo_manyfacturer());
//                intent.putExtra("info_country", products.get(position).getInfo_country());
//                intent.putExtra("subCount", products.get(position).getSubCount());
//                context.startActivity(intent);
//
//            }
//        });

        container.addView(view,0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
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
            bmImage.setImageBitmap(result);
        }
    }


}
