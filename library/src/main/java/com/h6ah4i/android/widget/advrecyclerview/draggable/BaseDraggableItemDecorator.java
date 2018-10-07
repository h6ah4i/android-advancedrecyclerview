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

import android.view.View;
import android.view.animation.Interpolator;

import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.recyclerview.widget.RecyclerView;

abstract class BaseDraggableItemDecorator extends RecyclerView.ItemDecoration {

    private static final int RETURN_TO_DEFAULT_POS_ANIMATE_THRESHOLD_DP = 2;
    private static final int RETURN_TO_DEFAULT_POS_ANIMATE_THRESHOLD_MSEC = 20;

    private int mReturnToDefaultPositionDuration = 200;
    private final int mReturnToDefaultPositionAnimateThreshold;
    private Interpolator mReturnToDefaultPositionInterpolator;

    protected final RecyclerView mRecyclerView;
    protected RecyclerView.ViewHolder mDraggingItemViewHolder;

    public BaseDraggableItemDecorator(RecyclerView recyclerView, RecyclerView.ViewHolder draggingItemViewHolder) {
        mRecyclerView = recyclerView;
        mDraggingItemViewHolder = draggingItemViewHolder;

        final float displayDensity = recyclerView.getResources().getDisplayMetrics().density;
        mReturnToDefaultPositionAnimateThreshold = (int) (RETURN_TO_DEFAULT_POS_ANIMATE_THRESHOLD_DP * displayDensity + 0.5f);
    }

    public void setReturnToDefaultPositionAnimationDuration(int duration) {
        mReturnToDefaultPositionDuration = duration;
    }

    public void setReturnToDefaultPositionAnimationInterpolator(Interpolator interpolator) {
        mReturnToDefaultPositionInterpolator = interpolator;
    }

    protected void moveToDefaultPosition(View targetView, float initialScaleX, float initialScaleY, float initialRotation, float initialAlpha, boolean animate) {
        final float initialTranslationZ = ViewCompat.getTranslationZ(targetView);

        final float durationFactor = determineMoveToDefaultPositionAnimationDurationFactor(
                targetView, initialScaleX, initialScaleY, initialRotation, initialAlpha);
        final int animDuration = (int) (mReturnToDefaultPositionDuration * durationFactor);

        if (animate && (animDuration > RETURN_TO_DEFAULT_POS_ANIMATE_THRESHOLD_MSEC)) {
            ViewPropertyAnimatorCompat animator = ViewCompat.animate(targetView);

            targetView.setScaleX(initialScaleX);
            targetView.setScaleY(initialScaleY);
            targetView.setRotation(initialRotation);
            targetView.setAlpha(initialAlpha);
            ViewCompat.setTranslationZ(targetView, initialTranslationZ + 1); // to render on top of other items

            animator.cancel();
            animator.setDuration(animDuration);
            animator.setInterpolator(mReturnToDefaultPositionInterpolator);
            animator.translationX(0.0f);
            animator.translationY(0.0f);
            animator.translationZ(initialTranslationZ);
            animator.alpha(1.0f);
            animator.rotation(0);
            animator.scaleX(1.0f);
            animator.scaleY(1.0f);

            animator.setListener(new ViewPropertyAnimatorListener() {
                @Override
                public void onAnimationStart(View view) {
                }

                @Override
                public void onAnimationEnd(View view) {
                    ViewPropertyAnimatorCompat animator = ViewCompat.animate(view);
                    animator.setListener(null);
                    resetDraggingItemViewEffects(view, initialTranslationZ);

                    // invalidate explicitly to refresh other decorations
                    if (view.getParent() instanceof RecyclerView) {
                        ViewCompat.postInvalidateOnAnimation((RecyclerView) view.getParent());
                    }
                }

                @Override
                public void onAnimationCancel(View view) {
                }
            });
            animator.start();
        } else {
            resetDraggingItemViewEffects(targetView, initialTranslationZ);
        }
    }

    protected float determineMoveToDefaultPositionAnimationDurationFactor(
            View targetView, float initialScaleX, float initialScaleY, float initialRotation, float initialAlpha) {
        final float curTranslationX = targetView.getTranslationX();
        final float curTranslationY = targetView.getTranslationY();
        final int halfItemWidth = targetView.getWidth() / 2;
        final int halfItemHeight = targetView.getHeight() / 2;
        final float translationXProportion = (halfItemWidth > 0) ? Math.abs(curTranslationX / halfItemWidth) : 0;
        final float translationYProportion = (halfItemHeight > 0) ? Math.abs(curTranslationY / halfItemHeight) : 0;
        final float scaleProportion = Math.abs(Math.max(initialScaleX, initialScaleY) - 1.0f);
        final float rotationProportion = Math.abs(initialRotation * (1.0f / 30));
        final float alphaProportion = Math.abs(initialAlpha - 1.0f);

        float factor = 0;

        factor = Math.max(factor, translationXProportion);
        factor = Math.max(factor, translationYProportion);
        factor = Math.max(factor, scaleProportion);
        factor = Math.max(factor, rotationProportion);
        factor = Math.max(factor, alphaProportion);
        factor = Math.min(factor, 1.0f);

        return factor;
    }

    protected static void resetDraggingItemViewEffects(View view, float initialTranslationZ) {
        view.setTranslationX(0);
        view.setTranslationY(0);
        ViewCompat.setTranslationZ(view, initialTranslationZ);
        view.setAlpha(1.0f);
        view.setRotation(0);
        view.setScaleX(1.0f);
        view.setScaleY(1.0f);
    }

    protected static void setItemTranslation(RecyclerView rv, RecyclerView.ViewHolder holder, float x, float y) {
        final RecyclerView.ItemAnimator itemAnimator = rv.getItemAnimator();
        if (itemAnimator != null) {
            itemAnimator.endAnimation(holder);
        }
        holder.itemView.setTranslationX(x);
        holder.itemView.setTranslationY(y);
    }
}
