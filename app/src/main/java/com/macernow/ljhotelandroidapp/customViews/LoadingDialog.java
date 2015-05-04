package com.macernow.ljhotelandroidapp.customViews;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.macernow.ljhotelandroidapp.R;

/**
 * Created by djstava on 15/2/28.
 */
public class LoadingDialog extends Dialog {
    private static final String TAG = LoadingDialog.class.getSimpleName();
    private TextView textView;

    public LoadingDialog(Context context) {
        super(context, R.style.loadingDialogStyle);
    }

    private LoadingDialog(Context context,int theme) {
        super(context,theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

    }
}
