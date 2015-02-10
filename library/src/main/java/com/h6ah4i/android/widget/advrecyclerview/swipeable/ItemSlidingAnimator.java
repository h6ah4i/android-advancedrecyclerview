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

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

public class ItemSlidingAnimator {
    private static final String TAG = "ItemSlidingAnimator";

    private Interpolator mSlideToDefaultPositionAnimationInterpolator = new AccelerateDecelerateInterpolator();
    private Interpolator mSlideToOutsideOfWindowAnimationInterpolator = new AccelerateInterpolator(0.8f);
    private List<RecyclerView.ViewHolder> mActive;
    private int[] mTmpLocation = new int[2];
    private Rect mTmpRect = new Rect();
    private int mImmediatelySetTranslationThreshold;

    public ItemSlidingAnimator() {
        mActive = new ArrayList<>();
    }

    public void slideToDefaultPosition(RecyclerView.ViewHolder holder, boolean shouldAnimate, long duration) {
        slideToSpecifiedPositionInternal(holder, 0, shouldAnimate, duration);
    }

    public void slideToOutsideOfWindow(RecyclerView.ViewHolder holder, boolean toLeft, boolean shouldAnimate, long duration) {
        slideToOutsideOfWindowInternal(holder, toLeft, shouldAnimate, duration);
    }

    public void slideToSpecifiedPosition(RecyclerView.ViewHolder holder, float position) {
        slideToSpecifiedPositionInternal(holder, position, false, 0);
    }

    private boolean slideToSpecifiedPositionInternal(RecyclerView.ViewHolder holder, float position, boolean shouldAnimate, long duration) {
        final int translationX;

        duration = (shouldAnimate) ? duration : 0;

        if (position != 0.0f) {
            final View containerView = ((SwipeableItemViewHolder) holder).getSwipeableContainerView();

            translationX = (int) (containerView.getWidth() * position + 0.5f);
        } else {
            translationX = 0;
        }

        return animateSlideInternalCompat(holder, translationX, duration, mSlideToDefaultPositionAnimationInterpolator);
    }

    private boolean slideToOutsideOfWindowInternal(RecyclerView.ViewHolder holder, boolean toLeft, boolean shouldAnimate, long duration) {
        if (!(holder instanceof SwipeableItemViewHolder)) {
            return false;
        }

        final View containerView = ((SwipeableItemViewHolder) holder).getSwipeableContainerView();
        final ViewGroup parent = (ViewGroup) containerView.getParent();

        if (parent == null) {
            return false;
        }

        final int left = containerView.getLeft();
        final int right = containerView.getRight();
        final int width = right - left;
        final boolean parentIsShown = parent.isShown();

        parent.getWindowVisibleDisplayFrame(mTmpRect);

        final int translateX;
        if ((width == 0) || !parentIsShown) {
            // not measured yet or not shown
            translateX = (toLeft) ? (-mTmpRect.width()) : (mTmpRect.width());
            shouldAnimate = false;
        } else {
            parent.getLocationInWindow(mTmpLocation);

            if (toLeft) {
                translateX = -(mTmpLocation[0] + width);
            } else {
                translateX = mTmpRect.width() - (mTmpLocation[0] - left);
            }
        }

        if (shouldAnimate) {
            shouldAnimate = containerView.isShown();
        }

        duration = (shouldAnimate) ? duration : 0;

        return animateSlideInternalCompat(holder, translateX, duration, mSlideToOutsideOfWindowAnimationInterpolator);
    }

    private boolean animateSlideInternalCompat(RecyclerView.ViewHolder holder, int translationX, long duration, Interpolator interpolator) {
        if (supportsViewPropertyAnimator()) {
            return animateSlideInternal(holder, translationX, duration, interpolator);
        } else {
            return animateSlideInternalPreHoneycomb(holder, translationX);
        }
    }

    @SuppressLint("RtlHardcoded")
    private boolean animateSlideInternalPreHoneycomb(RecyclerView.ViewHolder holder, int translationX) {
        if (!(holder instanceof SwipeableItemViewHolder)) {
            return false;
        }

        final View containerView = ((SwipeableItemViewHolder) holder).getSwipeableContainerView();

        final ViewGroup.LayoutParams lp = containerView.getLayoutParams();
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            final ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lp;
            mlp.leftMargin = translationX;
            mlp.rightMargin = -translationX;

            if (lp instanceof FrameLayout.LayoutParams) {
                ((FrameLayout.LayoutParams) lp).gravity = Gravity.TOP | Gravity.LEFT;
            }
            containerView.setLayoutParams(mlp);
        } else {
            Log.w(TAG, "should use MarginLayoutParams supported view for compatibility on Android 2.3");
        }

        return false;
    }

    private boolean animateSlideInternal(final RecyclerView.ViewHolder holder, int translationX, long duration, Interpolator interpolator) {
        if (!(holder instanceof SwipeableItemViewHolder)) {
            return false;
        }

        final View containerView = ((SwipeableItemViewHolder) holder).getSwipeableContainerView();

        final int prevTranslationX = (int) (ViewCompat.getTranslationX(containerView) + 0.5f);

        endAnimation(holder);

        final int toX = translationX;

        if (duration == 0 || Math.abs(toX - prevTranslationX) <= mImmediatelySetTranslationThreshold) {
            ViewCompat.setTranslationX(containerView, toX);
            return false;
        }

        ViewCompat.setTranslationX(containerView, prevTranslationX);

        final ViewPropertyAnimatorCompat animator = ViewCompat.animate(containerView);

        animator.setDuration(duration);
        if (interpolator != null) {
            animator.setInterpolator(interpolator);
        }
        animator.translationX(toX);
        animator.setListener(new ViewPropertyAnimatorListener() {
            @Override
            public void onAnimationStart(View view) {
            }

            @Override
            public void onAnimationEnd(View view) {
                animator.setListener(null);
                mActive.remove(holder);
                ViewCompat.setTranslationX(view, toX);
            }

            @Override
            public void onAnimationCancel(View view) {
            }
        });

        mActive.add(holder);

        animator.start();

        return true;
    }

    public void endAnimation(RecyclerView.ViewHolder holder) {
        if (!(holder instanceof SwipeableItemViewHolder)) {
            return;
        }

        final View containerView = ((SwipeableItemViewHolder) holder).getSwipeableContainerView();

        ViewCompat.animate(containerView).cancel();

        if (mActive.remove(holder)) {
            throw new IllegalStateException("after animation is cancelled, item should not be in the active animation list [slide]");
        }
    }

    public void endAnimations() {
        for (int i = mActive.size() - 1; i >= 0; i--) {
            final RecyclerView.ViewHolder holder = mActive.get(i);
            endAnimation(holder);
        }
    }

    public boolean isRunning(RecyclerView.ViewHolder holder) {
        return mActive.contains(holder);
    }

    public boolean isRunning() {
        return !(mActive.isEmpty());
    }

    public int getImmediatelySetTranslationThreshold() {
        return mImmediatelySetTranslationThreshold;
    }

    public void setImmediatelySetTranslationThreshold(int threshold) {
        mImmediatelySetTranslationThreshold = threshold;
    }

    private static boolean supportsViewPropertyAnimator() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }
}
