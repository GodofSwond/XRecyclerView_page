package com.lonch.xrecyclerview_page.recyclerview.base;


/**
 * Created by dongxuL on 18/5/7.
 */
public interface ItemViewDelegate<T> {

    int getItemViewLayoutId();

    boolean isForViewType(T item, int position);

    void convert(ViewHolder holder, T t, int position);

}
