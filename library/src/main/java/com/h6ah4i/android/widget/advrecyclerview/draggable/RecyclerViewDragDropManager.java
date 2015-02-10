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

import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;

import com.h6ah4i.android.widget.advrecyclerview.utils.CustomRecyclerViewUtils;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import java.lang.ref.WeakReference;

@SuppressWarnings("PointlessBitwiseExpression")
public class RecyclerViewDragDropManager {
    private static final String TAG = "ARVDragDropManager";

    public static final int STATE_FLAG_DRAGGING = (1 << 0);
    public static final int STATE_FLAG_IS_ACTIVE = (1 << 1);
    public static final int STATE_FLAG_IS_UPDATED = (1 << 31);

    private static final int SCROLL_DIR_NONE = 0;
    private static final int SCROLL_DIR_UP = (1 << 0);
    private static final int SCROLL_DIR_DOWN = (1 << 1);


    private static final boolean LOCAL_LOGV = false;
    private static final boolean LOCAL_LOGD = false;

    private static final float SCROLL_THRESHOLD = 0.3f; // 0.0f < X < 0.5f
    private static final float SCROLL_AMOUNT_COEFF = 25;
    private static final float EDGE_EFFECT_THRESHOLD = 0.5f; // <= 1.0f
    private static final float SCROLL_TOUCH_SLOP_MULTIPLY = 1.5f;

    private RecyclerView mRecyclerView;
    private Interpolator mSwapTargetTranslationInterpolator;
    private ScrollOnDraggingProcessRunnable mScrollOnDraggingProcess;

    private RecyclerView.OnScrollListener mUserOnScrollListener;
    private RecyclerView.OnItemTouchListener mInternalUseOnItemTouchListener;
    private RecyclerView.OnScrollListener mInternalUseOnScrollListener;

    private EdgeEffectDecorator mEdgeEffectDecorator;
    private NinePatchDrawable mShadowDrawable;

    private float mDisplayDensity;
    private int mTouchSlop;
    private int mScrollTouchSlop;
    private int mInitialTouchY;
    private long mInitialTouchItemId = RecyclerView.NO_ID;

    private boolean mInScrollByMethod;
    private int mActualScrollByAmount;

    private Rect mTmpRect1 = new Rect();

    // these fields are only valid while dragging
    private DraggableItemWrapperAdapter mAdapter;
    private long mDraggingItemId = RecyclerView.NO_ID;
    private RecyclerView.ViewHolder mDraggingItem;
    private Rect mDraggingItemMargins = new Rect();
    private DraggingItemDecorator mDraggingItemDecorator;
    private SwapTargetItemOperator mSwapTargetItemOperator;
    private int mLastTouchY;
    private int mDragStartTouchY;
    private int mDragMinTouchY;
    private int mDragMaxTouchY;
    private int mScrollDirMask = SCROLL_DIR_NONE;
    private int mGrabbedPositionY;
    private int mGrabbedItemHeight;
    private int mOrigOverScrollMode;
    private Runnable mDeferredCancelProcess;

    public RecyclerViewDragDropManager() {
        mInternalUseOnItemTouchListener = new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return RecyclerViewDragDropManager.this.onInterceptTouchEvent(rv, e);
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                RecyclerViewDragDropManager.this.onTouchEvent(rv, e);
            }
        };

        mInternalUseOnScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                RecyclerViewDragDropManager.this.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                RecyclerViewDragDropManager.this.onScrolled(recyclerView, dx, dy);
            }
        };

        mScrollOnDraggingProcess = new ScrollOnDraggingProcessRunnable(this);
    }

    public RecyclerView.Adapter createWrappedAdapter(RecyclerView.Adapter adapter) {
        if (mAdapter != null) {
            throw new IllegalStateException("already have a wrapped adapter");
        }

        mAdapter = new DraggableItemWrapperAdapter(this, adapter);

        return mAdapter;
    }

    public boolean isReleased() {
        return (mInternalUseOnItemTouchListener == null);
    }

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

        if (mAdapter == null || getDraggableItemWrapperAdapter(rv) != mAdapter) {
            throw new IllegalStateException("adapter is not set properly");
        }

        mRecyclerView = rv;
        mRecyclerView.addOnItemTouchListener(mInternalUseOnItemTouchListener);
        mRecyclerView.setOnScrollListener(mInternalUseOnScrollListener);
        mDisplayDensity = mRecyclerView.getResources().getDisplayMetrics().density;
        mTouchSlop = ViewConfiguration.get(mRecyclerView.getContext()).getScaledTouchSlop();
        mScrollTouchSlop = (int) (mTouchSlop * SCROLL_TOUCH_SLOP_MULTIPLY + 0.5f);

        if (supportsEdgeEffect()) {
            // edge effect is available on ICS or later
            mEdgeEffectDecorator = new EdgeEffectDecorator(mRecyclerView);
            mEdgeEffectDecorator.start();
        }
    }

    public void release() {
        cancelDrag();

        if (mEdgeEffectDecorator != null) {
            mEdgeEffectDecorator.finish();
            mEdgeEffectDecorator = null;
        }

        if (mRecyclerView != null && mInternalUseOnItemTouchListener != null) {
            mRecyclerView.removeOnItemTouchListener(mInternalUseOnItemTouchListener);
        }
        mInternalUseOnItemTouchListener = null;

        if (mRecyclerView != null && mInternalUseOnScrollListener != null) {
            mRecyclerView.setOnScrollListener(null);
        }
        mInternalUseOnScrollListener = null;

        if (mScrollOnDraggingProcess != null) {
            mScrollOnDraggingProcess.release();
            mScrollOnDraggingProcess = null;
        }
        mUserOnScrollListener = null;
        mAdapter = null;
        mRecyclerView = null;
        mSwapTargetTranslationInterpolator = null;
    }

    public boolean isDragging() {
        return (mDraggingItem != null) && (mDeferredCancelProcess == null);
    }

    public void setDraggingItemShadowDrawable(NinePatchDrawable drawable) {
        mShadowDrawable = drawable;
    }

    public void setOnScrollListener(RecyclerView.OnScrollListener listener) {
        mUserOnScrollListener = listener;
    }

    public void setSwapTargetTranslationInterpolator(Interpolator interpolator) {
        mSwapTargetTranslationInterpolator = interpolator;
    }

    public Interpolator setSwapTargetTranslationInterpolator() {
        return mSwapTargetTranslationInterpolator;
    }

    /*package*/ boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        final int action = MotionEventCompat.getActionMasked(e);

        if (LOCAL_LOGV) {
            Log.v(TAG, "onInterceptTouchEvent() action = " + action);
        }

        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handleActionUpOrCancel(rv, e);
                break;

            case MotionEvent.ACTION_DOWN:
                if (!isDragging()) {
                    handleActionDown(rv, e);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (isDragging()) {
                    // NOTE: The first ACTION_MOVE event will come here. (maybe a bug of RecyclerView?)
                    handleActionMoveWhileDragging(rv, e);
                    return true;
                } else {
                    if (handleActionMoveWhileNotDragging(rv, e)) {
                        return true;
                    }
                }
        }

        return false;
    }

    /*package*/ void onTouchEvent(RecyclerView rv, MotionEvent e) {
        final int action = MotionEventCompat.getActionMasked(e);

        if (LOCAL_LOGV) {
            Log.v(TAG, "onTouchEvent() action = " + action);
        }

        if (!isDragging()) {
            // Log.w(TAG, "onTouchEvent() - unexpected state");
            return;
        }

        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handleActionUpOrCancel(rv, e);
                break;

            case MotionEvent.ACTION_MOVE:
                handleActionMoveWhileDragging(rv, e);
                break;

        }
    }

    /*package*/ void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (LOCAL_LOGV) {
            Log.v(TAG, "onScrolled(dx = " + dx + ", dy = " + dy + ")");
        }

        if (mInScrollByMethod) {
            mActualScrollByAmount = dy;
        }

        if (mUserOnScrollListener != null) {
            mUserOnScrollListener.onScrolled(recyclerView, dx, dy);
        }
    }

    /*package*/ void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (LOCAL_LOGV) {
            Log.v(TAG, "onScrollStateChanged(newState = " + newState + ")");
        }

        if (mUserOnScrollListener != null) {
            mUserOnScrollListener.onScrollStateChanged(recyclerView, newState);
        }
    }

    private boolean handleActionDown(RecyclerView rv, MotionEvent e) {
        final RecyclerView.ViewHolder holder = CustomRecyclerViewUtils.findChildViewHolderUnderWithoutTranslation(rv, e.getX(), e.getY());

        if (!checkTouchedItemState(rv, holder)) {
            return false;
        }

        mInitialTouchY = mLastTouchY = (int) (e.getY() + 0.5f);
        mInitialTouchItemId = holder.getItemId();

        return true;
    }

    private void startDragging(RecyclerView rv, MotionEvent e, RecyclerView.ViewHolder holder) {
        safeEndAnimation(rv, holder);

        mDraggingItem = holder;

        // XXX if setIsRecyclable() is used, another view holder objects will be created
        // which has the same ID with currently dragging item... Not works as expected.

        // mDraggingItem.setIsRecyclable(false);

        mDraggingItemId = mDraggingItem.getItemId();

        final View itemView = mDraggingItem.itemView;

        mOrigOverScrollMode = ViewCompat.getOverScrollMode(rv);
        ViewCompat.setOverScrollMode(rv, ViewCompat.OVER_SCROLL_NEVER);

        mLastTouchY = (int) (e.getY() + 0.5f);

        // disable auto scrolling until user moves the item
        mDragStartTouchY = mDragMinTouchY = mDragMaxTouchY = mLastTouchY;
        mScrollDirMask = SCROLL_DIR_NONE;

        // calculate the view-local offset from the touched point
        mGrabbedPositionY = mLastTouchY - itemView.getTop();

        mGrabbedItemHeight = itemView.getHeight();
        CustomRecyclerViewUtils.getLayoutMargins(itemView, mDraggingItemMargins);

        mRecyclerView.getParent().requestDisallowInterceptTouchEvent(true);

        startScrollOnDraggingProcess();

        // raise onDragItemStarted() event
        mAdapter.onDragItemStarted(mDraggingItem);

        // setup decorators
        mAdapter.onBindViewHolder(mDraggingItem, mDraggingItem.getPosition());

        mDraggingItemDecorator = new DraggingItemDecorator(mRecyclerView, mDraggingItem);
        mDraggingItemDecorator.setShadowDrawable(mShadowDrawable);
        mDraggingItemDecorator.start(e, mGrabbedPositionY);

        if (supportsViewTranslation()) {
            mSwapTargetItemOperator = new SwapTargetItemOperator(mRecyclerView, mDraggingItem);
            mSwapTargetItemOperator.setSwapTargetTranslationInterpolator(mSwapTargetTranslationInterpolator);
            mSwapTargetItemOperator.start();
            mSwapTargetItemOperator.update(mDraggingItemDecorator.getDraggingItemTranslationY());
        }

        if (mEdgeEffectDecorator != null) {
            mEdgeEffectDecorator.reorderToTop();
        }
    }

    public void cancelDrag() {
        cancelDrag(false);
    }

    private void cancelDrag(boolean immediately) {
        if (immediately) {
            finishDragging(false);
        } else {
            if (mDraggingItem != null) {
                if (mDeferredCancelProcess == null) {
                    mDeferredCancelProcess = new Runnable() {
                        @Override
                        public void run() {
                            if (mDeferredCancelProcess == this) {
                                mDeferredCancelProcess = null;
                                finishDragging(false);
                            }
                        }
                    };
                    ViewCompat.postOnAnimation(mRecyclerView, mDeferredCancelProcess);
                }
            }
        }
    }


    private void finishDragging(boolean result) {
        final RecyclerView.ViewHolder draggedItem = mDraggingItem;

        // NOTE: setOverScrollMode() have to be called before calling removeItemDecoration()
        if (mRecyclerView != null && mDraggingItem != null) {
            ViewCompat.setOverScrollMode(mRecyclerView, mOrigOverScrollMode);
        }

        if (mDraggingItemDecorator != null) {
            mDraggingItemDecorator.finish();
        }

        if (mSwapTargetItemOperator != null) {
            mSwapTargetItemOperator.finish();
        }

        if (mEdgeEffectDecorator != null) {
            mEdgeEffectDecorator.releaseBothGlows();
        }

        stopScrollOnDraggingProcess();

        if (mRecyclerView != null && mRecyclerView.getParent() != null) {
            mRecyclerView.getParent().requestDisallowInterceptTouchEvent(false);
        }

        mDraggingItemDecorator = null;
        mSwapTargetItemOperator = null;
        mDraggingItem = null;
        mDraggingItemId = RecyclerView.NO_ID;

        mLastTouchY = 0;
        mDragStartTouchY = 0;
        mDragMinTouchY = 0;
        mDragMaxTouchY = 0;
        mGrabbedPositionY = 0;
        mGrabbedItemHeight = 0;

        // raise onDragItemFinished() event
        if ((mAdapter != null) && (draggedItem != null)) {
            mAdapter.onDragItemFinished(draggedItem, result);
        }

//        if (draggedItem != null) {
//            draggedItem.setIsRecyclable(true);
//        }
    }

    private boolean handleActionUpOrCancel(RecyclerView rv, MotionEvent e) {
        final boolean result = (MotionEventCompat.getActionMasked(e) == MotionEvent.ACTION_UP);

        mInitialTouchY = 0;
        mLastTouchY = 0;
        mDragStartTouchY = 0;
        mDragMinTouchY = 0;
        mDragMaxTouchY = 0;
        mInitialTouchItemId = RecyclerView.NO_ID;

        if (isDragging()) {
            if (LOCAL_LOGD) {
                Log.d(TAG, "dragging finished  --- result = " + result);
            }

            finishDragging(result);
        }

        return true;
    }

    private boolean handleActionMoveWhileNotDragging(RecyclerView rv, MotionEvent e) {
        final int touchX = (int) (e.getX() + 0.5f);
        final int touchY = (int) (e.getY() + 0.5f);

        mLastTouchY = touchY;

        if (mInitialTouchItemId == RecyclerView.NO_ID) {
            return false;
        }

        if (!(Math.abs(touchY - mInitialTouchY) > mTouchSlop)) {
            return false;
        }

        final RecyclerView.ViewHolder holder = CustomRecyclerViewUtils.findChildViewHolderUnderWithoutTranslation(rv, e.getX(), e.getY());

        if (!checkTouchedItemState(rv, holder)) {
            mInitialTouchItemId = RecyclerView.NO_ID;
            return false;
        }

        if (holder.getItemId() != mInitialTouchItemId) {
            mInitialTouchItemId = RecyclerView.NO_ID;
            return false;
        }

        final View view = holder.itemView;
        final int translateX = (int) (ViewCompat.getTranslationX(view) + 0.5f);
        final int translateY = (int) (ViewCompat.getTranslationY(view) + 0.5f);
        final int viewX = touchX - (view.getLeft() + translateX);
        final int viewY = touchY - (view.getTop() + translateY);

        if (!mAdapter.canStartDrag(holder, viewX, viewY)) {
            return false;
        }

        if (LOCAL_LOGD) {
            Log.d(TAG, "dragging started");
        }

        startDragging(rv, e, holder);

        return true;

    }

    private void handleActionMoveWhileDragging(RecyclerView rv, MotionEvent e) {
        mLastTouchY = (int) (e.getY() + 0.5f);
        mDragMinTouchY = Math.min(mDragMinTouchY, mLastTouchY);
        mDragMaxTouchY = Math.max(mDragMaxTouchY, mLastTouchY);

        // update drag direction mask
        if (((mDragStartTouchY - mDragMinTouchY) > mScrollTouchSlop) ||
            ((mDragMaxTouchY - mLastTouchY) > mScrollTouchSlop)) {
            mScrollDirMask |= SCROLL_DIR_UP;
        }
        if (((mDragMaxTouchY - mDragStartTouchY) > mScrollTouchSlop) ||
            ((mLastTouchY - mDragMinTouchY) > mScrollTouchSlop)) {
            mScrollDirMask |= SCROLL_DIR_DOWN;
        }

        // update decorators
        mDraggingItemDecorator.update(e);
        if (mSwapTargetItemOperator != null) {
            mSwapTargetItemOperator.update(mDraggingItemDecorator.getDraggingItemTranslationY());
        }

        // check swapping
        checkItemSwapping(rv);
    }

    private void checkItemSwapping(RecyclerView rv) {
        final RecyclerView.ViewHolder draggingItem = mDraggingItem;

        final int overlayItemTop = mLastTouchY - mGrabbedPositionY;
        final RecyclerView.ViewHolder swapTargetHolder = findSwapTargetItem(rv, draggingItem, mDraggingItemId, overlayItemTop);

        if ((swapTargetHolder != null) && (swapTargetHolder != mDraggingItem)) {
            swapItems(rv, draggingItem, swapTargetHolder);
        }
    }

    /*package*/ void handleScrollOnDragging() {
        final RecyclerView rv = mRecyclerView;
        final int height = rv.getHeight();

        if (height == 0) {
            return;
        }

        final float y = mLastTouchY * (1.0f / height);
        final float threshold = SCROLL_THRESHOLD;
        final float invThreshold = (1.0f / threshold);
        final float centerOffset = y - 0.5f;
        final float absCenterOffset = Math.abs(centerOffset);
        final float acceleration = Math.max(0.0f, threshold - (0.5f - absCenterOffset)) * invThreshold;
        int scrollAmount = (int) Math.signum(centerOffset) * (int) (SCROLL_AMOUNT_COEFF * mDisplayDensity * acceleration + 0.5f);
        int actualScrolledAmount = 0;

        // apply mask
        if (scrollAmount > 0) {
            if ((mScrollDirMask & SCROLL_DIR_DOWN) == 0) {
                scrollAmount = 0;
            }
        } else if (scrollAmount < 0) {
            if ((mScrollDirMask & SCROLL_DIR_UP) == 0) {
                scrollAmount = 0;
            }
        }

        mDraggingItemDecorator.setIsScrolling(scrollAmount != 0);

        if (scrollAmount != 0) {
            safeEndAnimations(rv);
            actualScrolledAmount = scrollByYAndGetScrolledAmount(scrollAmount);

            mDraggingItemDecorator.refresh();
            if (mSwapTargetItemOperator != null) {
                mSwapTargetItemOperator.update(mDraggingItemDecorator.getDraggingItemTranslationY());
            }
        }

        if (mEdgeEffectDecorator != null) {
            if ((acceleration >= EDGE_EFFECT_THRESHOLD) && (scrollAmount != 0) && (actualScrolledAmount == 0)) {
                // over scrolled
                final float distance = acceleration * 0.025f;

                if (scrollAmount < 0) {
                    // upward
                    mEdgeEffectDecorator.pullTopGlow(distance);
                } else {
                    // downward
                    mEdgeEffectDecorator.pullBottom(distance);
                }
            } else {
                mEdgeEffectDecorator.releaseBothGlows();
            }
        }

        checkItemSwapping(rv);
    }

    private int scrollByYAndGetScrolledAmount(int ry) {
        // NOTE: mActualScrollByAmount --- Hackish! To detect over scrolling.

        mActualScrollByAmount = 0;
        mInScrollByMethod = true;
        mRecyclerView.scrollBy(0, ry);
        mInScrollByMethod = false;

        return mActualScrollByAmount;
    }

    /*package*/ RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    private void startScrollOnDraggingProcess() {
        mScrollOnDraggingProcess.start();
    }

    private void stopScrollOnDraggingProcess() {
        mScrollOnDraggingProcess.stop();
    }

    private void swapItems(RecyclerView rv, RecyclerView.ViewHolder draggingItem, RecyclerView.ViewHolder swapTargetHolder) {
        final Rect swapTargetMargins = CustomRecyclerViewUtils.getLayoutMargins(swapTargetHolder.itemView, mTmpRect1);
        final int fromPosition = draggingItem.getPosition();
        final int toPosition = swapTargetHolder.getPosition();
        final int diffPosition = Math.abs(fromPosition - toPosition);
        boolean performSwapping = false;

        if (fromPosition == RecyclerView.NO_POSITION || toPosition == RecyclerView.NO_POSITION) {
            return;
        }

        final long actualDraggingItemId = rv.getAdapter().getItemId(fromPosition);
        if (actualDraggingItemId != mDraggingItemId) {
            if (LOCAL_LOGV) {
                Log.v(TAG, "RecyclerView state has not been synched to data yet");
            }
            return;
        }

        if (diffPosition == 0) {
        } else if (diffPosition == 1) {
            final View v1 = draggingItem.itemView;
            final View v2 = swapTargetHolder.itemView;
            final Rect m1 = mDraggingItemMargins;
            final Rect m2 = swapTargetMargins;

            final int top = Math.min(v1.getTop() - m1.top, v2.getTop() - m2.top);
            final int bottom = Math.max(v1.getBottom() + m1.bottom, v2.getBottom() + m2.bottom);

            final float midPointOfTheItems = top + ((bottom - top) * 0.5f);
            final float midPointOfTheOverlaidItem = (mLastTouchY - mGrabbedPositionY) + (mGrabbedItemHeight * 0.5f);

            if (toPosition < fromPosition) {
                if (midPointOfTheOverlaidItem < midPointOfTheItems) {
                    // swap (up direction)
                    performSwapping = true;
                }
            } else { // if (toPosition > fromPosition)
                if (midPointOfTheOverlaidItem > midPointOfTheItems) {
                    // swap (down direction)
                    performSwapping = true;
                }
            }
        } else { // diffPosition > 1
            performSwapping = true;
        }

        if (performSwapping) {
            if (LOCAL_LOGD) {
                Log.d(TAG, "item swap (from: " + fromPosition + ", to: " + toPosition + ")");
            }

            RecyclerView.ViewHolder firstVisibleTopItem = null;

            if (rv.getChildCount() > 0) {
                View child = rv.getChildAt(0);
                if (child != null) {
                    firstVisibleTopItem = rv.getChildViewHolder(child);
                }
            }
            final int prevTopItemPosition = (firstVisibleTopItem != null) ? firstVisibleTopItem.getPosition() : RecyclerView.NO_POSITION;


            mAdapter.moveItem(fromPosition, toPosition);

            if (fromPosition == prevTopItemPosition) {
                final Rect margins = swapTargetMargins;
                final int curTopItemHeight = swapTargetHolder.itemView.getHeight() + margins.top + margins.bottom;
                scrollByYAndGetScrolledAmount(-curTopItemHeight);
            } else if (toPosition == prevTopItemPosition) {
                final Rect margins = mDraggingItemMargins;
                final int curTopItemHeight = mGrabbedItemHeight + margins.top + margins.bottom;
                scrollByYAndGetScrolledAmount(-curTopItemHeight);
            }

            safeEndAnimations(rv);
        }
    }

    private static DraggableItemWrapperAdapter getDraggableItemWrapperAdapter(RecyclerView rv) {
        return WrapperAdapterUtils.findWrappedAdapter(rv.getAdapter(), DraggableItemWrapperAdapter.class);
    }

    private boolean checkTouchedItemState(RecyclerView rv, RecyclerView.ViewHolder holder) {
        if (!(holder instanceof DraggableItemViewHolder)) {
            return false;
        }

        final int itemPosition = holder.getPosition();
        final RecyclerView.Adapter adapter = rv.getAdapter();

        // verify the touched item is valid state
        if (!(itemPosition >= 0 && itemPosition < adapter.getItemCount())) {
            return false;
        }

        //noinspection RedundantIfStatement
        if (holder.getItemId() != adapter.getItemId(itemPosition)) {
            return false;
        }

        return true;
    }

    private static boolean supportsEdgeEffect() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    private static boolean supportsViewTranslation() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    private static void safeEndAnimation(RecyclerView rv, RecyclerView.ViewHolder holder) {
        final RecyclerView.ItemAnimator itemAnimator = (rv != null) ? rv.getItemAnimator() : null;
        if (itemAnimator != null) {
            itemAnimator.endAnimation(holder);
        }
    }

    private static void safeEndAnimations(RecyclerView rv) {
        final RecyclerView.ItemAnimator itemAnimator = (rv != null) ? rv.getItemAnimator() : null;
        if (itemAnimator != null) {
            itemAnimator.endAnimations();
        }
    }

    /*package*/
    static RecyclerView.ViewHolder findSwapTargetItem(
            RecyclerView rv, RecyclerView.ViewHolder draggingItem,
            long draggingItemId, int overlayItemTop) {
        final int draggingItemPosition = draggingItem.getPosition();
        final int draggingViewTop = draggingItem.itemView.getTop();
        RecyclerView.ViewHolder swapTargetHolder = null;

        // determine the swap target view
        if (draggingItemPosition != RecyclerView.NO_POSITION &&
                draggingItem.getItemId() == draggingItemId) {
            if (overlayItemTop < draggingViewTop) {
                if (draggingItemPosition > 0) {
                    swapTargetHolder = rv.findViewHolderForPosition(draggingItemPosition - 1);
                }
            } else if (overlayItemTop > draggingViewTop) {
                if (draggingItemPosition < (rv.getAdapter().getItemCount() - 1)) {
                    swapTargetHolder = rv.findViewHolderForPosition(draggingItemPosition + 1);
                }
            }
        }

        return swapTargetHolder;
    }

    private static class ScrollOnDraggingProcessRunnable implements Runnable {
        private final WeakReference<RecyclerViewDragDropManager> mHolderRef;
        private boolean mStarted;

        public ScrollOnDraggingProcessRunnable(RecyclerViewDragDropManager holder) {
            mHolderRef = new WeakReference<>(holder);
        }

        public void start() {
            if (mStarted) {
                return;
            }

            final RecyclerViewDragDropManager holder = mHolderRef.get();

            if (holder == null) {
                return;
            }

            final RecyclerView rv = holder.getRecyclerView();

            if (rv == null) {
                return;
            }

            ViewCompat.postOnAnimation(rv, this);

            mStarted = true;
        }

        public void stop() {
            if (!mStarted) {
                return;
            }

            mStarted = false;
        }

        public void release() {
            mHolderRef.clear();
            mStarted = false;
        }

        @Override
        public void run() {
            final RecyclerViewDragDropManager holder = mHolderRef.get();

            if (holder == null) {
                return;
            }

            if (!mStarted) {
                return;
            }

            // call scrolling process
            holder.handleScrollOnDragging();

            // re-schedule the process
            final RecyclerView rv = holder.getRecyclerView();

            if (rv != null && mStarted) {
                ViewCompat.postOnAnimation(rv, this);
            } else {
                mStarted = false;
            }
        }
    }
}
