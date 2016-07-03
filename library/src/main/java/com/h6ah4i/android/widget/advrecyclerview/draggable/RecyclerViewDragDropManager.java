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
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.h6ah4i.android.widget.advrecyclerview.utils.CustomRecyclerViewUtils;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;

/**
 * Provides item drag &amp; drop operation for {@link android.support.v7.widget.RecyclerView}
 */
@SuppressWarnings("PointlessBitwiseExpression")
public class RecyclerViewDragDropManager implements DraggableItemConstants {
    private static final String TAG = "ARVDragDropManager";

    /**
     * Default interpolator used for "swap target transition"
     */
    public static final Interpolator DEFAULT_SWAP_TARGET_TRANSITION_INTERPOLATOR = new BasicSwapTargetTranslationInterpolator();


    /**
     * Default interpolator used for "item settle back into place" animation
     */
    public static final Interpolator DEFAULT_ITEM_SETTLE_BACK_INTO_PLACE_ANIMATION_INTERPOLATOR = new DecelerateInterpolator();

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ITEM_MOVE_MODE_DEFAULT, ITEM_MOVE_MODE_SWAP})
    public @interface ItemMoveMode {
    }

    /**
     * Default item move mode
     */
    public static final int ITEM_MOVE_MODE_DEFAULT = 0;

    /**
     * Swap two items between dragging item and the item under a finger (or mouse pointer)
     */
    public static final int ITEM_MOVE_MODE_SWAP = 1;

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
         * @param toPosition   The new position of the item
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

        /**
         * Callback method to be invoked when the distance of currently dragging item is updated.
         *
         * @param offsetX The horizontal distance of currently dragging item from the initial position in pixels.
         * @param offsetY The vertical distance of currently dragging item from the initial position in pixels.
         */
        void onItemDragMoveDistanceUpdated(int offsetX, int offsetY);
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

    static class SwapTarget {
        public RecyclerView.ViewHolder holder;
        public int position;
        public boolean self;

        public void clear() {
            holder = null;
            position = RecyclerView.NO_POSITION;
            self = false;
        }
    }

    static class FindSwapTargetContext {
        public RecyclerView rv;
        public DraggingItemInfo draggingItemInfo;
        public RecyclerView.ViewHolder draggingItem;
        public int lastTouchX;
        public int lastTouchY;
        public int overlayItemLeft;
        public int overlayItemTop;
        public int overlayItemLeftNotClipped;
        public int overlayItemTopNotClipped;
        public int layoutType;
        public boolean vertical;
        public ItemDraggableRange range;
        public boolean checkCanSwap;

        public void setup(
                RecyclerView rv, RecyclerView.ViewHolder vh,
                DraggingItemInfo info, int lastTouchX, int lastTouchY,
                ItemDraggableRange range, boolean checkCanSwap) {
            this.rv = rv;
            this.draggingItemInfo = info;
            this.draggingItem = vh;
            this.lastTouchX = lastTouchX;
            this.lastTouchY = lastTouchY;
            this.range = range;
            this.checkCanSwap = checkCanSwap;
            this.layoutType = CustomRecyclerViewUtils.getLayoutType(rv);
            this.vertical = CustomRecyclerViewUtils.extractOrientation(this.layoutType) == CustomRecyclerViewUtils.ORIENTATION_VERTICAL;

            this.overlayItemLeft = this.overlayItemLeftNotClipped = lastTouchX - info.grabbedPositionX;
            this.overlayItemTop = this.overlayItemTopNotClipped = lastTouchY - info.grabbedPositionY;

            if (this.vertical) {
                this.overlayItemLeft = Math.max(this.overlayItemLeft, rv.getPaddingLeft());
                this.overlayItemLeft = Math.min(this.overlayItemLeft, Math.max(0, rv.getWidth() - rv.getPaddingRight() - draggingItemInfo.width));
            } else {
                this.overlayItemTop = Math.max(this.overlayItemTop, rv.getPaddingTop());
                this.overlayItemTop = Math.min(this.overlayItemTop, Math.max(0, rv.getHeight() - rv.getPaddingBottom() - draggingItemInfo.height));
            }
        }

        public void clear() {
            this.rv = null;
            this.draggingItemInfo = null;
            this.draggingItem = null;
        }
    }

    private RecyclerView mRecyclerView;
    private Interpolator mSwapTargetTranslationInterpolator = DEFAULT_SWAP_TARGET_TRANSITION_INTERPOLATOR;
    private ScrollOnDraggingProcessRunnable mScrollOnDraggingProcess;

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
    private boolean mInitiateOnTouch;
    private boolean mInitiateOnMove = true;
    private int mLongPressTimeout;
    private boolean mCheckCanDrop;

    private boolean mInScrollByMethod;
    private int mActualScrollByXAmount;
    private int mActualScrollByYAmount;
    private final Rect mTmpRect1 = new Rect();
    private int mItemSettleBackIntoPlaceAnimationDuration = 200;
    private Interpolator mItemSettleBackIntoPlaceAnimationInterpolator = DEFAULT_ITEM_SETTLE_BACK_INTO_PLACE_ANIMATION_INTERPOLATOR;
    private int mItemMoveMode = ITEM_MOVE_MODE_DEFAULT;

    // these fields are only valid while dragging
    private DraggableItemWrapperAdapter mAdapter;
    /*package*/ RecyclerView.ViewHolder mDraggingItemViewHolder;
    private DraggingItemInfo mDraggingItemInfo;
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
    private int mDragScrollDistanceX;
    private int mDragScrollDistanceY;
    private int mScrollDirMask = SCROLL_DIR_NONE;
    private int mOrigOverScrollMode;
    private ItemDraggableRange mDraggableRange;
    private InternalHandler mHandler;
    private OnItemDragEventListener mItemDragEventListener;
    private boolean mCanDragH;
    private boolean mCanDragV;
    private float mDragEdgeScrollSpeed = 1.0f;
    private int mCurrentItemMoveMode = ITEM_MOVE_MODE_DEFAULT;

    private SwapTarget mTempSwapTarget = new SwapTarget();
    private FindSwapTargetContext mFindSwapTargetContext = new FindSwapTargetContext();

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

        mLongPressTimeout = ViewConfiguration.getLongPressTimeout();
    }

    /**
     * Create wrapped adapter.
     *
     * @param adapter The target adapter.
     * @return Wrapped adapter which is associated to this {@link RecyclerViewDragDropManager} instance.
     */
    @SuppressWarnings("unchecked")
    public RecyclerView.Adapter createWrappedAdapter(@NonNull RecyclerView.Adapter adapter) {
        if (!adapter.hasStableIds()) {
            throw new IllegalArgumentException("The passed adapter does not support stable IDs");
        }

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
     * <p>Attaches {@link android.support.v7.widget.RecyclerView} instance.</p>
     * <p>Before calling this method, the target {@link android.support.v7.widget.RecyclerView} must set
     * the wrapped adapter instance which is returned by the
     * {@link #createWrappedAdapter(android.support.v7.widget.RecyclerView.Adapter)} method.</p>
     *
     * @param rv The {@link android.support.v7.widget.RecyclerView} instance
     */
    public void attachRecyclerView(@NonNull RecyclerView rv) {
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

        mRecyclerView.addOnScrollListener(mInternalUseOnScrollListener);
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
     * <p>Detach the {@link android.support.v7.widget.RecyclerView} instance and release internal field references.</p>
     * <p>This method should be called in order to avoid memory leaks.</p>
     */
    public void release() {
        cancelDrag(true);

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

        if (mRecyclerView != null && mInternalUseOnScrollListener != null) {
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
    }

    /**
     * Indicates whether currently performing item dragging.
     *
     * @return True if currently performing item dragging
     */
    public boolean isDragging() {
        return (mDraggingItemInfo != null) && (!mHandler.isCancelDragRequested());
    }

    /**
     * Sets 9-patch image which is used for the actively dragging item
     *
     * @param drawable The 9-patch drawable
     */
    public void setDraggingItemShadowDrawable(@Nullable NinePatchDrawable drawable) {
        mShadowDrawable = drawable;
    }

    /**
     * Sets the interpolator which is used for determining the position of the swapping item.
     *
     * @param interpolator Interpolator to set or null to clear
     */
    public void setSwapTargetTranslationInterpolator(@Nullable Interpolator interpolator) {
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
     * Returns whether dragging starts on touch the handle immediately.
     *
     * @return True if dragging starts on touch the handle immediately, false otherwise.
     */
    public boolean isInitiateOnTouchEnabled() {
        return mInitiateOnTouch;
    }

    /**
     * Sets whether dragging starts on touch the handle immediately. (default: false)
     *
     * @param initiateOnTouch True to initiate dragging on touch the handle immediately.
     */
    public void setInitiateOnTouch(boolean initiateOnTouch) {
        mInitiateOnTouch = initiateOnTouch;
    }

    /**
     * Sets the time required to consider press as long press. (default: 500ms)
     *
     * @param longPressTimeout Integer in milli seconds.
     */
    public void setLongPressTimeout(int longPressTimeout) {
        mLongPressTimeout = longPressTimeout;
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
    public
    @Nullable
    OnItemDragEventListener getOnItemDragEventListener() {
        return mItemDragEventListener;
    }

    /**
     * Sets OnItemDragEventListener listener
     *
     * @param listener The listener object
     */
    public void setOnItemDragEventListener(@Nullable OnItemDragEventListener listener) {
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

    /**
     * Sets whether to use {@link DraggableItemAdapter#onCheckCanDrop(int, int)}.
     *
     * @param enabled True if use {@link DraggableItemAdapter#onCheckCanDrop(int, int)}.
     */
    public void setCheckCanDropEnabled(boolean enabled) {
        mCheckCanDrop = enabled;
    }

    /**
     * Gets whether to use {@link DraggableItemAdapter#onCheckCanDrop(int, int)}.
     *
     * @return True if {@link DraggableItemAdapter#onCheckCanDrop(int, int)} is used, false otherwise.
     */
    public boolean isCheckCanDropEnabled() {
        return mCheckCanDrop;
    }

    /*package*/ boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        final int action = MotionEventCompat.getActionMasked(e);
        boolean handled = false;

        if (LOCAL_LOGV) {
            Log.v(TAG, "onInterceptTouchEvent() action = " + action);
        }

        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handleActionUpOrCancel(action, true);
                break;

            case MotionEvent.ACTION_DOWN:
                if (!isDragging()) {
                    handled = handleActionDown(rv, e);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (isDragging()) {
                    // NOTE: The first ACTION_MOVE event will come here. (maybe a bug of RecyclerView?)
                    handleActionMoveWhileDragging(rv, e);
                    handled = true;
                } else {
                    if (handleActionMoveWhileNotDragging(rv, e)) {
                        handled = true;
                    }
                }
        }

        return handled;
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
                handleActionUpOrCancel(action, true);
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
        } else if (isDragging()) {
            ViewCompat.postOnAnimationDelayed(mRecyclerView, mCheckItemSwappingRunnable, 500);
        }
    }

    /*package*/ void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (LOCAL_LOGV) {
            Log.v(TAG, "onScrollStateChanged(newState = " + newState + ")");
        }

        if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
            cancelDrag(true);
        }
    }

    private boolean handleActionDown(RecyclerView rv, MotionEvent e) {

        final RecyclerView.ViewHolder holder = CustomRecyclerViewUtils.findChildViewHolderUnderWithoutTranslation(rv, e.getX(), e.getY());

        if (!checkTouchedItemState(rv, holder)) {
            return false;
        }

        final int touchX = (int) (e.getX() + 0.5f);
        final int touchY = (int) (e.getY() + 0.5f);

        if (!canStartDrag(holder, touchX, touchY)) {
            return false;
        }

        final int orientation = CustomRecyclerViewUtils.getOrientation(mRecyclerView);
        final int spanCount = CustomRecyclerViewUtils.getSpanCount(mRecyclerView);

        mInitialTouchX = mLastTouchX = touchX;
        mInitialTouchY = mLastTouchY = touchY;
        mInitialTouchItemId = holder.getItemId();
        mCanDragH = (orientation == CustomRecyclerViewUtils.ORIENTATION_HORIZONTAL) ||
                ((orientation == CustomRecyclerViewUtils.ORIENTATION_VERTICAL) && (spanCount > 1));
        mCanDragV = (orientation == CustomRecyclerViewUtils.ORIENTATION_VERTICAL) ||
                ((orientation == CustomRecyclerViewUtils.ORIENTATION_HORIZONTAL) && (spanCount > 1));

        boolean handled;

        if (mInitiateOnTouch) {
            handled = checkConditionAndStartDragging(rv, e, false);
        } else if (mInitiateOnLongPress) {
            mHandler.startLongPressDetection(e, mLongPressTimeout);
            handled = false;
        } else {
            handled = false;
        }

        return handled;
    }

    /*package*/ void handleOnLongPress(MotionEvent e) {
        if (mInitiateOnLongPress) {
            checkConditionAndStartDragging(mRecyclerView, e, false);
        }
    }

    /*package*/ void handleOnCheckItemViewSizeUpdate() {
        final RecyclerView.ViewHolder vh = mRecyclerView.findViewHolderForItemId(mDraggingItemInfo.id);
        if (vh == null) {
            return;
        }

        final int w = vh.itemView.getWidth();
        final int h = vh.itemView.getHeight();

        if (!(w == mDraggingItemInfo.width && h == mDraggingItemInfo.height)) {
            mDraggingItemInfo = DraggingItemInfo.createWithNewView(mDraggingItemInfo, vh);
            mDraggingItemDecorator.updateDraggingItemView(mDraggingItemInfo, vh);
        }
    }

    @SuppressWarnings("unchecked")
    private void startDragging(RecyclerView rv, MotionEvent e, RecyclerView.ViewHolder holder, ItemDraggableRange range) {
        safeEndAnimation(rv, holder);

        mHandler.cancelLongPressDetection();

        mDraggingItemInfo = new DraggingItemInfo(rv, holder, mLastTouchX, mLastTouchY);
        mDraggingItemViewHolder = holder;

        // XXX if setIsRecyclable() is used, another view holder objects will be created
        // which has the same ID with currently dragging item... Not works as expected.

        // holder.setIsRecyclable(false);

        mDraggableRange = range;

        mOrigOverScrollMode = ViewCompat.getOverScrollMode(rv);
        ViewCompat.setOverScrollMode(rv, ViewCompat.OVER_SCROLL_NEVER);

        mLastTouchX = (int) (e.getX() + 0.5f);
        mLastTouchY = (int) (e.getY() + 0.5f);

        // disable auto scrolling until user moves the item
        mDragStartTouchY = mDragMinTouchY = mDragMaxTouchY = mLastTouchY;
        mDragStartTouchX = mDragMinTouchX = mDragMaxTouchX = mLastTouchX;
        mScrollDirMask = SCROLL_DIR_NONE;
        mCurrentItemMoveMode = mItemMoveMode;

        mRecyclerView.getParent().requestDisallowInterceptTouchEvent(true);

        startScrollOnDraggingProcess();

        // raise onDragItemStarted() event
        mAdapter.onDragItemStarted(mDraggingItemInfo, holder, mDraggableRange, mCurrentItemMoveMode);

        // setup decorators
        mAdapter.onBindViewHolder(holder, holder.getLayoutPosition());

        mDraggingItemDecorator = new DraggingItemDecorator(mRecyclerView, holder, mDraggableRange);
        mDraggingItemDecorator.setShadowDrawable(mShadowDrawable);
        mDraggingItemDecorator.start(e, mDraggingItemInfo);

        final int layoutType = CustomRecyclerViewUtils.getLayoutType(mRecyclerView);

        if (supportsViewTranslation() && !mCheckCanDrop && CustomRecyclerViewUtils.isLinearLayout(layoutType)) {
            mSwapTargetItemOperator = new SwapTargetItemOperator(mRecyclerView, holder, mDraggableRange, mDraggingItemInfo);
            mSwapTargetItemOperator.setSwapTargetTranslationInterpolator(mSwapTargetTranslationInterpolator);
            mSwapTargetItemOperator.start();
            mSwapTargetItemOperator.update(mDraggingItemDecorator.getDraggingItemTranslationX(), mDraggingItemDecorator.getDraggingItemTranslationY());
        }

        if (mEdgeEffectDecorator != null) {
            mEdgeEffectDecorator.reorderToTop();
        }

        if (mItemDragEventListener != null) {
            mItemDragEventListener.onItemDragStarted(mAdapter.getDraggingItemInitialPosition());
            mItemDragEventListener.onItemDragMoveDistanceUpdated(0, 0);
        }
    }

    /**
     * Gets item move mode
     *
     * @return item move mode
     */
    @ItemMoveMode
    public int getItemMoveMode() {
        return mItemMoveMode;
    }

    /**
     * Sets item move
     *
     * @param mode item move mode
     */
    public void setItemMoveMode(@ItemMoveMode int mode) {
        mItemMoveMode = mode;
    }

    /**
     * Cancel dragging.
     */
    public void cancelDrag() {
        cancelDrag(false);
    }

    /*package*/ void cancelDrag(boolean immediately) {
        handleActionUpOrCancel(MotionEvent.ACTION_CANCEL, false);

        if (immediately) {
            finishDragging(false);
        } else {
            if (isDragging()) {
                mHandler.requestDeferredCancelDrag();
            }
        }
    }

    private void finishDragging(boolean result) {
        // RecyclerView.ViewHolder draggedItem = mDraggingItemViewHolder;

        if (!isDragging()) {
            return;
        }

        // cancel deferred request
        if (mHandler != null) {
            mHandler.removeDeferredCancelDragRequest();
            mHandler.removeDraggingItemViewSizeUpdateCheckRequest();
        }

        // NOTE: setOverScrollMode() have to be called before calling removeItemDecoration()
        if (mRecyclerView != null && mDraggingItemViewHolder != null) {
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
        mDraggingItemViewHolder = null;
        mDraggingItemInfo = null;

        mLastTouchX = 0;
        mLastTouchY = 0;
        mDragStartTouchX = 0;
        mDragStartTouchY = 0;
        mDragMinTouchX = 0;
        mDragMinTouchY = 0;
        mDragMaxTouchX = 0;
        mDragMaxTouchY = 0;
        mDragScrollDistanceX = 0;
        mDragScrollDistanceY = 0;
        mCanDragH = false;
        mCanDragV = false;

        int draggingItemInitialPosition = RecyclerView.NO_POSITION;
        int draggingItemCurrentPosition = RecyclerView.NO_POSITION;

        // raise onDragItemFinished() event
        if (mAdapter != null) {
            draggingItemInitialPosition = mAdapter.getDraggingItemInitialPosition();
            draggingItemCurrentPosition = mAdapter.getDraggingItemCurrentPosition();
            mAdapter.onDragItemFinished(result);
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

    private boolean handleActionUpOrCancel(int action, boolean invokeFinish) {
        final boolean result = (action == MotionEvent.ACTION_UP);

        if (mHandler != null) {
            mHandler.cancelLongPressDetection();
        }

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
        mDragScrollDistanceX = 0;
        mDragScrollDistanceY = 0;
        mInitialTouchItemId = RecyclerView.NO_ID;
        mCanDragH = false;
        mCanDragV = false;

        if (invokeFinish && isDragging()) {
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
        if (mDraggingItemInfo != null) {
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

        if (!canStartDrag(holder, touchX, touchY)) {
            return false;
        }

        int position = holder.getAdapterPosition();
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

    private boolean canStartDrag(RecyclerView.ViewHolder holder, int touchX, int touchY) {
        final int itemPosition = holder.getAdapterPosition();

        if (itemPosition == RecyclerView.NO_POSITION) {
            return false;
        }

        final View view = holder.itemView;
        final int translateX = (int) (ViewCompat.getTranslationX(view) + 0.5f);
        final int translateY = (int) (ViewCompat.getTranslationY(view) + 0.5f);
        final int viewX = touchX - (view.getLeft() + translateX);
        final int viewY = touchY - (view.getTop() + translateY);

        if (mAdapter.canStartDrag(holder, itemPosition, viewX, viewY)) {
            // NOTE: notifyXXX method might be called inside of the user implemented code. that is not acceptable.
            return (holder.getAdapterPosition() == itemPosition);
        } else {
            return false;
        }
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
        final boolean updated = mDraggingItemDecorator.update(e, false);

        if (updated) {
            if (mSwapTargetItemOperator != null) {
                mSwapTargetItemOperator.update(mDraggingItemDecorator.getDraggingItemTranslationX(), mDraggingItemDecorator.getDraggingItemTranslationY());
            }

            // check swapping
            checkItemSwapping(rv);

            onItemMoveDistanceUpdated();
        }
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

    /*package*/ void checkItemSwapping(RecyclerView rv) {
        final RecyclerView.ViewHolder draggingItem = mDraggingItemViewHolder;

        final FindSwapTargetContext fc = mFindSwapTargetContext;

        fc.setup(rv, mDraggingItemViewHolder, mDraggingItemInfo, mLastTouchX, mLastTouchY, mDraggableRange, mCheckCanDrop);

        final int draggingItemInitialPosition = mAdapter.getDraggingItemInitialPosition();
        final int draggingItemCurrentPosition = mAdapter.getDraggingItemCurrentPosition();
        SwapTarget swapTarget;
        boolean canSwap = false;

        swapTarget = findSwapTargetItem(mTempSwapTarget, fc, false);

        if (swapTarget.position != RecyclerView.NO_POSITION) {
            if (!mCheckCanDrop) {
                canSwap = true;
            }
            if (!canSwap) {
                canSwap = mAdapter.canDropItems(draggingItemInitialPosition, swapTarget.position);
            }
            if (!canSwap) {
                swapTarget = findSwapTargetItem(mTempSwapTarget, fc, true);

                if (swapTarget.position != RecyclerView.NO_POSITION) {
                    canSwap = mAdapter.canDropItems(draggingItemInitialPosition, swapTarget.position);
                }
            }
        }

        if (canSwap) {
            swapItems(rv, draggingItemCurrentPosition, draggingItem, swapTarget.holder);
        }

        if (mSwapTargetItemOperator != null) {
            mSwapTargetItemOperator.setSwapTargetItem((canSwap) ? swapTarget.holder : null);
        }

        if (canSwap) {
            mHandler.scheduleDraggingItemViewSizeUpdateCheck();
        }

        swapTarget.clear();
        fc.clear();
    }

    private void onItemMoveDistanceUpdated() {
        if (mItemDragEventListener == null) {
            return;
        }

        final int moveX = mDragScrollDistanceX + mDraggingItemDecorator.getDraggingItemMoveOffsetX();
        final int moveY = mDragScrollDistanceY + mDraggingItemDecorator.getDraggingItemMoveOffsetY();

        mItemDragEventListener.onItemDragMoveDistanceUpdated(moveX, moveY);
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
            safeEndAnimationsIfRequired(rv);

            actualScrolledAmount = (horizontal)
                    ? scrollByXAndGetScrolledAmount(scrollAmount)
                    : scrollByYAndGetScrolledAmount(scrollAmount);

            if (scrollAmount < 0) {
                decorator.setIsScrolling(!reachedToFirstSoftLimit);
            } else {
                decorator.setIsScrolling(!reachedToLastSoftLimit);
            }

            decorator.refresh(true);
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

            final int draggingItemTopLeft = (horizontal) ? decorator.getTranslatedItemPositionLeft() : decorator.getTranslatedItemPositionTop();
            final int draggingItemBottomRight = (horizontal) ? decorator.getTranslatedItemPositionRight() : decorator.getTranslatedItemPositionBottom();
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

        if (actualScrolledAmount != 0) {
            if (horizontal) {
                mDragScrollDistanceX += actualScrolledAmount;
            } else {
                mDragScrollDistanceY += actualScrolledAmount;
            }

            onItemMoveDistanceUpdated();
        }
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

    private final Runnable mCheckItemSwappingRunnable = new Runnable() {
        @Override
        public void run() {
            if (mDraggingItemViewHolder != null) {
                checkItemSwapping(getRecyclerView());
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

    private void swapItems(
            RecyclerView rv,
            int draggingItemAdapterPosition,
            @Nullable RecyclerView.ViewHolder draggingItem,
            @NonNull RecyclerView.ViewHolder swapTargetHolder) {

        final Rect swapTargetMargins = CustomRecyclerViewUtils.getLayoutMargins(swapTargetHolder.itemView, mTmpRect1);
        @SuppressWarnings("UnnecessaryLocalVariable") final int fromPosition = draggingItemAdapterPosition;
        final int toPosition = swapTargetHolder.getAdapterPosition();
        final int diffPosition = Math.abs(fromPosition - toPosition);
        boolean performSwapping = false;

        if (fromPosition == RecyclerView.NO_POSITION || toPosition == RecyclerView.NO_POSITION) {
            return;
        }

        final long actualDraggingItemId = rv.getAdapter().getItemId(fromPosition);
        if (actualDraggingItemId != mDraggingItemInfo.id) {
            if (LOCAL_LOGV) {
                Log.v(TAG, "RecyclerView state has not been synchronized to data yet");
            }
            return;
        }

        final boolean isLinearLayout = CustomRecyclerViewUtils.isLinearLayout(CustomRecyclerViewUtils.getLayoutType(rv));
        final boolean swapNextItemSmoothlyInLinearLayout = isLinearLayout && (!supportsViewTranslation() || !mCheckCanDrop);

        //noinspection StatementWithEmptyBody
        if (diffPosition == 0) {
        } else if ((diffPosition == 1) && (draggingItem != null) && swapNextItemSmoothlyInLinearLayout) {
            final View v1 = draggingItem.itemView;
            final View v2 = swapTargetHolder.itemView;
            final Rect m1 = mDraggingItemInfo.margins;
            //noinspection UnnecessaryLocalVariable
            final Rect m2 = swapTargetMargins;

            if (mCanDragH) {
                final int left = Math.min(v1.getLeft() - m1.left, v2.getLeft() - m2.left);
                final int right = Math.max(v1.getRight() + m1.right, v2.getRight() + m2.right);

                final float midPointOfTheItems = left + ((right - left) * 0.5f);
                final float midPointOfTheOverlaidItem = (mLastTouchX - mDraggingItemInfo.grabbedPositionX) + (mDraggingItemInfo.width * 0.5f);

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
                final float midPointOfTheOverlaidItem = (mLastTouchY - mDraggingItemInfo.grabbedPositionY) + (mDraggingItemInfo.height * 0.5f);

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
            performSwapItems(rv, swapTargetHolder, swapTargetMargins, fromPosition, toPosition);
        }
    }

    private void performSwapItems(RecyclerView rv, @NonNull RecyclerView.ViewHolder swapTargetHolder, Rect swapTargetMargins, int fromPosition, int toPosition) {
        if (LOCAL_LOGD) {
            Log.d(TAG, "item swap (from: " + fromPosition + ", to: " + toPosition + ")");
        }

        if (mItemDragEventListener != null) {
            mItemDragEventListener.onItemDragPositionChanged(fromPosition, toPosition);
        }

        final RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        final int layoutType = CustomRecyclerViewUtils.getLayoutType(mRecyclerView);
        final boolean isVertical = (CustomRecyclerViewUtils.extractOrientation(layoutType) == CustomRecyclerViewUtils.ORIENTATION_VERTICAL);
        final int firstVisible = CustomRecyclerViewUtils.findFirstVisibleItemPosition(mRecyclerView, false);
        View fromView = CustomRecyclerViewUtils.findViewByPosition(layoutManager, fromPosition);
        View toView = CustomRecyclerViewUtils.findViewByPosition(layoutManager, toPosition);
        View firstView = CustomRecyclerViewUtils.findViewByPosition(layoutManager, firstVisible);
        Integer fromOrigin = getItemViewOrigin(fromView, isVertical);
        Integer toOrigin = getItemViewOrigin(toView, isVertical);
        Integer firstOrigin = getItemViewOrigin(firstView, isVertical);

        // NOTE: This method invokes notifyItemMoved() or notifyDataSetChanged() method internally. Be careful!
        mAdapter.moveItem(fromPosition, toPosition, layoutType);

        if ((firstVisible == fromPosition) && (firstOrigin != null) && (toOrigin != null)) {
            scrollBySpecifiedOrientation(rv, -(toOrigin - firstOrigin), isVertical);
            safeEndAnimations(rv);
        } else if ((firstVisible == toPosition) && (fromView != null) && (fromOrigin != null) && (!fromOrigin.equals(toOrigin))) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) fromView.getLayoutParams();
            int amount = (isVertical)
                    ? -(layoutManager.getDecoratedMeasuredHeight(fromView) + lp.topMargin + lp.bottomMargin)
                    : -(layoutManager.getDecoratedMeasuredWidth(fromView) + lp.leftMargin + lp.rightMargin);
            scrollBySpecifiedOrientation(rv, amount, isVertical);
            safeEndAnimations(rv);
        }
    }

    private static void scrollBySpecifiedOrientation(RecyclerView rv, int amount, boolean vertical) {
        if (vertical) {
            rv.scrollBy(0, amount);
        } else {
            rv.scrollBy(amount, 0);
        }
    }

    private static Integer getItemViewOrigin(View itemView, boolean vertical) {
        return (itemView != null) ? ((vertical) ? itemView.getTop() : itemView.getLeft()) : null;
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

    private void safeEndAnimationsIfRequired(RecyclerView rv) {
        if (mSwapTargetItemOperator != null) {
            safeEndAnimations(rv);
        }
    }

    /*package*/
    static SwapTarget findSwapTargetItem(SwapTarget dest, FindSwapTargetContext fc, boolean alternative) {
        RecyclerView.ViewHolder swapTargetHolder = null;

        dest.clear();

        if ((fc.draggingItem == null) || (
                fc.draggingItem.getAdapterPosition() != RecyclerView.NO_POSITION &&
                        fc.draggingItem.getItemId() == fc.draggingItemInfo.id)) {

            switch (fc.layoutType) {
                case CustomRecyclerViewUtils.LAYOUT_TYPE_GRID_HORIZONTAL:
                case CustomRecyclerViewUtils.LAYOUT_TYPE_GRID_VERTICAL:
                    swapTargetHolder = findSwapTargetItemForGridLayoutManager(fc, alternative);
                    break;
                case CustomRecyclerViewUtils.LAYOUT_TYPE_STAGGERED_GRID_HORIZONTAL:
                case CustomRecyclerViewUtils.LAYOUT_TYPE_STAGGERED_GRID_VERTICAL:
                    swapTargetHolder = findSwapTargetItemForStaggeredGridLayoutManager(fc, alternative);
                    break;
                case CustomRecyclerViewUtils.LAYOUT_TYPE_LINEAR_HORIZONTAL:
                case CustomRecyclerViewUtils.LAYOUT_TYPE_LINEAR_VERTICAL:
                    swapTargetHolder = findSwapTargetItemForLinearLayoutManager(fc, alternative);
                    break;
                default:
                    break;
            }
        }

        if (swapTargetHolder == fc.draggingItem) {
            swapTargetHolder = null;
            dest.self = true;
        }

        // check range
        if (swapTargetHolder != null && fc.range != null) {
            if (!fc.range.checkInRange(swapTargetHolder.getAdapterPosition())) {
                swapTargetHolder = null;
            }
        }

        dest.holder = swapTargetHolder;
        dest.position = CustomRecyclerViewUtils.safeGetAdapterPosition(swapTargetHolder);

        return dest;
    }

    private static RecyclerView.ViewHolder findSwapTargetItemForGridLayoutManager(FindSwapTargetContext fc, boolean alternative) {

        if (alternative) {
            return null;
        }

        RecyclerView.ViewHolder swapTargetHolder;

        swapTargetHolder = findSwapTargetItemForGridLayoutManagerInternal1(fc);

        if (swapTargetHolder == null) {
            swapTargetHolder = findSwapTargetItemForGridLayoutManagerInternal2(fc);
        }

        return swapTargetHolder;
    }

    private static RecyclerView.ViewHolder findSwapTargetItemForStaggeredGridLayoutManager(FindSwapTargetContext fc, boolean alternative) {

        if (alternative) {
            return null;
        }

        if (fc.draggingItem == null) {
            return null;
        }

        RecyclerView.ViewHolder swapTargetHolder = null;

        int spanCount = CustomRecyclerViewUtils.getSpanCount(fc.rv);
        int draggingItemSpanIndex = CustomRecyclerViewUtils.getSpanIndex(fc.draggingItem);

        RecyclerView.ViewHolder ssvh, csvh, esvh, sevh, cevh, eevh;
        int sSpanIndex, eSpanIndex;
        int overlayItemOrigin;
        int draggingItemOrigin;

        if (fc.vertical) {
            int sx = fc.overlayItemLeft + 1;
            int ex = fc.overlayItemLeft + fc.draggingItemInfo.width - 2;
            int sy = fc.overlayItemTop + 1;
            int cy = fc.overlayItemTop + fc.draggingItemInfo.height / 2 - 1;
            int ey = fc.overlayItemTop + fc.draggingItemInfo.height - 2;

            int sPadding = fc.rv.getPaddingLeft();
            int ePadding = fc.rv.getPaddingRight();
            int rvSize = fc.rv.getWidth();
            float spanLength = (rvSize - sPadding - ePadding) * (1.0f / spanCount);

            sSpanIndex = Math.min(Math.max((int) ((sx - fc.draggingItemInfo.margins.left - sPadding) / spanLength), 0), spanCount - 1);
            eSpanIndex = Math.min(Math.max((int) ((ex - fc.draggingItemInfo.margins.right - sPadding) / spanLength), 0), spanCount - 1);

            overlayItemOrigin = fc.overlayItemTop;
            draggingItemOrigin = fc.draggingItem.itemView.getTop();

            ssvh = CustomRecyclerViewUtils.findChildViewHolderUnderWithoutTranslation(fc.rv, sx, sy);
            csvh = CustomRecyclerViewUtils.findChildViewHolderUnderWithoutTranslation(fc.rv, sx, cy);
            esvh = CustomRecyclerViewUtils.findChildViewHolderUnderWithoutTranslation(fc.rv, sx, ey);
            sevh = CustomRecyclerViewUtils.findChildViewHolderUnderWithoutTranslation(fc.rv, ex, sy);
            cevh = CustomRecyclerViewUtils.findChildViewHolderUnderWithoutTranslation(fc.rv, ex, cy);
            eevh = CustomRecyclerViewUtils.findChildViewHolderUnderWithoutTranslation(fc.rv, ex, ey);
        } else {
            int sx = fc.overlayItemLeft + 1;
            int cx = fc.overlayItemLeft + fc.draggingItemInfo.width / 2 - 1;
            int ex = fc.overlayItemLeft + fc.draggingItemInfo.width - 2;
            int sy = fc.overlayItemTop + 1;
            int ey = fc.overlayItemTop + fc.draggingItemInfo.height - 2;

            int sPadding = fc.rv.getPaddingTop();
            int ePadding = fc.rv.getPaddingBottom();
            int rvSize = fc.rv.getHeight();
            float spanLength = (rvSize - sPadding - ePadding) * (1.0f / spanCount);

            sSpanIndex = Math.min(Math.max((int) ((sx - fc.draggingItemInfo.margins.top - sPadding) / spanLength), 0), spanCount - 1);
            eSpanIndex = Math.min(Math.max((int) ((ex - fc.draggingItemInfo.margins.left - sPadding) / spanLength), 0), spanCount - 1);

            overlayItemOrigin = fc.overlayItemLeft;
            draggingItemOrigin = fc.draggingItem.itemView.getLeft();

            ssvh = CustomRecyclerViewUtils.findChildViewHolderUnderWithoutTranslation(fc.rv, sx, sy);
            csvh = CustomRecyclerViewUtils.findChildViewHolderUnderWithoutTranslation(fc.rv, cx, sy);
            esvh = CustomRecyclerViewUtils.findChildViewHolderUnderWithoutTranslation(fc.rv, ex, sy);
            sevh = CustomRecyclerViewUtils.findChildViewHolderUnderWithoutTranslation(fc.rv, sx, ey);
            cevh = CustomRecyclerViewUtils.findChildViewHolderUnderWithoutTranslation(fc.rv, cx, ey);
            eevh = CustomRecyclerViewUtils.findChildViewHolderUnderWithoutTranslation(fc.rv, ex, ey);
        }

        int sState = 0;
        int eState = 0;

        if (csvh != null) {
            sState |= 1;
            if (csvh == ssvh) {
                sState |= 2;
            }
            if (csvh == esvh) {
                sState |= 4;
            }
        }

        if (cevh != null) {
            eState |= 1;
            if (cevh == sevh) {
                eState |= 2;
            }
            if (cevh == eevh) {
                eState |= 4;
            }
        }

        int sCount = Integer.bitCount(sState);
        int eCount = Integer.bitCount(eState);

        if (sSpanIndex != draggingItemSpanIndex && sSpanIndex == eSpanIndex) {
            if (sCount == 3) {
                swapTargetHolder = csvh;
            } else if (eCount == 3) {
                swapTargetHolder = cevh;
            }
        }

        if (swapTargetHolder == null) {
            if (sCount == 2 && eCount != 2) {
                swapTargetHolder = csvh;
            } else if (eCount == 2 && sCount != 2) {
                swapTargetHolder = cevh;
            }
        }

        if (swapTargetHolder != null) {
            int swapTargetItemSpanIndex = CustomRecyclerViewUtils.getSpanIndex(swapTargetHolder);

            if (draggingItemSpanIndex == swapTargetItemSpanIndex) {
                if (overlayItemOrigin <= draggingItemOrigin) {
                    // upward or left
                    if (((sState | eState) & 2) != 0) {
                        swapTargetHolder = null;
                    }
                } else {
                    // downward or right
                    if (((sState | eState) & 4) != 0) {
                        swapTargetHolder = null;
                    }
                }
            }
        }

        return swapTargetHolder;
    }

    private static RecyclerView.ViewHolder findSwapTargetItemForGridLayoutManagerInternal1(FindSwapTargetContext fc) {
        final int gap = (int) (fc.rv.getContext().getResources().getDisplayMetrics().density * 4);

        int cx = fc.overlayItemLeftNotClipped;
        int cy = fc.overlayItemTopNotClipped;

        cx += (int) (fc.draggingItemInfo.width * 0.5f);
        cy += (int) (fc.draggingItemInfo.height * 0.5f);

        if (fc.vertical) {
            cx = Math.max(cx, fc.rv.getPaddingLeft() + (2 * gap) + 1);
            cx = Math.min(cx, fc.rv.getWidth() - fc.rv.getPaddingRight() - (2 * gap) - 1);
        } else {
            cy = Math.max(cy, fc.rv.getPaddingTop() + (2 * gap) + 1);
            cy = Math.min(cy, fc.rv.getHeight() - fc.rv.getPaddingBottom() - (2 * gap) - 1);
        }

        RecyclerView.ViewHolder vh1 = CustomRecyclerViewUtils.findChildViewHolderUnderWithoutTranslation(fc.rv, cx - gap, cy - gap);
        if (vh1 == null || vh1 == fc.draggingItem) {
            return vh1;
        }
        RecyclerView.ViewHolder vh2 = CustomRecyclerViewUtils.findChildViewHolderUnderWithoutTranslation(fc.rv, cx + gap, cy - gap);
        if (vh2 == null || vh2 == fc.draggingItem) {
            return vh2;
        }
        RecyclerView.ViewHolder vh3 = CustomRecyclerViewUtils.findChildViewHolderUnderWithoutTranslation(fc.rv, cx - gap, cy + gap);
        if (vh3 == null || vh3 == fc.draggingItem) {
            return vh3;
        }
        RecyclerView.ViewHolder vh4 = CustomRecyclerViewUtils.findChildViewHolderUnderWithoutTranslation(fc.rv, cx + gap, cy + gap);
        if (vh4 == null || vh4 == fc.draggingItem) {
            return vh4;
        }

        if (!(vh1 == vh2 && vh1 == vh3 && vh1 == vh4)) {
            return null;
        }

        return vh1;
    }

    private static RecyclerView.ViewHolder findSwapTargetItemForGridLayoutManagerInternal2(FindSwapTargetContext fc) {

        final int spanCount = CustomRecyclerViewUtils.getSpanCount(fc.rv);
        final int height = fc.rv.getHeight();
        final int width = fc.rv.getWidth();
        final int paddingLeft = (fc.vertical) ? fc.rv.getPaddingLeft() : 0;
        final int paddingTop = (!fc.vertical) ? fc.rv.getPaddingTop() : 0;
        final int paddingRight = (fc.vertical) ? fc.rv.getPaddingRight() : 0;
        final int paddingBottom = (!fc.vertical) ? fc.rv.getPaddingBottom() : 0;
        final int columnWidth = (width - paddingLeft - paddingRight) / spanCount;
        final int rowHeight = (height - paddingTop - paddingBottom) / spanCount;

        final int cx = fc.overlayItemLeft + fc.draggingItemInfo.width / 2;
        final int cy = fc.overlayItemTop + fc.draggingItemInfo.height / 2;

        for (int i = spanCount - 1; i >= 0; i--) {
            int cx2 = (fc.vertical) ? (paddingLeft + (columnWidth * i) + (columnWidth / 2)) : cx;
            int cy2 = (!fc.vertical) ? (paddingTop + (rowHeight * i) + (rowHeight / 2)) : cy;
            RecyclerView.ViewHolder vh2 = CustomRecyclerViewUtils.findChildViewHolderUnderWithoutTranslation(fc.rv, cx2, cy2);

            if (vh2 != null) {
                int itemCount = fc.rv.getLayoutManager().getItemCount();
                int pos = vh2.getAdapterPosition();

                if ((pos != RecyclerView.NO_POSITION) && (pos == itemCount - 1)) {
                    return vh2;
                }
                break;
            }
        }

        return null;
    }

    private static RecyclerView.ViewHolder findSwapTargetItemForLinearLayoutManager(FindSwapTargetContext fc, boolean alternative) {
        RecyclerView.ViewHolder swapTargetHolder = null;

        if (fc.draggingItem == null) {
            return null;
        }

        if (!fc.checkCanSwap && !alternative) {
            final int draggingItemPosition = fc.draggingItem.getAdapterPosition();
            final int draggingViewOrigin = (fc.vertical) ? fc.draggingItem.itemView.getTop() : fc.draggingItem.itemView.getLeft();
            final int overlayItemOrigin = (fc.vertical) ? fc.overlayItemTop : fc.overlayItemLeft;

            if (overlayItemOrigin < draggingViewOrigin) {
                if (draggingItemPosition > 0) {
                    swapTargetHolder = fc.rv.findViewHolderForAdapterPosition(draggingItemPosition - 1);
                }
            } else if (overlayItemOrigin > draggingViewOrigin) {
                if (draggingItemPosition < (fc.rv.getAdapter().getItemCount() - 1)) {
                    swapTargetHolder = fc.rv.findViewHolderForAdapterPosition(draggingItemPosition + 1);
                }
            }
        } else {
            final float gap = fc.draggingItem.itemView.getResources().getDisplayMetrics().density * 8;
            final float hgap = Math.min(fc.draggingItemInfo.width * 0.2f, gap);
            final float vgap = Math.min(fc.draggingItemInfo.height * 0.2f, gap);
            final float cx = fc.overlayItemLeft + fc.draggingItemInfo.width * 0.5f;
            final float cy = fc.overlayItemTop + fc.draggingItemInfo.height * 0.5f;

            final RecyclerView.ViewHolder swapTargetHolder1 = CustomRecyclerViewUtils.findChildViewHolderUnderWithoutTranslation(fc.rv, cx - hgap, cy - vgap);
            final RecyclerView.ViewHolder swapTargetHolder2 = CustomRecyclerViewUtils.findChildViewHolderUnderWithoutTranslation(fc.rv, cx + hgap, cy + vgap);

            if (swapTargetHolder1 == swapTargetHolder2) {
                swapTargetHolder = swapTargetHolder1;
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
    public void setItemSettleBackIntoPlaceAnimationInterpolator(@Nullable Interpolator interpolator) {
        mItemSettleBackIntoPlaceAnimationInterpolator = interpolator;
    }

    /**
     * Gets the interpolator which ise used for "settle back into place" animation.
     *
     * @return Interpolator which is used for "settle back into place" animation
     */
    public
    @Nullable
    Interpolator getItemSettleBackIntoPlaceAnimationInterpolator() {
        return mItemSettleBackIntoPlaceAnimationInterpolator;
    }

    /*package*/ void onDraggingItemViewRecycled() {
        mDraggingItemViewHolder = null;
        mDraggingItemDecorator.invalidateDraggingItem();
    }

    /*package*/ void onNewDraggingItemViewBound(RecyclerView.ViewHolder holder) {
        mDraggingItemViewHolder = holder;
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
        private static final int MSG_LONGPRESS = 1;
        private static final int MSG_DEFERRED_CANCEL_DRAG = 2;
        private static final int MSG_CHECK_ITEM_VIEW_SIZE_UPDATE = 3;

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
                case MSG_DEFERRED_CANCEL_DRAG:
                    mHolder.cancelDrag(true);
                    break;
                case MSG_CHECK_ITEM_VIEW_SIZE_UPDATE:
                    mHolder.handleOnCheckItemViewSizeUpdate();
                    break;
            }
        }

        public void startLongPressDetection(MotionEvent e, int timeout) {
            cancelLongPressDetection();
            mDownMotionEvent = MotionEvent.obtain(e);
            sendEmptyMessageAtTime(MSG_LONGPRESS, e.getDownTime() + timeout);
        }

        public void cancelLongPressDetection() {
            removeMessages(MSG_LONGPRESS);
            if (mDownMotionEvent != null) {
                mDownMotionEvent.recycle();
                mDownMotionEvent = null;
            }
        }

        public void removeDeferredCancelDragRequest() {
            removeMessages(MSG_DEFERRED_CANCEL_DRAG);
        }

        public void requestDeferredCancelDrag() {
            if (isCancelDragRequested()) {
                return;
            }
            sendEmptyMessage(MSG_DEFERRED_CANCEL_DRAG);
        }

        public boolean isCancelDragRequested() {
            return hasMessages(MSG_DEFERRED_CANCEL_DRAG);
        }

        public void scheduleDraggingItemViewSizeUpdateCheck() {
            sendEmptyMessage(MSG_CHECK_ITEM_VIEW_SIZE_UPDATE);
        }

        public void removeDraggingItemViewSizeUpdateCheckRequest() {
            removeMessages(MSG_CHECK_ITEM_VIEW_SIZE_UPDATE);
        }
    }
}
