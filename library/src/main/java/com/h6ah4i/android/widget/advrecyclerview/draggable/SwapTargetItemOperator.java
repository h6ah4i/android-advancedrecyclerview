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

package com.h6ah4i.android.widget.advrecyclerview.draggable;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Interpolator;

import com.h6ah4i.android.widget.advrecyclerview.utils.CustomRecyclerViewUtils;

class SwapTargetItemOperator extends BaseDraggableItemDecorator {
    @SuppressWarnings("unused")
    private static final String TAG = "SwapTargetItemOperator";

    private RecyclerView.ViewHolder mSwapTargetItem;
    private Interpolator mSwapTargetTranslationInterpolator;
    private int mTranslationX;
    private int mTranslationY;
    private final Rect mSwapTargetDecorationOffsets = new Rect();
    private final Rect mSwapTargetItemMargins = new Rect();
    private final Rect mDraggingItemDecorationOffsets = new Rect();
    private boolean mStarted;
    private float mReqTranslationPhase;
    private float mCurTranslationPhase;
    private DraggingItemInfo mDraggingItemInfo;
    private boolean mSwapTargetItemChanged;

    private static final ViewPropertyAnimatorListener RESET_TRANSLATION_LISTENER = new ViewPropertyAnimatorListener() {
        @Override
        public void onAnimationStart(View view) {
        }

        @Override
        public void onAnimationEnd(View view) {
            ViewPropertyAnimatorCompat animator = ViewCompat.animate(view);
            animator.setListener(null);

            ViewCompat.setTranslationX(view, 0);
            ViewCompat.setTranslationY(view, 0);
        }

        @Override
        public void onAnimationCancel(View view) {
        }
    };

    public SwapTargetItemOperator(RecyclerView recyclerView, RecyclerView.ViewHolder draggingItem, DraggingItemInfo draggingItemInfo) {
        super(recyclerView, draggingItem);

        mDraggingItemInfo = draggingItemInfo;

        CustomRecyclerViewUtils.getDecorationOffsets(
                mRecyclerView.getLayoutManager(), mDraggingItemViewHolder.itemView, mDraggingItemDecorationOffsets);
    }

    private static float calculateCurrentTranslationPhase(float cur, float req) {
        final float A = 0.3f;
        final float B = 0.01f;
        final float tmp = (cur * (1.0f - A)) + (req * A);

        return (Math.abs(tmp - req) < B) ? req : tmp;
    }

    public void setSwapTargetTranslationInterpolator(Interpolator interpolator) {
        mSwapTargetTranslationInterpolator = interpolator;
    }

    public void setSwapTargetItem(RecyclerView.ViewHolder swapTargetItem) {
        if (mSwapTargetItem == swapTargetItem) {
            return;
        }

        // reset Y-translation if the swap target has been changed
        if (mSwapTargetItem != null) {
            ViewPropertyAnimatorCompat animator = ViewCompat.animate(mSwapTargetItem.itemView);
            animator.cancel();
            animator.setDuration(10)
                    .translationX(0)
                    .translationY(0)
                    .setListener(RESET_TRANSLATION_LISTENER)
                    .start();
        }

        mSwapTargetItem = swapTargetItem;

        if (mSwapTargetItem != null) {
            ViewPropertyAnimatorCompat animator = ViewCompat.animate(mSwapTargetItem.itemView);
            animator.cancel();
        }

        mSwapTargetItemChanged = true;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final RecyclerView.ViewHolder draggingItem = mDraggingItemViewHolder;
        final RecyclerView.ViewHolder swapTargetItem = mSwapTargetItem;

        if (draggingItem == null || swapTargetItem == null || draggingItem.getItemId() != mDraggingItemInfo.id) {
            return;
        }

        mReqTranslationPhase = calculateTranslationPhase(draggingItem, swapTargetItem);

        if (mSwapTargetItemChanged) {
            mSwapTargetItemChanged = false;
            mCurTranslationPhase = mReqTranslationPhase;
        } else {
            // interpolate to make it moves smoothly
            mCurTranslationPhase = calculateCurrentTranslationPhase(mCurTranslationPhase, mReqTranslationPhase);
        }

        updateSwapTargetTranslation(draggingItem, swapTargetItem, mCurTranslationPhase);
    }

    private float calculateTranslationPhase(RecyclerView.ViewHolder draggingItem, RecyclerView.ViewHolder swapTargetItem) {
        final View swapItemView = swapTargetItem.itemView;

        final int pos1 = draggingItem.getLayoutPosition();
        final int pos2 = swapTargetItem.getLayoutPosition();

        CustomRecyclerViewUtils.getDecorationOffsets(
                mRecyclerView.getLayoutManager(), swapItemView, mSwapTargetDecorationOffsets);
        CustomRecyclerViewUtils.getLayoutMargins(swapItemView, mSwapTargetItemMargins);

        final Rect m2 = mSwapTargetItemMargins;
        final Rect d2 = mSwapTargetDecorationOffsets;
        final int h2 = swapItemView.getHeight() + m2.top + m2.bottom + d2.top + d2.bottom;
        final int w2 = swapItemView.getWidth() + m2.left + m2.right + d2.left + d2.right;

        final float offsetXPx = draggingItem.itemView.getLeft() - mTranslationX; // == -(ViewCompat.getTranslationY(draggingItem.itemView)
        final float phaseX = (w2 != 0) ? (offsetXPx / w2) : 0.0f;
        final float offsetYPx = draggingItem.itemView.getTop() - mTranslationY; // == -(ViewCompat.getTranslationY(draggingItem.itemView)
        final float phaseY = (h2 != 0) ? (offsetYPx / h2) : 0.0f;

        float translationPhase = 0.0f;

        final int orientation = CustomRecyclerViewUtils.getOrientation(mRecyclerView);

        if (orientation == CustomRecyclerViewUtils.ORIENTATION_VERTICAL) {
            if (pos1 > pos2) {
                // dragging item moving to upward
                translationPhase = phaseY;
            } else {
                // dragging item moving to downward
                translationPhase = 1.0f + phaseY;
            }
        } else if (orientation == CustomRecyclerViewUtils.ORIENTATION_HORIZONTAL) {
            if (pos1 > pos2) {
                // dragging item moving to left
                translationPhase = phaseX;
            } else {
                // dragging item moving to right
                translationPhase = 1.0f + phaseX;
            }
        }


        return Math.min(Math.max(translationPhase, 0.0f), 1.0f);
    }

    private void updateSwapTargetTranslation(RecyclerView.ViewHolder draggingItem, RecyclerView.ViewHolder swapTargetItem, float translationPhase) {
        final View swapItemView = swapTargetItem.itemView;

        final int pos1 = draggingItem.getLayoutPosition();
        final int pos2 = swapTargetItem.getLayoutPosition();

        final Rect m1 = mDraggingItemInfo.margins;
        final Rect d1 = mDraggingItemDecorationOffsets;
        final int h1 = mDraggingItemInfo.height + m1.top + m1.bottom + d1.top + d1.bottom;
        final int w1 = mDraggingItemInfo.width + m1.left + m1.right + d1.left + d1.right;

        if (mSwapTargetTranslationInterpolator != null) {
            translationPhase = mSwapTargetTranslationInterpolator.getInterpolation(translationPhase);
        }

        switch (CustomRecyclerViewUtils.getOrientation(mRecyclerView)) {
            case CustomRecyclerViewUtils.ORIENTATION_VERTICAL:
                if (pos1 > pos2) {
                    // dragging item moving to upward
                    ViewCompat.setTranslationY(swapItemView, translationPhase * h1);
                } else {
                    // dragging item moving to downward
                    ViewCompat.setTranslationY(swapItemView, (translationPhase - 1.0f) * h1);
                }
                break;
            case CustomRecyclerViewUtils.ORIENTATION_HORIZONTAL:
                if (pos1 > pos2) {
                    // dragging item moving to left
                    ViewCompat.setTranslationX(swapItemView, translationPhase * w1);
                } else {
                    // dragging item moving to right
                    ViewCompat.setTranslationX(swapItemView, (translationPhase - 1.0f) * w1);
                }
                break;
        }
    }

    public void start() {
        if (mStarted) {
            return;
        }

        mRecyclerView.addItemDecoration(this, 0);

        mStarted = true;
    }

    public void finish(boolean animate) {
        if (mStarted) {
            mRecyclerView.removeItemDecoration(this);
        }

        final RecyclerView.ItemAnimator itemAnimator = mRecyclerView.getItemAnimator();
        if (itemAnimator != null) {
            itemAnimator.endAnimations();
        }
        mRecyclerView.stopScroll();

        if (mSwapTargetItem != null) {
            // return to default position
            updateSwapTargetTranslation(mDraggingItemViewHolder, mSwapTargetItem, mCurTranslationPhase);
            moveToDefaultPosition(mSwapTargetItem.itemView, 1.0f, 1.0f, 0.0f, 1.0f, animate);
            mSwapTargetItem = null;
        }

        mDraggingItemViewHolder = null;
        mTranslationX = 0;
        mTranslationY = 0;
        mCurTranslationPhase = 0.0f;
        mReqTranslationPhase = 0.0f;
        mStarted = false;
        mDraggingItemInfo = null;
    }

    public void update(int translationX, int translationY) {
        mTranslationX = translationX;
        mTranslationY = translationY;
    }

    public void onItemViewRecycled(RecyclerView.ViewHolder holder) {
        if (holder == mSwapTargetItem) {
            setSwapTargetItem(null);
        }
    }
}