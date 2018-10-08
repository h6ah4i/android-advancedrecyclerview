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

package com.h6ah4i.android.widget.advrecyclerview.expandable;

import com.h6ah4i.android.widget.advrecyclerview.expandable.annotation.ExpandableItemStateFlags;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * <p>Interface which provides required information for expanding item.</p>
 * <p>Implement this interface on your sub-class of the {@link androidx.recyclerview.widget.RecyclerView.ViewHolder}.</p>
 */
public interface ExpandableItemViewHolder {
    /**
     * Sets the state flags value for expanding item
     *
     * @param flags Bitwise OR of these flags;
     *              - {@link ExpandableItemConstants#STATE_FLAG_IS_GROUP}
     *              - {@link ExpandableItemConstants#STATE_FLAG_IS_CHILD}
     *              - {@link ExpandableItemConstants#STATE_FLAG_IS_EXPANDED}
     *              - {@link ExpandableItemConstants#STATE_FLAG_IS_UPDATED}
     * @see #getExpandState()
     */
    void setExpandStateFlags(@ExpandableItemStateFlags int flags);

    /**
     * Gets the state flags value for expanding item. You can access these flags more human friendly way through {@link #getExpandState()}.
     *
     * @return Bitwise OR of these flags;
     * - {@link ExpandableItemConstants#STATE_FLAG_IS_GROUP}
     * - {@link ExpandableItemConstants#STATE_FLAG_IS_CHILD}
     * - {@link ExpandableItemConstants#STATE_FLAG_IS_EXPANDED}
     * - {@link ExpandableItemConstants#STATE_FLAG_IS_UPDATED}
     * @see #getExpandState()
     */
    @ExpandableItemStateFlags
    int getExpandStateFlags();

    /**
     * Gets the state object for expandable item.
     * This method can be used inside of the {@link androidx.recyclerview.widget.RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int)}.
     *
     * @return {@link ExpandableItemState} object
     */
    @NonNull
    ExpandableItemState getExpandState();
}
