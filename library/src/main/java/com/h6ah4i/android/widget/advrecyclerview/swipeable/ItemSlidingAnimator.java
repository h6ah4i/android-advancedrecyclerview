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
import android.support.v4.view.ViewPropertyAnimatorUpdateListener;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ItemSlidingAnimator {
    private static final String TAG = "ItemSlidingAnimator";

    public static final int DIR_LEFT = 0;
    public static final int DIR_UP = 1;
    public static final int DIR_RIGHT = 2;
    public static final int DIR_DOWN = 3;

    private final SwipeableItemWrapperAdapter<RecyclerView.ViewHolder> mAdapter;
    private final Interpolator mSlideToDefaultPositionAnimationInterpolator = new AccelerateDecelerateInterpolator();
    private final Interpolator mSlideToOutsideOfWindowAnimationInterpolator = new AccelerateInterpolator(0.8f);
    private final List<RecyclerView.ViewHolder> mActive;
    private final List<WeakReference<ViewHolderDeferredProcess>> mDeferredProcesses;
    private final int[] mTmpLocation = new int[2];
    private final Rect mTmpRect = new Rect();
    private int mImmediatelySetTranslationThreshold;

    public ItemSlidingAnimator(SwipeableItemWrapperAdapter<RecyclerView.ViewHolder> adapter) {
        mAdapter = adapter;
        mActive = new ArrayList<>();
        mDeferredProcesses = new ArrayList<>();
    }

    public void slideToDefaultPosition(RecyclerView.ViewHolder holder, boolean horizontal, boolean shouldAnimate, long duration) {
        cancelDeferredProcess(holder);
        slideToSpecifiedPositionInternal(holder, 0, horizontal, shouldAnimate, duration, null);
    }

    public void slideToOutsideOfWindow(RecyclerView.ViewHolder holder, int dir, boolean shouldAnimate, long duration) {
        cancelDeferredProcess(holder);
        slideToOutsideOfWindowInternal(holder, dir, shouldAnimate, duration, null);
    }

    public void slideToSpecifiedPosition(RecyclerView.ViewHolder holder, float position, boolean horizontal) {
        cancelDeferredProcess(holder);
        slideToSpecifiedPositionInternal(holder, position, horizontal, false, 0, null);
    }

    public boolean finishSwipeSlideToDefaultPosition(
            RecyclerView.ViewHolder holder, boolean horizontal,
            boolean shouldAnimate, long duration,
            int itemPosition, SwipeResultAction resultAction) {
        cancelDeferredProcess(holder);
        return slideToSpecifiedPositionInternal(holder, 0, horizontal, shouldAnimate, duration,
                new SwipeFinishInfo(itemPosition, resultAction));
    }

    public boolean finishSwipeSlideToOutsideOfWindow(
            RecyclerView.ViewHolder holder, int dir,
            boolean shouldAnimate, long duration,
            int itemPosition, SwipeResultAction resultAction) {
        cancelDeferredProcess(holder);
        return slideToOutsideOfWindowInternal(
                holder, dir, shouldAnimate, duration,
                new SwipeFinishInfo(itemPosition, resultAction));
    }

    private void cancelDeferredProcess(RecyclerView.ViewHolder holder) {
        int n = mDeferredProcesses.size();
        for (int i = n - 1; i >= 0; i--) {
            ViewHolderDeferredProcess dp = mDeferredProcesses.get(i).get();

            if (dp != null && dp.hasTargetViewHolder(holder)) {
                holder.itemView.removeCallbacks(dp);
                mDeferredProcesses.remove(i);
            } else if (dp == null || dp.lostReference(holder)) {
                mDeferredProcesses.remove(i);
            }
        }
    }

    private void scheduleViewHolderDeferredSlideProcess(RecyclerView.ViewHolder holder, ViewHolderDeferredProcess deferredProcess) {
        mDeferredProcesses.add(new WeakReference<>(deferredProcess));
        holder.itemView.post(deferredProcess);
    }

    private boolean slideToSpecifiedPositionInternal(
            final RecyclerView.ViewHolder holder, final float position,
            boolean horizontal, boolean shouldAnimate, long duration,
            SwipeFinishInfo swipeFinish) {
        final Interpolator defaultInterpolator = mSlideToDefaultPositionAnimationInterpolator;

        duration = (shouldAnimate) ? duration : 0;

        if (position != 0.0f) {
            final View containerView = ((SwipeableItemViewHolder) holder).getSwipeableContainerView();
            final int width = containerView.getWidth();
            final int height = containerView.getHeight();

            if (horizontal && width != 0) {
                final int translationX;
                translationX = (int) (width * position + 0.5f);
                return animateSlideInternalCompat(
                        holder, horizontal, translationX, 0, duration, defaultInterpolator, swipeFinish);
            } else if (!horizontal && (height != 0)) {
                final int translationY;
                translationY = (int) (height * position + 0.5f);
                return animateSlideInternalCompat(
                        holder, horizontal, 0, translationY, duration, defaultInterpolator, swipeFinish);
            } else {
                if (swipeFinish != null) {
                    throw new IllegalStateException(
                            "Unexpected state in slideToSpecifiedPositionInternal (swipeFinish == null)");
                }

                scheduleViewHolderDeferredSlideProcess(
                        holder, new DeferredSlideProcess(holder, position, horizontal));

                return false;
            }
        } else {
            return animateSlideInternalCompat(
                    holder, horizontal, 0, 0, duration, defaultInterpolator, swipeFinish);
        }
    }

    private boolean slideToOutsideOfWindowInternal(
            RecyclerView.ViewHolder holder, int dir, boolean shouldAnimate, long duration,
            SwipeFinishInfo swipeFinish) {

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
        final int top = containerView.getTop();
        final int bottom = containerView.getBottom();
        final int width = right - left;
        final int height = bottom - top;
        final boolean parentIsShown = parent.isShown();

        parent.getWindowVisibleDisplayFrame(mTmpRect);
        final int windowWidth = mTmpRect.width();
        final int windowHeight = mTmpRect.height();

        int translateX = 0;
        int translateY = 0;

        if ((width == 0) || (height == 0) || !parentIsShown) {
            // not measured yet or not shown
            switch (dir) {
                case DIR_LEFT:
                    translateX = -windowWidth;
                    break;
                case DIR_UP:
                    translateY = -windowHeight;
                    break;
                case DIR_RIGHT:
                    translateX = windowWidth;
                    break;
                case DIR_DOWN:
                    translateY = windowHeight;
                    break;
                default:
                    break;
            }
            shouldAnimate = false;
        } else {
            parent.getLocationInWindow(mTmpLocation);
            final int x = mTmpLocation[0];
            final int y = mTmpLocation[1];

            switch (dir) {
                case DIR_LEFT:
                    translateX = -(x + width);
                    break;
                case DIR_UP:
                    translateY = -(y + height);
                    break;
                case DIR_RIGHT:
                    translateX = windowWidth - (x - left);
                    break;
                case DIR_DOWN:
                    translateY = windowHeight - (y - top);
                    break;
                default:
                    break;
            }
        }

        if (shouldAnimate) {
            shouldAnimate = containerView.isShown();
        }

        duration = (shouldAnimate) ? duration : 0;

        boolean horizontal = (dir == DIR_LEFT || dir == DIR_RIGHT);
        return animateSlideInternalCompat(
                holder, horizontal,
                translateX, translateY, duration, mSlideToOutsideOfWindowAnimationInterpolator,
                swipeFinish);
    }

    private boolean animateSlideInternalCompat(
            RecyclerView.ViewHolder holder,
            boolean horizontal, int translationX, int translationY, long duration, Interpolator interpolator,
            SwipeFinishInfo swipeFinish) {
        boolean result;

        if (supportsViewPropertyAnimator()) {
            result = animateSlideInternal(holder, horizontal, translationX, translationY, duration, interpolator, swipeFinish);
        } else {
            result = slideInternalPreHoneycomb(holder, horizontal, translationX, translationY);
        }

        // if ((swipeFinish != null) && !result) {
        // NOTE: Have to invoke the onSwipeSlideItemAnimationEnd() method in caller context
        // }

        return result;
    }

    static void slideInternalCompat(RecyclerView.ViewHolder holder, boolean horizontal, int translationX, int translationY) {
        if (supportsViewPropertyAnimator()) {
            slideInternal(holder, horizontal, translationX, translationY);
        } else {
            slideInternalPreHoneycomb(holder, horizontal, translationX, translationY);
        }
    }

    @SuppressLint("RtlHardcoded")
    private static boolean slideInternalPreHoneycomb(
            RecyclerView.ViewHolder holder, boolean horizontal, int translationX, int translationY) {
        if (!(holder instanceof SwipeableItemViewHolder)) {
            return false;
        }

        final View containerView = ((SwipeableItemViewHolder) holder).getSwipeableContainerView();

        final ViewGroup.LayoutParams lp = containerView.getLayoutParams();
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            final ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lp;

            mlp.leftMargin = translationX;
            mlp.rightMargin = -translationX;
            mlp.topMargin = translationY;
            mlp.bottomMargin = -translationY;

            if (lp instanceof FrameLayout.LayoutParams) {
                ((FrameLayout.LayoutParams) lp).gravity = Gravity.TOP | Gravity.LEFT;
            }

            containerView.setLayoutParams(mlp);
        } else {
            Log.w(TAG, "should use MarginLayoutParams supported view for compatibility on Android 2.3");
        }

        return false;
    }

    private static int getTranslationXPreHoneycomb(RecyclerView.ViewHolder holder) {
        final View containerView = ((SwipeableItemViewHolder) holder).getSwipeableContainerView();

        final ViewGroup.LayoutParams lp = containerView.getLayoutParams();
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            final ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lp;
            return mlp.leftMargin;
        } else {
            Log.w(TAG, "should use MarginLayoutParams supported view for compatibility on Android 2.3");
            return 0;
        }
    }

    private static int getTranslationYPreHoneycomb(RecyclerView.ViewHolder holder) {
        final View containerView = ((SwipeableItemViewHolder) holder).getSwipeableContainerView();

        final ViewGroup.LayoutParams lp = containerView.getLayoutParams();
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            final ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lp;
            return mlp.topMargin;
        } else {
            Log.w(TAG, "should use MarginLayoutParams supported view for compatibility on Android 2.3");
            return 0;
        }
    }

    private static void slideInternal(final RecyclerView.ViewHolder holder, boolean horizontal, int translationX, int translationY) {
        if (!(holder instanceof SwipeableItemViewHolder)) {
            return;
        }

        final View containerView = ((SwipeableItemViewHolder) holder).getSwipeableContainerView();
        ViewCompat.animate(containerView).cancel();
        ViewCompat.setTranslationX(containerView, translationX);
        ViewCompat.setTranslationY(containerView, translationY);
    }

    private boolean animateSlideInternal(
            RecyclerView.ViewHolder holder, boolean horizontal,
            int translationX, int translationY, long duration, Interpolator interpolator,
            SwipeFinishInfo swipeFinish) {
        if (!(holder instanceof SwipeableItemViewHolder)) {
            return false;
        }

        final View containerView = ((SwipeableItemViewHolder) holder).getSwipeableContainerView();

        final int prevTranslationX = (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int prevTranslationY = (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        endAnimation(holder);

        final int curTranslationX = (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int curTranslationY = (int) (ViewCompat.getTranslationY(containerView) + 0.5f);
        //noinspection UnnecessaryLocalVariable
        final int toX = translationX;
        //noinspection UnnecessaryLocalVariable
        final int toY = translationY;

        if ((duration == 0) ||
                (curTranslationX == toX && curTranslationY == toY) ||
                (Math.max(Math.abs(toX - prevTranslationX), Math.abs(toY - prevTranslationY)) <= mImmediatelySetTranslationThreshold)) {
            ViewCompat.setTranslationX(containerView, toX);
            ViewCompat.setTranslationY(containerView, toY);

            return false;
        }

        ViewCompat.setTranslationX(containerView, prevTranslationX);
        ViewCompat.setTranslationY(containerView, prevTranslationY);

        SlidingAnimatorListenerObject listener = new SlidingAnimatorListenerObject(
                mAdapter, mActive, holder, toX, toY, duration, horizontal, interpolator,
                swipeFinish);

        listener.start();

        return true;
    }

    public void endAnimation(RecyclerView.ViewHolder holder) {
        if (!(holder instanceof SwipeableItemViewHolder)) {
            return;
        }

        cancelDeferredProcess(holder);

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

    public int getSwipeContainerViewTranslationX(RecyclerView.ViewHolder holder) {
        if (supportsViewPropertyAnimator()) {
            final View containerView = ((SwipeableItemViewHolder) holder).getSwipeableContainerView();
            return (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        } else {
            return getTranslationXPreHoneycomb(holder);
        }
    }

    public int getSwipeContainerViewTranslationY(RecyclerView.ViewHolder holder) {
        if (supportsViewPropertyAnimator()) {
            final View containerView = ((SwipeableItemViewHolder) holder).getSwipeableContainerView();
            return (int) (ViewCompat.getTranslationY(containerView) + 0.5f);
        } else {
            return getTranslationYPreHoneycomb(holder);
        }
    }

    private static abstract class ViewHolderDeferredProcess implements Runnable {
        final WeakReference<RecyclerView.ViewHolder> mRefHolder;

        public ViewHolderDeferredProcess(RecyclerView.ViewHolder holder) {
            mRefHolder = new WeakReference<>(holder);
        }

        @Override
        public void run() {
            RecyclerView.ViewHolder holder = mRefHolder.get();

            if (holder != null) {
                onProcess(holder);
            }
        }

        public boolean lostReference(RecyclerView.ViewHolder holder) {
            RecyclerView.ViewHolder holder2 = mRefHolder.get();
            return (holder2 == null);
        }

        public boolean hasTargetViewHolder(RecyclerView.ViewHolder holder) {
            RecyclerView.ViewHolder holder2 = mRefHolder.get();
            return (holder2 == holder);
        }

        protected abstract void onProcess(RecyclerView.ViewHolder holder);
    }

    private static final class DeferredSlideProcess extends ViewHolderDeferredProcess {
        final float mPosition;
        final boolean mHorizontal;

        public DeferredSlideProcess(RecyclerView.ViewHolder holder, float position, boolean horizontal) {
            super(holder);
            mPosition = position;
            mHorizontal = horizontal;
        }

        @Override
        protected void onProcess(RecyclerView.ViewHolder holder) {
            final View containerView = ((SwipeableItemViewHolder) holder).getSwipeableContainerView();

            if (mHorizontal) {
                final int width = containerView.getWidth();
                final int translationX;

                translationX = (int) (width * mPosition + 0.5f);
                slideInternalCompat(holder, mHorizontal, translationX, 0);
            } else {
                final int height = containerView.getHeight();
                final int translationY;

                translationY = (int) (height * mPosition + 0.5f);
                slideInternalCompat(holder, mHorizontal, 0, translationY);
            }
        }
    }

    private static class SlidingAnimatorListenerObject
            implements ViewPropertyAnimatorListener,
            ViewPropertyAnimatorUpdateListener {

        private SwipeableItemWrapperAdapter<RecyclerView.ViewHolder> mAdapter;
        private List<RecyclerView.ViewHolder> mActive;
        private RecyclerView.ViewHolder mViewHolder;
        private ViewPropertyAnimatorCompat mAnimator;
        private final int mToX;
        private final int mToY;
        private final long mDuration;
        private final boolean mHorizontal;
        private final SwipeFinishInfo mSwipeFinish;
        private final Interpolator mInterpolator;
        private float mInvSize;

        SlidingAnimatorListenerObject(
                SwipeableItemWrapperAdapter<RecyclerView.ViewHolder> adapter,
                List<RecyclerView.ViewHolder> activeViewHolders,
                RecyclerView.ViewHolder holder, int toX, int toY, long duration, boolean horizontal,
                Interpolator interpolator, SwipeFinishInfo swipeFinish) {
            mAdapter = adapter;
            mActive = activeViewHolders;
            mViewHolder = holder;
            mToX = toX;
            mToY = toY;
            mHorizontal = horizontal;
            mSwipeFinish = swipeFinish;
            mDuration = duration;
            mInterpolator = interpolator;
        }

        void start() {
            final View containerView = ((SwipeableItemViewHolder) mViewHolder).getSwipeableContainerView();

            mInvSize = (1.0f / Math.max(1.0f, mHorizontal ? containerView.getWidth() : containerView.getHeight()));

            // setup animator
            mAnimator = ViewCompat.animate(containerView);
            mAnimator.setDuration(mDuration);
            mAnimator.translationX(mToX);
            mAnimator.translationY(mToY);
            if (mInterpolator != null) {
                mAnimator.setInterpolator(mInterpolator);
            }
            mAnimator.setListener(this);
            mAnimator.setUpdateListener(this);

            // start
            mActive.add(mViewHolder);
            mAnimator.start();
        }

        @Override
        public void onAnimationUpdate(View view) {
            float translation = mHorizontal ? ViewCompat.getTranslationX(view) : ViewCompat.getTranslationY(view);
            float amount = translation * mInvSize;

            mAdapter.onUpdateSlideAmount(mViewHolder, mViewHolder.getLayoutPosition(), mHorizontal, amount, false);
        }

        @Override
        public void onAnimationStart(View view) {
        }

        @Override
        public void onAnimationEnd(View view) {
            mAnimator.setListener(null);
            // [WORKAROUND]
            // Issue 189686: NPE can be occurred when using the ViewPropertyAnimatorCompat
            // https://code.google.com/p/android/issues/detail?id=189686
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                InternalHelperKK.clearViewPropertyAnimatorUpdateListener(view);
            } else {
                mAnimator.setUpdateListener(null);
            }

            ViewCompat.setTranslationX(view, mToX);
            ViewCompat.setTranslationY(view, mToY);


            mActive.remove(mViewHolder);

            // [WORKAROUND]
            // issue #152 - bug:Samsung S3 4.1.1(Genymotion) with swipe left
            ViewParent itemParentView = mViewHolder.itemView.getParent();
            if (itemParentView != null) {
                ViewCompat.postInvalidateOnAnimation((View) itemParentView);
            }

            if (mSwipeFinish != null) {
                mSwipeFinish.resultAction.slideAnimationEnd();
            }

            // clean up
            mActive = null;
            mAnimator = null;
            mViewHolder = null;
            mAdapter = null;
        }

        @Override
        public void onAnimationCancel(View view) {
        }
    }

    private static class SwipeFinishInfo {
        final int itemPosition;
        SwipeResultAction resultAction;

        public SwipeFinishInfo(int itemPosition, SwipeResultAction resultAction) {
            this.itemPosition = itemPosition;
            this.resultAction = resultAction;
        }

        public void clear() {
            this.resultAction = null;
        }
    }
}
