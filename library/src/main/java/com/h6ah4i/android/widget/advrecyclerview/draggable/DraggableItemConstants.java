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

@SuppressWarnings("PointlessBitwiseExpression")
public interface DraggableItemConstants {
    /**
     * State flag for the {@link DraggableItemViewHolder#setDragStateFlags(int)} and {@link DraggableItemViewHolder#getDragStateFlags()} methods.
     * Indicates that currently performing dragging.
     */
    int STATE_FLAG_DRAGGING = (1 << 0);

    /**
     * State flag for the {@link DraggableItemViewHolder#setDragStateFlags(int)} and {@link DraggableItemViewHolder#getDragStateFlags()} methods.
     * Indicates that this item is being dragged.
     */
    int STATE_FLAG_IS_ACTIVE = (1 << 1);

    /**
     * State flag for the {@link DraggableItemViewHolder#setDragStateFlags(int)} and {@link DraggableItemViewHolder#getDragStateFlags()} methods.
     * Indicates that this item is in the range of drag-sortable items
     */
    int STATE_FLAG_IS_IN_RANGE = (1 << 2);

    /**
     * State flag for the {@link DraggableItemViewHolder#setDragStateFlags(int)} and {@link DraggableItemViewHolder#getDragStateFlags()} methods.
     * If this flag is set, some other flags are changed and require to apply.
     */
    int STATE_FLAG_IS_UPDATED = (1 << 31);

    // ---
}
