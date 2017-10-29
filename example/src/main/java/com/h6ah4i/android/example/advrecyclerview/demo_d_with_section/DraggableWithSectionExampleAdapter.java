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

package com.h6ah4i.android.example.advrecyclerview.demo_d_with_section;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.h6ah4i.android.example.advrecyclerview.R;
import com.h6ah4i.android.example.advrecyclerview.common.data.AbstractDataProvider;
import com.h6ah4i.android.example.advrecyclerview.common.data.ExampleSectionDataProvider;
import com.h6ah4i.android.example.advrecyclerview.common.utils.DrawableUtils;
import com.h6ah4i.android.example.advrecyclerview.common.utils.ViewUtils;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

class DraggableWithSectionExampleAdapter
        extends RecyclerView.Adapter<DraggableWithSectionExampleAdapter.MyViewHolder>
        implements DraggableItemAdapter<DraggableWithSectionExampleAdapter.MyViewHolder> {
    private static final String TAG = "MyDragSectionAdapter";

    // NOTE: Make accessible with short name
    private interface Draggable extends DraggableItemConstants {
    }

    private static final int ITEM_VIEW_TYPE_SECTION_HEADER = ExampleSectionDataProvider.ITEM_VIEW_TYPE_SECTION_HEADER;
    private static final int ITEM_VIEW_TYPE_SECTION_ITEM = ExampleSectionDataProvider.ITEM_VIEW_TYPE_SECTION_ITEM;

    private AbstractDataProvider mProvider;

    public static class MyViewHolder extends AbstractDraggableItemViewHolder {
        public FrameLayout mContainer;
        public View mDragHandle;
        public TextView mTextView;

        public MyViewHolder(View v) {
            super(v);
            mContainer = v.findViewById(R.id.container);
            mDragHandle = v.findViewById(R.id.drag_handle);
            mTextView = v.findViewById(android.R.id.text1);
        }
    }

    public DraggableWithSectionExampleAdapter(AbstractDataProvider dataProvider) {
        mProvider = dataProvider;

        // DraggableItemAdapter requires stable ID, and also
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

        final View v;
        switch (viewType) {
            case ITEM_VIEW_TYPE_SECTION_HEADER:
                v = inflater.inflate(R.layout.list_section_header, parent, false);
                break;
            case ITEM_VIEW_TYPE_SECTION_ITEM:
                v = inflater.inflate(R.layout.list_item_draggable, parent, false);
                break;
            default:
                throw new IllegalStateException("Unexpected viewType (= " + viewType + ")");
        }

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case ITEM_VIEW_TYPE_SECTION_HEADER:
                onBindSectionHeaderViewHolder(holder, position);
                break;
            case ITEM_VIEW_TYPE_SECTION_ITEM:
                onBindSectionItemViewHolder(holder, position);
                break;
        }
    }

    private void onBindSectionHeaderViewHolder(MyViewHolder holder, int position) {
        final AbstractDataProvider.Data item = mProvider.getItem(position);

        // set text
        holder.mTextView.setText(item.getText());
    }

    private void onBindSectionItemViewHolder(MyViewHolder holder, int position) {
        final AbstractDataProvider.Data item = mProvider.getItem(position);

        // set text
        holder.mTextView.setText(item.getText());

        // set background resource (target view ID: container)
        final int dragState = holder.getDragStateFlags();

        if (((dragState & Draggable.STATE_FLAG_IS_UPDATED) != 0)) {
            int bgResId;

            if ((dragState & Draggable.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_dragging_active_state;

                // need to clear drawable state here to get correct appearance of the dragging item.
                DrawableUtils.clearState(holder.mContainer.getForeground());
            } else if (
                    ((dragState & Draggable.STATE_FLAG_DRAGGING) != 0) &&
                    ((dragState & Draggable.STATE_FLAG_IS_IN_RANGE) != 0)) {
                bgResId = R.drawable.bg_item_dragging_state;
            } else {
                bgResId = R.drawable.bg_item_normal_state;
            }

            holder.mContainer.setBackgroundResource(bgResId);
        }
    }

    @Override
    public int getItemCount() {
        return mProvider.getCount();
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        Log.d(TAG, "onMoveItem(fromPosition = " + fromPosition + ", toPosition = " + toPosition + ")");

        mProvider.moveItem(fromPosition, toPosition);
    }

    @Override
    public boolean onCheckCanStartDrag(MyViewHolder holder, int position, int x, int y) {
        // x, y --- relative from the itemView's top-left

        // return false if the item is a section header
        if (holder.getItemViewType() != ITEM_VIEW_TYPE_SECTION_ITEM) {
            return false;
        }

        final View containerView = holder.mContainer;
        final View dragHandleView = holder.mDragHandle;

        final int offsetX = containerView.getLeft() + (int) (containerView.getTranslationX() + 0.5f);
        final int offsetY = containerView.getTop() + (int) (containerView.getTranslationY() + 0.5f);

        return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(MyViewHolder holder, int position) {
        final int start = findFirstSectionItem(position);
        final int end = findLastSectionItem(position);

        return new ItemDraggableRange(start, end);
    }

    @Override
    public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
        return true;
    }

    @Override
    public void onItemDragStarted(int position) {
        notifyDataSetChanged();
    }

    @Override
    public void onItemDragFinished(int fromPosition, int toPosition, boolean result) {
        notifyDataSetChanged();
    }

    private int findFirstSectionItem(int position) {
        AbstractDataProvider.Data item = mProvider.getItem(position);

        if (item.isSectionHeader()) {
            throw new IllegalStateException("section item is expected");
        }

        while (position > 0) {
            AbstractDataProvider.Data prevItem = mProvider.getItem(position - 1);

            if (prevItem.isSectionHeader()) {
                break;
            }

            position -= 1;
        }

        return position;
    }

    private int findLastSectionItem(int position) {
        AbstractDataProvider.Data item = mProvider.getItem(position);

        if (item.isSectionHeader()) {
            throw new IllegalStateException("section item is expected");
        }

        final int lastIndex = getItemCount() - 1;

        while (position < lastIndex) {
            AbstractDataProvider.Data nextItem = mProvider.getItem(position + 1);

            if (nextItem.isSectionHeader()) {
                break;
            }

            position += 1;
        }

        return position;
    }
}
