/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.h6ah4i.android.example.advrecyclerview.demo_selection;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.h6ah4i.android.example.advrecyclerview.R;
import com.h6ah4i.android.example.advrecyclerview.common.data.AbstractDataProvider;
import com.h6ah4i.android.widget.advrecyclerview.selectable.ElevatingSelectableViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.selectable.SelectableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.selectable.SwappingSelectableViewHolder;

public class MySelectableItemAdapter
        extends RecyclerView.Adapter<MySelectableItemAdapter.MyViewHolder>
        implements SelectableItemAdapter<MySelectableItemAdapter.MyViewHolder> {
    private static final String TAG = "MySelectableItemAdapter";

    private AbstractDataProvider mProvider;

    private  EventListener eventListener;

    public interface EventListener {
        void onItemSelected(int position, boolean value);
    }

    public static class MyViewHolder extends ElevatingSelectableViewHolder {
        public ViewGroup mContainer;
        public TextView mTextView;

        public MyViewHolder(View v) {
            super(v);
            mContainer = (ViewGroup) v.findViewById(R.id.container);
            mTextView = (TextView) v.findViewById(android.R.id.text1);
        }
    }

    public MySelectableItemAdapter(AbstractDataProvider dataProvider) {
        mProvider = dataProvider;

        // SelectableItemAdapter requires stable ID, and also
        // have to implement the getItemId() method appropriately.
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return mProvider.getItem(position).getId();
    }

    @Override
    public int getItemViewType(int position) {
        return mProvider.getItem(position).getViewType();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate((viewType == 0) ? R.layout.list_item : R.layout.list_item2, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final AbstractDataProvider.Data item = mProvider.getItem(position);

        // set text
        holder.mTextView.setText(item.getText());
    }

    public EventListener getEventListener() {
        return eventListener;
    }

    public void setEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    @Override
    public int getItemCount() {
        return mProvider.getCount();
    }

    @Override
    public void onItemSelected(MyViewHolder holder, boolean value) {
        if (eventListener!=null)
            eventListener.onItemSelected(holder.getPosition(),value);

    }
}
