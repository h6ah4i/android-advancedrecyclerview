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
package com.h6ah4i.android.widget.advrecyclerview.swipeable;

import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

class SwipeableViewHolderUtils {
    public static View getSwipeableContainerView(@Nullable RecyclerView.ViewHolder vh) {
        if (vh instanceof SwipeableItemViewHolder) {
            return getSwipeableContainerView((SwipeableItemViewHolder) vh);
        } else {
            return null;
        }
    }

    public static View getSwipeableContainerView(@Nullable SwipeableItemViewHolder vh) {
        if (vh instanceof RecyclerView.ViewHolder) {
            View containerView = vh.getSwipeableContainerView();
            View itemView = ((RecyclerView.ViewHolder) vh).itemView;

            if (containerView == itemView) {
                throw new IllegalStateException("Inconsistency detected! getSwipeableContainerView() returns itemView");
            }

            return containerView;
        } else {
            return null;
        }
    }
}
