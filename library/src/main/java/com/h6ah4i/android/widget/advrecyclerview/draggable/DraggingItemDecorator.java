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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import com.h6ah4i.android.widget.advrecyclerview.utils.CustomRecyclerViewUtils;

class DraggingItemDecorator extends BaseDraggableItemDecorator {
    @SuppressWarnings("unused")
    private static final String TAG = "DraggingItemDecorator";

    private int mGrabbedPositionX;
    private int mGrabbedPositionY;
    private int mTranslationX;
    private int mTranslationY;
    private int mRecyclerViewPaddingLeft;
    private Bitmap mDraggingItemImage;
    private int mTranslationLeftLimit;
    private int mTranslationRightLimit;
    private int mTranslationTopLimit;
    private int mTranslationBottomLimit;
    private int mGrabbedItemWidth;
    private int mGrabbedItemHeight;
    private int mTouchPositionX;
    private int mTouchPositionY;
    private NinePatchDrawable mShadowDrawable;
    private Rect mShadowPadding = new Rect();
    private Rect mDraggingItemMargins = new Rect();
    private boolean mStarted;
    private boolean mIsScrolling;
    private ItemDraggableRange mRange;

    public DraggingItemDecorator(RecyclerView recyclerView, RecyclerView.ViewHolder draggingItem, ItemDraggableRange range) {
        super(recyclerView, draggingItem);
        mRange = range;
        CustomRecyclerViewUtils.getLayoutMargins(mDraggingItem.itemView, mDraggingItemMargins);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        // NOTE:
        // On lollipop or later, View has Z-axis property and no needed to draw the dragging view manually.
        // However, if the RecyclerView has any other decorations or RecyclerView is in scrolling state,
        // need to draw it to avoid visual corruptions.
        if (mDraggingItemImage != null) {
            final float left = mTranslationX + /*mRecyclerViewPaddingLeft*/ + mDraggingItemMargins.left - mShadowPadding.left;
            final float top = /*mDraggingItemMargins.top +*/ mTranslationY - mShadowPadding.top;
            c.drawBitmap(mDraggingItemImage, left, top, null);
        }
    }

    public void start(MotionEvent e, float grabbedPositionX, float grabbedPositionY) {
        if (mStarted) {
            return;
        }

        final View itemView = mDraggingItem.itemView;

        mGrabbedPositionX = (int) (grabbedPositionX + 0.5f);
        mGrabbedPositionY = (int) (grabbedPositionY + 0.5f);

        // draw the grabbed item on bitmap
        mDraggingItemImage = createDraggingItemImage(itemView, mShadowDrawable);

        mGrabbedItemWidth = itemView.getWidth();
        mGrabbedItemHeight = itemView.getHeight();
        mTranslationLeftLimit = mRecyclerView.getPaddingLeft();
        mTranslationTopLimit = mRecyclerView.getPaddingTop();
        mRecyclerViewPaddingLeft = mRecyclerView.getPaddingLeft();

        // hide
        itemView.setVisibility(View.INVISIBLE);

        update(e);

        mRecyclerView.addItemDecoration(this);

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

        // return to default position
        updateDraggingItemPosition(mTranslationX, mTranslationY);
        if (mDraggingItem != null) {
            moveToDefaultPosition(mDraggingItem.itemView, animate);
        }

        // show
        if (mDraggingItem != null) {
            mDraggingItem.itemView.setVisibility(View.VISIBLE);
        }
        mDraggingItem = null;

        if (mDraggingItemImage != null) {
            mDraggingItemImage.recycle();
            mDraggingItemImage = null;
        }

        mRange = null;
        mGrabbedPositionX = 0;
        mGrabbedPositionY = 0;
        mTranslationX = 0;
        mTranslationY = 0;
        mTranslationLeftLimit = 0;
        mTranslationRightLimit = 0;
        mTranslationTopLimit = 0;
        mTranslationBottomLimit = 0;
        mRecyclerViewPaddingLeft = 0;
        mGrabbedItemWidth = 0;
        mGrabbedItemHeight = 0;
        mTouchPositionX = 0;
        mTouchPositionY = 0;
        mStarted = false;
    }

    public void update(MotionEvent e) {
        mTouchPositionX = (int) (e.getX() + 0.5f);
        mTouchPositionY = (int) (e.getY() + 0.5f);
        refresh();
    }

    public void refresh() {
        updateTranslationOffset();
        updateDraggingItemPosition(mTranslationX, mTranslationY);

        ViewCompat.postInvalidateOnAnimation(mRecyclerView);
    }

    public void setShadowDrawable(NinePatchDrawable shadowDrawable) {
        mShadowDrawable = shadowDrawable;

        if (mShadowDrawable != null) {
            mShadowDrawable.getPadding(mShadowPadding);
        }
    }

    public int getDraggingItemTranslationY() {
        return mTranslationY;
    }

    private void updateTranslationOffset() {
        final RecyclerView rv = mRecyclerView;
        final int childCount = rv.getChildCount();
        if (childCount > 0) {
            mTranslationLeftLimit = rv.getPaddingLeft();
            mTranslationRightLimit = Math.max(0, (rv.getWidth() - rv.getPaddingLeft() - mGrabbedItemWidth));

            mTranslationTopLimit = rv.getPaddingTop();
            mTranslationBottomLimit = Math.max(0, (rv.getHeight() - rv.getPaddingBottom() - mGrabbedItemHeight));

            if (!mIsScrolling) {
                final int firstVisiblePosition = CustomRecyclerViewUtils.findFirstVisibleItemPosition(rv);
                final int lastVisiblePosition = CustomRecyclerViewUtils.findLastVisibleItemPosition(rv);
                final View topChild = findRangeFirstItem(rv, mRange, firstVisiblePosition, lastVisiblePosition);
                final View bottomChild = findRangeLastItem(rv, mRange, firstVisiblePosition, lastVisiblePosition);

                if (topChild != null) {
                    mTranslationTopLimit = Math.min(mTranslationBottomLimit, topChild.getTop());
                }

                if (bottomChild != null) {
                    mTranslationBottomLimit = Math.min(mTranslationBottomLimit, bottomChild.getTop());
                }

                // XXX horizontal handling
            }
        } else {
            mTranslationRightLimit = mTranslationLeftLimit = rv.getPaddingLeft();
            mTranslationBottomLimit = mTranslationTopLimit = rv.getPaddingTop();
        }

        mTranslationX = mTouchPositionX - mGrabbedPositionX;
        mTranslationY = mTouchPositionY - mGrabbedPositionY;

        mTranslationX = clip(mTranslationX, mTranslationLeftLimit, mTranslationRightLimit);
        mTranslationY = clip(mTranslationY, mTranslationTopLimit, mTranslationBottomLimit);
    }

    private static int clip(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }

    public boolean isReachedToTopLimit() {
        return (mTranslationY == mTranslationTopLimit);
    }

    public boolean isReachedToBottomLimit() {
        return (mTranslationY == mTranslationBottomLimit);
    }

    private Bitmap createDraggingItemImage(View v, NinePatchDrawable shadow) {
        int width = v.getWidth() + mShadowPadding.left + mShadowPadding.right;
        int height = v.getHeight() + mShadowPadding.top + mShadowPadding.bottom;

        final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        final Canvas canvas = new Canvas(bitmap);

        if (shadow != null) {
            shadow.setBounds(0, 0, width, height);
            shadow.draw(canvas);
        }

        final int savedCount = canvas.save(Canvas.CLIP_SAVE_FLAG | Canvas.MATRIX_SAVE_FLAG);
        // NOTE: Explicitly set clipping rect. This is required on Gingerbread.
        canvas.clipRect(mShadowPadding.left, mShadowPadding.top, width - mShadowPadding.right, height - mShadowPadding.bottom);
        canvas.translate(mShadowPadding.left, mShadowPadding.top);
        v.draw(canvas);
        canvas.restoreToCount(savedCount);

        return bitmap;
    }

    private void updateDraggingItemPosition(float translationX, int translationY) {
        // NOTE: Need to update the view position to make other decorations work properly while dragging
        if (mDraggingItem != null) {
            setItemTranslation(
                    mRecyclerView, mDraggingItem,
                    translationX - mDraggingItem.itemView.getLeft(),
                    translationY - mDraggingItem.itemView.getTop());
        }
    }

    public void setIsScrolling(boolean isScrolling) {
        if (mIsScrolling == isScrolling) {
            return;
        }

        mIsScrolling = isScrolling;

    }

    public int getTranslatedItemPositionTop() {
        return mTranslationY;
    }

    public int getTranslatedItemPositionBottom() {
        return mTranslationY + mGrabbedItemHeight;
    }


    private static View findRangeFirstItem(RecyclerView rv, ItemDraggableRange range, int firstVisiblePosition, int lastVisiblePosition) {
        if (firstVisiblePosition == RecyclerView.NO_POSITION || lastVisiblePosition == RecyclerView.NO_POSITION) {
            return null;
        }

        View v = null;

        final int childCount = rv.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View v2 = rv.getChildAt(i);
            final RecyclerView.ViewHolder vh = rv.getChildViewHolder(v2);

            if (vh != null) {
                final int position = vh.getLayoutPosition();

                if ((position >= firstVisiblePosition) &&
                        (position <= lastVisiblePosition) &&
                        range.checkInRange(position)) {
                    v = v2;
                    break;
                }

            }
        }

        return v;
    }

    private static View findRangeLastItem(RecyclerView rv, ItemDraggableRange range, int firstVisiblePosition, int lastVisiblePosition) {
        if (firstVisiblePosition == RecyclerView.NO_POSITION || lastVisiblePosition == RecyclerView.NO_POSITION) {
            return null;
        }

        View v = null;

        final int childCount = rv.getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            final View v2 = rv.getChildAt(i);
            final RecyclerView.ViewHolder vh = rv.getChildViewHolder(v2);

            if (vh != null) {
                final int position = vh.getLayoutPosition();

                if ((position >= firstVisiblePosition) &&
                        (position <= lastVisiblePosition) &&
                        range.checkInRange(position)) {
                    v = v2;
                    break;
                }
            }
        }

        return v;
    }

    public void invalidateDraggingItem() {
        if (mDraggingItem != null) {
            mDraggingItem.itemView.setVisibility(View.VISIBLE);
        }

        mDraggingItem = null;
    }

    public void setDraggingItemViewHolder(RecyclerView.ViewHolder holder) {
        if (mDraggingItem != null) {
            throw new IllegalStateException("A new view holder is attempt to be assigned before invalidating the older one");
        }

        mDraggingItem = holder;

        holder.itemView.setVisibility(View.INVISIBLE);
    }
}