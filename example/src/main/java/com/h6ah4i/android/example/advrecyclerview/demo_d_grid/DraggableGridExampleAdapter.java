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

package com.h6ah4i.android.example.advrecyclerview.demo_d_grid;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.h6ah4i.android.example.advrecyclerview.R;
import com.h6ah4i.android.example.advrecyclerview.common.data.AbstractDataProvider;
import com.h6ah4i.android.example.advrecyclerview.common.utils.DrawableUtils;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils;

import java.util.List;

class DraggableGridExampleAdapter
        extends RecyclerView.Adapter<DraggableGridExampleAdapter.MyViewHolder>
        implements DraggableItemAdapter<DraggableGridExampleAdapter.MyViewHolder>, View.OnClickListener {
    private static final String TAG = "MyDraggableItemAdapter";
    private int mItemMoveMode = RecyclerViewDragDropManager.ITEM_MOVE_MODE_DEFAULT;

    private final Object PAYLOAD_DRAGGABLE_ITEM_CHANGED = new Object();

    @Override
    public void onClick(View v) {
        RecyclerView.ViewHolder vh = RecyclerViewAdapterUtils.getViewHolder(v);
        int clickedItemPosition = vh.getAdapterPosition();

        if (clickedItemPosition < 0) {
            return;
        }

        int prevDraggableItemPosition = mDraggableItemPosition;

        if (mDraggableItemPosition == clickedItemPosition) {
            mDraggableItemPosition = -1;
        } else {
            mDraggableItemPosition = clickedItemPosition;
        }

        if (prevDraggableItemPosition >= 0) {
            notifyItemChanged(prevDraggableItemPosition, PAYLOAD_DRAGGABLE_ITEM_CHANGED);
        }
        notifyItemChanged(mDraggableItemPosition, PAYLOAD_DRAGGABLE_ITEM_CHANGED);
    }

    // NOTE: Make accessible with short name
    private interface Draggable extends DraggableItemConstants {
    }

    private AbstractDataProvider mProvider;
    private int mDraggableItemPosition = -1;

    public static class MyViewHolder extends AbstractDraggableItemViewHolder {
        public FrameLayout mContainer;
        public View mDragHandle;
        public TextView mTextView;

        public MyViewHolder(View v) {
            super(v);
            mContainer = (FrameLayout) v.findViewById(R.id.container);
            mDragHandle = v.findViewById(R.id.drag_handle);
            mTextView = (TextView) v.findViewById(android.R.id.text1);
        }
    }

    public DraggableGridExampleAdapter(AbstractDataProvider dataProvider) {
        mProvider = dataProvider;

        // DraggableItemAdapter requires stable ID, and also
        // have to implement the getItemId() method appropriately.
        setHasStableIds(true);
    }

    public void setItemMoveMode(int itemMoveMode) {
        mItemMoveMode = itemMoveMode;
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
        final View v = inflater.inflate(R.layout.list_grid_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        vh.mContainer.setOnClickListener(this);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        throw new IllegalStateException();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position, List<Object> payloads) {
        final AbstractDataProvider.Data item = mProvider.getItem(position);

        // set text
        holder.mTextView.setText(item.getText());

        // set background resource (target view ID: container)
        final int dragState = holder.getDragStateFlags();
        boolean draggableChanged = payloads.contains(PAYLOAD_DRAGGABLE_ITEM_CHANGED);

        if (draggableChanged || ((dragState & Draggable.STATE_FLAG_IS_UPDATED) != 0)) {
            int bgResId;

            if ((dragState & Draggable.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_dragging_active_state;

                // need to clear drawable state here to get correct appearance of the dragging item.
                DrawableUtils.clearState(holder.mContainer.getForeground());
            } else if ((dragState & Draggable.STATE_FLAG_DRAGGING) != 0) {
                bgResId = R.drawable.bg_item_dragging_state;
            } else {
                bgResId = R.drawable.bg_item_normal_state;
            }

            holder.mContainer.setBackgroundResource(bgResId);
        }

        if ((((dragState & Draggable.STATE_FLAG_IS_ACTIVE) != 0) || (dragState & Draggable.STATE_FLAG_DRAGGING) == 0)
                && (position == mDraggableItemPosition)) {
            holder.mContainer.setBackgroundResource(R.drawable.bg_item_dragging_active_state);
            ViewCompat.setScaleX(holder.itemView, 1.2f);
            ViewCompat.setScaleY(holder.itemView, 1.2f);
            ViewCompat.setTranslationZ(holder.itemView, 2.0f);
        } else {
            ViewCompat.setScaleX(holder.itemView, 1.0f);
            ViewCompat.setScaleY(holder.itemView, 1.0f);
            ViewCompat.setTranslationZ(holder.itemView, 0.0f);
        }
    }

    @Override
    public void onViewRecycled(MyViewHolder holder) {
        super.onViewRecycled(holder);

        holder.mContainer.setBackgroundResource(R.drawable.bg_item_normal_state);
        ViewCompat.setScaleX(holder.itemView, 1.0f);
        ViewCompat.setScaleY(holder.itemView, 1.0f);
        ViewCompat.setTranslationZ(holder.itemView, 0.0f);
    }

    @Override
    public int getItemCount() {
        return mProvider.getCount();
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        Log.d(TAG, "onMoveItem(fromPosition = " + fromPosition + ", toPosition = " + toPosition + ")");

        if (fromPosition == toPosition) {
            return;
        }

        mDraggableItemPosition = -1;

        if (mItemMoveMode == RecyclerViewDragDropManager.ITEM_MOVE_MODE_DEFAULT) {
            mProvider.moveItem(fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);
        } else {
            mProvider.swapItem(fromPosition, toPosition);
            notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCheckCanStartDrag(MyViewHolder holder, int position, int x, int y) {
        return position == mDraggableItemPosition;
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(MyViewHolder holder, int position) {
//        return new ItemDraggableRange(0, getItemCount() - 2);
        return null;
    }

    @Override
    public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
        return true;
    }
}
