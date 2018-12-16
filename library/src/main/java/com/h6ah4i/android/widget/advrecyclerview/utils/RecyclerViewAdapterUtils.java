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

package com.h6ah4i.android.widget.advrecyclerview.utils;

import android.view.View;
import android.view.ViewParent;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapterUtils {
    private RecyclerViewAdapterUtils() {
    }

    /**
     * Gets parent RecyclerView instance.
     * @param view Child view of the RecyclerView's item
     * @return Parent RecyclerView instance
     */
    @Nullable
    public static RecyclerView getParentRecyclerView(@Nullable View view) {
        if (view == null) {
            return null;
        }
        ViewParent parent = view.getParent();
        if (parent instanceof RecyclerView) {
            return (RecyclerView) parent;
        } else if (parent instanceof View) {
            return getParentRecyclerView((View) parent);
        } else {
            return null;
        }
    }

    /**
     * Gets directly child of RecyclerView (== {@link androidx.recyclerview.widget.RecyclerView.ViewHolder#itemView}})
     * @param view Child view of the RecyclerView's item
     * @return Item view
     */
    @Nullable
    public static View getParentViewHolderItemView(@Nullable View view) {
        RecyclerView rv = getParentRecyclerView(view);
        if (rv == null) {
            return null;
        }
        return rv.findContainingItemView(view);
    }

    /**
     * Gets {@link androidx.recyclerview.widget.RecyclerView.ViewHolder}.
     * @param view Child view of the RecyclerView's item
     * @return ViewHolder
     */
    @Nullable
    public static RecyclerView.ViewHolder getViewHolder(@Nullable View view) {
        RecyclerView rv = getParentRecyclerView(view);
        if (rv == null) {
            return null;
        }
        return rv.findContainingViewHolder(view);
    }
}
