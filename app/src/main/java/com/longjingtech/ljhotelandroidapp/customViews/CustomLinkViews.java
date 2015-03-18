package com.longjingtech.ljhotelandroidapp.customViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.longjingtech.ljhotelandroidapp.R;

/**
 * Created by djstava on 15/1/21.
 */
public class CustomLinkViews extends LinearLayout {

    private ImageView imageView;
    private TextView textView;

    public CustomLinkViews(Context context) {
        super(context);
    }

    public CustomLinkViews(Context context,AttributeSet attributeSet) {
        super(context,attributeSet);

        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.link_custom_view,this);

        imageView = (ImageView)findViewById(R.id.link_customView_imageView);
        textView = (TextView)findViewById(R.id.link_customView_textView);
    }

    public void setImageResource(int resId) {
        imageView.setImageResource(resId);
    }

    public void setTextViewText(String textViewText) {
        textView.setText(textViewText);
    }

    public void setTextViewColor(int color) {
        textView.setTextColor(color);
    }

}
