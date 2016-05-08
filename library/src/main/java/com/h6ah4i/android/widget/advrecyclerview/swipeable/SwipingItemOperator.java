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

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Interpolator;

class SwipingItemOperator {
    @SuppressWarnings("unused")
    private static final String TAG = "SwipingItemOperator";

    private static final int REACTION_CAN_NOT_SWIPE = InternalConstants.REACTION_CAN_NOT_SWIPE;
    private static final int REACTION_CAN_NOT_SWIPE_WITH_RUBBER_BAND_EFFECT = InternalConstants.REACTION_CAN_NOT_SWIPE_WITH_RUBBER_BAND_EFFECT;
    private static final int REACTION_CAN_SWIPE = InternalConstants.REACTION_CAN_SWIPE;

    private static final float RUBBER_BAND_LIMIT = 0.15f;
    private static final int MIN_GRABBING_AREA_SIZE = 48;

    private static final Interpolator RUBBER_BAND_INTERPOLATOR = new RubberBandInterpolator(RUBBER_BAND_LIMIT);

    private RecyclerViewSwipeManager mSwipeManager;
    private RecyclerView.ViewHolder mSwipingItem;
    private View mSwipingItemContainerView;
    private int mLeftSwipeReactionType;
    private int mUpSwipeReactionType;
    private int mRightSwipeReactionType;
    private int mDownSwipeReactionType;
    private int mSwipingItemWidth;
    private final int mSwipingItemHeight;
    private float mInvSwipingItemWidth;
    private float mInvSwipingItemHeight;
    private int mSwipeDistanceX;
    private int mSwipeDistanceY;
    private float mPrevTranslateAmount;
    private int mInitialTranslateAmountX;
    private int mInitialTranslateAmountY;
    private final boolean mSwipeHorizontal;

    public SwipingItemOperator(
            RecyclerViewSwipeManager manager, RecyclerView.ViewHolder swipingItem,
            int swipeReactionType, boolean swipeHorizontal) {

        mSwipeManager = manager;
        mSwipingItem = swipingItem;
        mLeftSwipeReactionType = SwipeReactionUtils.extractLeftReaction(swipeReactionType);
        mUpSwipeReactionType = SwipeReactionUtils.extractUpReaction(swipeReactionType);
        mRightSwipeReactionType = SwipeReactionUtils.extractRightReaction(swipeReactionType);
        mDownSwipeReactionType = SwipeReactionUtils.extractDownReaction(swipeReactionType);
        mSwipeHorizontal = swipeHorizontal;

        mSwipingItemContainerView = ((SwipeableItemViewHolder) swipingItem).getSwipeableContainerView();
        mSwipingItemWidth = mSwipingItemContainerView.getWidth();
        mSwipingItemHeight = mSwipingItemContainerView.getHeight();
        mInvSwipingItemWidth = calcInv(mSwipingItemWidth);
        mInvSwipingItemHeight = calcInv(mSwipingItemHeight);
    }

    public void start() {
        float density = mSwipingItem.itemView.getResources().getDisplayMetrics().density;
        int maxAmountH = Math.max(0, mSwipingItemWidth - (int) (density * MIN_GRABBING_AREA_SIZE));
        int maxAmountV = Math.max(0, mSwipingItemHeight - (int) (density * MIN_GRABBING_AREA_SIZE));

        mInitialTranslateAmountX = clip(mSwipeManager.getSwipeContainerViewTranslationX(mSwipingItem), -maxAmountH, maxAmountH);
        mInitialTranslateAmountY = clip(mSwipeManager.getSwipeContainerViewTranslationY(mSwipingItem), -maxAmountV, maxAmountV);
    }

    public void finish() {
        mSwipeManager = null;
        mSwipingItem = null;
        mSwipeDistanceX = 0;
        mSwipeDistanceY = 0;
        mSwipingItemWidth = 0;
        mInvSwipingItemWidth = 0;
        mInvSwipingItemHeight = 0;
        mLeftSwipeReactionType = REACTION_CAN_NOT_SWIPE;
        mUpSwipeReactionType = REACTION_CAN_NOT_SWIPE;
        mRightSwipeReactionType = REACTION_CAN_NOT_SWIPE;
        mDownSwipeReactionType = REACTION_CAN_NOT_SWIPE;
        mPrevTranslateAmount = 0;
        mInitialTranslateAmountX = 0;
        mInitialTranslateAmountY = 0;
        mSwipingItemContainerView = null;
    }

    public void update(int itemPosition, int swipeDistanceX, int swipeDistanceY) {
        if ((mSwipeDistanceX == swipeDistanceX) && (mSwipeDistanceY == swipeDistanceY)) {
            return;
        }

        mSwipeDistanceX = swipeDistanceX;
        mSwipeDistanceY = swipeDistanceY;

        final int distance = (mSwipeHorizontal)
                ? (mSwipeDistanceX + mInitialTranslateAmountX)
                : (mSwipeDistanceY + mInitialTranslateAmountY);
        final int itemSize = (mSwipeHorizontal) ? mSwipingItemWidth  : mSwipingItemHeight;
        final float invItemSize = (mSwipeHorizontal) ? mInvSwipingItemWidth : mInvSwipingItemHeight;

        final int reactionType;

        if (mSwipeHorizontal) {
            reactionType = (distance > 0) ? mRightSwipeReactionType : mLeftSwipeReactionType;
        } else {
            reactionType = (distance > 0) ? mDownSwipeReactionType : mUpSwipeReactionType;
        }

        float translateAmount = 0;

        switch (reactionType) {
            case REACTION_CAN_NOT_SWIPE:
                break;
            case REACTION_CAN_NOT_SWIPE_WITH_RUBBER_BAND_EFFECT:
                float proportion = Math.min(Math.abs(distance), itemSize) * invItemSize;
                translateAmount = Math.signum(distance) * RUBBER_BAND_INTERPOLATOR.getInterpolation(proportion);
                break;
            case REACTION_CAN_SWIPE:
                translateAmount = Math.min(Math.max((distance * invItemSize), -1.0f), 1.0f);
                break;
        }

        mSwipeManager.applySlideItem(
                mSwipingItem, itemPosition,
                mPrevTranslateAmount, translateAmount, mSwipeHorizontal,
                false, true);

        mPrevTranslateAmount = translateAmount;
    }

    private static float calcInv(int value) {
        return (value != 0) ? (1.0f / value) : 0.0f;
    }

    private static int clip(int v, int min, int max) {
        return Math.min(Math.max(v, min), max);
    }
}