/*
 *    Copyright (C) 2016 Haruki Hasegawa
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

package com.h6ah4i.android.example.advrecyclerview.demo_hf_add_remove;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.h6ah4i.android.example.advrecyclerview.R;
import com.h6ah4i.android.example.advrecyclerview.common.adapter.OnListItemClickMessageListener;
import com.h6ah4i.android.widget.advrecyclerview.headerfooter.AbstractHeaderFooterWrapperAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import java.util.ArrayList;
import java.util.List;

class MyHeaderFooterAdapter
        extends AbstractHeaderFooterWrapperAdapter<MyHeaderFooterAdapter.HeaderViewHolder, MyHeaderFooterAdapter.FooterViewHolder>
        implements View.OnClickListener {
    int mHeaderCounter;
    int mFooterCounter;
    List<HeaderFooterItem> mHeaderItems;
    List<HeaderFooterItem> mFooterItems;
    OnListItemClickMessageListener mOnItemClickListener;

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }

    static class FooterViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public FooterViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }

    static class HeaderFooterItem {
        public final int viewType;
        public final String text;
        public final int bgColor;

        public HeaderFooterItem(int viewType, String text, int bgColor) {
            this.viewType = viewType;
            this.text = text;
            this.bgColor = bgColor;
        }
    }

    static final int[] COLORS = new int[]{
            0xFFF44336, // Red - 500
            0xFFE91E63, // Pink - 500
            0xFF9C27B0, // Purple - 500
            0xFF673AB7, // Deep Purple - 500
            0xFF3F51B5, // Indigo - 500
            0xFF2196F3, // Blue - 500
            0xFF03A9F4, // Light Blue - 500
            0xFF00BCD4, // Cyan - 500
            0xFF009688, // Teal - 500
            0xFF4CAF50, // Green - 500
            0xFF8BC34A, // Light Green - 500
            0xFFCDDC39, // Lime - 500
            0xFFFFEB3B, // Yellow - 500
            0xFFFFC107, // Amber - 500
            0xFFFF9800, // Orange - 500
            0xFFFF5722, // Deep Orange - 500
            0xFF795548, // Brown - 500
            0xFF9E9E9E, // Grey - 500
            0xFF607D8B, // Blue Grey - 500
    };

    public MyHeaderFooterAdapter(RecyclerView.Adapter adapter, OnListItemClickMessageListener clickListener) {
        setAdapter(adapter);
        mOnItemClickListener = clickListener;
        mHeaderItems = new ArrayList<>();
        mFooterItems = new ArrayList<>();
    }

    @Override
    public int getHeaderItemCount() {
        return mHeaderItems.size();
    }

    @Override
    public int getFooterItemCount() {
        return mFooterItems.size();
    }

    @Override
    public int getHeaderItemViewType(int localPosition) {
        return mHeaderItems.get(localPosition).viewType;
    }

    @Override
    public int getFooterItemViewType(int localPosition) {
        return mFooterItems.get(localPosition).viewType;
    }

    @Override
    public HeaderViewHolder onCreateHeaderItemViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_item, parent, false);
        HeaderViewHolder vh = new HeaderViewHolder(v);
        vh.itemView.setOnClickListener(this);
        return vh;
    }

    @Override
    public FooterViewHolder onCreateFooterItemViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_item, parent, false);
        FooterViewHolder vh = new FooterViewHolder(v);
        vh.itemView.setOnClickListener(this);
        return vh;
    }

    @Override
    public void onBindHeaderItemViewHolder(HeaderViewHolder holder, int localPosition) {
        HeaderFooterItem item = mHeaderItems.get(localPosition);
        holder.textView.setText(item.text);
        holder.itemView.setBackgroundColor(item.bgColor);
    }

    @Override
    public void onBindFooterItemViewHolder(FooterViewHolder holder, int localPosition) {
        HeaderFooterItem item = mFooterItems.get(localPosition);
        holder.textView.setText(item.text);
        holder.itemView.setBackgroundColor(item.bgColor);
    }

    public void addHeaderItem() {
        int viewType = mHeaderCounter % COLORS.length;
        String text = "Header - " + mHeaderCounter;
        int bgColor = COLORS[viewType];
        mHeaderCounter += 1;
        mHeaderItems.add(new HeaderFooterItem(viewType, text, bgColor));
        getHeaderAdapter().notifyItemInserted(mHeaderItems.size() - 1);
    }

    public void removeHeaderItem() {
        if (mHeaderItems.isEmpty()) {
            return;
        }
        mHeaderItems.remove(mHeaderItems.size() - 1);
        getHeaderAdapter().notifyItemRemoved(mHeaderItems.size());
    }

    public void addFooterItem() {
        int viewType = mFooterCounter % COLORS.length;
        String text = "Footer - " + mFooterCounter;
        int bgColor = COLORS[COLORS.length - 1 - viewType];
        mFooterCounter += 1;
        mFooterItems.add(new HeaderFooterItem(viewType, text, bgColor));
        getFooterAdapter().notifyItemInserted(mFooterItems.size() - 1);
    }

    public void removeFooterItem() {
        if (mFooterItems.isEmpty()) {
            return;
        }
        mFooterItems.remove(mFooterItems.size() - 1);
        getFooterAdapter().notifyItemRemoved(mFooterItems.size());
    }

    @Override
    public void onClick(View v) {
        RecyclerView rv = RecyclerViewAdapterUtils.getParentRecyclerView(v);
        RecyclerView.ViewHolder vh = rv.findContainingViewHolder(v);

        int rootPosition = vh.getAdapterPosition();
        if (rootPosition == RecyclerView.NO_POSITION) {
            return;
        }

        // need to determine adapter local position like this:
        RecyclerView.Adapter rootAdapter = rv.getAdapter();
        int localPosition = WrapperAdapterUtils.unwrapPosition(rootAdapter, this, rootPosition);

        // get segment
        long segmentedPosition = getSegmentedPosition(localPosition);
        int segment = extractSegmentPart(segmentedPosition);
        int offset = extractSegmentOffsetPart(segmentedPosition);

        String message;

        if (segment == SEGMENT_TYPE_HEADER) {
            message = "CLICKED: Header item " + offset;
        } else if (segment == SEGMENT_TYPE_FOOTER) {
            message = "CLICKED: Footer item " + offset;
        } else {
            throw new IllegalStateException("Something wrong.");
        }

        mOnItemClickListener.onItemClicked(message);
    }
}