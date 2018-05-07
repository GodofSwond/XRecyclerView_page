package com.lonch.xrecyclerview_page;


import android.content.Context;


import com.lonch.xrecyclerview_page.recyclerview.CommonAdapter;
import com.lonch.xrecyclerview_page.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * Created by dongxuL on 18/5/7.
 */

public class MyReadCircleAdapter extends CommonAdapter<String> {

    public MyReadCircleAdapter(Context context, List<String> datas) {
        super(context, R.layout.item_recycleview, datas);
    }

    @Override
    public void convert(ViewHolder viewHolder, String string, int position) {

        viewHolder.setText(R.id.tv, string);
        viewHolder.setText(R.id.num, string);
    }
}






