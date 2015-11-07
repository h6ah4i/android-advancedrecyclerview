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
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Item decoration which draws drop shadow of each item views.
 */
public class ItemShadowDecorator extends RecyclerView.ItemDecoration {
    private final NinePatchDrawable mShadowDrawable;
    private final Rect mShadowPadding = new Rect();
    private final boolean mCastShadowForTransparentBackgroundItem;

    /**
     * Constructor.
     *
     * @param shadow 9-patch drawable used for drop shadow
     */
    public ItemShadowDecorator(@NonNull NinePatchDrawable shadow) {
        this(shadow, true);
    }

    /**
     * Constructor.
     *
     * @param shadow 9-patch drawable used for drop shadow
     * @param castShadowForTransparentBackgroundItem Whether to cast shadows for transparent items
     */
    public ItemShadowDecorator(@NonNull NinePatchDrawable shadow, boolean castShadowForTransparentBackgroundItem) {
        mShadowDrawable = shadow;
        mShadowDrawable.getPadding(mShadowPadding);
        mCastShadowForTransparentBackgroundItem = castShadowForTransparentBackgroundItem;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final int childCount = parent.getChildCount();

        if (childCount == 0) {
            return;
        }

        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            if (!shouldDrawDropShadow(child)) {
                continue;
            }

            final int tx = (int) (ViewCompat.getTranslationX(child) + 0.5f);
            final int ty = (int) (ViewCompat.getTranslationY(child) + 0.5f);

            final int left = child.getLeft() - mShadowPadding.left;
            final int right = child.getRight() + mShadowPadding.right;
            final int top = child.getTop() - mShadowPadding.top;
            final int bottom = child.getBottom() + mShadowPadding.bottom;

            mShadowDrawable.setBounds(left + tx, top + ty, right + tx, bottom + ty);
            mShadowDrawable.draw(c);
        }
    }

    private boolean shouldDrawDropShadow(View child) {
        if (child.getVisibility() != View.VISIBLE) {
            return false;
        }
        if (ViewCompat.getAlpha(child) != 1.0f) {
            return false;
        }

        Drawable background = child.getBackground();
        if (background == null) {
            return false;
        }

        if (!mCastShadowForTransparentBackgroundItem && (background instanceof ColorDrawable)) {
            //noinspection RedundantCast
            if (((ColorDrawable) background).getAlpha() == 0) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, 0, 0);
    }
}
