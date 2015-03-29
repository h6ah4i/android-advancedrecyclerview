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

package com.h6ah4i.android.widget.advrecyclerview.utils;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.selectable.SelectableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.selectable.SwappingSelectableViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemViewHolder;

public abstract class AbstractDraggableSwipeableSelectableItemViewHolder extends RecyclerView.ViewHolder implements DraggableItemViewHolder, SwipeableItemViewHolder, SelectableItemViewHolder {
    private int mDragStateFlags;
    private int mSwipeStateFlags;
    private int mSwipeResult = RecyclerViewSwipeManager.RESULT_NONE;
    private int mAfterSwipeReaction = RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_DEFAULT;
    private float mSwipeAmount;
    private boolean mSelectable;

    public AbstractDraggableSwipeableSelectableItemViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setDragStateFlags(int flags) {
        mDragStateFlags = flags;
    }

    @Override
    public int getDragStateFlags() {
        return mDragStateFlags;
    }

    @Override
    public void setSwipeStateFlags(int flags) {
        mSwipeStateFlags = flags;
    }

    @Override
    public int getSwipeStateFlags() {
        return mSwipeStateFlags;
    }

    @Override
    public void setSwipeResult(int result) {
        mSwipeResult = result;
    }

    @Override
    public int getSwipeResult() {
        return mSwipeResult;
    }

    @Override
    public int getAfterSwipeReaction() {
        return mAfterSwipeReaction;
    }

    @Override
    public void setAfterSwipeReaction(int reaction) {
        mAfterSwipeReaction = reaction;
    }

    @Override
    public float getSwipeItemSlideAmount() {
        return mSwipeAmount;
    }

    @Override
    public void setSwipeItemSlideAmount(float amount) {
        mSwipeAmount = amount;
    }

    @Override
    public boolean isSelectable() { return mSelectable; }

    @Override
    public void setSelectable(boolean mSelectable) { this.mSelectable = mSelectable; }

    @Override
    public boolean isActivated() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return itemView.isActivated();
        } else {
            return itemView.isSelected();
        }
    }

    @Override
    public void setActivated(boolean activated) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            itemView.setActivated(activated);
        } else {
            itemView.setSelected(activated);
        }
    }
}
