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

package com.h6ah4i.android.example.advrecyclerview.demo_s_viewpager;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class PagerSwipeItemFrameLayout extends FrameLayout {
    private boolean mCanSwipeLeft;
    private boolean mCanSwipeRight;

    public PagerSwipeItemFrameLayout(Context context) {
        super(context);
    }

    public PagerSwipeItemFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PagerSwipeItemFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean canScrollHorizontally(int direction) {

        if (mCanSwipeLeft && direction > 0) {
            // return true to avoid view pager consume swipe left (= scroll right) touch events
            return true;
        }
        if (mCanSwipeRight && direction < 0) {
            // return true to avoid view pager consume swipe right (= scroll left) touch events
            return true;
        }

        return false;
    }

    public void setCanSwipeLeft(boolean canSwipeLeft) {
        mCanSwipeLeft = canSwipeLeft;
    }

    public void setCanSwipeRight(boolean canSwipeRight) {
        mCanSwipeRight = canSwipeRight;
    }
}
