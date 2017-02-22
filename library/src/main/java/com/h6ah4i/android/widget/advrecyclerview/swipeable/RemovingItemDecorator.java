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

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Interpolator;

import com.h6ah4i.android.widget.advrecyclerview.utils.CustomRecyclerViewUtils;

import java.lang.ref.WeakReference;

class RemovingItemDecorator extends RecyclerView.ItemDecoration {
    @SuppressWarnings("unused")
    private static final String TAG = "RemovingItemDecorator";

    private static final int NOTIFY_REMOVAL_EFFECT_PHASE_1 = 0;
    private static final int NOTIFY_REMOVAL_EFFECT_END = 1;

    private static final long ADDITIONAL_REMOVE_DURATION = 50;  // workaround: to avoid the gap between the below item

    private RecyclerView mRecyclerView;
    private RecyclerView.ViewHolder mSwipingItem;
    private final long mSwipingItemId;
    private final Rect mSwipingItemBounds = new Rect();
    private int mTranslationX;
    private int mTranslationY;
    private long mStartTime;
    private final long mRemoveAnimationDuration;
    private final long mMoveAnimationDuration;
    private Interpolator mMoveAnimationInterpolator;
    private Drawable mSwipeBackgroundDrawable;

    private final boolean mHorizontal;
    private int mPendingNotificationMask = 0;

    public RemovingItemDecorator(RecyclerView rv, RecyclerView.ViewHolder swipingItem, int result, long removeAnimationDuration, long moveAnimationDuration) {
        mRecyclerView = rv;
        mSwipingItem = swipingItem;
        mSwipingItemId = swipingItem.getItemId();
        mHorizontal = (result == RecyclerViewSwipeManager.RESULT_SWIPED_LEFT || result == RecyclerViewSwipeManager.RESULT_SWIPED_RIGHT);

        mRemoveAnimationDuration = removeAnimationDuration + ADDITIONAL_REMOVE_DURATION;
        mMoveAnimationDuration = moveAnimationDuration;
        mTranslationX = (int) (ViewCompat.getTranslationX(swipingItem.itemView) + 0.5f);
        mTranslationY = (int) (ViewCompat.getTranslationY(swipingItem.itemView) + 0.5f);

        CustomRecyclerViewUtils.getViewBounds(mSwipingItem.itemView, mSwipingItemBounds);
    }

    public void setMoveAnimationInterpolator(Interpolator interpolator) {
        mMoveAnimationInterpolator = interpolator;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final long elapsedTime = getElapsedTime(mStartTime);

        final float scale = determineBackgroundScaleSwipeCompletedSuccessfully(elapsedTime);

        fillSwipingItemBackground(c, mSwipeBackgroundDrawable, scale);

        if (mSwipingItemId == mSwipingItem.getItemId()) {
            mTranslationX = (int) (ViewCompat.getTranslationX(mSwipingItem.itemView) + 0.5f);
            mTranslationY = (int) (ViewCompat.getTranslationY(mSwipingItem.itemView) + 0.5f);
        }

        if (requiresContinuousAnimationAfterSwipeCompletedSuccessfully(elapsedTime)) {
            postInvalidateOnAnimation();
        }
    }

    private boolean requiresContinuousAnimationAfterSwipeCompletedSuccessfully(long elapsedTime) {
        return (elapsedTime >= mRemoveAnimationDuration) &&
                (elapsedTime < (mRemoveAnimationDuration + mMoveAnimationDuration));
    }

    private float determineBackgroundScaleSwipeCompletedSuccessfully(long elapsedTime) {
        float heightScale = 0.0f;

        if (elapsedTime < mRemoveAnimationDuration) {
            heightScale = 1.0f;
        } else if (elapsedTime < (mRemoveAnimationDuration + mMoveAnimationDuration)) {
            if (mMoveAnimationDuration != 0) {
                heightScale = 1.0f - (float) (elapsedTime - mRemoveAnimationDuration) / mMoveAnimationDuration;
                if (mMoveAnimationInterpolator != null) {
                    heightScale = mMoveAnimationInterpolator.getInterpolation(heightScale);
                }
            }
        }

        return heightScale;
    }

    private void fillSwipingItemBackground(Canvas c, Drawable drawable, float scale) {
        final Rect bounds = mSwipingItemBounds;
        final int translationX = mTranslationX;
        final int translationY = mTranslationY;
        final float hScale = (mHorizontal) ? 1.0f : scale;
        final float vScale = (mHorizontal) ? scale : 1.0f;

        int width = (int) (hScale * bounds.width() + 0.5f);
        int height = (int) (vScale * bounds.height() + 0.5f);

        if ((height == 0) || (width == 0) || (drawable == null)) {
            return;
        }

        final int savedCount = c.save();

        c.clipRect(
                bounds.left + translationX,
                bounds.top + translationY,
                bounds.left + translationX + width,
                bounds.top + translationY + height);

        // c.drawColor(0xffff0000); // <-- debug

        c.translate(
                bounds.left + translationX - (bounds.width() - width) / 2,
                bounds.top + translationY - (bounds.height() - height) / 2);
        drawable.setBounds(0, 0, bounds.width(), bounds.height());

        drawable.draw(c);

        c.restoreToCount(savedCount);
    }

    private void postInvalidateOnAnimation() {
        ViewCompat.postInvalidateOnAnimation(mRecyclerView);
    }

    public void start() {
        final View containerView = SwipeableViewHolderUtils.getSwipeableContainerView(mSwipingItem);

        ViewCompat.animate(containerView).cancel();

        mRecyclerView.addItemDecoration(this);

        mStartTime = System.currentTimeMillis();
        mTranslationY = (int) (ViewCompat.getTranslationY(mSwipingItem.itemView) + 0.5f);
        mSwipeBackgroundDrawable = mSwipingItem.itemView.getBackground();

        postInvalidateOnAnimation();
        notifyDelayed(NOTIFY_REMOVAL_EFFECT_PHASE_1, mRemoveAnimationDuration);
    }

    private void notifyDelayed(int code, long delay) {
        final int mask = (1 << code);

        if ((mPendingNotificationMask & mask) != 0) {
            return;
        }

        mPendingNotificationMask |= mask;

        final DelayedNotificationRunner notification = new DelayedNotificationRunner(this, code);
        ViewCompat.postOnAnimationDelayed(mRecyclerView, notification, delay);
    }

    /*package*/ void onDelayedNotification(int code) {
        final int mask = (1 << code);
        final long elapsedTime = getElapsedTime(mStartTime);

        mPendingNotificationMask &= (~mask);

        switch (code) {
            case NOTIFY_REMOVAL_EFFECT_PHASE_1:
                if (elapsedTime < mRemoveAnimationDuration) {
                    notifyDelayed(NOTIFY_REMOVAL_EFFECT_PHASE_1, (mRemoveAnimationDuration - elapsedTime));
                } else {
                    postInvalidateOnAnimation();
                    notifyDelayed(NOTIFY_REMOVAL_EFFECT_END, mMoveAnimationDuration);
                }
                break;
            case NOTIFY_REMOVAL_EFFECT_END:
                finish();
                break;
        }
    }

    private void finish() {
        mRecyclerView.removeItemDecoration(this);
        postInvalidateOnAnimation(); // this is required to avoid remnant of the decoration

        mRecyclerView = null;
        mSwipingItem = null;
        mTranslationY = 0;
        mMoveAnimationInterpolator = null;
    }

    protected static long getElapsedTime(long initialTime) {
        final long curTime = System.currentTimeMillis();
        return (curTime >= initialTime) ? (curTime - initialTime) : Long.MAX_VALUE;
    }

    private static class DelayedNotificationRunner implements Runnable {
        private WeakReference<RemovingItemDecorator> mRefDecorator;
        private final int mCode;

        public DelayedNotificationRunner(RemovingItemDecorator decorator, int code) {
            mRefDecorator = new WeakReference<>(decorator);
            mCode = code;
        }

        @Override
        public void run() {
            final RemovingItemDecorator decorator = mRefDecorator.get();
            mRefDecorator.clear();
            mRefDecorator = null;

            if (decorator != null) {
                decorator.onDelayedNotification(mCode);
            }
        }
    }
}