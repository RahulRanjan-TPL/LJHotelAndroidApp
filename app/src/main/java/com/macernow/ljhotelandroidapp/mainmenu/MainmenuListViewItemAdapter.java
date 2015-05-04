package com.macernow.ljhotelandroidapp.mainmenu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.macernow.ljhotelandroidapp.R;

/**
 * Created by djstava on 15/1/16.
 */
public class MainmenuListViewItemAdapter extends BaseAdapter {
    private static final String TAG = MainmenuListViewItemAdapter.class.getSimpleName();
    private int[] mIcons;
    private int[] mTexts;
    private Context context;
    private LayoutInflater layoutInflater;

    public MainmenuListViewItemAdapter(Context context,int [] mIcons,int [] mTexts) {
        layoutInflater = LayoutInflater.from(context);
        this.mIcons = mIcons;
        this.mTexts = mTexts;
    }

    @Override
    public int getCount() {
        return this.mIcons.length;
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
            view = layoutInflater.inflate(R.layout.mainmenu_listview_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView)view.findViewById(R.id.mainmenu_item_imageView);
            viewHolder.textView = (TextView)view.findViewById(R.id.mainmenu_item_textView);
            view.setTag(viewHolder);
        }
        else {
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }

        viewHolder.imageView.setImageResource(mIcons[position]);
        viewHolder.textView.setText(mTexts[position]);

        return view;
    }

    class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}
