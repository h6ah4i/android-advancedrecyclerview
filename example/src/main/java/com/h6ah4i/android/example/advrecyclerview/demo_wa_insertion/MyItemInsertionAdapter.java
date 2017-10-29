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

package com.h6ah4i.android.example.advrecyclerview.demo_wa_insertion;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.h6ah4i.android.example.advrecyclerview.R;
import com.h6ah4i.android.example.advrecyclerview.common.adapter.OnListItemClickMessageListener;
import com.h6ah4i.android.widget.advrecyclerview.adapter.AdapterPathSegment;
import com.h6ah4i.android.widget.advrecyclerview.adapter.SimpleWrapperAdapter;
import com.h6ah4i.android.widget.advrecyclerview.adapter.UnwrapPositionResult;
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import java.util.List;

/**
 * Created by hasegawa on 9/11/16.
 */
class MyItemInsertionAdapter extends SimpleWrapperAdapter implements View.OnClickListener {
    private static final int ODD_POS_ITEM_VIEW_TYPE = 100;
    private boolean mInsertionEnabled = true;
    private OnListItemClickMessageListener mOnItemClickListener;

    static class OddPosItemViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public OddPosItemViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }

    public MyItemInsertionAdapter(@NonNull RecyclerView.Adapter adapter, OnListItemClickMessageListener clickListener) {
        super(adapter);
        mOnItemClickListener = clickListener;
    }

    @Override
    public int getItemCount() {
        if (isInsertionEnabled()) {
            return super.getItemCount() * 2;
        } else {
            return super.getItemCount();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isInsertionEnabled()) {
            if ((position % 2) == 0) {
                return super.getItemViewType(position / 2);
            } else {
                return ODD_POS_ITEM_VIEW_TYPE;
            }
        } else {
            return super.getItemViewType(position);
        }
    }

    @Override
    public long getItemId(int position) {
        if (isInsertionEnabled()) {
            if ((position % 2) == 0) {
                return super.getItemId(position / 2);
            } else {
                return RecyclerView.NO_ID;
            }
        } else {
            return super.getItemId(position);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (isInsertionEnabled()) {
            if (viewType != ODD_POS_ITEM_VIEW_TYPE) {
                return super.onCreateViewHolder(parent, viewType);
            } else {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_minimal, parent, false);
                OddPosItemViewHolder vh = new OddPosItemViewHolder(v);
                vh.itemView.setOnClickListener(this);
                return vh;
            }
        } else {
            return super.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
        if (isInsertionEnabled()) {
            if ((position % 2) == 0) {
                super.onBindViewHolder(holder, position / 2, payloads);
            } else {
                OddPosItemViewHolder vh = (OddPosItemViewHolder) holder;
                vh.itemView.setBackgroundColor(Color.RED);
                vh.textView.setText("--- inserted (" + (position / 2) + ") ---");
            }
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        if (isInsertionEnabled()) {
            if (!(holder instanceof OddPosItemViewHolder)) {
                super.onViewAttachedToWindow(holder);
            }
        } else {
            super.onViewAttachedToWindow(holder);
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        if (isInsertionEnabled()) {
            if (!(holder instanceof OddPosItemViewHolder)) {
                super.onViewDetachedFromWindow(holder);
            }
        } else {
            super.onViewDetachedFromWindow(holder);
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        if (isInsertionEnabled()) {
            if (!(holder instanceof OddPosItemViewHolder)) {
                super.onViewRecycled(holder);
            }
        } else {
            super.onViewRecycled(holder);
        }
    }

    @Override
    public void unwrapPosition(@NonNull UnwrapPositionResult dest, int position) {
        if (isInsertionEnabled()) {
            if ((position % 2) == 0) {
                super.unwrapPosition(dest, position / 2);
            }
        } else {
            super.unwrapPosition(dest, position);
        }
    }

    @Override
    public int wrapPosition(@NonNull AdapterPathSegment pathSegment, int position) {
        if (isInsertionEnabled()) {
            if (pathSegment.adapter == getWrappedAdapter()) {
                return super.wrapPosition(pathSegment, position * 2);
            } else {
                return RecyclerView.NO_POSITION;
            }
        } else {
            return super.wrapPosition(pathSegment, position);
        }
    }

    @Override
    protected void onHandleWrappedAdapterChanged() {
        super.onHandleWrappedAdapterChanged();
    }

    @Override
    protected void onHandleWrappedAdapterItemRangeChanged(int positionStart, int itemCount) {
        if (isInsertionEnabled()) {
            super.onHandleWrappedAdapterItemRangeChanged(positionStart * 2, itemCount * 2);
        } else {
            super.onHandleWrappedAdapterItemRangeChanged(positionStart, itemCount);
        }
    }

    @Override
    protected void onHandleWrappedAdapterItemRangeChanged(int positionStart, int itemCount, Object payload) {
        if (isInsertionEnabled()) {
            super.onHandleWrappedAdapterItemRangeChanged(positionStart * 2, itemCount * 2, payload);
        } else {
            super.onHandleWrappedAdapterItemRangeChanged(positionStart, itemCount, payload);
        }
    }

    @Override
    protected void onHandleWrappedAdapterItemRangeInserted(int positionStart, int itemCount) {
        if (isInsertionEnabled()) {
            super.onHandleWrappedAdapterItemRangeInserted(positionStart * 2, itemCount * 2);
        } else {
            super.onHandleWrappedAdapterItemRangeInserted(positionStart, itemCount);
        }
    }

    @Override
    protected void onHandleWrappedAdapterItemRangeRemoved(int positionStart, int itemCount) {
        if (isInsertionEnabled()) {
            super.onHandleWrappedAdapterItemRangeRemoved(positionStart * 2, itemCount * 2);
        } else {
            super.onHandleWrappedAdapterItemRangeRemoved(positionStart, itemCount);
        }
    }

    @Override
    protected void onHandleWrappedAdapterRangeMoved(int fromPosition, int toPosition, int itemCount) {
        if (isInsertionEnabled()) {
            // NOTE: You cannot call onHandleWrappedAdapterRangeMoved() here,
            // because inserted items exist and their positions are also changed together.
            notifyDataSetChanged();
        } else {
            super.onHandleWrappedAdapterRangeMoved(fromPosition, toPosition, itemCount);
        }
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

        String message = "CLICKED: Inserted item " + (localPosition / 2);

        mOnItemClickListener.onItemClicked(message);
    }

    public boolean isInsertionEnabled() {
        return mInsertionEnabled;
    }

    public void setInsertionEnabled(boolean enabled) {
        if (mInsertionEnabled == enabled) {
            return;
        }
        mInsertionEnabled = enabled;
        notifyDataSetChanged();
    }
}