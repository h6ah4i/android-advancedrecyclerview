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
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.view.View;
import android.view.animation.Interpolator;

import com.h6ah4i.android.widget.advrecyclerview.utils.CustomRecyclerViewUtils;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

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
    private int mLayoutType;
    private DraggingItemInfo mDraggingItemInfo;
    private Paint mPaint;
    private long mStartMillis;

    private long mStartAnimationDurationMillis = 0;
    private float mTargetDraggingItemScale = 1.0f;
    private float mTargetDraggingItemRotation = 0.0f;
    private float mTargetDraggingItemAlpha = 1.0f;
    private float mInitialDraggingItemScaleX;
    private float mInitialDraggingItemScaleY;
    private Interpolator mScaleInterpolator = null;
    private Interpolator mRotationInterpolator = null;
    private Interpolator mAlphaInterpolator = null;
    private float mLastDraggingItemScaleX;
    private float mLastDraggingItemScaleY;
    private float mLastDraggingItemRotation;
    private float mLastDraggingItemAlpha;


    public DraggingItemDecorator(RecyclerView recyclerView, RecyclerView.ViewHolder draggingItem, ItemDraggableRange range) {
        super(recyclerView, draggingItem);
        mRange = range;
        mPaint = new Paint();
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
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (mDraggingItemImage == null) {
            return;
        }

        final int elapsedMillis = (int) Math.min(System.currentTimeMillis() - mStartMillis, mStartAnimationDurationMillis);
        final float t = (mStartAnimationDurationMillis > 0) ? ((float) elapsedMillis / mStartAnimationDurationMillis) : 1.0f;
        final float tScale = getInterpolation(mScaleInterpolator, t);
        final float scaleX = tScale * (mTargetDraggingItemScale - mInitialDraggingItemScaleX) + mInitialDraggingItemScaleX;
        final float scaleY = tScale * (mTargetDraggingItemScale - mInitialDraggingItemScaleY) + mInitialDraggingItemScaleY;
        final float alpha = getInterpolation(mAlphaInterpolator, t) * (mTargetDraggingItemAlpha - 1.0f) + 1.0f;
        final float rotation = getInterpolation(mRotationInterpolator, t) * mTargetDraggingItemRotation;

        if (scaleX > 0.0f && scaleY > 0.0f && alpha > 0.0f) {
            mPaint.setAlpha((int) (alpha * 255));

            int savedCount = c.save();

            c.translate(mTranslationX + mDraggingItemInfo.grabbedPositionX, mTranslationY + mDraggingItemInfo.grabbedPositionY);
            c.scale(scaleX, scaleY);
            c.rotate(rotation);
            c.translate(-(mShadowPadding.left + mDraggingItemInfo.grabbedPositionX), -(mShadowPadding.top + mDraggingItemInfo.grabbedPositionY));

            c.drawBitmap(mDraggingItemImage, 0, 0, mPaint);
            c.restoreToCount(savedCount);
        }

        if (t < 1.0f) {
            ViewCompat.postInvalidateOnAnimation(mRecyclerView);
        }

        mLastDraggingItemScaleX = scaleX;
        mLastDraggingItemScaleY = scaleY;
        mLastDraggingItemRotation = rotation;
        mLastDraggingItemAlpha = alpha;
    }

    public void setupDraggingItemEffects(DraggingItemEffectsInfo info) {
        mStartAnimationDurationMillis = info.durationMillis;
        mTargetDraggingItemScale = info.scale;
        mScaleInterpolator = info.scaleInterpolator;
        mTargetDraggingItemRotation = info.rotation;
        mRotationInterpolator = info.rotationInterpolator;
        mTargetDraggingItemAlpha = info.alpha;
        mAlphaInterpolator = info.alphaInterpolator;
    }

    public void start(DraggingItemInfo draggingItemInfo, int touchX, int touchY) {
        if (mStarted) {
            return;
        }

        final View itemView = mDraggingItemViewHolder.itemView;

        mDraggingItemInfo = draggingItemInfo;
        mDraggingItemImage = createDraggingItemImage(itemView, mShadowDrawable);

        mTranslationLeftLimit = mRecyclerView.getPaddingLeft();
        mTranslationTopLimit = mRecyclerView.getPaddingTop();
        mLayoutOrientation = CustomRecyclerViewUtils.getOrientation(mRecyclerView);
        mLayoutType = CustomRecyclerViewUtils.getLayoutType(mRecyclerView);

        mInitialDraggingItemScaleX = itemView.getScaleX();
        mInitialDraggingItemScaleY = itemView.getScaleY();

        mLastDraggingItemScaleX = 1.0f;
        mLastDraggingItemScaleY = 1.0f;
        mLastDraggingItemRotation = 0.0f;
        mLastDraggingItemAlpha = 1.0f;

        // hide
        itemView.setVisibility(View.INVISIBLE);

        update(touchX, touchY, true);

        mRecyclerView.addItemDecoration(this);
        mStartMillis = System.currentTimeMillis();

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
            moveToDefaultPosition(
                    mDraggingItemViewHolder.itemView,
                    mLastDraggingItemScaleX, mLastDraggingItemScaleY,
                    mLastDraggingItemRotation, mLastDraggingItemAlpha,
                    animate);
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

    public boolean update(int touchX, int touchY, boolean force) {
        mTouchPositionX = touchX;
        mTouchPositionY = touchY;

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
                    mTranslationTopLimit = -mDraggingItemInfo.height;
                    mTranslationBottomLimit = rv.getHeight();
                    mTranslationLeftLimit += rv.getPaddingLeft();
                    mTranslationRightLimit -= rv.getPaddingRight();
                    break;
                }
                case CustomRecyclerViewUtils.ORIENTATION_HORIZONTAL: {
                    mTranslationTopLimit += rv.getPaddingTop();
                    mTranslationBottomLimit -= rv.getPaddingBottom();
                    mTranslationLeftLimit = -mDraggingItemInfo.width;
                    mTranslationRightLimit = rv.getWidth();
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

        if (CustomRecyclerViewUtils.isLinearLayout(mLayoutType)) {
            mTranslationX = clip(mTranslationX, mTranslationLeftLimit, mTranslationRightLimit);
            mTranslationY = clip(mTranslationY, mTranslationTopLimit, mTranslationBottomLimit);
        }
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
        int viewTop = v.getTop();
        int viewLeft = v.getLeft();
        int viewWidth = v.getWidth();
        int viewHeight = v.getHeight();

        int canvasWidth = viewWidth + mShadowPadding.left + mShadowPadding.right;
        int canvasHeight = viewHeight + mShadowPadding.top + mShadowPadding.bottom;

        v.measure(
                View.MeasureSpec.makeMeasureSpec(viewWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(viewHeight, View.MeasureSpec.EXACTLY));

        v.layout(viewLeft, viewTop, viewLeft + viewWidth, viewTop + viewHeight);

        final Bitmap bitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);

        final Canvas canvas = new Canvas(bitmap);

        if (shadow != null) {
            shadow.setBounds(0, 0, canvasWidth, canvasHeight);
            shadow.draw(canvas);
        }

        final int savedCount = canvas.save();
        // NOTE: Explicitly set clipping rect. This is required on Gingerbread.
        canvas.clipRect(mShadowPadding.left, mShadowPadding.top, canvasWidth - mShadowPadding.right, canvasHeight - mShadowPadding.bottom);
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
            mDraggingItemViewHolder.itemView.setTranslationX(0);
            mDraggingItemViewHolder.itemView.setTranslationY(0);
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

    private static float getInterpolation(Interpolator interpolator, float input) {
        return (interpolator != null) ? interpolator.getInterpolation(input) : input;
    }
}
