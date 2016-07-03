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

package com.h6ah4i.android.example.advrecyclerview.common.widget;

import android.content.Context;
import android.os.Build;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.FrameLayout;

public class ExpandableItemIndicator extends FrameLayout {
    static abstract class Impl {
        public abstract void onInit(Context context, AttributeSet attrs, int defStyleAttr, ExpandableItemIndicator thiz);

        public abstract void setExpandedState(boolean isExpanded, boolean animate);
    }

    private Impl mImpl;

    public ExpandableItemIndicator(Context context) {
        super(context);
        onInit(context, null, 0);
    }

    public ExpandableItemIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        onInit(context, attrs, 0);
    }

    public ExpandableItemIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onInit(context, attrs, defStyleAttr);
    }

    protected boolean shouldUseAnimatedIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        // NOTE: AnimatedVectorDrawableCompat works on API level 11+,
        // but I prefer to use it on API level 16+ only due to performance reason of
        // both hardware and Android platform.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    protected void onInit(Context context, AttributeSet attrs, int defStyleAttr) {
        if (shouldUseAnimatedIndicator(context, attrs, defStyleAttr)) {
            mImpl = new ExpandableItemIndicatorImplAnim();
        } else {
            mImpl = new ExpandableItemIndicatorImplNoAnim();
        }
        mImpl.onInit(context, attrs, defStyleAttr, this);
    }

    @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        super.dispatchFreezeSelfOnly(container);
    }

    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        super.dispatchThawSelfOnly(container);
    }

    public void setExpandedState(boolean isExpanded, boolean animate) {
        mImpl.setExpandedState(isExpanded, animate);
    }
}
