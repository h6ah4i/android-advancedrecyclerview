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

package com.h6ah4i.android.widget.advrecyclerview.animator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

public abstract class BaseItemAnimator extends SimpleItemAnimator {
    private ItemAnimatorListener mListener;

    /**
     * The interface to be implemented by listeners to animation events from this
     * ItemAnimator. This is used internally and is not intended for developers to
     * create directly.
     */
    public interface ItemAnimatorListener {
        void onRemoveFinished(@NonNull RecyclerView.ViewHolder item);

        void onAddFinished(@NonNull RecyclerView.ViewHolder item);

        void onMoveFinished(@NonNull RecyclerView.ViewHolder item);

        void onChangeFinished(@NonNull RecyclerView.ViewHolder item);
    }

    /**
     * Internal only:
     * Sets the listener that must be called when the animator is finished
     * animating the item (or immediately if no animation happens). This is set
     * internally and is not intended to be set by external code.
     *
     * @param listener The listener that must be called.
     */
    public void setListener(@Nullable ItemAnimatorListener listener) {
        mListener = listener;
    }

    @Override
    public final void onAddStarting(RecyclerView.ViewHolder item) {
        onAddStartingImpl(item);
    }

    @Override
    public final void onAddFinished(RecyclerView.ViewHolder item) {
        onAddFinishedImpl(item);

        if (mListener != null) {
            mListener.onAddFinished(item);
        }
    }

    @Override
    public final void onChangeStarting(RecyclerView.ViewHolder item, boolean oldItem) {
        onChangeStartingItem(item, oldItem);
    }

    @Override
    public final void onChangeFinished(RecyclerView.ViewHolder item, boolean oldItem) {
        onChangeFinishedImpl(item, oldItem);

        if (mListener != null) {
            mListener.onChangeFinished(item);
        }
    }

    @Override
    public final void onMoveStarting(RecyclerView.ViewHolder item) {
        onMoveStartingImpl(item);
    }

    @Override
    public final void onMoveFinished(RecyclerView.ViewHolder item) {
        onMoveFinishedImpl(item);

        if (mListener != null) {
            mListener.onMoveFinished(item);
        }
    }

    @Override
    public final void onRemoveStarting(RecyclerView.ViewHolder item) {
        onRemoveStartingImpl(item);
    }

    @Override
    public final void onRemoveFinished(RecyclerView.ViewHolder item) {
        onRemoveFinishedImpl(item);

        if (mListener != null) {
            mListener.onRemoveFinished(item);
        }
    }

    @SuppressWarnings("EmptyMethod")
    protected void onAddStartingImpl(@NonNull RecyclerView.ViewHolder item) {
    }

    @SuppressWarnings("EmptyMethod")
    protected void onAddFinishedImpl(@NonNull RecyclerView.ViewHolder item) {
    }

    @SuppressWarnings("EmptyMethod")
    protected void onChangeStartingItem(@NonNull RecyclerView.ViewHolder item, boolean oldItem) {
    }

    @SuppressWarnings("EmptyMethod")
    protected void onChangeFinishedImpl(@NonNull RecyclerView.ViewHolder item, boolean oldItem) {
    }

    @SuppressWarnings("EmptyMethod")
    protected void onMoveStartingImpl(@NonNull RecyclerView.ViewHolder item) {
    }

    @SuppressWarnings("EmptyMethod")
    protected void onMoveFinishedImpl(@NonNull RecyclerView.ViewHolder item) {
    }

    @SuppressWarnings("EmptyMethod")
    protected void onRemoveStartingImpl(@NonNull RecyclerView.ViewHolder item) {
    }

    @SuppressWarnings("EmptyMethod")
    protected void onRemoveFinishedImpl(@NonNull RecyclerView.ViewHolder item) {
    }

    public boolean dispatchFinishedWhenDone() {
        if (!isRunning()) {
            dispatchAnimationsFinished();
            return true;
        } else {
            return false;
        }
    }

    public boolean debugLogEnabled() {
        return false;
    }
}
