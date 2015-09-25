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
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.h6ah4i.android.widget.advrecyclerview.event.RecyclerViewOnScrollEventDistributor;
import com.h6ah4i.android.widget.advrecyclerview.utils.CustomRecyclerViewUtils;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import java.lang.ref.WeakReference;

/**
 * Provides item drag &amp; drop operation for {@link android.support.v7.widget.RecyclerView}
 */
@SuppressWarnings("PointlessBitwiseExpression")
public class RecyclerViewDragDropManager {
    private static final String TAG = "ARVDragDropManager";

    /**
     * State flag for the {@link DraggableItemViewHolder#setDragStateFlags(int)} and {@link DraggableItemViewHolder#getDragStateFlags()} methods.
     * Indicates that currently performing dragging.
     */
    public static final int STATE_FLAG_DRAGGING = (1 << 0);

    /**
     * State flag for the {@link DraggableItemViewHolder#setDragStateFlags(int)} and {@link DraggableItemViewHolder#getDragStateFlags()} methods.
     * Indicates that this item is being dragged.
     */
    public static final int STATE_FLAG_IS_ACTIVE = (1 << 1);

    /**
     * State flag for the {@link DraggableItemViewHolder#setDragStateFlags(int)} and {@link DraggableItemViewHolder#getDragStateFlags()} methods.
     * Indicates that this item is in the range of drag-sortable items
     */
    public static final int STATE_FLAG_IS_IN_RANGE = (1 << 2);

    /**
     * State flag for the {@link DraggableItemViewHolder#setDragStateFlags(int)} and {@link DraggableItemViewHolder#getDragStateFlags()} methods.
     * If this flag is set, some other flags are changed and require to apply.
     */
    public static final int STATE_FLAG_IS_UPDATED = (1 << 31);

    // ---

    /**
     * Default interpolator used for "swap target transition"
     */
    public static final Interpolator DEFAULT_SWAP_TARGET_TRANSITION_INTERPOLATOR = new BasicSwapTargetTranslationInterpolator();


    /**
     * Default interpolator used for "item settle back into place" animation
     */
    public static final Interpolator DEFAULT_ITEM_SETTLE_BACK_INTO_PLACE_ANIMATION_INTERPOLATOR = new DecelerateInterpolator();

    // ---

    /**
     * Used for listening item drag events
     */
    public interface OnItemDragEventListener {
        /**
         * Callback method to be invoked when dragging is started.
         *
         * @param position The position of the item.
         */
        void onItemDragStarted(int position);

	    /**
	     * Callback method to be invoked when item position is changed during drag.
	     *
	     * @param fromPosition The old position of the item.
	     * @param toPosition The new position of the item
	     */
	    void onItemDragPositionChanged(int fromPosition, int toPosition);

        /**
         * Callback method to be invoked when dragging is finished.
         *
         * @param fromPosition Previous position of the item.
         * @param toPosition   New position of the item.
         * @param result       Indicates whether the dragging operation was succeeded.
         */
        void onItemDragFinished(int fromPosition, int toPosition, boolean result);
    }

    // --

    private static final int SCROLL_DIR_NONE = 0;
    private static final int SCROLL_DIR_UP = (1 << 0);
    private static final int SCROLL_DIR_DOWN = (1 << 1);
    private static final int SCROLL_DIR_LEFT = (1 << 2);
    private static final int SCROLL_DIR_RIGHT = (1 << 3);

    private static final boolean LOCAL_LOGV = false;
    private static final boolean LOCAL_LOGD = false;

    private static final float SCROLL_THRESHOLD = 0.3f; // 0.0f < X < 0.5f
    private static final float SCROLL_AMOUNT_COEFF = 25;
    private static final float SCROLL_TOUCH_SLOP_MULTIPLY = 1.5f;

    private RecyclerView mRecyclerView;
    private Interpolator mSwapTargetTranslationInterpolator = DEFAULT_SWAP_TARGET_TRANSITION_INTERPOLATOR;
    private ScrollOnDraggingProcessRunnable mScrollOnDraggingProcess;
    private boolean mScrollEventRegisteredToDistributor;

    private RecyclerView.OnItemTouchListener mInternalUseOnItemTouchListener;
    private RecyclerView.OnScrollListener mInternalUseOnScrollListener;

    private BaseEdgeEffectDecorator mEdgeEffectDecorator;
    private NinePatchDrawable mShadowDrawable;

    private float mDisplayDensity;
    private int mTouchSlop;
    private int mScrollTouchSlop;
    private int mInitialTouchX;
    private int mInitialTouchY;
    private long mInitialTouchItemId = RecyclerView.NO_ID;
    private boolean mInitiateOnLongPress;
    private boolean mInitiateOnMove = true;

    private boolean mInScrollByMethod;
    private int mActualScrollByXAmount;
    private int mActualScrollByYAmount;
    private Rect mTmpRect1 = new Rect();
    private Runnable mDeferredCancelProcess;
    private int mItemSettleBackIntoPlaceAnimationDuration = 200;
    private Interpolator mItemSettleBackIntoPlaceAnimationInterpolator = DEFAULT_ITEM_SETTLE_BACK_INTO_PLACE_ANIMATION_INTERPOLATOR;

    // these fields are only valid while dragging
    private DraggableItemWrapperAdapter mAdapter;
    private long mDraggingItemId = RecyclerView.NO_ID;
    private RecyclerView.ViewHolder mDraggingItem;
    private Rect mDraggingItemMargins = new Rect();
    private DraggingItemDecorator mDraggingItemDecorator;
    private SwapTargetItemOperator mSwapTargetItemOperator;
    private int mLastTouchX;
    private int mLastTouchY;
    private int mDragStartTouchX;
    private int mDragStartTouchY;
    private int mDragMinTouchX;
    private int mDragMinTouchY;
    private int mDragMaxTouchX;
    private int mDragMaxTouchY;
    private int mScrollDirMask = SCROLL_DIR_NONE;
    private int mGrabbedPositionX;
    private int mGrabbedPositionY;
    private int mGrabbedItemWidth;
    private int mGrabbedItemHeight;
    private int mOrigOverScrollMode;
    private ItemDraggableRange mDraggableRange;
    private InternalHandler mHandler;
    private OnItemDragEventListener mItemDragEventListener;
    private boolean mCanDragH;
    private boolean mCanDragV;
    private float mDragEdgeScrollSpeed = 1.0f;

    /**
     * Constructor.
     */
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

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                RecyclerViewDragDropManager.this.onRequestDisallowInterceptTouchEvent(disallowIntercept);
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

    /**
     * Create wrapped adapter.
     *
     * @param adapter The target adapter.
     * @return Wrapped adapter which is associated to this {@link RecyclerViewDragDropManager} instance.
     */
    @SuppressWarnings("unchecked")
    public RecyclerView.Adapter createWrappedAdapter(RecyclerView.Adapter adapter) {
        if (mAdapter != null) {
            throw new IllegalStateException("already have a wrapped adapter");
        }

        mAdapter = new DraggableItemWrapperAdapter(this, adapter);

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
        //noinspection deprecation
        attachRecyclerView(rv, null);
    }

    /**
     * Attaches {@link android.support.v7.widget.RecyclerView} instance.
     * <p/>
     * Before calling this method, the target {@link android.support.v7.widget.RecyclerView} must set
     * the wrapped adapter instance which is returned by the
     * {@link #createWrappedAdapter(android.support.v7.widget.RecyclerView.Adapter)} method.
     *
     * @param rv                     The {@link android.support.v7.widget.RecyclerView} instance
     * @param scrollEventDistributor The distributor for {@link android.support.v7.widget.RecyclerView.OnScrollListener} event
     */
    @Deprecated
    public void attachRecyclerView(RecyclerView rv, @SuppressWarnings("deprecation") RecyclerViewOnScrollEventDistributor scrollEventDistributor) {
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

        if (scrollEventDistributor != null) {
            final RecyclerView rv2 = scrollEventDistributor.getRecyclerView();

            if (rv2 != null && rv2 != rv) {
                throw new IllegalArgumentException("The scroll event distributor attached to different RecyclerView instance");
            }
        }

        mRecyclerView = rv;

        if (scrollEventDistributor != null) {
            scrollEventDistributor.add(mInternalUseOnScrollListener);
            mScrollEventRegisteredToDistributor = true;
        } else {
            mRecyclerView.addOnScrollListener(mInternalUseOnScrollListener);
            mScrollEventRegisteredToDistributor = false;
        }

        mRecyclerView.addOnItemTouchListener(mInternalUseOnItemTouchListener);

        mDisplayDensity = mRecyclerView.getResources().getDisplayMetrics().density;
        mTouchSlop = ViewConfiguration.get(mRecyclerView.getContext()).getScaledTouchSlop();
        mScrollTouchSlop = (int) (mTouchSlop * SCROLL_TOUCH_SLOP_MULTIPLY + 0.5f);
        mHandler = new InternalHandler(this);

        if (supportsEdgeEffect()) {
            // edge effect is available on ICS or later
            switch (CustomRecyclerViewUtils.getOrientation(mRecyclerView)) {
                case CustomRecyclerViewUtils.ORIENTATION_HORIZONTAL:
                    mEdgeEffectDecorator = new LeftRightEdgeEffectDecorator(mRecyclerView);
                    break;
                case CustomRecyclerViewUtils.ORIENTATION_VERTICAL:
                    mEdgeEffectDecorator = new TopBottomEdgeEffectDecorator(mRecyclerView);
                    break;
            }
            if (mEdgeEffectDecorator != null) {
                mEdgeEffectDecorator.start();
            }
        }
    }

    /**
     * Detach the {@link android.support.v7.widget.RecyclerView} instance and release internal field references.
     * <p/>
     * This method should be called in order to avoid memory leaks.
     */
    public void release() {
        cancelDrag();

        if (mHandler != null) {
            mHandler.release();
            mHandler = null;
        }

        if (mEdgeEffectDecorator != null) {
            mEdgeEffectDecorator.finish();
            mEdgeEffectDecorator = null;
        }

        if (mRecyclerView != null && mInternalUseOnItemTouchListener != null) {
            mRecyclerView.removeOnItemTouchListener(mInternalUseOnItemTouchListener);
        }
        mInternalUseOnItemTouchListener = null;

        if (mRecyclerView != null && mInternalUseOnScrollListener != null && mScrollEventRegisteredToDistributor) {
            mRecyclerView.removeOnScrollListener(mInternalUseOnScrollListener);
        }
        mInternalUseOnScrollListener = null;

        if (mScrollOnDraggingProcess != null) {
            mScrollOnDraggingProcess.release();
            mScrollOnDraggingProcess = null;
        }
        mAdapter = null;
        mRecyclerView = null;
        mSwapTargetTranslationInterpolator = null;
        mScrollEventRegisteredToDistributor = false;
    }

    /**
     * Indicates whether currently performing item dragging.
     *
     * @return True if currently performing item dragging
     */
    public boolean isDragging() {
        return (mDraggingItemId != RecyclerView.NO_ID) && (mDeferredCancelProcess == null);
    }

    /**
     * Sets 9-patch image which is used for the actively dragging item
     *
     * @param drawable The 9-patch drawable
     */
    public void setDraggingItemShadowDrawable(NinePatchDrawable drawable) {
        mShadowDrawable = drawable;
    }

    /**
     * Sets the interpolator which is used for determining the position of the swapping item.
     *
     * @param interpolator Interpolator to set or null to clear
     */
    public void setSwapTargetTranslationInterpolator(Interpolator interpolator) {
        mSwapTargetTranslationInterpolator = interpolator;
    }

    /**
     * Returns whether dragging starts on a long press or not.
     *
     * @return True if dragging starts on a long press, false otherwise.
     */
    public boolean isInitiateOnLongPressEnabled() {
        return mInitiateOnLongPress;
    }

    /**
     * Sets whether dragging starts on a long press. (default: false)
     *
     * @param initiateOnLongPress True to initiate dragging on long press.
     */
    public void setInitiateOnLongPress(boolean initiateOnLongPress) {
        mInitiateOnLongPress = initiateOnLongPress;
    }

    /**
     * Returns whether dragging starts on move motions.
     *
     * @return True if dragging starts on move motions, false otherwise.
     */
    public boolean isInitiateOnMoveEnabled() {
        return mInitiateOnMove;
    }

    /**
     * Sets whether dragging starts on move motions. (default: true)
     *
     * @param initiateOnMove True to initiate dragging on move motions.
     */
    public void setInitiateOnMove(boolean initiateOnMove) {
        mInitiateOnMove = initiateOnMove;
    }

    /**
     * Gets the interpolator which ise used for determining the position of the swapping item.
     *
     * @return Interpolator which is used for determining the position of the swapping item
     */
    public Interpolator setSwapTargetTranslationInterpolator() {
        return mSwapTargetTranslationInterpolator;
    }

    /**
     * Gets OnItemDragEventListener listener
     *
     * @return The listener object
     */
    public OnItemDragEventListener getOnItemDragEventListener() {
        return mItemDragEventListener;
    }

    /**
     * Sets OnItemDragEventListener listener
     *
     * @param listener The listener object
     */
    public void setOnItemDragEventListener(OnItemDragEventListener listener) {
        mItemDragEventListener = listener;
    }

    /**
     * Sets drag edge scroll speed.
     *
     * @param speed The coefficient value of drag edge scrolling speed. (valid range: 0.0f .. 2.0)
     */
    public void setDragEdgeScrollSpeed(float speed) {
        mDragEdgeScrollSpeed = Math.min(Math.max(speed, 0.0f), 2.0f);
    }

    /**
     * Gets drag edge scroll speed.
     *
     * @return The coefficient value of drag edges scrolling speed.
     */
    public float getDragEdgeScrollSpeed() {
        return mDragEdgeScrollSpeed;
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

    /*package */ void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (disallowIntercept) {
            cancelDrag(true);
        }
    }


    /*package*/ void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (LOCAL_LOGV) {
            Log.v(TAG, "onScrolled(dx = " + dx + ", dy = " + dy + ")");
        }

        if (mInScrollByMethod) {
            mActualScrollByXAmount = dx;
            mActualScrollByYAmount = dy;
        }
    }

    /*package*/ void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (LOCAL_LOGV) {
            Log.v(TAG, "onScrollStateChanged(newState = " + newState + ")");
        }
    }

    private boolean handleActionDown(RecyclerView rv, MotionEvent e) {

        final RecyclerView.ViewHolder holder = CustomRecyclerViewUtils.findChildViewHolderUnderWithoutTranslation(rv, e.getX(), e.getY());

        if (!checkTouchedItemState(rv, holder)) {
            return false;
        }

        final int orientation = CustomRecyclerViewUtils.getOrientation(mRecyclerView);
        final int spanCount = CustomRecyclerViewUtils.getSpanCount(mRecyclerView);

        mInitialTouchX = mLastTouchX = (int) (e.getX() + 0.5f);
        mInitialTouchY = mLastTouchY = (int) (e.getY() + 0.5f);
        mInitialTouchItemId = holder.getItemId();
        mCanDragH = (orientation == CustomRecyclerViewUtils.ORIENTATION_HORIZONTAL) ||
                ((orientation == CustomRecyclerViewUtils.ORIENTATION_VERTICAL) && (spanCount > 1));
        mCanDragV = (orientation == CustomRecyclerViewUtils.ORIENTATION_VERTICAL) ||
                ((orientation == CustomRecyclerViewUtils.ORIENTATION_HORIZONTAL) && (spanCount > 1));


        if (mInitiateOnLongPress) {
            mHandler.startLongPressDetection(e);
        }

        return true;
    }

    private void handleOnLongPress(MotionEvent e) {
        if (mInitiateOnLongPress) {
            checkConditionAndStartDragging(mRecyclerView, e, false);
        }
    }

    @SuppressWarnings("unchecked")
    private void startDragging(RecyclerView rv, MotionEvent e, RecyclerView.ViewHolder holder, ItemDraggableRange range) {
        safeEndAnimation(rv, holder);

        mHandler.cancelLongPressDetection();

        mDraggingItem = holder;

        // XXX if setIsRecyclable() is used, another view holder objects will be created
        // which has the same ID with currently dragging item... Not works as expected.

        // mDraggingItem.setIsRecyclable(false);

        mDraggingItemId = mDraggingItem.getItemId();
        mDraggableRange = range;

        final View itemView = mDraggingItem.itemView;

        mOrigOverScrollMode = ViewCompat.getOverScrollMode(rv);
        ViewCompat.setOverScrollMode(rv, ViewCompat.OVER_SCROLL_NEVER);

        mLastTouchX = (int) (e.getX() + 0.5f);
        mLastTouchY = (int) (e.getY() + 0.5f);

        // disable auto scrolling until user moves the item
        mDragStartTouchY = mDragMinTouchY = mDragMaxTouchY = mLastTouchY;
        mDragStartTouchX = mDragMinTouchX = mDragMaxTouchX = mLastTouchX;
        mScrollDirMask = SCROLL_DIR_NONE;

        // calculate the view-local offset from the touched point
        mGrabbedPositionX = mLastTouchX - itemView.getLeft();
        mGrabbedPositionY = mLastTouchY - itemView.getTop();

        mGrabbedItemWidth = itemView.getWidth();
        mGrabbedItemHeight = itemView.getHeight();
        CustomRecyclerViewUtils.getLayoutMargins(itemView, mDraggingItemMargins);

        mRecyclerView.getParent().requestDisallowInterceptTouchEvent(true);

        startScrollOnDraggingProcess();

        // raise onDragItemStarted() event
        mAdapter.onDragItemStarted(mDraggingItem, mDraggableRange);

        // setup decorators
        mAdapter.onBindViewHolder(mDraggingItem, mDraggingItem.getLayoutPosition());

        mDraggingItemDecorator = new DraggingItemDecorator(mRecyclerView, mDraggingItem, mDraggableRange);
        mDraggingItemDecorator.setShadowDrawable(mShadowDrawable);
        mDraggingItemDecorator.start(e, mGrabbedPositionX, mGrabbedPositionY);

        int layoutType = CustomRecyclerViewUtils.getLayoutType(mRecyclerView);

        if (supportsViewTranslation() &&
                (layoutType == CustomRecyclerViewUtils.LAYOUT_TYPE_LINEAR_VERTICAL ||
                        layoutType == CustomRecyclerViewUtils.LAYOUT_TYPE_LINEAR_HORIZONTAL)) {
            mSwapTargetItemOperator = new SwapTargetItemOperator(mRecyclerView, mDraggingItem, mDraggableRange);
            mSwapTargetItemOperator.setSwapTargetTranslationInterpolator(mSwapTargetTranslationInterpolator);
            mSwapTargetItemOperator.start();
            mSwapTargetItemOperator.update(mDraggingItemDecorator.getDraggingItemTranslationX(), mDraggingItemDecorator.getDraggingItemTranslationY());
        }

        if (mEdgeEffectDecorator != null) {
            mEdgeEffectDecorator.reorderToTop();
        }

        if (mItemDragEventListener != null) {
            mItemDragEventListener.onItemDragStarted(mAdapter.getDraggingItemInitialPosition());
        }
    }

    /**
     * Cancel dragging.
     */
    public void cancelDrag() {
        cancelDrag(false);
    }

    private void cancelDrag(boolean immediately) {
        if (immediately) {
            finishDragging(false);
        } else {
            if (isDragging()) {
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
                    mRecyclerView.post(mDeferredCancelProcess);
                }
            }
        }
    }

    private void finishDragging(boolean result) {
        final RecyclerView.ViewHolder draggedItem = mDraggingItem;

        if (draggedItem == null) {
            result = false;
        }

        // cancel deferred request
        if (mDeferredCancelProcess != null) {
            mRecyclerView.removeCallbacks(mDeferredCancelProcess);
            mDeferredCancelProcess = null;
        }

        // NOTE: setOverScrollMode() have to be called before calling removeItemDecoration()
        if (mRecyclerView != null && mDraggingItem != null) {
            ViewCompat.setOverScrollMode(mRecyclerView, mOrigOverScrollMode);
        }

        if (mDraggingItemDecorator != null) {
            mDraggingItemDecorator.setReturnToDefaultPositionAnimationDuration(mItemSettleBackIntoPlaceAnimationDuration);
            mDraggingItemDecorator.setReturnToDefaultPositionAnimationInterpolator(mItemSettleBackIntoPlaceAnimationInterpolator);
            mDraggingItemDecorator.finish(true);
        }

        if (mSwapTargetItemOperator != null) {
            mSwapTargetItemOperator.setReturnToDefaultPositionAnimationDuration(mItemSettleBackIntoPlaceAnimationDuration);
            mDraggingItemDecorator.setReturnToDefaultPositionAnimationInterpolator(mItemSettleBackIntoPlaceAnimationInterpolator);
            mSwapTargetItemOperator.finish(true);
        }

        if (mEdgeEffectDecorator != null) {
            mEdgeEffectDecorator.releaseBothGlows();
        }

        stopScrollOnDraggingProcess();

        if (mRecyclerView != null && mRecyclerView.getParent() != null) {
            mRecyclerView.getParent().requestDisallowInterceptTouchEvent(false);
        }

        if (mRecyclerView != null) {
            mRecyclerView.invalidate();
        }

        mDraggableRange = null;
        mDraggingItemDecorator = null;
        mSwapTargetItemOperator = null;
        mDraggingItem = null;
        mDraggingItemId = RecyclerView.NO_ID;

        mLastTouchX = 0;
        mLastTouchY = 0;
        mDragStartTouchX = 0;
        mDragStartTouchY = 0;
        mDragMinTouchX = 0;
        mDragMinTouchY = 0;
        mDragMaxTouchX = 0;
        mDragMaxTouchY = 0;
        mGrabbedPositionX = 0;
        mGrabbedPositionY = 0;
        mGrabbedItemWidth = 0;
        mGrabbedItemHeight = 0;
        mCanDragH = false;
        mCanDragV = false;


        int draggingItemInitialPosition = RecyclerView.NO_POSITION;
        int draggingItemCurrentPosition = RecyclerView.NO_POSITION;

        // raise onDragItemFinished() event
        if (mAdapter != null) {
            draggingItemInitialPosition = mAdapter.getDraggingItemInitialPosition();
            draggingItemCurrentPosition = mAdapter.getDraggingItemCurrentPosition();
            mAdapter.onDragItemFinished(draggedItem, result);
        }

//        if (draggedItem != null) {
//            draggedItem.setIsRecyclable(true);
//        }

        if (mItemDragEventListener != null) {
            mItemDragEventListener.onItemDragFinished(
                    draggingItemInitialPosition,
                    draggingItemCurrentPosition,
                    result);
        }
    }

    private boolean handleActionUpOrCancel(RecyclerView rv, MotionEvent e) {
        final boolean result = (MotionEventCompat.getActionMasked(e) == MotionEvent.ACTION_UP);

        mHandler.cancelLongPressDetection();

        mInitialTouchX = 0;
        mInitialTouchY = 0;
        mLastTouchX = 0;
        mLastTouchY = 0;
        mDragStartTouchX = 0;
        mDragStartTouchY = 0;
        mDragMinTouchX = 0;
        mDragMinTouchY = 0;
        mDragMaxTouchX = 0;
        mDragMaxTouchY = 0;
        mInitialTouchItemId = RecyclerView.NO_ID;
        mCanDragH = false;
        mCanDragV = false;

        if (isDragging()) {
            if (LOCAL_LOGD) {
                Log.d(TAG, "dragging finished  --- result = " + result);
            }

            finishDragging(result);
        }

        return true;
    }

    private boolean handleActionMoveWhileNotDragging(RecyclerView rv, MotionEvent e) {
        if (mInitiateOnMove) {
            return checkConditionAndStartDragging(rv, e, true);
        } else {
            return false;
        }
    }

    private boolean checkConditionAndStartDragging(RecyclerView rv, MotionEvent e, boolean checkTouchSlop) {
        if (mDraggingItem != null) {
            return false;
        }

        final int touchX = (int) (e.getX() + 0.5f);
        final int touchY = (int) (e.getY() + 0.5f);

        mLastTouchX = touchX;
        mLastTouchY = touchY;

        if (mInitialTouchItemId == RecyclerView.NO_ID) {
            return false;
        }

        if (checkTouchSlop) {
            if (!((mCanDragH && (Math.abs(touchX - mInitialTouchX) > mTouchSlop)) ||
                    (mCanDragV && (Math.abs(touchY - mInitialTouchY) > mTouchSlop)))) {
                return false;
            }
        }

        final RecyclerView.ViewHolder holder = CustomRecyclerViewUtils.findChildViewHolderUnderWithoutTranslation(rv, mInitialTouchX, mInitialTouchY);

        if (holder == null) {
            return false;
        }

        int position = CustomRecyclerViewUtils.getSynchronizedPosition(holder);

        if (position == RecyclerView.NO_POSITION) {
            return false;
        }

        final View view = holder.itemView;
        final int translateX = (int) (ViewCompat.getTranslationX(view) + 0.5f);
        final int translateY = (int) (ViewCompat.getTranslationY(view) + 0.5f);
        final int viewX = touchX - (view.getLeft() + translateX);
        final int viewY = touchY - (view.getTop() + translateY);

        if (!mAdapter.canStartDrag(holder, position, viewX, viewY)) {
            return false;
        }

        ItemDraggableRange range = mAdapter.getItemDraggableRange(holder, position);

        if (range == null) {
            range = new ItemDraggableRange(0, Math.max(0, mAdapter.getItemCount() - 1));
        }

        verifyItemDraggableRange(range, holder);


        if (LOCAL_LOGD) {
            Log.d(TAG, "dragging started");
        }

        startDragging(rv, e, holder, range);

        return true;
    }

    private void verifyItemDraggableRange(ItemDraggableRange range, RecyclerView.ViewHolder holder) {
        final int start = 0;
        final int end = Math.max(0, mAdapter.getItemCount() - 1);

        if (range.getStart() > range.getEnd()) {
            throw new IllegalStateException("Invalid range specified --- start > range (range = " + range + ")");
        }

        if (range.getStart() < start) {
            throw new IllegalStateException("Invalid range specified --- start < 0 (range = " + range + ")");
        }

        if (range.getEnd() > end) {
            throw new IllegalStateException("Invalid range specified --- end >= count (range = " + range + ")");
        }

        if (!range.checkInRange(holder.getAdapterPosition())) {
            throw new IllegalStateException(
                    "Invalid range specified --- does not contain drag target item"
                            + " (range = " + range + ", position = " + holder.getAdapterPosition() + ")");
        }
    }

    private void handleActionMoveWhileDragging(RecyclerView rv, MotionEvent e) {

        mLastTouchX = (int) (e.getX() + 0.5f);
        mLastTouchY = (int) (e.getY() + 0.5f);

        mDragMinTouchX = Math.min(mDragMinTouchX, mLastTouchX);
        mDragMinTouchY = Math.min(mDragMinTouchY, mLastTouchY);
        mDragMaxTouchX = Math.max(mDragMaxTouchX, mLastTouchX);
        mDragMaxTouchY = Math.max(mDragMaxTouchY, mLastTouchY);

        // update drag direction mask
        updateDragDirectionMask();

        // update decorators
        mDraggingItemDecorator.update(e);
        if (mSwapTargetItemOperator != null) {
            mSwapTargetItemOperator.update(mDraggingItemDecorator.getDraggingItemTranslationX(), mDraggingItemDecorator.getDraggingItemTranslationY());
        }

        // check swapping
        checkItemSwapping(rv);
    }

    private void updateDragDirectionMask() {
        if (CustomRecyclerViewUtils.getOrientation(mRecyclerView) == CustomRecyclerViewUtils.ORIENTATION_VERTICAL) {
            if (((mDragStartTouchY - mDragMinTouchY) > mScrollTouchSlop) ||
                    ((mDragMaxTouchY - mLastTouchY) > mScrollTouchSlop)) {
                mScrollDirMask |= SCROLL_DIR_UP;
            }
            if (((mDragMaxTouchY - mDragStartTouchY) > mScrollTouchSlop) ||
                    ((mLastTouchY - mDragMinTouchY) > mScrollTouchSlop)) {
                mScrollDirMask |= SCROLL_DIR_DOWN;
            }
        } else if (CustomRecyclerViewUtils.getOrientation(mRecyclerView) == CustomRecyclerViewUtils.ORIENTATION_HORIZONTAL) {
            if (((mDragStartTouchX - mDragMinTouchX) > mScrollTouchSlop) ||
                    ((mDragMaxTouchX - mLastTouchX) > mScrollTouchSlop)) {
                mScrollDirMask |= SCROLL_DIR_LEFT;
            }
            if (((mDragMaxTouchX - mDragStartTouchX) > mScrollTouchSlop) ||
                    ((mLastTouchX - mDragMinTouchX) > mScrollTouchSlop)) {
                mScrollDirMask |= SCROLL_DIR_RIGHT;
            }
        }
    }

    private void checkItemSwapping(RecyclerView rv) {
        final RecyclerView.ViewHolder draggingItem = mDraggingItem;

        final int overlayItemLeft = mLastTouchX - mGrabbedPositionX;
        final int overlayItemTop = mLastTouchY - mGrabbedPositionY;
        final RecyclerView.ViewHolder swapTargetHolder =
                findSwapTargetItem(rv, draggingItem, mDraggingItemId, overlayItemLeft, overlayItemTop, mDraggableRange);

        if ((swapTargetHolder != null) && (swapTargetHolder != mDraggingItem)) {
            swapItems(rv, draggingItem, swapTargetHolder);
        }
    }

    /*package*/ void handleScrollOnDragging() {
        final RecyclerView rv = mRecyclerView;

        switch (CustomRecyclerViewUtils.getOrientation(rv)) {
            case CustomRecyclerViewUtils.ORIENTATION_VERTICAL:
                handleScrollOnDraggingInternal(rv, false);
                break;
            case CustomRecyclerViewUtils.ORIENTATION_HORIZONTAL:
                handleScrollOnDraggingInternal(rv, true);
                break;
        }
    }

    private void handleScrollOnDraggingInternal(RecyclerView rv, boolean horizontal) {
        final int edge = (horizontal) ? rv.getWidth() : rv.getHeight();

        if (edge == 0) {
            return;
        }

        final float invEdge = (1.0f / edge);
        final float normalizedTouchPos = (horizontal ? mLastTouchX : mLastTouchY) * invEdge;
        final float threshold = SCROLL_THRESHOLD;
        final float invThreshold = (1.0f / threshold);
        final float centerOffset = normalizedTouchPos - 0.5f;
        final float absCenterOffset = Math.abs(centerOffset);
        final float acceleration = Math.max(0.0f, threshold - (0.5f - absCenterOffset)) * invThreshold;
        final int mask = mScrollDirMask;
        final DraggingItemDecorator decorator = mDraggingItemDecorator;

        int scrollAmount = (int) Math.signum(centerOffset) * (int) (SCROLL_AMOUNT_COEFF * mDragEdgeScrollSpeed * mDisplayDensity * acceleration + 0.5f);
        int actualScrolledAmount = 0;

        final ItemDraggableRange range = mDraggableRange;

        final int firstVisibleChild = CustomRecyclerViewUtils.findFirstCompletelyVisibleItemPosition(mRecyclerView);
        final int lastVisibleChild = CustomRecyclerViewUtils.findLastCompletelyVisibleItemPosition(mRecyclerView);

        boolean reachedToFirstHardLimit = false;
        boolean reachedToFirstSoftLimit = false;
        boolean reachedToLastHardLimit = false;
        boolean reachedToLastSoftLimit = false;

        if (firstVisibleChild != RecyclerView.NO_POSITION) {
            if (firstVisibleChild <= range.getStart()) {
                reachedToFirstSoftLimit = true;
            }
            if (firstVisibleChild <= (range.getStart() - 1)) {
                reachedToFirstHardLimit = true;
            }
        }

        if (lastVisibleChild != RecyclerView.NO_POSITION) {
            if (lastVisibleChild >= range.getEnd()) {
                reachedToLastSoftLimit = true;
            }
            if (lastVisibleChild >= (range.getEnd() + 1)) {
                reachedToLastHardLimit = true;
            }
        }

        // apply mask
        if (scrollAmount > 0) {
            if ((mask & (horizontal ? SCROLL_DIR_RIGHT : SCROLL_DIR_DOWN)) == 0) {
                scrollAmount = 0;
            }
        } else if (scrollAmount < 0) {
            if ((mask & (horizontal ? SCROLL_DIR_LEFT : SCROLL_DIR_UP)) == 0) {
                scrollAmount = 0;
            }
        }

        // scroll
        if ((!reachedToFirstHardLimit && (scrollAmount < 0)) ||
                (!reachedToLastHardLimit && (scrollAmount > 0))) {
            safeEndAnimations(rv);

            actualScrolledAmount = (horizontal)
                    ? scrollByXAndGetScrolledAmount(scrollAmount)
                    : scrollByYAndGetScrolledAmount(scrollAmount);

            if (scrollAmount < 0) {
                decorator.setIsScrolling(!reachedToFirstSoftLimit);
            } else {
                decorator.setIsScrolling(!reachedToLastSoftLimit);
            }

            decorator.refresh();
            if (mSwapTargetItemOperator != null) {
                mSwapTargetItemOperator.update(
                        decorator.getDraggingItemTranslationX(),
                        decorator.getDraggingItemTranslationY());
            }
        } else {
            decorator.setIsScrolling(false);
        }

        final boolean actualIsScrolling = (actualScrolledAmount != 0);


        if (mEdgeEffectDecorator != null) {
            final float edgeEffectStrength = 0.005f;

            final int draggingItemTopLeft = (horizontal)? decorator.getTranslatedItemPositionLeft() : decorator.getTranslatedItemPositionTop();
            final int draggingItemBottomRight = (horizontal)? decorator.getTranslatedItemPositionRight() : decorator.getTranslatedItemPositionBottom();
            final int draggingItemCenter = (draggingItemTopLeft + draggingItemBottomRight) / 2;
            final int nearEdgePosition;

            if (firstVisibleChild == 0 && lastVisibleChild == 0) {
                // has only 1 item
                nearEdgePosition = (scrollAmount < 0) ? draggingItemTopLeft : draggingItemBottomRight;
            } else {
                nearEdgePosition = (draggingItemCenter < (edge / 2)) ? draggingItemTopLeft : draggingItemBottomRight;
            }

            final float nearEdgeOffset = (nearEdgePosition * invEdge) - 0.5f;
            final float absNearEdgeOffset = Math.abs(nearEdgeOffset);
            float edgeEffectPullDistance = 0;

            if ((absNearEdgeOffset > 0.4f) && (scrollAmount != 0) && !actualIsScrolling) {
                if (nearEdgeOffset < 0) {
                    if (horizontal ? decorator.isReachedToLeftLimit() : decorator.isReachedToTopLimit()) {
                        edgeEffectPullDistance = -mDisplayDensity * edgeEffectStrength;
                    }
                } else {
                    if (horizontal ? decorator.isReachedToRightLimit() : decorator.isReachedToBottomLimit()) {
                        edgeEffectPullDistance = mDisplayDensity * edgeEffectStrength;
                    }
                }
            }

            updateEdgeEffect(edgeEffectPullDistance);
        }

        ViewCompat.postOnAnimation(mRecyclerView, mCheckItemSwappingRunnable);
    }

    private void updateEdgeEffect(float distance) {
        if (distance != 0.0f) {
            if (distance < 0) {
                // upward
                mEdgeEffectDecorator.pullFirstEdge(distance);
            } else {
                // downward
                mEdgeEffectDecorator.pullSecondEdge(distance);
            }
        } else {
            mEdgeEffectDecorator.releaseBothGlows();
        }
    }

    private Runnable mCheckItemSwappingRunnable = new Runnable() {
        @Override
        public void run() {
            if (mDraggingItem != null) {
                checkItemSwapping(mRecyclerView);
            }
        }
    };

    private int scrollByYAndGetScrolledAmount(int ry) {
        // NOTE: mActualScrollByAmount --- Hackish! To detect over scrolling.

        mActualScrollByYAmount = 0;
        mInScrollByMethod = true;
        mRecyclerView.scrollBy(0, ry);
        mInScrollByMethod = false;

        return mActualScrollByYAmount;
    }

    private int scrollByXAndGetScrolledAmount(int rx) {
        mActualScrollByXAmount = 0;
        mInScrollByMethod = true;
        mRecyclerView.scrollBy(rx, 0);
        mInScrollByMethod = false;

        return mActualScrollByXAmount;
    }

    /*package*/ RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    private void startScrollOnDraggingProcess() {
        mScrollOnDraggingProcess.start();
    }

    private void stopScrollOnDraggingProcess() {
        if (mScrollOnDraggingProcess != null) {
            mScrollOnDraggingProcess.stop();
        }
    }

    private void swapItems(RecyclerView rv, RecyclerView.ViewHolder draggingItem, RecyclerView.ViewHolder swapTargetHolder) {
        final Rect swapTargetMargins = CustomRecyclerViewUtils.getLayoutMargins(swapTargetHolder.itemView, mTmpRect1);
        final int fromPosition = draggingItem.getAdapterPosition();
        final int toPosition = swapTargetHolder.getAdapterPosition();
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

        //noinspection StatementWithEmptyBody
        if (diffPosition == 0) {
        } else if (diffPosition == 1) {
            final View v1 = draggingItem.itemView;
            final View v2 = swapTargetHolder.itemView;
            final Rect m1 = mDraggingItemMargins;
            //noinspection UnnecessaryLocalVariable
            final Rect m2 = swapTargetMargins;

            if (mCanDragH) {
                final int left = Math.min(v1.getLeft() - m1.left, v2.getLeft() - m2.left);
                final int right = Math.max(v1.getRight() + m1.right, v2.getRight() + m2.right);

                final float midPointOfTheItems = left + ((right - left) * 0.5f);
                final float midPointOfTheOverlaidItem = (mLastTouchX - mGrabbedPositionX) + (mGrabbedItemWidth * 0.5f);

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
            }

            if (!performSwapping && mCanDragV) {
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
            }
        } else { // diffPosition > 1
            performSwapping = true;
        }

        if (performSwapping) {
            if (LOCAL_LOGD) {
                Log.d(TAG, "item swap (from: " + fromPosition + ", to: " + toPosition + ")");
            }

	        if (mItemDragEventListener != null) {
		        mItemDragEventListener.onItemDragPositionChanged(fromPosition, toPosition);
	        }

            RecyclerView.ViewHolder firstVisibleItem = null;

            if (rv.getChildCount() > 0) {
                View child = rv.getChildAt(0);
                if (child != null) {
                    firstVisibleItem = rv.getChildViewHolder(child);
                }
            }
            final int prevFirstItemPosition = (firstVisibleItem != null) ? firstVisibleItem.getAdapterPosition() : RecyclerView.NO_POSITION;

            // NOTE: This method invokes notifyItemMoved() method internally. Be careful!
            mAdapter.moveItem(fromPosition, toPosition);

            safeEndAnimations(rv);

            if (CustomRecyclerViewUtils.getOrientation(rv) == CustomRecyclerViewUtils.ORIENTATION_VERTICAL) {
                if (fromPosition == prevFirstItemPosition) {
                    //noinspection UnnecessaryLocalVariable
                    final Rect margins = swapTargetMargins;
                    final int curTopItemHeight = swapTargetHolder.itemView.getHeight() + margins.top + margins.bottom;
                    scrollByYAndGetScrolledAmount(-curTopItemHeight);
                } else if (toPosition == prevFirstItemPosition) {
                    final Rect margins = mDraggingItemMargins;
                    final int curTopItemHeight = mGrabbedItemHeight + margins.top + margins.bottom;
                    scrollByYAndGetScrolledAmount(-curTopItemHeight);
                }
            } else if (CustomRecyclerViewUtils.getOrientation(rv) == CustomRecyclerViewUtils.ORIENTATION_HORIZONTAL) {
                if (fromPosition == prevFirstItemPosition) {
                    //noinspection UnnecessaryLocalVariable
                    final Rect margins = swapTargetMargins;
                    final int curLeftItemHeight = swapTargetHolder.itemView.getWidth() + margins.left + margins.right;
                    scrollByXAndGetScrolledAmount(-curLeftItemHeight);
                } else if (toPosition == prevFirstItemPosition) {
                    final Rect margins = mDraggingItemMargins;
                    final int curLeftItemHeight = mGrabbedItemWidth + margins.left + margins.right;
                    scrollByXAndGetScrolledAmount(-curLeftItemHeight);
                }
            }

            safeEndAnimations(rv);
        }
    }

    private static DraggableItemWrapperAdapter getDraggableItemWrapperAdapter(RecyclerView rv) {
        return WrapperAdapterUtils.findWrappedAdapter(rv.getAdapter(), DraggableItemWrapperAdapter.class);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean checkTouchedItemState(RecyclerView rv, RecyclerView.ViewHolder holder) {
        if (!(holder instanceof DraggableItemViewHolder)) {
            return false;
        }

        final int itemPosition = holder.getAdapterPosition();
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
            long draggingItemId, int overlayItemLeft, int overlayItemTop, ItemDraggableRange range) {
        RecyclerView.ViewHolder swapTargetHolder;

        final int layoutType = CustomRecyclerViewUtils.getLayoutType(rv);
        switch (layoutType) {
            case CustomRecyclerViewUtils.LAYOUT_TYPE_GRID_HORIZONTAL:
            case CustomRecyclerViewUtils.LAYOUT_TYPE_GRID_VERTICAL:
                swapTargetHolder = findSwapTargetItemForGridLayoutManager(rv, draggingItem, draggingItemId, overlayItemLeft, overlayItemTop, range);
                break;
            case CustomRecyclerViewUtils.LAYOUT_TYPE_LINEAR_HORIZONTAL:
                swapTargetHolder = findSwapTargetItemForLinearLayoutManagerHorizontal(rv, draggingItem, draggingItemId, overlayItemLeft, overlayItemTop, range);
                break;
            case CustomRecyclerViewUtils.LAYOUT_TYPE_LINEAR_VERTICAL:
                swapTargetHolder = findSwapTargetItemForLinearLayoutManagerVertical(rv, draggingItem, draggingItemId, overlayItemLeft, overlayItemTop, range);
                break;
            default:
                swapTargetHolder = null;
                break;
        }

        // check range
        if (swapTargetHolder != null && range != null) {
            if (!range.checkInRange(swapTargetHolder.getAdapterPosition())) {
                swapTargetHolder = null;
            }
        }

        return swapTargetHolder;
    }

    private static RecyclerView.ViewHolder findSwapTargetItemForGridLayoutManager(
            RecyclerView rv, RecyclerView.ViewHolder draggingItem,
            long draggingItemId, int overlayItemLeft, int overlayItemTop, ItemDraggableRange range) {
        final int draggingItemPosition = draggingItem.getAdapterPosition();
        RecyclerView.ViewHolder swapTargetHolder = null;

        if (draggingItemPosition != RecyclerView.NO_POSITION &&
                draggingItem.getItemId() == draggingItemId) {

            int cx = overlayItemLeft + draggingItem.itemView.getWidth() / 2;
            int cy = overlayItemTop + draggingItem.itemView.getHeight() / 2;

            RecyclerView.ViewHolder vh = CustomRecyclerViewUtils.findChildViewHolderUnderWithoutTranslation(rv, cx, cy);
            if (draggingItem != vh) {
                swapTargetHolder = vh;
            }
        }

        return swapTargetHolder;
    }

    private static RecyclerView.ViewHolder findSwapTargetItemForLinearLayoutManagerVertical(
            RecyclerView rv, RecyclerView.ViewHolder draggingItem,
            long draggingItemId, int overlayItemLeft, int overlayItemTop, ItemDraggableRange range) {
        final int draggingItemPosition = draggingItem.getAdapterPosition();
        final int draggingViewTop = draggingItem.itemView.getTop();
        RecyclerView.ViewHolder swapTargetHolder = null;

        // determine the swap target view
        if (draggingItemPosition != RecyclerView.NO_POSITION &&
                draggingItem.getItemId() == draggingItemId) {
            if (overlayItemTop < draggingViewTop) {
                if (draggingItemPosition > 0) {
                    swapTargetHolder = rv.findViewHolderForAdapterPosition(draggingItemPosition - 1);
                }
            } else if (overlayItemTop > draggingViewTop) {
                if (draggingItemPosition < (rv.getAdapter().getItemCount() - 1)) {
                    swapTargetHolder = rv.findViewHolderForAdapterPosition(draggingItemPosition + 1);
                }
            }
        }

        return swapTargetHolder;
    }

    private static RecyclerView.ViewHolder findSwapTargetItemForLinearLayoutManagerHorizontal(
            RecyclerView rv, RecyclerView.ViewHolder draggingItem,
            long draggingItemId, int overlayItemLeft, int overlayItemTop, ItemDraggableRange range) {
        final int draggingItemPosition = draggingItem.getAdapterPosition();
        final int draggingViewLeft = draggingItem.itemView.getLeft();
        RecyclerView.ViewHolder swapTargetHolder = null;

        // determine the swap target view
        if (draggingItemPosition != RecyclerView.NO_POSITION &&
                draggingItem.getItemId() == draggingItemId) {
            if (overlayItemLeft < draggingViewLeft) {
                if (draggingItemPosition > 0) {
                    swapTargetHolder = rv.findViewHolderForAdapterPosition(draggingItemPosition - 1);
                }
            } else if (overlayItemLeft > draggingViewLeft) {
                if (draggingItemPosition < (rv.getAdapter().getItemCount() - 1)) {
                    swapTargetHolder = rv.findViewHolderForAdapterPosition(draggingItemPosition + 1);
                }
            }
        }

        return swapTargetHolder;
    }

    /**
     * Sets the duration of "settle back into place" animation.
     *
     * @param duration Specify the animation duration in milliseconds
     */
    public void setItemSettleBackIntoPlaceAnimationDuration(int duration) {
        mItemSettleBackIntoPlaceAnimationDuration = duration;
    }

    /**
     * Gets the duration of "settle back into place" animation.
     *
     * @return The duration of "settle back into place" animation in milliseconds
     */
    public int getItemSettleBackIntoPlaceAnimationDuration() {
        return mItemSettleBackIntoPlaceAnimationDuration;
    }

    /**
     * Sets the interpolator which is used for "settle back into place" animation.
     *
     * @param interpolator Interpolator to set or null to clear
     */
    public void setItemSettleBackIntoPlaceAnimationInterpolator(Interpolator interpolator) {
        mItemSettleBackIntoPlaceAnimationInterpolator = interpolator;
    }

    /**
     * Gets the interpolator which ise used for "settle back into place" animation.
     *
     * @return Interpolator which is used for "settle back into place" animation
     */
    public Interpolator getItemSettleBackIntoPlaceAnimationInterpolator() {
        return mItemSettleBackIntoPlaceAnimationInterpolator;
    }

    /*package*/ void onDraggingItemViewRecycled() {
        mDraggingItemDecorator.invalidateDraggingItem();
    }

    /*package*/ void onNewDraggingItemViewBinded(RecyclerView.ViewHolder holder) {
        mDraggingItem = holder;
        mDraggingItemDecorator.setDraggingItemViewHolder(holder);
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

    private static class InternalHandler extends Handler {
        private static final int LONGPRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout();// + ViewConfiguration.getTapTimeout();
        private static final int MSG_LONGPRESS = 1;

        private RecyclerViewDragDropManager mHolder;
        private MotionEvent mDownMotionEvent;

        public InternalHandler(RecyclerViewDragDropManager holder) {
            mHolder = holder;
        }

        public void release() {
            removeCallbacks(null);
            mHolder = null;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LONGPRESS:
                    mHolder.handleOnLongPress(mDownMotionEvent);
                    break;
            }
        }

        public void startLongPressDetection(MotionEvent e) {
            cancelLongPressDetection();
            mDownMotionEvent = MotionEvent.obtain(e);
            sendEmptyMessageAtTime(MSG_LONGPRESS, e.getDownTime() + LONGPRESS_TIMEOUT);
        }

        public void cancelLongPressDetection() {
            removeMessages(MSG_LONGPRESS);
            if (mDownMotionEvent != null) {
                mDownMotionEvent.recycle();
                mDownMotionEvent = null;
            }
        }
    }
}
