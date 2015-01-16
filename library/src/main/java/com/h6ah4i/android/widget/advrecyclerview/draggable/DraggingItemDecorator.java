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

class DraggingItemDecorator extends RecyclerView.ItemDecoration {
    @SuppressWarnings("unused")
    private static final String TAG = "DraggingItemDecorator";

    private RecyclerView mRecyclerView;
    private RecyclerView.ViewHolder mDraggingItem;
    private RecyclerView.ViewHolder mSwapTargetItem;
    private int mGrabbedPositionY;
    private int mTranslationY;
    private int mRecyclerViewPaddingLeft;
    private Bitmap mDraggingItemImage;
    private int mTranslationTopLimit;
    private int mTranslationBottomLimit;
    private int mGrabbedItemHeight;
    private int mTouchPositionY;
    private NinePatchDrawable mShadowDrawable;
    private Rect mShadowPadding = new Rect();
    private Rect mDraggingItemMargins = new Rect();
    private Rect mDraggingItemDecorationOffsets = new Rect();
    private boolean mStarted;
    private boolean mIsScrolling;

    public DraggingItemDecorator(RecyclerView recyclerView, RecyclerView.ViewHolder draggingItem) {
        mRecyclerView = recyclerView;
        mDraggingItem = draggingItem;

        CustomRecyclerViewUtils.getLayoutMargins(mDraggingItem.itemView, mDraggingItemMargins);
        CustomRecyclerViewUtils.getDecorationOffsets(
                mRecyclerView.getLayoutManager(), mDraggingItem.itemView, mDraggingItemDecorationOffsets);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        // NOTE:
        // On lollipop or later, View has Z-axis property and no needed to draw the dragging view manually.
        // However, if the RecyclerView has any other decorations or RecyclerView is in scrolling state,
        // need to draw it to avoid visual corruptions.
        if (mDraggingItemImage != null) {
            final float left = mRecyclerViewPaddingLeft + mDraggingItemMargins.left - mShadowPadding.left;
            final float top = /*mDraggingItemMargins.top +*/ mTranslationY - mShadowPadding.top;
            c.drawBitmap(mDraggingItemImage, left, top, null);
        }
    }

    public void start(MotionEvent e, float grabbedPositionY) {
        if (mStarted) {
            return;
        }

        final View itemView = mDraggingItem.itemView;

        mGrabbedPositionY = (int) (grabbedPositionY + 0.5f);

        // draw the grabbed item on bitmap
        mDraggingItemImage = createDraggingItemImage(itemView, mShadowDrawable);

        mGrabbedItemHeight = itemView.getHeight();
        mTranslationTopLimit = mRecyclerView.getPaddingTop();
        mRecyclerViewPaddingLeft = mRecyclerView.getPaddingLeft();

        // hide
        itemView.setVisibility(View.INVISIBLE);

        update(e);

        mRecyclerView.addItemDecoration(this);

        mStarted = true;
    }

    public void finish() {
        if (mStarted) {
            mRecyclerView.removeItemDecoration(this);
        }

        final RecyclerView.ItemAnimator itemAnimator = mRecyclerView.getItemAnimator();
        if (itemAnimator != null) {
            itemAnimator.endAnimations();
        }
        mRecyclerView.stopScroll();

        updateDraggingItemPosition(0);

        ViewCompat.setTranslationY(mDraggingItem.itemView, 0.0f);

        // show
        mDraggingItem.itemView.setVisibility(View.VISIBLE);
        mDraggingItem = null;

        if (mDraggingItemImage != null) {
            mDraggingItemImage.recycle();
            mDraggingItemImage = null;
        }

        if (mSwapTargetItem != null) {
            ViewCompat.setTranslationY(mSwapTargetItem.itemView, 0.0f);
            mSwapTargetItem = null;
        }

        mGrabbedPositionY = 0;
        mTranslationY = 0;
        mTranslationTopLimit = 0;
        mTranslationBottomLimit = 0;
        mRecyclerViewPaddingLeft = 0;
        mGrabbedItemHeight = 0;
        mTouchPositionY = 0;
        mStarted = false;
    }

    public void update(MotionEvent e) {
        mTouchPositionY = (int) (e.getY() + 0.5f);
        refresh();
    }

    public void refresh() {
        updateTranslationOffset();
        updateDraggingItemPosition(mTranslationY);

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
        final int childCount = mRecyclerView.getChildCount();
        if (childCount > 0) {
            mTranslationBottomLimit = Math.max(0, (mRecyclerView.getHeight() - mRecyclerView.getPaddingBottom() - mGrabbedItemHeight));

            if (!mIsScrolling) {
                final View lastChild = mRecyclerView.getChildAt(childCount - 1);
                mTranslationBottomLimit = Math.min(mTranslationBottomLimit, lastChild.getTop());
            }
        } else {
            mTranslationBottomLimit = mTranslationTopLimit;
        }

        mTranslationY = mTouchPositionY + -mGrabbedPositionY;
        mTranslationY = Math.min(Math.max(mTranslationY, mTranslationTopLimit), mTranslationBottomLimit);
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

    private void updateDraggingItemPosition(int translationY) {
        // NOTE: Need to update the view position to make other decorations work properly while dragging
        setItemTranslationY(mRecyclerView, mDraggingItem, translationY - mDraggingItem.itemView.getTop());
    }

    private static void setItemTranslationY(RecyclerView rv, RecyclerView.ViewHolder holder, float y) {
        final RecyclerView.ItemAnimator itemAnimator = rv.getItemAnimator();
        if (itemAnimator != null) {
            itemAnimator.endAnimation(holder);
        }
        ViewCompat.setTranslationY(holder.itemView, y);
    }

    public void setIsScrolling(boolean isScrolling) {
        mIsScrolling = isScrolling;
    }
}