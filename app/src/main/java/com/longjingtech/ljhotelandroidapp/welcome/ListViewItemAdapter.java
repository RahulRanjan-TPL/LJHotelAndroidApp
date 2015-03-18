package com.longjingtech.ljhotelandroidapp.welcome;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.ArrayList;
import com.longjingtech.ljhotelandroidapp.R;

/**
 * Created by djstava on 15/1/16.
 */
public class ListViewItemAdapter extends BaseAdapter{

    private static final String TAG = ListViewItemAdapter.class.getSimpleName();
    private ArrayList<ListViewDataModel> arrayList;

    public ListViewItemAdapter() {

        arrayList = new ArrayList<ListViewDataModel>(2);

        arrayList.add(new ListViewDataModel("中文"));
        arrayList.add(new ListViewDataModel("English"));
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int index) {
        return arrayList.get(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(int position,View view,ViewGroup parent) {
        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            view = layoutInflater.inflate(R.layout.welcome_listview_item,parent,false);
        }

        final ListViewDataModel listViewDataModel = arrayList.get(position);

        Button button = (Button)view.findViewById(R.id.welcome_listViewButton);
        button.setText("" + listViewDataModel.getButtonName());

        return view;
    }

}
