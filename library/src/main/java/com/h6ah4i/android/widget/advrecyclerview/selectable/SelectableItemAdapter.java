package com.h6ah4i.android.widget.advrecyclerview.selectable;

import android.support.v7.widget.RecyclerView;

public interface SelectableItemAdapter<T extends RecyclerView.ViewHolder> {

    public void onItemSelected(T holder, boolean selected);

    public Object getItem(int position);
}
