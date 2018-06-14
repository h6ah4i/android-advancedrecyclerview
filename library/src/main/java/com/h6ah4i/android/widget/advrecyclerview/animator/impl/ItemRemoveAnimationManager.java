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

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;

import com.h6ah4i.android.widget.advrecyclerview.animator.BaseItemAnimator;

public abstract class ItemRemoveAnimationManager extends BaseItemAnimationManager<RemoveAnimationInfo> {
    private static final String TAG = "ARVItemRemoveAnimMgr";

    public ItemRemoveAnimationManager(BaseItemAnimator itemAnimator) {
        super(itemAnimator);
    }

    @Override
    public long getDuration() {
        return mItemAnimator.getRemoveDuration();
    }

    @Override
    public void setDuration(long duration) {
        mItemAnimator.setRemoveDuration(duration);
    }

    @Override
    public void dispatchStarting(RemoveAnimationInfo info, RecyclerView.ViewHolder item) {
        if (debugLogEnabled()) {
            Log.d(TAG, "dispatchRemoveStarting(" + item + ")");
        }
        mItemAnimator.dispatchRemoveStarting(item);
    }

    @Override
    public void dispatchFinished(RemoveAnimationInfo info, RecyclerView.ViewHolder item) {
        if (debugLogEnabled()) {
            Log.d(TAG, "dispatchRemoveFinished(" + item + ")");
        }
        mItemAnimator.dispatchRemoveFinished(item);
    }

    @Override
    protected boolean endNotStartedAnimation(RemoveAnimationInfo info, RecyclerView.ViewHolder item) {
        if ((info.holder != null) && ((item == null) || (info.holder == item))) {
            onAnimationEndedBeforeStarted(info, info.holder);
            dispatchFinished(info, info.holder);
            info.clear(info.holder);
            return true;
        } else {
            return false;
        }
    }

    public abstract boolean addPendingAnimation(RecyclerView.ViewHolder holder);
}
