package com.macernow.ljhotelandroidapp.mainmenu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.macernow.ljhotelandroidapp.R;

/**
 * Created by Administrator on 2015/1/29.
 */
public class VideoMenuListViewItemAdapter extends BaseAdapter {
    private static final String TAG = VideoMenuListViewItemAdapter.class.getSimpleName();
    private String[] category;
    private Context context;
    private LayoutInflater layoutInflater;

    public VideoMenuListViewItemAdapter(Context context,String[] category) {
        layoutInflater = LayoutInflater.from(context);
        this.category = category;
    }

    @Override
    public int getCount() {
        return this.category.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent) {
        View view;
        ViewHolder viewHolder;

        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.videomenu_listview_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView)view.findViewById(R.id.videomenu_item_textView);
            view.setTag(viewHolder);
        }
        else {
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }

        viewHolder.textView.setText(category[position]);

        return view;
    }

    class ViewHolder {
        TextView textView;
    }
}

