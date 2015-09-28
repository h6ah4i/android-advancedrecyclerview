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

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.h6ah4i.android.example.advrecyclerview.R;
import com.wnafee.vector.MorphButton;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
class ExpandableItemIndicatorImplAnim extends ExpandableItemIndicator.Impl {
    private MorphButton mMorphButton;

    @Override
    public void onInit(Context context, AttributeSet attrs, int defStyleAttr, ExpandableItemIndicator thiz) {
        View v = LayoutInflater.from(context).inflate(R.layout.widget_expandable_item_indicator_anim, thiz, true);
        mMorphButton = (MorphButton) v.findViewById(R.id.morph_button);
    }

    @Override
    public void setExpandedState(boolean isExpanded, boolean animate) {
        MorphButton.MorphState indicatorState = (isExpanded) ? MorphButton.MorphState.END : MorphButton.MorphState.START;

        if (mMorphButton.getState() != indicatorState) {
            mMorphButton.setState(indicatorState, animate);
        }
    }
}
