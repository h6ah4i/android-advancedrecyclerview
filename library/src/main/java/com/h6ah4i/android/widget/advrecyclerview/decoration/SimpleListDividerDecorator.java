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

package com.h6ah4i.android.widget.advrecyclerview.decoration;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Item decoration which draws item divider between each items.
 */
public class SimpleListDividerDecorator extends RecyclerView.ItemDecoration {
    private final Drawable mHorizontalDrawable;
    private final Drawable mVerticalDrawable;
    private final int mHorizontalDividerHeight;
    private final int mVerticalDividerWidth;
    private final boolean mOverlap;

    /**
     * Constructor.
     *
     * @param divider horizontal divider drawable
     * @param overlap whether the divider is drawn overlapped on bottom of the item.
     */
    public SimpleListDividerDecorator(@Nullable Drawable divider, boolean overlap) {
        this(divider, null, overlap);
    }

    /**
     * Constructor.
     *
     * @param horizontalDivider horizontal divider drawable
     * @param verticalDivider   vertical divider drawable
     * @param overlap           whether the divider is drawn overlapped on bottom (or right) of the item.
     */
    public SimpleListDividerDecorator(@Nullable Drawable horizontalDivider, @Nullable Drawable verticalDivider, boolean overlap) {
        mHorizontalDrawable = horizontalDivider;
        mVerticalDrawable = verticalDivider;
        mHorizontalDividerHeight = (mHorizontalDrawable != null) ? mHorizontalDrawable.getIntrinsicHeight() : 0;
        mVerticalDividerWidth = (mVerticalDrawable != null) ? mVerticalDrawable.getIntrinsicWidth() : 0;
        mOverlap = overlap;
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        final int childCount = parent.getChildCount();


        if (childCount == 0) {
            return;
        }

        final float xPositionThreshold = (mOverlap) ? 1.0f : (mVerticalDividerWidth + 1.0f); // [px]
        final float yPositionThreshold = (mOverlap) ? 1.0f : (mHorizontalDividerHeight + 1.0f); // [px]
        final float zPositionThreshold = 1.0f; // [px]

        for (int i = 0; i < childCount - 1; i++) {
            final View child = parent.getChildAt(i);
            final View nextChild = parent.getChildAt(i + 1);

            if ((child.getVisibility() != View.VISIBLE) ||
                    (nextChild.getVisibility() != View.VISIBLE)) {
                continue;
            }

            // check if the next item is placed at the bottom or right
            final float childBottom = child.getBottom() + child.getTranslationY();
            final float nextChildTop = nextChild.getTop() + nextChild.getTranslationY();
            final float childRight = child.getRight() + child.getTranslationX();
            final float nextChildLeft = nextChild.getLeft() + nextChild.getTranslationX();

            if (!(((mHorizontalDividerHeight != 0) && (Math.abs(nextChildTop - childBottom) < yPositionThreshold)) ||
                    ((mVerticalDividerWidth != 0) && (Math.abs(nextChildLeft - childRight) < xPositionThreshold)))) {
                continue;
            }

            // check if the next item is placed on the same plane
            final float childZ = ViewCompat.getTranslationZ(child) + ViewCompat.getElevation(child);
            final float nextChildZ = ViewCompat.getTranslationZ(nextChild) + ViewCompat.getElevation(nextChild);

            if (!(Math.abs(nextChildZ - childZ) < zPositionThreshold)) {
                continue;
            }

            final float childAlpha = child.getAlpha();
            final float nextChildAlpha = nextChild.getAlpha();

            final int tx = (int) (child.getTranslationX() + 0.5f);
            final int ty = (int) (child.getTranslationY() + 0.5f);

            if (mHorizontalDividerHeight != 0) {
                final int left = child.getLeft();
                final int right = child.getRight();
                final int top = child.getBottom() - (mOverlap ? mHorizontalDividerHeight : 0);
                final int bottom = top + mHorizontalDividerHeight;

                mHorizontalDrawable.setAlpha((int) ((0.5f * 255) * (childAlpha + nextChildAlpha) + 0.5f));
                mHorizontalDrawable.setBounds(left + tx, top + ty, right + tx, bottom + ty);
                mHorizontalDrawable.draw(c);
            }

            if (mVerticalDividerWidth != 0) {
                final int left = child.getRight() - (mOverlap ? mVerticalDividerWidth : 0);
                final int right = left + mVerticalDividerWidth;
                final int top = child.getTop();
                final int bottom = child.getBottom();

                mVerticalDrawable.setAlpha((int) ((0.5f * 255) * (childAlpha + nextChildAlpha) + 0.5f));
                mVerticalDrawable.setBounds(left + tx, top + ty, right + tx, bottom + ty);
                mVerticalDrawable.draw(c);
            }
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (mOverlap) {
            outRect.set(0, 0, 0, 0);
        } else {
            outRect.set(0, 0, mVerticalDividerWidth, mHorizontalDividerHeight);
        }
    }
}
