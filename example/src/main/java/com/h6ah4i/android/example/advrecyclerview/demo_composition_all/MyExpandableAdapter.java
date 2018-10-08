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
package com.h6ah4i.android.example.advrecyclerview.demo_composition_all;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.h6ah4i.android.example.advrecyclerview.R;
import com.h6ah4i.android.example.advrecyclerview.common.adapter.OnListItemClickMessageListener;
import com.h6ah4i.android.example.advrecyclerview.common.widget.ExpandableItemIndicator;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemState;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class MyExpandableAdapter
        extends AbstractExpandableItemAdapter<MyExpandableAdapter.MyGroupViewHolder, MyExpandableAdapter.MyChildViewHolder>
        implements View.OnClickListener {

    static abstract class MyBaseItem {
        public final long id;
        public final String text;

        public MyBaseItem(long id, String text) {
            this.id = id;
            this.text = text;
        }
    }

    static class MyGroupItem extends MyBaseItem {
        public final List<MyChildItem> children;

        public MyGroupItem(long id, String text) {
            super(id, text);
            children = new ArrayList<>();
        }
    }

    static class MyChildItem extends MyBaseItem {
        public MyChildItem(long id, String text) {
            super(id, text);
        }
    }

    static abstract class MyBaseViewHolder extends AbstractExpandableItemViewHolder {
        View containerView;
        TextView textView;

        public MyBaseViewHolder(View itemView) {
            super(itemView);
            containerView = itemView.findViewById(R.id.container);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }

    static class MyGroupViewHolder extends MyBaseViewHolder {
        ExpandableItemIndicator indicator;

        public MyGroupViewHolder(View itemView) {
            super(itemView);
            indicator = itemView.findViewById(R.id.indicator);
        }
    }

    static class MyChildViewHolder extends MyBaseViewHolder {
        public MyChildViewHolder(View itemView) {
            super(itemView);
        }
    }

    RecyclerViewExpandableItemManager mExpandableItemManager;
    OnListItemClickMessageListener mOnItemClickListener;
    List<MyGroupItem> mItems;

    public MyExpandableAdapter(RecyclerViewExpandableItemManager exaMgr, OnListItemClickMessageListener clickListener) {
        setHasStableIds(true); // this is required for expandable feature.

        mExpandableItemManager = exaMgr;
        mOnItemClickListener = clickListener;
        mItems = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            MyGroupItem group = new MyGroupItem(i, "GROUP " + i);
            for (int j = 0; j < 5; j++) {
                group.children.add(new MyChildItem(j, "child " + j));
            }
            mItems.add(group);
        }
    }

    @Override
    public int getGroupCount() {
        return mItems.size();
    }

    @Override
    public int getChildCount(int groupPosition) {
        return mItems.get(groupPosition).children.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        // This method need to return unique value within all group items.
        return mItems.get(groupPosition).id;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // This method need to return unique value within the group.
        return mItems.get(groupPosition).children.get(childPosition).id;
    }

    @Override
    @NonNull
    public MyGroupViewHolder onCreateGroupViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_group_item, parent, false);
        MyGroupViewHolder vh = new MyGroupViewHolder(v);
        vh.containerView.setOnClickListener(this);
        return vh;
    }

    @Override
    @NonNull
    public MyChildViewHolder onCreateChildViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        MyChildViewHolder vh = new MyChildViewHolder(v);
        vh.containerView.setOnClickListener(this);
        return vh;
    }

    @Override
    public void onBindGroupViewHolder(@NonNull MyGroupViewHolder holder, int groupPosition, int viewType) {
        MyGroupItem group = mItems.get(groupPosition);
        holder.textView.setText(group.text);
        final ExpandableItemState expandState = holder.getExpandState();

        if (expandState.isUpdated()) {
            boolean animateIndicator = expandState.hasExpandedStateChanged();

            holder.indicator.setExpandedState(expandState.isExpanded(), animateIndicator);
        }
    }

    @Override
    public void onBindChildViewHolder(@NonNull MyChildViewHolder holder, int groupPosition, int childPosition, int viewType) {
        MyChildItem child = mItems.get(groupPosition).children.get(childPosition);
        holder.textView.setText(child.text);
    }


    @Override
    public boolean onCheckCanExpandOrCollapseGroup(@NonNull MyGroupViewHolder holder, int groupPosition, int x, int y, boolean expand) {
        // handles click event manually (to show Snackbar message)
        return false;
    }

    @Override
    public void onClick(@NonNull View v) {
        RecyclerView rv = RecyclerViewAdapterUtils.getParentRecyclerView(v);
        RecyclerView.ViewHolder vh = rv.findContainingViewHolder(v);

        int rootPosition = vh.getAdapterPosition();
        if (rootPosition == RecyclerView.NO_POSITION) {
            return;
        }

        // need to determine adapter local flat position like this:
        RecyclerView.Adapter rootAdapter = rv.getAdapter();
        int localFlatPosition = WrapperAdapterUtils.unwrapPosition(rootAdapter, this, rootPosition);

        long expandablePosition = mExpandableItemManager.getExpandablePosition(localFlatPosition);
        int groupPosition = RecyclerViewExpandableItemManager.getPackedPositionGroup(expandablePosition);
        int childPosition = RecyclerViewExpandableItemManager.getPackedPositionChild(expandablePosition);

        String message;
        if (childPosition == RecyclerView.NO_POSITION) {
            // Clicked item is a group!

            // toggle expand/collapse
            if (mExpandableItemManager.isGroupExpanded(groupPosition)) {
                mExpandableItemManager.collapseGroup(groupPosition);
                message = "COLLAPSE: Group " + groupPosition;
            } else {
                mExpandableItemManager.expandGroup(groupPosition);
                message = "EXPAND: Group " + groupPosition;
            }
        } else {
            // Clicked item is a child!

            message = "CLICKED: Child " + groupPosition + "-" + childPosition;
        }

        mOnItemClickListener.onItemClicked(message);
    }
}
