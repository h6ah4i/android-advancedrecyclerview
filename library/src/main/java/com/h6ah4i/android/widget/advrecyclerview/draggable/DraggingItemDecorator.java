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

    private int mTranslationX;
    private int mTranslationY;
    private Bitmap mDraggingItemImage;
    private int mTranslationLeftLimit;
    private int mTranslationRightLimit;
    private int mTranslationTopLimit;
    private int mTranslationBottomLimit;
    private int mTouchPositionX;
    private int mTouchPositionY;
    private NinePatchDrawable mShadowDrawable;
    private final Rect mShadowPadding = new Rect();
    private boolean mStarted;
    private boolean mIsScrolling;
    private ItemDraggableRange mRange;
    private int mLayoutOrientation;
    private DraggingItemInfo mDraggingItemInfo;

    public DraggingItemDecorator(RecyclerView recyclerView, RecyclerView.ViewHolder draggingItem, ItemDraggableRange range) {
        super(recyclerView, draggingItem);
        mRange = range;
    }

    private static int clip(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
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

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        // NOTE:
        // On lollipop or later, View has Z-axis property and no needed to draw the dragging view manually.
        // However, if the RecyclerView has any other decorations or RecyclerView is in scrolling state,
        // need to draw it to avoid visual corruptions.
        if (mDraggingItemImage != null) {
            final float left = mTranslationX - mShadowPadding.left;
            final float top = mTranslationY - mShadowPadding.top;
            c.drawBitmap(mDraggingItemImage, left, top, null);
        }
    }

    public void start(MotionEvent e, DraggingItemInfo draggingItemInfo) {
        if (mStarted) {
            return;
        }

        final View itemView = mDraggingItemViewHolder.itemView;

        mDraggingItemInfo = draggingItemInfo;
        mDraggingItemImage = createDraggingItemImage(itemView, mShadowDrawable);

        mTranslationLeftLimit = mRecyclerView.getPaddingLeft();
        mTranslationTopLimit = mRecyclerView.getPaddingTop();
        mLayoutOrientation = CustomRecyclerViewUtils.getOrientation(mRecyclerView);

        // hide
        itemView.setVisibility(View.INVISIBLE);

        update(e, true);

        mRecyclerView.addItemDecoration(this);

        mStarted = true;
    }

    public void updateDraggingItemView(DraggingItemInfo info, RecyclerView.ViewHolder vh) {
        if (!mStarted) {
            return;
        }
        if (mDraggingItemViewHolder != vh) {
            invalidateDraggingItem();
            mDraggingItemViewHolder = vh;
        }
        mDraggingItemImage = createDraggingItemImage(vh.itemView, mShadowDrawable);
        mDraggingItemInfo = info;
        refresh(true);
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
        if (mDraggingItemViewHolder != null) {
            moveToDefaultPosition(mDraggingItemViewHolder.itemView, animate);
        }

        // show
        if (mDraggingItemViewHolder != null) {
            mDraggingItemViewHolder.itemView.setVisibility(View.VISIBLE);
        }
        mDraggingItemViewHolder = null;

        if (mDraggingItemImage != null) {
            mDraggingItemImage.recycle();
            mDraggingItemImage = null;
        }

        mRange = null;
        mTranslationX = 0;
        mTranslationY = 0;
        mTranslationLeftLimit = 0;
        mTranslationRightLimit = 0;
        mTranslationTopLimit = 0;
        mTranslationBottomLimit = 0;
        mTouchPositionX = 0;
        mTouchPositionY = 0;
        mStarted = false;
    }

    public boolean update(MotionEvent e, boolean force) {
        mTouchPositionX = (int) (e.getX() + 0.5f);
        mTouchPositionY = (int) (e.getY() + 0.5f);

        return refresh(force);
    }

    public boolean refresh(boolean force) {
        final int prevTranslationX = mTranslationX;
        final int prevTranslationY = mTranslationY;

        updateTranslationOffset();

        final boolean updated = (prevTranslationX != mTranslationX) || (prevTranslationY != mTranslationY);

        if (updated || force) {
            updateDraggingItemPosition(mTranslationX, mTranslationY);
            ViewCompat.postInvalidateOnAnimation(mRecyclerView);
        }

        return updated;
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

    public int getDraggingItemTranslationX() {
        return mTranslationX;
    }

    public int getDraggingItemMoveOffsetY() {
        return mTranslationY - mDraggingItemInfo.initialItemTop;
    }

    public int getDraggingItemMoveOffsetX() {
        return mTranslationX - mDraggingItemInfo.initialItemLeft;
    }

    private void updateTranslationOffset() {
        final RecyclerView rv = mRecyclerView;
        final int childCount = rv.getChildCount();

        if (childCount > 0) {
            mTranslationLeftLimit = 0;
            mTranslationRightLimit = rv.getWidth() - mDraggingItemInfo.width;

            mTranslationTopLimit = 0;
            mTranslationBottomLimit = rv.getHeight() - mDraggingItemInfo.height;

            switch (mLayoutOrientation) {
                case CustomRecyclerViewUtils.ORIENTATION_VERTICAL: {
                    mTranslationLeftLimit += rv.getPaddingLeft();
                    mTranslationRightLimit -= rv.getPaddingRight();
                    break;
                }
                case CustomRecyclerViewUtils.ORIENTATION_HORIZONTAL: {
                    mTranslationTopLimit += rv.getPaddingTop();
                    mTranslationBottomLimit -= rv.getPaddingBottom();
                    break;
                }
            }

            mTranslationRightLimit = Math.max(mTranslationLeftLimit, mTranslationRightLimit);
            mTranslationBottomLimit = Math.max(mTranslationTopLimit, mTranslationBottomLimit);

            if (!mIsScrolling) {
                final int firstVisiblePosition = CustomRecyclerViewUtils.findFirstVisibleItemPosition(rv, true);
                final int lastVisiblePosition = CustomRecyclerViewUtils.findLastVisibleItemPosition(rv, true);
                final View firstChild = findRangeFirstItem(rv, mRange, firstVisiblePosition, lastVisiblePosition);
                final View lastChild = findRangeLastItem(rv, mRange, firstVisiblePosition, lastVisiblePosition);

                switch (mLayoutOrientation) {
                    case CustomRecyclerViewUtils.ORIENTATION_VERTICAL: {

                        if (firstChild != null) {
                            mTranslationTopLimit = Math.min(mTranslationBottomLimit, firstChild.getTop());
                        }

                        if (lastChild != null) {
                            final int limit = Math.max(0, lastChild.getBottom() - mDraggingItemInfo.height);
                            mTranslationBottomLimit = Math.min(mTranslationBottomLimit, limit);
                        }
                        break;
                    }
                    case CustomRecyclerViewUtils.ORIENTATION_HORIZONTAL: {
                        if (firstChild != null) {
                            mTranslationLeftLimit = Math.min(mTranslationLeftLimit, firstChild.getLeft());
                        }

                        if (lastChild != null) {
                            final int limit = Math.max(0, lastChild.getRight() - mDraggingItemInfo.width);
                            mTranslationRightLimit = Math.min(mTranslationRightLimit, limit);
                        }
                        break;
                    }
                }
            }
        } else {
            mTranslationRightLimit = mTranslationLeftLimit = rv.getPaddingLeft();
            mTranslationBottomLimit = mTranslationTopLimit = rv.getPaddingTop();
        }

        mTranslationX = mTouchPositionX - mDraggingItemInfo.grabbedPositionX;
        mTranslationY = mTouchPositionY - mDraggingItemInfo.grabbedPositionY;

        mTranslationX = clip(mTranslationX, mTranslationLeftLimit, mTranslationRightLimit);
        mTranslationY = clip(mTranslationY, mTranslationTopLimit, mTranslationBottomLimit);
    }

    private static int toSpanAlignedPosition(int position, int spanCount) {
        if (position == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION;
        }
        return (position / spanCount) * spanCount;
    }

    public boolean isReachedToTopLimit() {
        return (mTranslationY == mTranslationTopLimit);
    }

    public boolean isReachedToBottomLimit() {
        return (mTranslationY == mTranslationBottomLimit);
    }

    public boolean isReachedToLeftLimit() {
        return (mTranslationX == mTranslationLeftLimit);
    }

    public boolean isReachedToRightLimit() {
        return (mTranslationX == mTranslationRightLimit);
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
        if (mDraggingItemViewHolder != null) {
            setItemTranslation(
                    mRecyclerView, mDraggingItemViewHolder,
                    translationX - mDraggingItemViewHolder.itemView.getLeft(),
                    translationY - mDraggingItemViewHolder.itemView.getTop());
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
        return mTranslationY + mDraggingItemInfo.height;
    }

    public int getTranslatedItemPositionLeft() {
        return mTranslationX;
    }

    public int getTranslatedItemPositionRight() {
        return mTranslationX + mDraggingItemInfo.width;
    }

    public void invalidateDraggingItem() {
        if (mDraggingItemViewHolder != null) {
            ViewCompat.setTranslationX(mDraggingItemViewHolder.itemView, 0);
            ViewCompat.setTranslationY(mDraggingItemViewHolder.itemView, 0);
            mDraggingItemViewHolder.itemView.setVisibility(View.VISIBLE);
        }

        mDraggingItemViewHolder = null;
    }

    public void setDraggingItemViewHolder(RecyclerView.ViewHolder holder) {
        if (mDraggingItemViewHolder != null) {
            throw new IllegalStateException("A new view holder is attempt to be assigned before invalidating the older one");
        }

        mDraggingItemViewHolder = holder;

        holder.itemView.setVisibility(View.INVISIBLE);
    }
}