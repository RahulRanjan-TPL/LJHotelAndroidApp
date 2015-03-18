package com.longjingtech.ljhotelandroidapp.vod;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.longjingtech.ljhotelandroidapp.R;

/**
 * Created by djstava on 15/1/9.
 */
public class CustomAlertDialog extends Dialog {

    public CustomAlertDialog (Context context) {
        super(context);
    }

    public CustomAlertDialog (Context context,int theme) {
        super(context,theme);
    }

    public static class Builder {

        private static final String TAG = "CustomAlertDialog";
        private Context context;
        private String title;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;
        private DialogInterface.OnClickListener positiveButtonClickListener,negativeButtonClickListener;


        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        //参数id，指R.string.*
        public Builder setMessage(int id) {
            this.message = (String)context.getText(id);
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        //参数id，指R.string.*
        public Builder setTitle(int id) {
            this.title = (String)context.getText(id);
            return this;
        }

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText,DialogInterface.OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        //参数id，指R.string.*
        public Builder setPositiveButton(int id,DialogInterface.OnClickListener listener) {
            this.positiveButtonText = (String)context.getText(id);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText,DialogInterface.OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int id,DialogInterface.OnClickListener listener) {
            this.negativeButtonText = (String)context.getText(id);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Boolean onKeyDown(int keyCode,KeyEvent keyEvent) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    Log.e(TAG,"======djstava backKey.");
            }

            return true;
        }

        public CustomAlertDialog create() {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final CustomAlertDialog dialog = new CustomAlertDialog(context, R.style.CustomAlertDialog);
            View layout = inflater.inflate(R.layout.vod_custom_alertdialog,null);
            dialog.addContentView(layout,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ((TextView)layout.findViewById(R.id.title)).setText(title);
            if (positiveButtonText != null) {
                ((Button)layout.findViewById(R.id.positiveButton)).setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    ((Button)layout.findViewById(R.id.positiveButton)).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            positiveButtonClickListener.onClick(dialog,DialogInterface.BUTTON_POSITIVE);
                        }
                    });
                }
            }
            else {
                layout.findViewById(R.id.positiveButton).setVisibility(View.GONE);
            }

            if (negativeButtonText != null) {
                ((Button)layout.findViewById(R.id.negativeButton)).setText(negativeButtonText);
                if (negativeButtonClickListener != null) {
                    ((Button)layout.findViewById(R.id.negativeButton)).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            negativeButtonClickListener.onClick(dialog,DialogInterface.BUTTON_NEGATIVE);
                        }
                    });
                }
            }
            else {
                layout.findViewById(R.id.negativeButton).setVisibility(View.GONE);
            }

            if (message != null) {
                ((TextView)layout.findViewById(R.id.message)).setText(message);
            }
            else if (contentView != null) {
                ((LinearLayout)layout.findViewById(R.id.content)).removeAllViews();
                ((LinearLayout)layout.findViewById(R.id.content)).addView(contentView,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
            }

            dialog.setOnKeyListener(new OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            return true;

                        default:
                            return false;
                    }
                }
            });

            dialog.setContentView(layout);
            return dialog;
        }
    }
}
