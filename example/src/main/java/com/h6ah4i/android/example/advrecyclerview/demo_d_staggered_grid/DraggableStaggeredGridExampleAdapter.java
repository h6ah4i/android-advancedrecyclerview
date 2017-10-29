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

package com.h6ah4i.android.example.advrecyclerview.demo_d_staggered_grid;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

class DraggableStaggeredGridExampleAdapter
        extends RecyclerView.Adapter<DraggableStaggeredGridExampleAdapter.BaseViewHolder>
        implements DraggableItemAdapter<DraggableStaggeredGridExampleAdapter.BaseViewHolder> {
    private static final String TAG = "MyDraggableItemAdapter";
    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_NORMAL_ITEM_OFFSET = 1;

    private static final boolean USE_DUMMY_HEADER = true;
    private static final boolean RANDOMIZE_ITEM_SIZE = true;

    // NOTE: Make accessible with short name
    private interface Draggable extends DraggableItemConstants {
    }

    private AbstractDataProvider mProvider;

    public static class BaseViewHolder extends AbstractDraggableItemViewHolder {
        public BaseViewHolder(View v) {
            super(v);
        }
    }

    public static class HeaderItemViewHolder extends BaseViewHolder {
        public HeaderItemViewHolder(View v) {
            super(v);
        }
    }

    public static class NormalItemViewHolder extends BaseViewHolder {
        public FrameLayout mContainer;
        public View mDragHandle;
        public TextView mTextView;

        public NormalItemViewHolder(View v) {
            super(v);
            mContainer = v.findViewById(R.id.container);
            mDragHandle = v.findViewById(R.id.drag_handle);
            mTextView = v.findViewById(android.R.id.text1);
        }
    }

    public DraggableStaggeredGridExampleAdapter(AbstractDataProvider dataProvider) {
        mProvider = dataProvider;

        // DraggableItemAdapter requires stable ID, and also
        // have to implement the getItemId() method appropriately.
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        if (isHeader(position)) {
            return RecyclerView.NO_ID;
        } else {
            return mProvider.getItem(toNormalItemPosition(position)).getId();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeader(position)) {
            return ITEM_VIEW_TYPE_HEADER;
        } else {
            return ITEM_VIEW_TYPE_NORMAL_ITEM_OFFSET + mProvider.getItem(toNormalItemPosition(position)).getViewType();
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ITEM_VIEW_TYPE_HEADER: {
                // NOTE:
                // This dummy header item is required to workaround the
                // weired animation when occurs on moving the item 0
                //
                // Related issue
                //   Issue 99047:	Inconsistent behavior produced by mAdapter.notifyItemMoved(indexA,indexB);
                //   https://code.google.com/p/android/issues/detail?id=99047&q=notifyItemMoved&colspec=ID%20Status%20Priority%20Owner%20Summary%20Stars%20Reporter%20Opened&
                final View v = inflater.inflate(R.layout.dummy_header_item, parent, false);
                return new HeaderItemViewHolder(v);
            }
            default: {
                final View v = inflater.inflate(R.layout.list_grid_item, parent, false);
                return new NormalItemViewHolder(v);
            }
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (isHeader(position)) {
            onBindHeaderViewHolder((HeaderItemViewHolder) holder, position);
        } else {
            onBindNormalItemViewHolder((NormalItemViewHolder) holder, toNormalItemPosition(position));
        }
    }

    private void onBindHeaderViewHolder(HeaderItemViewHolder holder, int position) {
        StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
        lp.setFullSpan(true);
    }

    private void onBindNormalItemViewHolder(NormalItemViewHolder holder, int position) {

        final AbstractDataProvider.Data item = mProvider.getItem(position);

        // set text
        holder.mTextView.setText(item.getText());

        // set item view height
        Context context = holder.itemView.getContext();
        int itemHeight = calcItemHeight(context, item);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp.height != itemHeight) {
            lp.height = itemHeight;
            holder.itemView.setLayoutParams(lp);
        }

        // set background resource (target view ID: container)
        final int dragState = holder.getDragStateFlags();

        if (((dragState & Draggable.STATE_FLAG_IS_UPDATED) != 0)) {
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
    }

    @Override
    public int getItemCount() {
        int headerCount = getHeaderItemCount();
        int count = mProvider.getCount();
        return headerCount + count;
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        Log.d(TAG, "onMoveItem(fromPosition = " + fromPosition + ", toPosition = " + toPosition + ")");

        fromPosition = toNormalItemPosition(fromPosition);
        toPosition = toNormalItemPosition(toPosition);

        mProvider.moveItem(fromPosition, toPosition);
    }

    @Override
    public boolean onCheckCanStartDrag(BaseViewHolder holder, int position, int x, int y) {
        return !isHeader(position);
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(BaseViewHolder holder, int position) {
        int headerCount = getHeaderItemCount();
        return new ItemDraggableRange(headerCount, getItemCount() - 1);
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

    static int getHeaderItemCount() {
        return (USE_DUMMY_HEADER) ? 1 : 0;
    }

    static boolean isHeader(int position) {
        return (position < getHeaderItemCount());
    }

    static int toNormalItemPosition(int position) {
        return position - getHeaderItemCount();
    }

    static int calcItemHeight(Context context, AbstractDataProvider.Data item) {
        float density = context.getResources().getDisplayMetrics().density;
        if (RANDOMIZE_ITEM_SIZE) {
            int s = (int) item.getId();
            s = swapBit(s, 0, 8);
            s = swapBit(s, 1, 5);
            s = swapBit(s, 3, 2);
            return (int) ((8 + (s % 13)) * 10 * density);
        } else {
            return (int) (100 * density);
        }
    }

    static int swapBit(int x, int pos1, int pos2) {
        int m1 = 1 << pos1;
        int m2 = 1 << pos2;
        int y = x & ~(m1 | m2);

        if ((x & m1) != 0) {
            y |= m2;
        }
        if ((x & m2) != 0) {
            y |= m1;
        }

        return y;
    }
}
