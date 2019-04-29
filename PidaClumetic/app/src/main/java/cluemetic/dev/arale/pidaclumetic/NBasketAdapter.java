package cluemetic.dev.arale.pidaclumetic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

public class NBasketAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<NBasketItem> listViewItemList = new ArrayList<NBasketItem>() ;

    // ListViewAdapter의 생성자
    public NBasketAdapter(ArrayList<NBasketItem> listViewItemList) {
        this.listViewItemList = listViewItemList;
    }


    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "shipping_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.shipping_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.img) ;
        TextView titleTextView = (TextView) convertView.findViewById(R.id.txt1) ;
        TextView descTextView = (TextView) convertView.findViewById(R.id.txt2) ;

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        NBasketItem listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        new DownloadImageTask(iconImageView).execute(listViewItem.getImg());

        titleTextView.setText(listViewItem.getTitle());
        descTextView.setText(listViewItem.getPrice().toString() + "원   "+ listViewItem.getQuantity().toString() + "개");

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String img, String title, Integer price, Integer quan) {
        NBasketItem item = new NBasketItem(img,title,price,quan);
        listViewItemList.add(item);
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            if (urls[0] == null) return null;
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
            if (result!=null) bmImage.setImageBitmap(result);
        }
    }
}