package cluemetic.dev.arale.pidaclumetic;

import android.content.Context;
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
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ISetupNoticeAdapter extends BaseAdapter {
    private ArrayList<ISetupNoticeItem> listViewItemList = new ArrayList<ISetupNoticeItem>() ;

    public ISetupNoticeAdapter(ArrayList<ISetupNoticeItem> listViewItemList) {
        this.listViewItemList = listViewItemList;
    }

    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.notice_item, parent, false);
        }

        ISetupNoticeItem listViewItem = listViewItemList.get(position);
        TextView titleTextView = (TextView) convertView.findViewById(R.id.title) ;
        TextView descTextView = (TextView) convertView.findViewById(R.id.content) ;
        titleTextView.setText(listViewItem.getTitleStr());
        descTextView.setText(listViewItem.getContentStr());
        if (listViewItem.getClicked()) descTextView.setVisibility(View.VISIBLE);
        else descTextView.setVisibility(View.GONE);

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position ;
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    public void addItem(String title, String desc) {
        ISetupNoticeItem item = new ISetupNoticeItem(title, desc, false);
        listViewItemList.add(item);
    }

    public void updateResults(ArrayList<ISetupNoticeItem> results) {
        listViewItemList = results;
        notifyDataSetChanged();
    }

}
