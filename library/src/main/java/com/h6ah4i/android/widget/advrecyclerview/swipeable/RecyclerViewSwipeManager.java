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

import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.utils.CustomRecyclerViewUtils;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

/**
 * Provides item swipe operation for {@link android.support.v7.widget.RecyclerView}
 */
@SuppressWarnings("PointlessBitwiseExpression")
public class RecyclerViewSwipeManager {
    private static final String TAG = "ARVSwipeManager";

    static final int BIT_SHIFT_AMOUNT_LEFT = 0;
    static final int BIT_SHIFT_AMOUNT_UP = 8;
    static final int BIT_SHIFT_AMOUNT_RIGHT = 16;
    static final int BIT_SHIFT_AMOUNT_DOWN = 24;

    /**
     * Result code of swipe operation. Used for the third argument of the
     * {@link SwipeableItemAdapter#onSwipeItem(RecyclerView.ViewHolder, int, int)} method.
     * <p/>
     * None. (internal default value, this value is not used for the argument)
     */
    public static final int RESULT_NONE = 0;

    /**
     * Result code of swipe operation. Used for the third argument of the
     * {@link SwipeableItemAdapter#onSwipeItem(RecyclerView.ViewHolder, int, int)} method.
     * <p/>
     * Canceled.
     */
    public static final int RESULT_CANCELED = 1;

    /**
     * Result code of swipe operation. Used for the third argument of the
     * {@link SwipeableItemAdapter#onSwipeItem(RecyclerView.ViewHolder, int, int)} method.
     * <p/>
     * Swipe left performed.
     */
    public static final int RESULT_SWIPED_LEFT = 2;

    /**
     * Result code of swipe operation. Used for the third argument of the
     * {@link SwipeableItemAdapter#onSwipeItem(RecyclerView.ViewHolder, int, int)} method.
     * <p/>
     * Swipe up performed.
     */
    public static final int RESULT_SWIPED_UP = 3;

    /**
     * Result code of swipe operation. Used for the third argument of the
     * {@link SwipeableItemAdapter#onSwipeItem(RecyclerView.ViewHolder, int, int)} method.
     * <p/>
     * Swipe right performed.
     */
    public static final int RESULT_SWIPED_RIGHT = 4;

    /**
     * Result code of swipe operation. Used for the third argument of the
     * {@link SwipeableItemAdapter#onSwipeItem(RecyclerView.ViewHolder, int, int)} method.
     * <p/>
     * Swipe down performed.
     */
    public static final int RESULT_SWIPED_DOWN = 5;

    // ---
    /**
     * Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.
     * <p/>
     * Indicates "can not swipe any direction"
     */
    public static final int REACTION_CAN_NOT_SWIPE_ANY = 0;

    /**
     * Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.
     * <p/>
     * Indicates "can not swipe left" (completely no reactions)
     */
    public static final int REACTION_CAN_NOT_SWIPE_LEFT = (0 << BIT_SHIFT_AMOUNT_LEFT);

    /**
     * Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.
     * <p/>
     * Indicates "can not swipe left"  (not swipeable, but rubber-band effect applied)
     */
    public static final int REACTION_CAN_NOT_SWIPE_LEFT_WITH_RUBBER_BAND_EFFECT = (1 << BIT_SHIFT_AMOUNT_LEFT);

    /**
     * Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.
     * <p/>
     * Indicates "can swipe left"
     */
    public static final int REACTION_CAN_SWIPE_LEFT = (2 << BIT_SHIFT_AMOUNT_LEFT);

    /**
     * Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.
     * <p/>
     * If this flag is specified, swipe operation will not start even if user swipe an item to the LEFT direction.
     * <p/>
     */
    public static final int REACTION_FLAG_MASK_START_SWIPE_LEFT = (128 << BIT_SHIFT_AMOUNT_LEFT);

    /**
     * Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.
     * <p/>
     * Indicates "can not swipe up" (completely no reactions)
     */
    public static final int REACTION_CAN_NOT_SWIPE_UP = (0 << BIT_SHIFT_AMOUNT_UP);

    /**
     * Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.
     * <p/>
     * Indicates "can not swipe up"  (not swipeable, but rubber-band effect applied)
     */
    public static final int REACTION_CAN_NOT_SWIPE_UP_WITH_RUBBER_BAND_EFFECT = (1 << BIT_SHIFT_AMOUNT_UP);

    /**
     * Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.
     * <p/>
     * Indicates "can swipe up"
     */
    public static final int REACTION_CAN_SWIPE_UP = (2 << BIT_SHIFT_AMOUNT_UP);

    /**
     * Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.
     * <p/>
     * If this flag is specified, swipe operation will not start even if user swipe an item to the UP direction.
     * <p/>
     */
    public static final int REACTION_FLAG_MASK_START_SWIPE_UP = (128 << BIT_SHIFT_AMOUNT_UP);

    /**
     * Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.
     * <p/>
     * Indicates "can not swipe right" (completely no reactions)
     */
    public static final int REACTION_CAN_NOT_SWIPE_RIGHT = (0 << BIT_SHIFT_AMOUNT_RIGHT);

    /**
     * Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.
     * <p/>
     * Indicates "can not swipe right"  (not swipeable, but rubber-band effect applied)
     */
    public static final int REACTION_CAN_NOT_SWIPE_RIGHT_WITH_RUBBER_BAND_EFFECT = (1 << BIT_SHIFT_AMOUNT_RIGHT);

    /**
     * Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.
     * <p/>
     * Indicates "can swipe right"
     */
    public static final int REACTION_CAN_SWIPE_RIGHT = (2 << BIT_SHIFT_AMOUNT_RIGHT);

    /**
     * Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.
     * <p/>
     * If this flag is specified, swipe operation will not start even if user swipe an item to the RIGHT direction.
     * <p/>
     */
    public static final int REACTION_FLAG_MASK_START_SWIPE_RIGHT = (128 << BIT_SHIFT_AMOUNT_RIGHT);

    /**
     * Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.
     * <p/>
     * Indicates "can not swipe down" (completely no reactions)
     */
    public static final int REACTION_CAN_NOT_SWIPE_DOWN = (0 << BIT_SHIFT_AMOUNT_DOWN);

    /**
     * Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.
     * <p/>
     * Indicates "can not swipe down"  (not swipeable, but rubber-band effect applied)
     */
    public static final int REACTION_CAN_NOT_SWIPE_DOWN_WITH_RUBBER_BAND_EFFECT = (1 << BIT_SHIFT_AMOUNT_DOWN);

    /**
     * Reaction type to swipe operation. Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.
     * <p/>
     * Indicates "can swipe down"
     */
    public static final int REACTION_CAN_SWIPE_DOWN = (2 << BIT_SHIFT_AMOUNT_DOWN);

    /**
     * Used for the return value of the
     * {@link SwipeableItemAdapter#onGetSwipeReactionType(android.support.v7.widget.RecyclerView.ViewHolder, int, int, int)} method.
     * <p/>
     * If this flag is specified, swipe operation will not start even if user swipe an item to the left direction.
     * <p/>
     */
    public static final int REACTION_FLAG_MASK_START_SWIPE_DOWN = (128 << BIT_SHIFT_AMOUNT_DOWN);

    // ---

    /**
     * Convenient constant value: Equals to {@link #REACTION_CAN_NOT_SWIPE_LEFT} | {@link #REACTION_CAN_NOT_SWIPE_RIGHT}
     */
    public static final int REACTION_CAN_NOT_SWIPE_BOTH_H =
            REACTION_CAN_NOT_SWIPE_LEFT | REACTION_CAN_NOT_SWIPE_RIGHT;

    /**
     * Convenient constant value: Equals to {@link #REACTION_CAN_NOT_SWIPE_LEFT_WITH_RUBBER_BAND_EFFECT} | {@link #REACTION_CAN_NOT_SWIPE_RIGHT_WITH_RUBBER_BAND_EFFECT}
     */
    public static final int REACTION_CAN_NOT_SWIPE_BOTH_H_WITH_RUBBER_BAND_EFFECT =
            REACTION_CAN_NOT_SWIPE_LEFT_WITH_RUBBER_BAND_EFFECT |
                    REACTION_CAN_NOT_SWIPE_RIGHT_WITH_RUBBER_BAND_EFFECT;

    /**
     * Convenient constant value: Equals to {@link #REACTION_CAN_SWIPE_LEFT} | {@link #REACTION_CAN_SWIPE_RIGHT}
     */
    public static final int REACTION_CAN_SWIPE_BOTH_H =
            REACTION_CAN_SWIPE_LEFT | REACTION_CAN_SWIPE_RIGHT;

    /**
     * Convenient constant value: Equals to {@link #REACTION_CAN_NOT_SWIPE_UP} | {@link #REACTION_CAN_NOT_SWIPE_DOWN}
     */
    public static final int REACTION_CAN_NOT_SWIPE_BOTH_V =
            REACTION_CAN_NOT_SWIPE_UP | REACTION_CAN_NOT_SWIPE_DOWN;

    /**
     * Convenient constant value: Equals to {@link #REACTION_CAN_NOT_SWIPE_UP_WITH_RUBBER_BAND_EFFECT} | {@link #REACTION_CAN_NOT_SWIPE_DOWN_WITH_RUBBER_BAND_EFFECT}
     */
    public static final int REACTION_CAN_NOT_SWIPE_BOTH_V_WITH_RUBBER_BAND_EFFECT =
            REACTION_CAN_NOT_SWIPE_UP_WITH_RUBBER_BAND_EFFECT |
                    REACTION_CAN_NOT_SWIPE_DOWN_WITH_RUBBER_BAND_EFFECT;

    /**
     * Convenient constant value: Equals to {@link #REACTION_CAN_SWIPE_UP} | {@link #REACTION_CAN_SWIPE_DOWN}
     */
    public static final int REACTION_CAN_SWIPE_BOTH_V =
            REACTION_CAN_SWIPE_UP | REACTION_CAN_SWIPE_DOWN;

    // ---

    /**
     * @deprecated Use {@link #REACTION_CAN_NOT_SWIPE_ANY} directly.
     */
    public static final int REACTION_CAN_NOT_SWIPE_BOTH = REACTION_CAN_NOT_SWIPE_ANY;

    /**
     * @deprecated Use {@link #REACTION_CAN_NOT_SWIPE_BOTH_H_WITH_RUBBER_BAND_EFFECT} directly.
     */
    public static final int REACTION_CAN_NOT_SWIPE_BOTH_WITH_RUBBER_BAND_EFFECT = REACTION_CAN_NOT_SWIPE_BOTH_H_WITH_RUBBER_BAND_EFFECT;

    /**
     * @deprecated Use {@link #REACTION_CAN_SWIPE_BOTH_H} directly.
     */
    public static final int REACTION_CAN_SWIPE_BOTH = REACTION_CAN_SWIPE_BOTH_H;

    // ---

    /**
     * Background drawable type used for the second argument of the
     * {@link SwipeableItemAdapter#onSetSwipeBackground(android.support.v7.widget.RecyclerView.ViewHolder, int, int)} method.
     * <p/>
     * Background image for the neutral (= not swiping) item.
     */
    public static final int DRAWABLE_SWIPE_NEUTRAL_BACKGROUND = 0;

    /**
     * Background drawable type used for the second argument of the
     * {@link SwipeableItemAdapter#onSetSwipeBackground(android.support.v7.widget.RecyclerView.ViewHolder, int, int)} method.
     * <p/>
     * Background image for the swiping-left item.
     */
    public static final int DRAWABLE_SWIPE_LEFT_BACKGROUND = 1;

    /**
     * Background drawable type used for the second argument of the
     * {@link SwipeableItemAdapter#onSetSwipeBackground(android.support.v7.widget.RecyclerView.ViewHolder, int, int)} method.
     * <p/>
     * Background image for the swiping-up item.
     */
    public static final int DRAWABLE_SWIPE_UP_BACKGROUND = 2;

    /**
     * Background drawable type used for the second argument of the
     * {@link SwipeableItemAdapter#onSetSwipeBackground(android.support.v7.widget.RecyclerView.ViewHolder, int, int)} method.
     * <p/>
     * Background image for the swiping-right item.
     */
    public static final int DRAWABLE_SWIPE_RIGHT_BACKGROUND = 3;

    /**
     * Background drawable type used for the second argument of the
     * {@link SwipeableItemAdapter#onSetSwipeBackground(android.support.v7.widget.RecyclerView.ViewHolder, int, int)} method.
     * <p/>
     * Background image for the swiping-down item.
     */
    public static final int DRAWABLE_SWIPE_DOWN_BACKGROUND = 4;

    // ---

    /**
     * After-reaction type used for the {@link SwipeableItemViewHolder#setAfterSwipeReaction(int)} and {@link SwipeableItemViewHolder#getAfterSwipeReaction()} methods.
     * Represents perform nothing.
     */
    public static final int AFTER_SWIPE_REACTION_DEFAULT = 0;

    /**
     * After-reaction type used for the {@link SwipeableItemViewHolder#setAfterSwipeReaction(int)} and {@link SwipeableItemViewHolder#getAfterSwipeReaction()} methods.
     * Represents remove the swiped item.
     */
    public static final int AFTER_SWIPE_REACTION_REMOVE_ITEM = 1;

    /**
     * After-reaction type used for the {@link SwipeableItemViewHolder#setAfterSwipeReaction(int)} and {@link SwipeableItemViewHolder#getAfterSwipeReaction()} methods.
     * Represents that the item moved to swiped direction.
     */
    public static final int AFTER_SWIPE_REACTION_MOVE_TO_SWIPED_DIRECTION = 2;

    // ---
    /**
     * Special value for the {@link SwipeableItemViewHolder#setSwipeItemHorizontalSlideAmount(float)}
     * and {@link SwipeableItemViewHolder#getSwipeItemHorizontalSlideAmount()} methods.
     * Indicates that this item is pinned to LEFT of the window.
     */
    public static final float OUTSIDE_OF_THE_WINDOW_LEFT = -((1 << 16) + 0);

    /**
     * Special value for the {@link SwipeableItemViewHolder#setSwipeItemVerticalSlideAmount(float)}
     * and {@link SwipeableItemViewHolder#getSwipeItemVerticalSlideAmount()} methods.
     * Indicates that this item is pinned to UP of the window.
     */
    public static final float OUTSIDE_OF_THE_WINDOW_TOP = -((1 << 16) + 1);

    /**
     * Special value for the {@link SwipeableItemViewHolder#setSwipeItemHorizontalSlideAmount(float)}
     * and {@link SwipeableItemViewHolder#getSwipeItemHorizontalSlideAmount()} methods.
     * Indicates that this item is pinned to RIGHT the window.
     */
    public static final float OUTSIDE_OF_THE_WINDOW_RIGHT = ((1 << 16) + 0);

    /**
     * Special value for the {@link SwipeableItemViewHolder#setSwipeItemVerticalSlideAmount(float)}
     * and {@link SwipeableItemViewHolder#getSwipeItemVerticalSlideAmount()} methods.
     * Indicates that this item is pinned to DOWN the window.
     */
    public static final float OUTSIDE_OF_THE_WINDOW_BOTTOM = ((1 << 16) + 1);


    // ---
    /**
     * State flag for the {@link SwipeableItemViewHolder#setSwipeStateFlags(int)} and {@link SwipeableItemViewHolder#getSwipeStateFlags()} methods.
     * Indicates that currently performing swiping.
     */
    public static final int STATE_FLAG_SWIPING = (1 << 0);

    /**
     * State flag for the {@link SwipeableItemViewHolder#setSwipeStateFlags(int)} and {@link SwipeableItemViewHolder#getSwipeStateFlags()} methods.
     * Indicates that this item is being swiped.
     */
    public static final int STATE_FLAG_IS_ACTIVE = (1 << 1);

    /**
     * State flag for the {@link SwipeableItemViewHolder#setSwipeStateFlags(int)} and {@link SwipeableItemViewHolder#getSwipeStateFlags()} methods.
     * If this flag is set, some other flags are changed and require to apply.
     */
    public static final int STATE_FLAG_IS_UPDATED = (1 << 31);

    // ---

    /**
     * Used for listening item swipe events
     */
    public interface OnItemSwipeEventListener {
        /**
         * Callback method to be invoked when swiping is started.
         *
         * @param position The position of the item.
         */
        void onItemSwipeStarted(int position);

        /**
         * Callback method to be invoked when swiping is finished.
         *
         * @param position           The position of the item.
         * @param result             The result code of the swipe operation.
         * @param afterSwipeReaction The reaction type to the swipe operation.
         */
        void onItemSwipeFinished(int position, int result, int afterSwipeReaction);
    }

    // ---

    private static final int MIN_DISTANCE_TOUCH_SLOP_MUL = 10;
    private static final int SLIDE_ITEM_IMMEDIATELY_SET_TRANSLATION_THRESHOLD_DP = 8;

    private static final boolean LOCAL_LOGV = false;
    private static final boolean LOCAL_LOGD = false;

    private RecyclerView.OnItemTouchListener mInternalUseOnItemTouchListener;
    private RecyclerView mRecyclerView;

    private long mReturnToDefaultPositionAnimationDuration = 300;
    private long mMoveToOutsideWindowAnimationDuration = 200;

    private int mTouchSlop;
    private int mMinFlingVelocity; // [pixels per second]
    private int mMaxFlingVelocity; // [pixels per second]
    private int mInitialTouchX;
    private int mInitialTouchY;
    private long mCheckingTouchSlop = RecyclerView.NO_ID;
    private boolean mSwipeHorizontal;

    private ItemSlidingAnimator mItemSlideAnimator;
    private SwipeableItemWrapperAdapter<RecyclerView.ViewHolder> mAdapter;
    private RecyclerView.ViewHolder mSwipingItem;
    private int mSwipingItemPosition = RecyclerView.NO_POSITION;
    private Rect mSwipingItemMargins = new Rect();
    private int mTouchedItemOffsetX;
    private int mTouchedItemOffsetY;
    private int mLastTouchX;
    private int mLastTouchY;
    private int mSwipingItemReactionType;
    private VelocityTracker mVelocityTracker;
    private SwipingItemOperator mSwipingItemOperator;
    private OnItemSwipeEventListener mItemSwipeEventListener;

    /**
     * Constructor.
     */
    public RecyclerViewSwipeManager() {
        mInternalUseOnItemTouchListener = new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return RecyclerViewSwipeManager.this.onInterceptTouchEvent(rv, e);
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                RecyclerViewSwipeManager.this.onTouchEvent(rv, e);
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        };
        mVelocityTracker = VelocityTracker.obtain();
    }

    /**
     * Create wrapped adapter.
     *
     * @param adapter The target adapter.
     * @return Wrapped adapter which is associated to this {@link RecyclerViewSwipeManager} instance.
     */
    @SuppressWarnings("unchecked")
    public RecyclerView.Adapter createWrappedAdapter(RecyclerView.Adapter adapter) {
        if (mAdapter != null) {
            throw new IllegalStateException("already have a wrapped adapter");
        }

        mAdapter = new SwipeableItemWrapperAdapter(this, adapter);

        return mAdapter;
    }

    /**
     * Indicates this manager instance has released or not.
     *
     * @return True if this manager instance has released
     */
    public boolean isReleased() {
        return (mInternalUseOnItemTouchListener == null);
    }

    /**
     * Attaches {@link android.support.v7.widget.RecyclerView} instance.
     * <p/>
     * Before calling this method, the target {@link android.support.v7.widget.RecyclerView} must set
     * the wrapped adapter instance which is returned by the
     * {@link #createWrappedAdapter(android.support.v7.widget.RecyclerView.Adapter)} method.
     *
     * @param rv The {@link android.support.v7.widget.RecyclerView} instance
     */
    public void attachRecyclerView(RecyclerView rv) {
        if (rv == null) {
            throw new IllegalArgumentException("RecyclerView cannot be null");
        }

        if (isReleased()) {
            throw new IllegalStateException("Accessing released object");
        }

        if (mRecyclerView != null) {
            throw new IllegalStateException("RecyclerView instance has already been set");
        }

        if (mAdapter == null || getSwipeableItemWrapperAdapter(rv) != mAdapter) {
            throw new IllegalStateException("adapter is not set properly");
        }

        mRecyclerView = rv;
        mRecyclerView.addOnItemTouchListener(mInternalUseOnItemTouchListener);

        final ViewConfiguration vc = ViewConfiguration.get(rv.getContext());

        mTouchSlop = vc.getScaledTouchSlop();
        mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
        mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();

        mItemSlideAnimator = new ItemSlidingAnimator(mAdapter);
        mItemSlideAnimator.setImmediatelySetTranslationThreshold(
                (int) (rv.getResources().getDisplayMetrics().density * SLIDE_ITEM_IMMEDIATELY_SET_TRANSLATION_THRESHOLD_DP + 0.5f));
    }

    /**
     * Detach the {@link android.support.v7.widget.RecyclerView} instance and release internal field references.
     * <p/>
     * This method should be called in order to avoid memory leaks.
     */
    public void release() {
        if (mRecyclerView != null && mInternalUseOnItemTouchListener != null) {
            mRecyclerView.removeOnItemTouchListener(mInternalUseOnItemTouchListener);
        }
        mInternalUseOnItemTouchListener = null;

        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }

        if (mItemSlideAnimator != null) {
            mItemSlideAnimator.endAnimations();
            mItemSlideAnimator = null;
        }

        mAdapter = null;
        mRecyclerView = null;
    }

    /**
     * Indicates whether currently performing item swiping.
     *
     * @return True if currently performing item swiping
     */
    public boolean isSwiping() {
        return (mSwipingItem != null);
    }

    /*package*/ boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        final int action = MotionEventCompat.getActionMasked(e);

        if (LOCAL_LOGV) {
            Log.v(TAG, "onInterceptTouchEvent() action = " + action);
        }

        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isSwiping()) {
                    handleActionUpOrCancelWhileSwiping(e);
                    return true;
                } else {
                    handleActionUpOrCancelWhileNotSwiping();
                }
                break;

            case MotionEvent.ACTION_DOWN:
                if (!isSwiping()) {
                    handleActionDown(rv, e);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (isSwiping()) {
                    // NOTE: The first ACTION_MOVE event will come here. (maybe a bug of RecyclerView?)
                    handleActionMoveWhileSwiping(e);
                    return true;
                } else {
                    if (handleActionMoveWhileNotSwiping(rv, e)) {
                        return true;
                    }
                }
                break;
        }

        return false;
    }

    /*package*/ void onTouchEvent(RecyclerView rv, MotionEvent e) {
        final int action = MotionEventCompat.getActionMasked(e);

        if (LOCAL_LOGV) {
            Log.v(TAG, "onTouchEvent() action = " + action);
        }

        if (!isSwiping()) {
            // Log.w(TAG, "onTouchEvent() - unexpected state");
            return;
        }

        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handleActionUpOrCancelWhileSwiping(e);
                break;

            case MotionEvent.ACTION_MOVE:
                handleActionMoveWhileSwiping(e);
                break;
        }
    }

    private boolean handleActionDown(RecyclerView rv, MotionEvent e) {
        final RecyclerView.Adapter adapter = rv.getAdapter();
        final RecyclerView.ViewHolder holder = CustomRecyclerViewUtils.findChildViewHolderUnderWithTranslation(rv, e.getX(), e.getY());

        if (!(holder instanceof SwipeableItemViewHolder)) {
            return false;
        }

        final int itemPosition = CustomRecyclerViewUtils.getSynchronizedPosition(holder);

        // verify the touched item is valid state
        if (!(itemPosition >= 0 && itemPosition < adapter.getItemCount())) {
            return false;
        }
        if (holder.getItemId() != adapter.getItemId(itemPosition)) {
            return false;
        }

        final int touchX = (int) (e.getX() + 0.5f);
        final int touchY = (int) (e.getY() + 0.5f);

        final View view = holder.itemView;
        final int translateX = (int) (ViewCompat.getTranslationX(view) + 0.5f);
        final int translateY = (int) (ViewCompat.getTranslationY(view) + 0.5f);
        final int viewX = touchX - (view.getLeft() + translateX);
        final int viewY = touchY - (view.getTop() + translateY);

        final int reactionType = mAdapter.getSwipeReactionType(holder, itemPosition, viewX, viewY);

        if (reactionType == 0) {
            return false;
        }

        final int layoutOrientation = CustomRecyclerViewUtils.getOrientation(mRecyclerView);
        if (layoutOrientation == CustomRecyclerViewUtils.ORIENTATION_UNKNOWN) {
            return false;
        }

        mInitialTouchX = touchX;
        mInitialTouchY = touchY;
        mCheckingTouchSlop = holder.getItemId();
        mSwipingItemReactionType = reactionType;
        mSwipeHorizontal = (layoutOrientation == CustomRecyclerViewUtils.ORIENTATION_VERTICAL);

        return true;
    }

    private static SwipeableItemWrapperAdapter getSwipeableItemWrapperAdapter(RecyclerView rv) {
        return WrapperAdapterUtils.findWrappedAdapter(rv.getAdapter(), SwipeableItemWrapperAdapter.class);
    }

    private void handleActionUpOrCancelWhileNotSwiping() {
        mCheckingTouchSlop = RecyclerView.NO_ID;
        mSwipingItemReactionType = 0;
    }

    private boolean handleActionUpOrCancelWhileSwiping(MotionEvent e) {
        mLastTouchX = (int) (e.getX() + 0.5f);
        mLastTouchY = (int) (e.getY() + 0.5f);

        int result = RESULT_CANCELED;

        if (MotionEventCompat.getActionMasked(e) == MotionEvent.ACTION_UP) {
            final boolean horizontal = mSwipeHorizontal;
            final View itemView = mSwipingItem.itemView;
            final int viewSize = (horizontal) ? itemView.getWidth() : itemView.getHeight();
            final float distance = (horizontal) ? (mLastTouchX - mInitialTouchX) : (mLastTouchY - mInitialTouchY);
            final float absDistance = Math.abs(distance);

            mVelocityTracker.computeCurrentVelocity(1000, mMaxFlingVelocity); // 1000: pixels per second

            final float velocity = (horizontal) ? mVelocityTracker.getXVelocity() : mVelocityTracker.getYVelocity();
            final float absVelocity = Math.abs(velocity);

            if ((absDistance > (mTouchSlop * MIN_DISTANCE_TOUCH_SLOP_MUL)) &&
                    ((distance * velocity) > 0.0f) &&
                    (absVelocity <= mMaxFlingVelocity) &&
                    ((absDistance > (viewSize / 2)) || (absVelocity >= mMinFlingVelocity))) {

                if (horizontal && (distance < 0) && SwipeReactionUtils.canSwipeLeft(mSwipingItemReactionType)) {
                    result = RESULT_SWIPED_LEFT;
                } else if ((!horizontal) && (distance < 0) && SwipeReactionUtils.canSwipeUp(mSwipingItemReactionType)) {
                    result = RESULT_SWIPED_UP;
                } else if (horizontal && (distance > 0) && SwipeReactionUtils.canSwipeRight(mSwipingItemReactionType)) {
                    result = RESULT_SWIPED_RIGHT;
                } else if ((!horizontal) && (distance > 0) && SwipeReactionUtils.canSwipeDown(mSwipingItemReactionType)) {
                    result = RESULT_SWIPED_DOWN;
                }
            }
        }

        if (LOCAL_LOGD) {
            Log.d(TAG, "swping finished  --- result = " + result);
        }

        finishSwiping(result);

        return true;
    }

    private boolean handleActionMoveWhileNotSwiping(RecyclerView rv, MotionEvent e) {
        if (mCheckingTouchSlop == RecyclerView.NO_ID) {
            return false;
        }

        final int dx = (int) (e.getX() + 0.5f) - mInitialTouchX;
        final int dy = (int) (e.getY() + 0.5f) - mInitialTouchY;

        final int scrollAxisDelta;
        final int swipeAxisDelta;

        if (mSwipeHorizontal) {
            scrollAxisDelta = dy;
            swipeAxisDelta = dx;
        } else {
            scrollAxisDelta = dx;
            swipeAxisDelta = dy;
        }

        if (Math.abs(scrollAxisDelta) > mTouchSlop) {
            // scrolling occurred
            mCheckingTouchSlop = RecyclerView.NO_ID;
            return false;
        }

        if (Math.abs(swipeAxisDelta) <= mTouchSlop) {
            return false;
        }

        // check swipeable direction mask
        boolean dirMasked;
        if (mSwipeHorizontal) {
            if (swipeAxisDelta < 0) {
                dirMasked = ((mSwipingItemReactionType & REACTION_FLAG_MASK_START_SWIPE_LEFT) != 0);
            } else {
                dirMasked = ((mSwipingItemReactionType & REACTION_FLAG_MASK_START_SWIPE_RIGHT) != 0);
            }
        } else {
            if (swipeAxisDelta < 0) {
                dirMasked = ((mSwipingItemReactionType & REACTION_FLAG_MASK_START_SWIPE_UP) != 0);
            } else {
                dirMasked = ((mSwipingItemReactionType & REACTION_FLAG_MASK_START_SWIPE_DOWN) != 0);
            }
        }

        if (dirMasked) {
            // masked
            mCheckingTouchSlop = RecyclerView.NO_ID;
            return false;
        }

        final RecyclerView.ViewHolder holder = CustomRecyclerViewUtils.findChildViewHolderUnderWithTranslation(rv, e.getX(), e.getY());

        if (holder == null || holder.getItemId() != mCheckingTouchSlop) {
            mCheckingTouchSlop = RecyclerView.NO_ID;
            return false;
        }

        final int itemPosition = CustomRecyclerViewUtils.getSynchronizedPosition(holder);

        if (itemPosition == RecyclerView.NO_POSITION) {
            return false;
        }

        if (LOCAL_LOGD) {
            Log.d(TAG, "swiping started");
        }

        startSwiping(rv, e, holder, itemPosition);

        return true;
    }

    private void handleActionMoveWhileSwiping(MotionEvent e) {
        mLastTouchX = (int) (e.getX() + 0.5f);
        mLastTouchY = (int) (e.getY() + 0.5f);
        mVelocityTracker.addMovement(e);

        final int swipeDistanceX = mLastTouchX - mTouchedItemOffsetX;
        final int swipeDistanceY = mLastTouchY - mTouchedItemOffsetY;

        mSwipingItemOperator.update(swipeDistanceX, swipeDistanceY);
    }

    private void startSwiping(RecyclerView rv, MotionEvent e, RecyclerView.ViewHolder holder, int itemPosition) {
        mSwipingItem = holder;
        mSwipingItemPosition = itemPosition;
        mLastTouchX = (int) (e.getX() + 0.5f);
        mLastTouchY = (int) (e.getY() + 0.5f);
        mTouchedItemOffsetX = mLastTouchX;
        mTouchedItemOffsetY = mLastTouchY;
        CustomRecyclerViewUtils.getLayoutMargins(holder.itemView, mSwipingItemMargins);

        mSwipingItemOperator = new SwipingItemOperator(this, mSwipingItem, mSwipingItemPosition, mSwipingItemReactionType, mSwipeHorizontal);
        mSwipingItemOperator.start();

        mVelocityTracker.clear();
        mVelocityTracker.addMovement(e);

        mRecyclerView.getParent().requestDisallowInterceptTouchEvent(true);

        // raise onItemSwipeStarted() event
        if (mItemSwipeEventListener != null) {
            mItemSwipeEventListener.onItemSwipeStarted(itemPosition);
        }

        // raise onSwipeItemStarted() event
        mAdapter.onSwipeItemStarted(this, holder, itemPosition);
    }

    private void finishSwiping(int result) {
        final RecyclerView.ViewHolder swipingItem = mSwipingItem;

        if (swipingItem == null) {
            return;
        }

        if (mRecyclerView != null && mRecyclerView.getParent() != null) {
            mRecyclerView.getParent().requestDisallowInterceptTouchEvent(false);
        }

        final int itemPosition = mSwipingItemPosition;

        mVelocityTracker.clear();

        mSwipingItem = null;
        mSwipingItemPosition = RecyclerView.NO_POSITION;
        mLastTouchX = 0;
        mLastTouchY = 0;
        mInitialTouchX = 0;
        mTouchedItemOffsetX = 0;
        mTouchedItemOffsetY = 0;
        mCheckingTouchSlop = RecyclerView.NO_ID;
        mSwipingItemReactionType = 0;

        if (mSwipingItemOperator != null) {
            mSwipingItemOperator.finish();
            mSwipingItemOperator = null;
        }

        final int slideDir = resultCodeToSlideDirection(result);
        SwipeResultAction resultAction = null;

        if (mAdapter != null) {
            resultAction = mAdapter.onSwipeItemFinished(swipingItem, itemPosition, result);
        }

        if (resultAction == null) {
            resultAction = null; // TODO set default action
        }

        int afterReaction = resultAction.getResultActionType();

        verifyAfterReaction(result, afterReaction);

        boolean slideAnimated = false;

        switch (afterReaction) {
            case AFTER_SWIPE_REACTION_DEFAULT:
                slideAnimated = mItemSlideAnimator.finishSwipeSlideToDefaultPosition(
                        swipingItem, mSwipeHorizontal, true, mReturnToDefaultPositionAnimationDuration,
                        itemPosition, resultAction);
                break;
            case AFTER_SWIPE_REACTION_MOVE_TO_SWIPED_DIRECTION:
                slideAnimated = mItemSlideAnimator.finishSwipeSlideToOutsideOfWindow(
                        swipingItem, slideDir, true, mMoveToOutsideWindowAnimationDuration,
                        itemPosition, resultAction);
                break;
            case AFTER_SWIPE_REACTION_REMOVE_ITEM: {
                final RecyclerView.ItemAnimator itemAnimator = mRecyclerView.getItemAnimator();

                final long removeAnimationDuration = (itemAnimator != null) ? itemAnimator.getRemoveDuration() : 0;

                if (supportsViewPropertyAnimator()) {
                    final long moveAnimationDuration = (itemAnimator != null) ? itemAnimator.getMoveDuration() : 0;

                    final RemovingItemDecorator decorator = new RemovingItemDecorator(
                            mRecyclerView, swipingItem, removeAnimationDuration, moveAnimationDuration);

                    decorator.setMoveAnimationInterpolator(SwipeDismissItemAnimator.MOVE_INTERPOLATOR);
                    decorator.start();
                }

                slideAnimated = mItemSlideAnimator.finishSwipeSlideToOutsideOfWindow(
                        swipingItem, slideDir, true, removeAnimationDuration,
                        itemPosition, resultAction);
            }
            break;
            default:
                throw new IllegalStateException("Unknown after reaction type: " + afterReaction);
        }

        if (mAdapter != null) {
            mAdapter.onSwipeItemFinished2(swipingItem, itemPosition, result, afterReaction, resultAction);
        }

        // raise onItemSwipeFinished() event
        if (mItemSwipeEventListener != null) {
            mItemSwipeEventListener.onItemSwipeFinished(itemPosition, result, afterReaction);
        }

        // invoke onSwipeSlideItemAnimationEnd
        if (!slideAnimated) {
            resultAction.slideAnimationEnd();
        }
    }

    private static void verifyAfterReaction(int result, int afterReaction) {
        if ((afterReaction == AFTER_SWIPE_REACTION_MOVE_TO_SWIPED_DIRECTION) ||
                (afterReaction == AFTER_SWIPE_REACTION_REMOVE_ITEM)) {
            switch (result) {
                case RESULT_SWIPED_LEFT:
                case RESULT_SWIPED_UP:
                case RESULT_SWIPED_RIGHT:
                case RESULT_SWIPED_DOWN:
                    break;
                default:
                    throw new IllegalStateException("Unexpected after reaction has been requested: result = " + result +", afterReaction = " + afterReaction);
            }
        }
    }

    private static int resultCodeToSlideDirection(int result) {
        switch (result) {
            case RESULT_SWIPED_LEFT:
                return ItemSlidingAnimator.DIR_LEFT;
            case RESULT_SWIPED_UP:
                return ItemSlidingAnimator.DIR_UP;
            case RESULT_SWIPED_RIGHT:
                return ItemSlidingAnimator.DIR_RIGHT;
            case RESULT_SWIPED_DOWN:
                return ItemSlidingAnimator.DIR_DOWN;
            default:
                // NOTE: returned value should not be used.
                return ItemSlidingAnimator.DIR_LEFT;
        }
    }

    public void cancelSwipe() {
        finishSwiping(RESULT_CANCELED);
    }

    /*package*/ boolean isAnimationRunning(RecyclerView.ViewHolder item) {
        return (mItemSlideAnimator != null) && (mItemSlideAnimator.isRunning(item));
    }

    private void slideItem(RecyclerView.ViewHolder holder, float amount, boolean horizontal, boolean shouldAnimate) {
        if (amount == OUTSIDE_OF_THE_WINDOW_LEFT) {
            mItemSlideAnimator.slideToOutsideOfWindow(holder, ItemSlidingAnimator.DIR_LEFT, shouldAnimate, mMoveToOutsideWindowAnimationDuration);
        } else if (amount == OUTSIDE_OF_THE_WINDOW_TOP) {
            mItemSlideAnimator.slideToOutsideOfWindow(holder, ItemSlidingAnimator.DIR_UP, shouldAnimate, mMoveToOutsideWindowAnimationDuration);
        } else if (amount == OUTSIDE_OF_THE_WINDOW_RIGHT) {
            mItemSlideAnimator.slideToOutsideOfWindow(holder, ItemSlidingAnimator.DIR_RIGHT, shouldAnimate, mMoveToOutsideWindowAnimationDuration);
        } else if (amount == OUTSIDE_OF_THE_WINDOW_BOTTOM) {
            mItemSlideAnimator.slideToOutsideOfWindow(holder, ItemSlidingAnimator.DIR_DOWN, shouldAnimate, mMoveToOutsideWindowAnimationDuration);
        } else if (amount == 0.0f) {
            mItemSlideAnimator.slideToDefaultPosition(holder, horizontal, shouldAnimate, mReturnToDefaultPositionAnimationDuration);
        } else {
            mItemSlideAnimator.slideToSpecifiedPosition(holder, amount, horizontal);
        }
    }

    /**
     * Gets the duration of the "return to default position" animation
     *
     * @return Duration of the "return to default position" animation in milliseconds
     */
    public long getReturnToDefaultPositionAnimationDuration() {
        return mReturnToDefaultPositionAnimationDuration;
    }

    /**
     * Sets the duration of the "return to default position" animation
     *
     * @param duration Duration of the "return to default position" animation in milliseconds
     */
    public void setReturnToDefaultPositionAnimationDuration(long duration) {
        mReturnToDefaultPositionAnimationDuration = duration;
    }

    /**
     * Gets the duration of the "move to outside of the window" animation
     *
     * @return Duration of the "move to outside of the window" animation in milliseconds
     */
    public long getMoveToOutsideWindowAnimationDuration() {
        return mMoveToOutsideWindowAnimationDuration;
    }

    /**
     * Sets the duration of the "move to outside of the window" animation
     *
     * @param duration Duration of the "move to outside of the window" animation in milliseconds
     */
    public void setMoveToOutsideWindowAnimationDuration(long duration) {
        mMoveToOutsideWindowAnimationDuration = duration;
    }

    /**
     * Gets OnItemSwipeEventListener listener
     *
     * @return The listener object
     */
    public OnItemSwipeEventListener getOnItemSwipeEventListener() {
        return mItemSwipeEventListener;
    }

    /**
     * Sets OnItemSwipeEventListener listener
     *
     * @param listener The listener object
     */
    public void setOnItemSwipeEventListener(OnItemSwipeEventListener listener) {
        mItemSwipeEventListener = listener;
    }

    /*package*/ boolean swipeHorizontal() {
        return mSwipeHorizontal;
    }

    /*package*/ void applySlideItem(
            RecyclerView.ViewHolder holder, int itemPosition,
            float prevAmount, float amount, boolean horizontal, boolean shouldAnimate, boolean isSwiping) {
        final SwipeableItemViewHolder holder2 = (SwipeableItemViewHolder) holder;
        final View containerView = holder2.getSwipeableContainerView();

        if (containerView == null) {
            return;
        }

        final int reqBackgroundType;

        if (amount == 0.0f) {
            if (prevAmount == 0.0f) {
                reqBackgroundType = DRAWABLE_SWIPE_NEUTRAL_BACKGROUND;
            } else {
                reqBackgroundType = determineBackgroundType(prevAmount, horizontal);
            }
        } else {
            reqBackgroundType = determineBackgroundType(amount, horizontal);
        }

        if (amount == 0.0f) {
            slideItem(holder, amount, horizontal, shouldAnimate);
            mAdapter.onUpdateSlideAmount(holder, itemPosition, horizontal, amount, isSwiping, reqBackgroundType);
        } else {
            float adjustedAmount = amount;
            float minLimit = horizontal ? holder2.getMaxLeftSwipeAmount() : holder2.getMaxUpSwipeAmount();
            float maxLimit = horizontal ? holder2.getMaxRightSwipeAmount() : holder2.getMaxDownSwipeAmount();

            adjustedAmount = Math.max(adjustedAmount, minLimit);
            adjustedAmount = Math.min(adjustedAmount, maxLimit);

            mAdapter.onUpdateSlideAmount(holder, itemPosition, horizontal, amount, isSwiping, reqBackgroundType);
            slideItem(holder, adjustedAmount, horizontal, shouldAnimate);
        }
    }

    private static int determineBackgroundType(float amount, boolean horizontal) {
        if (horizontal) {
            return (amount < 0)
                    ? DRAWABLE_SWIPE_LEFT_BACKGROUND
                    : DRAWABLE_SWIPE_RIGHT_BACKGROUND;
        } else {
            return (amount < 0)
                    ? DRAWABLE_SWIPE_UP_BACKGROUND
                    : DRAWABLE_SWIPE_DOWN_BACKGROUND;
        }
    }

    /*package*/ void cancelPendingAnimations(RecyclerView.ViewHolder holder) {
        if (mItemSlideAnimator != null) {
            mItemSlideAnimator.endAnimation(holder);
        }
    }

    /*package*/ int getSwipeContainerViewTranslationX(RecyclerView.ViewHolder holder) {
        return mItemSlideAnimator.getSwipeContainerViewTranslationX(holder);
    }

    /*package*/ int getSwipeContainerViewTranslationY(RecyclerView.ViewHolder holder) {
        return mItemSlideAnimator.getSwipeContainerViewTranslationY(holder);
    }

    private static boolean supportsViewPropertyAnimator() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }
}
