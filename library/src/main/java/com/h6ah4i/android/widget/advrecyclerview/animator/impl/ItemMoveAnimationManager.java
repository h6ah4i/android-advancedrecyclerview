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

package com.h6ah4i.android.widget.advrecyclerview.animator.impl;

import android.util.Log;

import com.h6ah4i.android.widget.advrecyclerview.animator.BaseItemAnimator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public abstract class ItemMoveAnimationManager extends BaseItemAnimationManager<MoveAnimationInfo> {
    public static final String TAG = "ARVItemMoveAnimMgr";

    public ItemMoveAnimationManager(@NonNull BaseItemAnimator itemAnimator) {
        super(itemAnimator);
    }

    @Override
    public long getDuration() {
        return mItemAnimator.getMoveDuration();
    }

    @Override
    public void setDuration(long duration) {
        mItemAnimator.setMoveDuration(duration);
    }

    @Override
    public void dispatchStarting(@NonNull MoveAnimationInfo info, @NonNull RecyclerView.ViewHolder item) {
        if (debugLogEnabled()) {
            Log.d(TAG, "dispatchMoveStarting(" + item + ")");
        }
        mItemAnimator.dispatchMoveStarting(item);
    }

    @Override
    public void dispatchFinished(@NonNull MoveAnimationInfo info, @NonNull RecyclerView.ViewHolder item) {
        if (debugLogEnabled()) {
            Log.d(TAG, "dispatchMoveFinished(" + item + ")");
        }
        mItemAnimator.dispatchMoveFinished(item);
    }

    @Override
    protected boolean endNotStartedAnimation(@NonNull MoveAnimationInfo info, @Nullable RecyclerView.ViewHolder item) {
        if ((info.holder != null) && ((item == null) || (info.holder == item))) {
            onAnimationEndedBeforeStarted(info, info.holder);
            dispatchFinished(info, info.holder);
            info.clear(info.holder);
            return true;
        } else {
            return false;
        }
    }

    public abstract boolean addPendingAnimation(@NonNull RecyclerView.ViewHolder item, int fromX, int fromY, int toX, int toY);
}
