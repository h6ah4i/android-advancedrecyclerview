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

package com.h6ah4i.android.widget.advrecyclerview.swipeable;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.utils.BaseWrapperAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import java.util.List;

class SwipeableItemWrapperAdapter<VH extends RecyclerView.ViewHolder> extends BaseWrapperAdapter<VH> {
    private static final String TAG = "ARVSwipeableWrapper";

    private interface Constants extends SwipeableItemConstants {
    }

    private static final int STATE_FLAG_INITIAL_VALUE = -1;

    private static final boolean LOCAL_LOGV = false;
    private static final boolean LOCAL_LOGD = false;

    private BaseSwipeableItemAdapter mSwipeableItemAdapter;
    private RecyclerViewSwipeManager mSwipeManager;
    private long mSwipingItemId = RecyclerView.NO_ID;

    public SwipeableItemWrapperAdapter(RecyclerViewSwipeManager manager, RecyclerView.Adapter<VH> adapter) {
        super(adapter);

        mSwipeableItemAdapter = getSwipeableItemAdapter(adapter);
        if (mSwipeableItemAdapter == null) {
            throw new IllegalArgumentException("adapter does not implement SwipeableItemAdapter");
        }

        if (manager == null) {
            throw new IllegalArgumentException("manager cannot be null");
        }

        mSwipeManager = manager;
    }

    @Override
    protected void onRelease() {
        super.onRelease();

        mSwipeableItemAdapter = null;
        mSwipeManager = null;
        mSwipingItemId = RecyclerView.NO_ID;
    }

    @Override
    public void onViewRecycled(VH holder) {
        super.onViewRecycled(holder);

        if ((mSwipingItemId != RecyclerView.NO_ID) && (mSwipingItemId == holder.getItemId())) {
            mSwipeManager.cancelSwipe();
        }

        // reset SwipeableItemViewHolder state
        if (holder instanceof SwipeableItemViewHolder) {
            if (mSwipeManager != null) {
                mSwipeManager.cancelPendingAnimations(holder);
            }

            SwipeableItemViewHolder swipeableHolder = (SwipeableItemViewHolder) holder;

            swipeableHolder.setSwipeItemHorizontalSlideAmount(0);
            swipeableHolder.setSwipeItemVerticalSlideAmount(0);

            View containerView = swipeableHolder.getSwipeableContainerView();

            if (containerView != null) {
                ViewCompat.animate(containerView).cancel();
                ViewCompat.setTranslationX(containerView, 0.0f);
                ViewCompat.setTranslationY(containerView, 0.0f);
            }
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        final VH holder = super.onCreateViewHolder(parent, viewType);

        if (holder instanceof SwipeableItemViewHolder) {
            ((SwipeableItemViewHolder) holder).setSwipeStateFlags(STATE_FLAG_INITIAL_VALUE);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(VH holder, int position, List<Object> payloads) {
        float prevSwipeItemSlideAmount = 0;

        if (holder instanceof SwipeableItemViewHolder) {
            prevSwipeItemSlideAmount = getSwipeItemSlideAmount(((SwipeableItemViewHolder) holder), swipeHorizontal());
        }

        if (isSwiping()) {
            int flags = Constants.STATE_FLAG_SWIPING;

            if (holder.getItemId() == mSwipingItemId) {
                flags |= Constants.STATE_FLAG_IS_ACTIVE;
            }

            safeUpdateFlags(holder, flags);
            super.onBindViewHolder(holder, position, payloads);
        } else {
            safeUpdateFlags(holder, 0);
            super.onBindViewHolder(holder, position, payloads);
        }

        if (holder instanceof SwipeableItemViewHolder) {
            final float swipeItemSlideAmount = getSwipeItemSlideAmount(((SwipeableItemViewHolder) holder), swipeHorizontal());

            boolean isSwiping = mSwipeManager.isSwiping();
            boolean isAnimationRunning = mSwipeManager.isAnimationRunning(holder);
            if ((prevSwipeItemSlideAmount != swipeItemSlideAmount) || !(isSwiping || isAnimationRunning)) {
                mSwipeManager.applySlideItem(
                        holder, position,
                        prevSwipeItemSlideAmount, swipeItemSlideAmount, swipeHorizontal(),
                        true, isSwiping);
            }
        }
    }

    @Override
    protected void onHandleWrappedAdapterChanged() {
        if (isSwiping()) {
            cancelSwipe();
        } else {
            super.onHandleWrappedAdapterChanged();
        }
    }

    @Override
    protected void onHandleWrappedAdapterItemRangeChanged(int positionStart, int itemCount) {
        if (isSwiping()) {
            cancelSwipe();
        } else {
            super.onHandleWrappedAdapterItemRangeChanged(positionStart, itemCount);
        }
    }

    @Override
    protected void onHandleWrappedAdapterItemRangeInserted(int positionStart, int itemCount) {
        if (isSwiping()) {
            cancelSwipe();
        } else {
            super.onHandleWrappedAdapterItemRangeInserted(positionStart, itemCount);
        }
    }

    @Override
    protected void onHandleWrappedAdapterItemRangeRemoved(int positionStart, int itemCount) {
        if (isSwiping()) {
            cancelSwipe();
        } else {
            super.onHandleWrappedAdapterItemRangeRemoved(positionStart, itemCount);
        }
    }

    @Override
    protected void onHandleWrappedAdapterRangeMoved(int fromPosition, int toPosition, int itemCount) {
        if (isSwiping()) {
            cancelSwipe();
        } else {
            super.onHandleWrappedAdapterRangeMoved(fromPosition, toPosition, itemCount);
        }
    }

    private void cancelSwipe() {
        if (mSwipeManager != null) {
            mSwipeManager.cancelSwipe();
        }
    }

    // NOTE: This method is called from RecyclerViewSwipeManager
    /*package*/
    @SuppressWarnings("unchecked")
    int getSwipeReactionType(RecyclerView.ViewHolder holder, int position, int x, int y) {
        if (LOCAL_LOGV) {
            Log.v(TAG, "getSwipeReactionType(holder = " + holder + ", position = " + position + ", x = " + x + ", y = " + y + ")");
        }

        return mSwipeableItemAdapter.onGetSwipeReactionType(holder, position, x, y);
    }

    // NOTE: This method is called from RecyclerViewSwipeManager
    /*package*/
    @SuppressWarnings("unchecked")
    void onUpdateSlideAmount(RecyclerView.ViewHolder holder, int position, boolean horizontal, float amount, boolean isSwiping, int type) {
        if (LOCAL_LOGV) {
            Log.v(TAG, "onUpdateSlideAmount(holder = " + holder +
                    ", position = " + position +
                    ", horizontal = " + horizontal +
                    ", amount = " + amount +
                    ", isSwiping = " + isSwiping +
                    ", type = " + type + ")");
        }

        mSwipeableItemAdapter.onSetSwipeBackground(holder, position, type);
        ((SwipeableItemViewHolder) holder).onSlideAmountUpdated(
                (horizontal ? amount : 0.0f), (horizontal ? 0.0f : amount), isSwiping);
    }

    // NOTE: This method is called from ItemSlidingAnimator
    /*package*/
    @SuppressWarnings("unchecked")
    void onUpdateSlideAmount(RecyclerView.ViewHolder holder, int position, boolean horizontal, float amount, boolean isSwiping) {
        if (LOCAL_LOGV) {
            Log.v(TAG, "onUpdateSlideAmount(holder = " + holder +
                    ", position = " + position +
                    ", horizontal = " + horizontal +
                    ", amount = " + amount +
                    ", isSwiping = " + isSwiping + ")");
        }

        ((SwipeableItemViewHolder) holder).onSlideAmountUpdated(
                (horizontal ? amount : 0.0f), (horizontal ? 0.0f : amount), isSwiping);
    }

    // NOTE: This method is called from RecyclerViewSwipeManager
    /*package*/ void onSwipeItemStarted(RecyclerViewSwipeManager manager, RecyclerView.ViewHolder holder, long id) {
        if (LOCAL_LOGD) {
            Log.d(TAG, "onSwipeItemStarted(holder = " + holder + ", id = " + id + ")");
        }

        mSwipingItemId = id;

        notifyDataSetChanged();
    }

    // NOTE: This method is called from RecyclerViewSwipeManager
    /*package*/
    @SuppressWarnings("unchecked")
    SwipeResultAction onSwipeItemFinished(RecyclerView.ViewHolder holder, int position, int result) {
        if (LOCAL_LOGD) {
            Log.d(TAG, "onSwipeItemFinished(holder = " + holder + ", position = " + position + ", result = " + result + ")");
        }

        mSwipingItemId = RecyclerView.NO_ID;

        return SwipeableItemInternalUtils.invokeOnSwipeItem(mSwipeableItemAdapter, holder, position, result);
    }

    /*package*/
    @SuppressWarnings("unchecked")
    void onSwipeItemFinished2(RecyclerView.ViewHolder holder, int position, int result, int afterReaction, SwipeResultAction resultAction) {

        ((SwipeableItemViewHolder) holder).setSwipeResult(result);
        ((SwipeableItemViewHolder) holder).setAfterSwipeReaction(afterReaction);

        setSwipeItemSlideAmount(
                ((SwipeableItemViewHolder) holder),
                getSwipeAmountFromAfterReaction(result, afterReaction),
                swipeHorizontal());

        resultAction.performAction();

        notifyDataSetChanged();
    }

    protected boolean isSwiping() {
        return (mSwipingItemId != RecyclerView.NO_ID);
    }

    private boolean swipeHorizontal() {
        return mSwipeManager.swipeHorizontal();
    }

    private static float getSwipeItemSlideAmount(SwipeableItemViewHolder holder, boolean horizontal) {
        if (horizontal) {
            return holder.getSwipeItemHorizontalSlideAmount();
        } else {
            return holder.getSwipeItemVerticalSlideAmount();
        }
    }

    private static void setSwipeItemSlideAmount(SwipeableItemViewHolder holder, float amount, boolean horizontal) {
        if (horizontal) {
            holder.setSwipeItemHorizontalSlideAmount(amount);
        } else {
            holder.setSwipeItemVerticalSlideAmount(amount);
        }
    }

    private static float getSwipeAmountFromAfterReaction(int result, int afterReaction) {
        switch (afterReaction) {
            case RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_DEFAULT:
                return 0.0f;
            case RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_MOVE_TO_SWIPED_DIRECTION:
            case RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_REMOVE_ITEM:
                switch (result) {
                    case RecyclerViewSwipeManager.RESULT_SWIPED_LEFT:
                        return RecyclerViewSwipeManager.OUTSIDE_OF_THE_WINDOW_LEFT;
                    case RecyclerViewSwipeManager.RESULT_SWIPED_RIGHT:
                        return RecyclerViewSwipeManager.OUTSIDE_OF_THE_WINDOW_RIGHT;
                    case RecyclerViewSwipeManager.RESULT_SWIPED_UP:
                        return RecyclerViewSwipeManager.OUTSIDE_OF_THE_WINDOW_TOP;
                    case RecyclerViewSwipeManager.RESULT_SWIPED_DOWN:
                        return RecyclerViewSwipeManager.OUTSIDE_OF_THE_WINDOW_BOTTOM;
                    default:
                        return 0.0f;
                }
            default:
                return 0.0f;
        }
    }

    private static void safeUpdateFlags(RecyclerView.ViewHolder holder, int flags) {
        if (!(holder instanceof SwipeableItemViewHolder)) {
            return;
        }

        final SwipeableItemViewHolder holder2 = (SwipeableItemViewHolder) holder;

        final int curFlags = holder2.getSwipeStateFlags();
        final int mask = ~Constants.STATE_FLAG_IS_UPDATED;

        // append UPDATED flag
        if ((curFlags == STATE_FLAG_INITIAL_VALUE) || (((curFlags ^ flags) & mask) != 0)) {
            flags |= Constants.STATE_FLAG_IS_UPDATED;
        }

        ((SwipeableItemViewHolder) holder).setSwipeStateFlags(flags);
    }

    private static BaseSwipeableItemAdapter getSwipeableItemAdapter(RecyclerView.Adapter adapter) {
        return WrapperAdapterUtils.findWrappedAdapter(adapter, BaseSwipeableItemAdapter.class);
    }
}
